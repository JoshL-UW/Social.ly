package com.cs407.socially

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class AppExplanationFragment : Fragment() {

    private lateinit var closeButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.app_explanation, container, false)
        closeButton = view.findViewById(R.id.appExplanationCloseButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_appExplanationFragment_to_loginFragment)
        }
    }
}