package com.cs407.socially

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView


class ConnectionsAdapter (
    private val connections: List<String>,
    private val onConnectionClick: (String) -> Unit ) : RecyclerView.Adapter<ConnectionsAdapter.ConnectionViewHolder>() {

        inner class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val connectionButton: Button = itemView.findViewById(R.id.connectionButton)

            fun bind(connectionId: String) {
                connectionButton.text = connectionId
                connectionButton.setOnClickListener {
                    onConnectionClick(connectionId)
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_connections, parent, false)
            return ConnectionViewHolder(view)
        }

        override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
            holder.bind(connections[position])
        }

        override fun getItemCount(): Int = connections.size
    }




