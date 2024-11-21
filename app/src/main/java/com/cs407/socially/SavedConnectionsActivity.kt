package com.cs407.socially

import android.app.ApplicationExitInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedConnectionsActivity : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var closeSavedConnectionsButton: Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { val view = inflater.inflate(R.layout.fragment_saved_connections, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        closeSavedConnectionsButton = view.findViewById(R.id.closeSavedConnectionsButton)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != null) {
            Log.d("SavedConnections", "Current userId: $userId")
            fetchConnections(userId)
        }

        closeSavedConnectionsButton.setOnClickListener {
            findNavController().navigate(R.id.action_savedConnectionsActivity2_to_mainMenuFragment)
        }
    }

    private fun fetchConnections(userId: String) {
        Log.d("fetchConnections", "fetchConnections called with userId: $userId")
        db.collection("Connections").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val connections = document.get("connections") as? List<String> ?: emptyList()
                    Log.d("SavedConnections", "Fetched connections: $connections")
                    if (connections.isNotEmpty()) {
                        Toast.makeText(requireContext(), "Connections: $connections", Toast.LENGTH_SHORT).show()
                    }
                    val adapter = ConnectionsAdapter(connections) { connectionId ->
                        navToProfile(connectionId)
                    }
                    recyclerView.adapter = adapter

                }
                else {
                    Toast.makeText(requireContext(),"No Connections Found", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("No document", "No document found")
            }

    }
    private fun navToProfile(connectionId: String) {
        val bundle = Bundle()
        bundle.putString("connectionId", connectionId)
        Log.d("SavedConnections", "Navigating to profile for connectionId: $connectionId")
        findNavController().navigate(R.id.action_savedConnectionsActivity2_to_singleSavedConnection, bundle)
    }


}