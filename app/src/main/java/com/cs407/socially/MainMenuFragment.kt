package com.cs407.socially

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainMenuFragment : Fragment() {

    private lateinit var firestoreDB: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firestoreDB = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitButton = view.findViewById<Button>(R.id.submitEventCodeButton)
        val eventCodeText = view.findViewById<EditText>(R.id.eventcode_editText)
        val savedConnectionsButton = view.findViewById<Button>(R.id.savedConnectionsButton)
        val settingsButton = view.findViewById<ImageButton>(R.id.settingsButton)
        val createEventButton = view.findViewById<Button>(R.id.createEventButton)

        // Todo event code stuff


        savedConnectionsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_savedConnectionsActivity2)
        }

        createEventButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_createEventFragment)
        }

        submitButton.setOnClickListener {
            // Todo: go to event fragment
            val eventCode = eventCodeText.text.toString().trim()
            if (eventCode.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an event code", Toast.LENGTH_SHORT).show()
            } else if (eventCode.length != 8) {
                Toast.makeText(requireContext(), "Event codes are 8 numbers long", Toast.LENGTH_SHORT).show()
            } else {
                checkCode(eventCode)
            }
        }

        settingsButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), settingsButton)
            popupMenu.menuInflater.inflate(R.menu.profile_settings_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_profile -> {
                        findNavController().navigate(R.id.action_mainMenuFragment_to_profileActivity2)

                        true
                    }
                    R.id.action_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        findNavController().navigate(R.id.action_mainMenuFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun checkCode(code:String) {
        firestoreDB.collection("EventCodes").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var codeFound = false

                // Loop through documents in the collection
                for (document in task.result!!) {
                    val storedCode = document.getString("code")

                    if (code == storedCode) {
                        codeFound = true
                        break
                    }
                }

                // Show a message based on whether the code was found
                if (codeFound) {
                    Toast.makeText(requireContext(), "Code is valid!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_mainMenuFragment_to_connectingActivity2)
                } else {
                    Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.exception)
                Toast.makeText(requireContext(), "Error checking code", Toast.LENGTH_SHORT).show()
            }
        }
    }
}