package com.cs407.socially

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var appExplanation: TextView

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        signInButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment)
        }
        signUpButton.setOnClickListener {  }
        appExplanation.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_appExplanationFragment)
        }
    }
}