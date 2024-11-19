package com.cs407.socially

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController


class CreateEventFragment : Fragment() {
    private lateinit var newEventCode: EditText
    private lateinit var submitNewEventButton: Button
    private lateinit var  backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { val view = inflater.inflate(R.layout.fragment_create_event, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitNewEventButton = view.findViewById<Button>(R.id.submitNewEventButton)
        backButton = view.findViewById<Button>(R.id.returnToMainMenuButton)
        newEventCode = view.findViewById(R.id.createEventCode)

        submitNewEventButton.setOnClickListener {

            val eventCode = newEventCode.text.toString().trim()
            // TODO: submit to firestore as a new event code

            Toast.makeText(requireContext(), "New Event Created", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_createEventFragment_to_mainMenuFragment)
        }
    }
}