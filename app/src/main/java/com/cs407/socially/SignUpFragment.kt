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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        auth = FirebaseAuth.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = view.findViewById<EditText>(R.id.createUserNameEditText)
        val password = view.findViewById<EditText>(R.id.createPasswordEditText)
        val confirm = view.findViewById<EditText>(R.id.createConfirmPasswordEditText)
        val createAccountButton = view.findViewById<Button>(R.id.createAccountButton)

        createAccountButton.setOnClickListener {
            val usernameInput = username.text.toString().trim()
            val passwordInput = password.text.toString().trim()
            val confirmInput = confirm.text.toString().trim()

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                // Todo: show toast to tell user that a field is empty
                showToast("Please fill out all fields")
            }
            else if (passwordInput != confirmInput) {
                showToast("Passwords do not match")
            }
            else {
                createUser(usernameInput, passwordInput)
            }

        }
    }


    private fun createUser(username: String, password: String) {

        // TODO: Maybe just use emails not username
        val usernameToEmail = "$username@email.com"

        auth.createUserWithEmailAndPassword(usernameToEmail, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    createDefaultProfile(username, userId)
                }

                showToast("Account created successfully")


            }
            else {
                val errorMessage = task.exception?.message ?: "Signup failed"
                showToast(errorMessage)
            }
        }
    }

    private fun createDefaultProfile(username: String, userId: String) {
        val db = FirebaseFirestore.getInstance()

        val defaultProfile = mapOf(
            "name" to username,
            "bio" to "User Biography",
            "company" to "User Company",
            "email" to "User email",
            "linkedIn" to "User linkedIn",
            "phoneNumber" to "User Phone Number",
            "position" to "User Position",
            "profilePicture" to ""
        )

        db.collection("Users").document(userId)
            .set(defaultProfile).addOnSuccessListener {
                showToast("Account created successfully, please log in")
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

            }
            .addOnFailureListener { e ->
                showToast("Account creation failed: ${e.message}")
            }

        db.collection("Connections").document(userId)
            .set(mapOf("connections" to emptyList<String>()))

        }




    private fun showToast(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
}