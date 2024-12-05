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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreateEventFragment : Fragment() {
    private lateinit var generateNewCodeButton: Button
    private lateinit var backButton: Button
    private lateinit var newCode: TextView
    private lateinit var firestoreDB: FirebaseFirestore

    data class EventData(
        val date: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_event, container, false)
        firestoreDB = FirebaseFirestore.getInstance()
        newCode = view.findViewById<TextView>(R.id.newCode)
        newCode.visibility = View.INVISIBLE
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateNewCodeButton = view.findViewById<Button>(R.id.submitNewEventButton)
        backButton = view.findViewById<Button>(R.id.returnToMainMenuButton)

        generateNewCodeButton.setOnClickListener {
            newCode.visibility = View.VISIBLE
            val eventCode = generateUniqueEventCode()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val eventData = EventData(date = currentDate)

            // TODO: submit to firestore as a new event code
            firestoreDB.collection("EventCodes").document(eventCode)
                .set(eventData)
                .addOnSuccessListener {
                    generateNewCodeButton.isEnabled = false
                    val displayText = "Copy this: $eventCode"
                    newCode.text = displayText
                    backButton.text = getString(R.string.confirm_code)
                    Toast.makeText(requireContext(), "New Event Created", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding document", e)
                    Toast.makeText(requireContext(), "Failed to add code", Toast.LENGTH_SHORT).show()
                }
        }
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_createEventFragment_to_mainMenuFragment)
        }
    }

    //https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun generateUniqueEventCode(): String {
        var eventCode = ""
        var codeExists = true

        while (codeExists) {
            eventCode = getRandomString(10)
            codeExists = checkIfCodeExists(eventCode)
        }

        return eventCode
    }

    fun checkIfCodeExists(code: String): Boolean {
        var exists = false
        val task = firestoreDB.collection("EventCodes").document(code).get()
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                exists = task.result.exists()
            }
        }
        return exists
    }

}