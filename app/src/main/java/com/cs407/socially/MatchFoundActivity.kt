package com.cs407.socially

import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.WorkDuration
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.concurrent.timer
import android.content.Intent
import android.nfc.NdefMessage
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MatchFoundActivity : Fragment() {
    private lateinit var profileImageView: ImageView
    private lateinit var nameToFindTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var eventListener: ListenerRegistration
    private lateinit var leaveEventButton: Button
    private lateinit var chatButton: Button

    private val firestoreDB = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var timer: CountDownTimer? = null
    private var pairedUserIdCopy = ""
    private var nfcAdapter: NfcAdapter? = null // Declare NFC adapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_match_found, container, false)
        profileImageView = view.findViewById(R.id.profileImageView)
        nameToFindTextView = view.findViewById(R.id.nameToFindTextView)
        timerTextView = view.findViewById(R.id.timerTextView)
        leaveEventButton = view.findViewById(R.id.leaveEventButton)
        chatButton = view.findViewById(R.id.openChatButton)

        ///////////////////////////////
        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "NFC is not supported on this device.", Toast.LENGTH_LONG).show()
        } else if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(requireContext(), "NFC is disabled. Please enable it to continue.", Toast.LENGTH_LONG).show()
        }
        /////////////////////////////

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatButton.setOnClickListener {
            findNavController().navigate(R.id.action_matchFoundActivity2_to_ChatFragment)
        }

        leaveEventButton.setOnClickListener {
            val eventCode = arguments?.getString("eventCode")
            if (eventCode != null) {
                removeUserFromEvent(eventCode)
            }
        }

        // save profile button clicked
        val checkCircleImageView = view.findViewById<Button>(R.id.checkCircleImageView)
        checkCircleImageView.setOnClickListener {

            //code to save to db
            if(currentUserId != null) {
                val userIdDoc = firestoreDB.collection("Connections").document(currentUserId)
                userIdDoc.update("connections", FieldValue.arrayUnion(pairedUserIdCopy))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Connection successfully added!")
                        Toast.makeText(requireContext(), "Connection successfully added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding connection", e)
                        Toast.makeText(requireContext(), "Error adding connection.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val eventCode = arguments?.getString("eventCode")
        if (eventCode == null) {
            Log.e("MatchFoundActivity", "eventCode is null. Cannot proceed.")
            return
        }
        if (currentUserId != null) {
            listenForUpdates(eventCode)
        }
    }

    private fun listenForUpdates(eventCode: String) {
        val event = firestoreDB.collection("EventCodes").document(eventCode)

        eventListener = event.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MatchFoundActivity", "Error listening for updates: $error")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val pairs = snapshot.get("pairs") as? List<Map<String, String>>

                //  retrieve server side time of last change
                val nextUpdateTime = snapshot.getTimestamp("nextUpdateTime")?.toDate()?.time

                loadCurrentMatch(pairs)

                if (nextUpdateTime != null) {
                    val localTime = System.currentTimeMillis()
                    val timeLeft = nextUpdateTime - localTime

                    if (timeLeft > 0) {
                        startTimer(timeLeft)
                    }
                    else {
                        Log.d("match found fragment", "Some issue with timer")
                    }
                    if (timeLeft < 5) {
                        Toast.makeText(requireContext(), "Finding new match...", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }

    private fun loadCurrentMatch(pairs: List<Map<String, String>>?) {
        if (pairs == null) {
            Log.e("MatchFoundActivity", "no pairs found in event document")
            return
        }
        val currPair = pairs.find{ it["participant1"] == currentUserId || it["participant2"] == currentUserId }

        val pairedUserId = if (currPair != null) {
            if (currPair["participant1"] == currentUserId) { currPair["participant2"] }
            else { currPair["participant1"] }
        }
        else { null }

        if (pairedUserId != null) {
            pairedUserIdCopy = pairedUserId
        }

        if (pairedUserId != null) {
            firestoreDB.collection("Users").document(pairedUserId)
                .get()
                .addOnSuccessListener { userData ->
                    val name = userData.getString("name") ?: "Unknown"
                    nameToFindTextView.text = name
                    val profilePictureBase64 = userData.getString("profilePicture")
                    if (!profilePictureBase64.isNullOrEmpty()) {
                        val bitmap = decodeBase64ToImage(profilePictureBase64)
                        profileImageView.setImageBitmap(bitmap)
                    }
                }
        }
    }


    private fun decodeBase64ToImage(base64String: String): Bitmap {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun startTimer(duration: Long) {

        timer?.cancel() //delete previous timer

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60

                timerTextView.text = String.format("%02d:%02d", min, sec)
            }

            override fun onFinish() {
                Log.d("MatchFoundActivity", "Timer finished.")
            }
        }

        timer?.start()

    }

    private fun removeUserFromEvent(eventCode: String) {

        val event = firestoreDB.collection("EventCodes").document(eventCode)
        event.update("participants", FieldValue.arrayRemove(currentUserId))
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_matchFoundActivity2_to_mainMenuFragment)
            }

    }

    ///////////////////////////////////
     fun handleNfcIntent(intent: Intent) {

        //code to save to db
        if(currentUserId != null) {
            val userIdDoc = firestoreDB.collection("Connections").document(currentUserId)
            userIdDoc.update("connections", FieldValue.arrayUnion(pairedUserIdCopy))
                .addOnSuccessListener {
                    Log.d("Firestore", "Connection successfully added!")
                    Toast.makeText(requireContext(), "Connection successfully added!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding connection", e)
                    Toast.makeText(requireContext(), "Error adding connection.", Toast.LENGTH_SHORT).show()
                }
        }

    }
    ///////////////////////////////////

    override fun onDestroyView() {
        super.onDestroyView()

        if (::eventListener.isInitialized) {
            eventListener.remove()
        }
    }
}



