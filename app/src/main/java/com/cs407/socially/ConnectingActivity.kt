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

class ConnectingActivity : Fragment() {
    private lateinit var placeHolderButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_connecting, container, false)
        placeHolderButton = view.findViewById<Button>(R.id.placeHolderButton)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        placeHolderButton.setOnClickListener {

            findNavController().navigate(R.id.action_connectingActivity2_to_matchFoundActivity2)
        }
    }


}