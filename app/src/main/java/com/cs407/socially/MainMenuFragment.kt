package com.cs407.socially

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitButton = view.findViewById<Button>(R.id.submitEventCodeButton)
        val eventCodeText = view.findViewById<EditText>(R.id.eventcode_editText)
        val savedConnectionsButton = view.findViewById<Button>(R.id.savedConnectionsButton)
        val settingsButton = view.findViewById<ImageButton>(R.id.settingsButton)

        // Todo event code stuff



        savedConnectionsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_savedConnections)
        }

        submitButton.setOnClickListener {
            // Todo: go to event fragment
        }

        settingsButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), settingsButton)
            popupMenu.menuInflater.inflate(R.menu.profile_settings_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_profile -> {
                        // todo go to profile fragment

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
}