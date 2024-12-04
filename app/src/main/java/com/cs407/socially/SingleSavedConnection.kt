package com.cs407.socially


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SingleSavedConnection : Fragment() {
    private lateinit var saveAndCloseButton: Button
    private lateinit var exitWithoutSaveButton: Button
    private lateinit var profileName: TextView
    private lateinit var aboutProfile: TextView
    private lateinit var profileCompany: TextView
    private lateinit var profilePosition: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var email: TextView
    private lateinit var linkedIn: TextView
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_saved_connections, container, false)
        saveAndCloseButton = view.findViewById<Button>(R.id.connectionSaveButton)
        exitWithoutSaveButton = view.findViewById<Button>(R.id.connectionExitButton)
        profileName = view.findViewById(R.id.connectionNameTitleTextView)
        aboutProfile = view.findViewById(R.id.connectionAboutEditText)
        profileCompany = view.findViewById(R.id.connectionCompanyEditText)
        profilePosition = view.findViewById(R.id.connectionPositionEditText)
        phoneNumber = view.findViewById(R.id.connectionPhoneEditText)
        email = view.findViewById(R.id.connectionEmailEditText)
        linkedIn = view.findViewById(R.id.connectionLinkedinEditText)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectionId = arguments?.getString("connectionId")
        Log.d("SingleSavedConnection", "Received connectionId: $connectionId")
        if (connectionId != null) {
            loadProfile(connectionId)
        }

        exitWithoutSaveButton.setOnClickListener {
            findNavController().navigate(R.id.action_singleSavedConnection_to_savedConnectionsActivity2)
        }


    }
    private fun loadProfile(connectionId: String) {
        db.collection("Users").document(connectionId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    profileName.setText(document.getString("name"))
                    aboutProfile.setText(document.getString("bio"))
                    profileCompany.setText(document.getString("company"))
                    profilePosition.setText(document.getString("position"))
                    phoneNumber.setText(document.getString("phoneNumber"))
                    email.setText(document.getString("email"))
                    linkedIn.setText(document.getString("linkedIn"))
                    // TODO: profile picture
                }
                else { Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show() }
            }

    }

}