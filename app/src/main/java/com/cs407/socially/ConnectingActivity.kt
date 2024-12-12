package com.cs407.socially

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.concurrent.timerTask

class ConnectingActivity : Fragment() {
    private lateinit var placeHolderButton: Button
    private lateinit var connectingTextView: TextView
    private lateinit var welcomeText: TextView
    private lateinit var profilePicture: ImageView
    private var timer: CountDownTimer? = null
    private lateinit var eventListener: ListenerRegistration
    private val firestoreDB = FirebaseFirestore.getInstance()
    private var hasNavigated = false
    private var timerCompleted = false
    private var timerStarted = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_connecting, container, false)
        connectingTextView = view.findViewById(R.id.connectingTextView)
        placeHolderButton = view.findViewById<Button>(R.id.placeHolderButton)
        welcomeText = view.findViewById(R.id.welcomeToEventText)
        profilePicture = view.findViewById(R.id.profileImageView)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val eventCode = arguments?.getString("eventCode").toString()
        //navigateToMatchFound(eventCode)


        if (eventCode != null) {
            displayUserDetails()
            listenForUpdates(eventCode)
            firestoreDB.collection("EventCodes").document(eventCode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val nextUpdateTime = snapshot.getTimestamp("nextUpdateTime")?.toDate()?.time
                    if (nextUpdateTime != null) {
                        val timeLeft = nextUpdateTime - System.currentTimeMillis()
                        if (timeLeft > 0) {
                            startTimer(timeLeft)
                        } else {
                            connectingTextView.text = "Waiting for next update..."
                        }
                    } else {
                        Log.e("ConnectingActivity", "nextUpdateTime is missing")
                        connectingTextView.text = "Error: Unable to retrieve update time"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ConnectingActivity", "Failed to retrieve nextUpdateTime: ${e.message}")
                    connectingTextView.text = "Error: Unable to retrieve update time"
                }
        } else {
            Log.e("ConnectingActivity", "No event code provided")
            Toast.makeText(requireContext(), "Error: No event code provided.", Toast.LENGTH_SHORT)
                .show()
        }
        placeHolderButton.setOnClickListener {
            removeUserFromEvent(eventCode)
        }
    }




    private fun listenForUpdates(eventCode: String) {
        val event = firestoreDB.collection("EventCodes").document(eventCode)
        var initialLoad = true

        eventListener = event.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MatchFoundActivity", "Error listening for updates: $error")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                if (initialLoad) {
                    initialLoad = false
                    return@addSnapshotListener

                }
                navigateToMatchFound(eventCode)

            }
        }
    }
    private fun startTimer(timeLeft: Long) {
        timer?.cancel()

        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (hasNavigated) {
                    cancel()
                    return
                }
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60

                connectingTextView.text = "Round in progress... \n " +
                        "Time until next round: \n" +
                        String.format("%02d:%02d", min,sec)
            }

            override fun onFinish() {
                timerCompleted = true

                Log.d("Connecting activty", "Reached onfinish to nav to match found in sstartTimer")
            }
        }.start()


    }
    private fun navigateToMatchFound(eventCode: String) {
        if (!hasNavigated) {
            hasNavigated = true
            timerCompleted
            timer?.cancel()
            timerStarted = false;

            if (::eventListener.isInitialized) {
                eventListener.remove()
            }
            val bundle = Bundle().apply {
                putString("eventCode", eventCode)
            }
            Log.d("Connecting activty", "Reached navigateToMAtchFound")
            findNavController().navigate(
                R.id.action_connectingActivity2_to_matchFoundActivity2,
                bundle
            )
        }
    }
    private fun removeUserFromEvent(eventCode: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val event = firestoreDB.collection("EventCodes").document(eventCode)

        event.update("participants", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                if (findNavController().currentDestination?.id == R.id.connectingActivity2) {
                    findNavController().navigate(R.id.action_connectingActivity2_to_mainMenuFragment)
                }
                if (findNavController().currentDestination?.id == R.id.matchFoundActivity2) {
                    findNavController().navigate(R.id.action_matchFoundActivity2_to_mainMenuFragment)
                }
                //findNavController().navigate(R.id.action_connectingActivity2_to_mainMenuFragment)
            }
    }

    private fun displayUserDetails(){
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUser == null) {
            Log.e("Connecting Activity", "No user signed in")
            return
        }

        firestoreDB.collection("Users").document(currentUser)
            .get()
            .addOnSuccessListener { user ->
                val name = user.getString("name") ?: "User"
                val profilePictureBase64 = user.getString("profilePicture")

                welcomeText.text = "Welcome to event $name!"


                if (!profilePictureBase64.isNullOrEmpty()) {
                    val bitmap = decodeBase64ToImage(profilePictureBase64)
                    profilePicture.setImageBitmap(bitmap)
                }


            }

    }
    private fun decodeBase64ToImage(base64String: String): Bitmap {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (::eventListener.isInitialized) {
            eventListener.remove()
        }
        timer?.cancel()
    }


}