package com.cs407.socially


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

class ProfileActivity : Fragment() {
    private lateinit var saveAndCloseButton: Button
    private lateinit var exitWithoutSaveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        saveAndCloseButton = view.findViewById<Button>(R.id.profileSaveButton)
        exitWithoutSaveButton = view.findViewById<Button>(R.id.profileExitButton)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveAndCloseButton.setOnClickListener {
            // TODO: save profile here/submit

            findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
        }

        exitWithoutSaveButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
        }


    }
}

