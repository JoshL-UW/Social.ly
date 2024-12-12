package com.cs407.socially

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class ChatFragment : Fragment() {
    private lateinit var backButton: Button
    private lateinit var sendButton: Button
    private lateinit var textEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        backButton = view.findViewById(R.id.closeChatButton)
        sendButton = view.findViewById(R.id.sendChatButton)
        textEdit = view.findViewById(R.id.chatEditText)
        return inflater.inflate(R.layout.fragment_chat, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // send button
        sendButton.setOnClickListener {
            sendMessage()
        }

        // back button
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ChatFragment_to_matchFoundActivity2)
        }
    }

    fun sendMessage() {
        val textToSend = textEdit.text.toString()

    }

}