package com.cs407.socially

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.concurrent.timer

class MatchFoundActivity : Fragment() {
    private lateinit var profileImageView: ImageView
    private lateinit var nameToFindTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var eventListener: ListenerRegistration
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var timer: CountDownTimer? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_match_found, container, false)
        profileImageView = view.findViewById(R.id.profileImageView)
        nameToFindTextView = view.findViewById(R.id.nameToFindTextView)
        timerTextView = view.findViewById(R.id.timerTextView)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()

        if (::eventListener.isInitialized) {
            eventListener.remove()
        }
    }
}



