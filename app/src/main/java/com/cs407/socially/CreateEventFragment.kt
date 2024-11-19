package com.cs407.socially

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class CreateEventFragment : Fragment() {
    private lateinit var newEventCode: EditText
    private lateinit var submitNewEventButton: Button
    private lateinit var backButton: Button
    private lateinit var firestoreDB: FirebaseFirestore

    data class EventCode(
        val code: String = ""
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_event, container, false)
        firestoreDB = FirebaseFirestore.getInstance()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitNewEventButton = view.findViewById<Button>(R.id.submitNewEventButton)
        backButton = view.findViewById<Button>(R.id.returnToMainMenuButton)
        newEventCode = view.findViewById(R.id.createEventCode)

        submitNewEventButton.setOnClickListener {

            val eventCodeInput = newEventCode.text.toString().trim()
            // TODO: submit to firestore as a new event code
            if (eventCodeInput.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a code", Toast.LENGTH_SHORT).show()
            } else if (eventCodeInput.length != 8) {
                Toast.makeText(requireContext(), "Please enter an 8 digit code", Toast.LENGTH_SHORT).show()
            } else {
                val eventCode = EventCode(eventCodeInput)
                firestoreDB.collection("EventCodes").whereEqualTo("code", eventCodeInput).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (!task.result.isEmpty) {
                                Toast.makeText(requireContext(), "Code already exists!", Toast.LENGTH_SHORT).show()
                            } else {
                                firestoreDB.collection("EventCodes").add(eventCode)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(requireContext(), "New Event Created", Toast.LENGTH_SHORT).show()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            findNavController().navigate(R.id.action_createEventFragment_to_mainMenuFragment)
                                        }, 2000)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error adding document", e)
                                        Toast.makeText(requireContext(), "Failed to add code", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Log.e("Firestore", "Error checking for duplicates: ", task.exception)
                            Toast.makeText(requireContext(), "Error checking code", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_createEventFragment_to_mainMenuFragment)
        }
    }
}