package com.cs407.socially


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : Fragment() {
    private lateinit var saveAndCloseButton: Button
    private lateinit var exitWithoutSaveButton: Button
    private lateinit var profileNameEdit: EditText
    private lateinit var aboutProfileEdit: EditText
    private lateinit var profileCompanyEdit: EditText
    private lateinit var profilePositionEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var linkedInEdit: EditText
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        saveAndCloseButton = view.findViewById<Button>(R.id.profileSaveButton)
        exitWithoutSaveButton = view.findViewById<Button>(R.id.profileExitButton)
        profileNameEdit = view.findViewById(R.id.profileNameEditText)
        aboutProfileEdit = view.findViewById(R.id.profileAboutEditText)
        profileCompanyEdit = view.findViewById(R.id.profileCompanyEditText)
        profilePositionEdit = view.findViewById(R.id.profilePositionEditText)
        phoneNumberEdit = view.findViewById(R.id.profilePhoneEditText)
        emailEdit = view.findViewById(R.id.profileEmailEditText)
        linkedInEdit = view.findViewById(R.id.profileLinkedinEditText)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != null) {
            loadProfile()
        }

        saveAndCloseButton.setOnClickListener {
            if (userId != null) {
                saveProfile()
            }
        }

        exitWithoutSaveButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
        }


    }
    private fun loadProfile() {
        db.collection("Users").document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    profileNameEdit.setText(document.getString("name"))
                    aboutProfileEdit.setText(document.getString("bio"))
                    profileCompanyEdit.setText(document.getString("company"))
                    profilePositionEdit.setText(document.getString("position"))
                    phoneNumberEdit.setText(document.getString("phoneNumber"))
                    emailEdit.setText(document.getString("email"))
                    linkedInEdit.setText(document.getString("linkedIn"))
                    // TODO: profile picture
                }
                else { Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show() }
            }

    }
    private fun saveProfile() {
        val updatedProfile = mapOf(
            "name" to profileNameEdit.text.toString(),
            "bio" to aboutProfileEdit.text.toString(),
            "company" to profileCompanyEdit.text.toString(),
            "position" to profilePositionEdit.text.toString(),
            "phoneNumber" to phoneNumberEdit.text.toString(),
            "email" to emailEdit.text.toString(),
            "linkedIn" to linkedInEdit.text.toString()
        )
        db.collection("Users").document(userId!!).update(updatedProfile)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
            }
    }
}

