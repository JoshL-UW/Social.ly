package com.cs407.socially

import android.app.ApplicationExitInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SavedConnectionsActivity : Fragment() {
    private lateinit var saveConnectionButton: Button
    private lateinit var exitConnectionButton: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { val view = inflater.inflate(R.layout.activity_saved_connections, container, false)
        saveConnectionButton = view.findViewById<Button>(R.id.connectionSaveButton)
        exitConnectionButton = view.findViewById<Button>(R.id.connectionExitButton)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveConnectionButton.setOnClickListener {
            // TODO: save connection here in cloud

            findNavController().navigate(R.id.action_savedConnectionsActivity2_to_mainMenuFragment)


        }

        exitConnectionButton.setOnClickListener {
            findNavController().navigate(R.id.action_savedConnectionsActivity2_to_mainMenuFragment)
        }

    }


}