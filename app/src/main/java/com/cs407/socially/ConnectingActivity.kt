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

class ConnectingActivity : Fragment() {
    private lateinit var placeHolderButton: Button
    private lateinit var connectingTextView: TextView
    private var timer: CountDownTimer? = null
    private lateinit var eventListener: ListenerRegistration
    private val firestoreDB = FirebaseFirestore.getInstance()


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

        val eventCode = arguments?.getString("eventCode")

        if (eventCode != null) {
            listenForUpdates(eventCode)
        }
        else {
            Log.d("Connecting Fragment", "No event code error")
        }

        /* Only use for debugging
        placeHolderButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("eventCode", eventCode)
            }
            findNavController().navigate(R.id.action_connectingActivity2_to_matchFoundActivity2, bundle)
        }

         */
    }


    private fun listenForUpdates(eventCode: String) {
        val event = firestoreDB.collection("EventCodes").document(eventCode)

        eventListener = event.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MatchFoundActivity", "Error listening for updates: $error")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                //  retrieve server side time of last change
                val nextUpdateTime = snapshot.getTimestamp("nextUpdateTime")?.toDate()?.time

                if (nextUpdateTime != null) {
                    val localTime = System.currentTimeMillis()
                    val timeLeft = nextUpdateTime - localTime

                    if (timeLeft > 0) {
                        startTimer(timeLeft, eventCode)
                    }
                    else {
                        navigateToMatchFound(eventCode)
                    }
                    if (timeLeft < 5) {
                        Toast.makeText(requireContext(), "Finding new match...", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }
    private fun startTimer(timeLeft: Long, eventCode: String) {
        timer?.cancel()

        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60

                connectingTextView.text = "Round in progress... \n " +
                        "Time until next round: \n" +
                        String.format("%02d:%02d", min,sec)
            }

            override fun onFinish() {
                navigateToMatchFound(eventCode)
            }
        }.start()


    }
    private fun navigateToMatchFound(eventCode: String) {
        val bundle = Bundle().apply {
            putString("eventCode", eventCode)
        }
        findNavController().navigate(R.id.action_connectingActivity2_to_matchFoundActivity2, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (::eventListener.isInitialized) {
            eventListener.remove()
        }
        timer?.cancel()
    }

}