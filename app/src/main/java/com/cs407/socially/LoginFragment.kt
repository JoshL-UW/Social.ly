package com.cs407.socially

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var appExplanation: TextView
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        usernameEditText = view.findViewById(R.id.editUsername)
        passwordEditText = view.findViewById(R.id.editPassword)
        signInButton = view.findViewById(R.id.signInButton)
        signUpButton = view.findViewById(R.id.signUpButton)
        appExplanation = view.findViewById(R.id.sociallyTip)
        auth = FirebaseAuth.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        signInButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()

            }
            else {
                signIn(username, password)
            }
        }
        signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        appExplanation.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_appExplanationFragment)
        }
    }
    private fun signIn(username: String, password: String) {
        // TODO: change this if switching to email sign in
        val usernameToEmail = "$username@email.com"

        auth.signInWithEmailAndPassword(usernameToEmail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                // Go to main menu
                findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment)
            }
            else {
                val exception = task.exception
                when {
                    exception?.message?.contains("There is no user record") == true -> {
                        Toast.makeText(requireContext(), "Username not found. Please sign up first.", Toast.LENGTH_SHORT).show()
                    }
                    exception?.message?.contains("The password is invalid") == true -> {
                        Toast.makeText(requireContext(), "Incorrect Password.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
}