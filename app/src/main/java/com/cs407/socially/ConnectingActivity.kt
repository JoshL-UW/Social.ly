package com.cs407.socially

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.concurrent.timerTask

class ConnectingActivity : Fragment() {
    private lateinit var placeHolderButton: Button
    private lateinit var connectingTextView: TextView
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


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val eventCode = arguments?.getString("eventCode").toString()
        //navigateToMatchFound(eventCode)


        if (eventCode != null) {

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

    override fun onDestroyView() {
        super.onDestroyView()

        if (::eventListener.isInitialized) {
            eventListener.remove()
        }
        timer?.cancel()
    }

}