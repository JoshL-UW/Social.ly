package com.cs407.socially

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.nfc.NfcAdapter
import android.widget.Toast

private var nfcAdapter: NfcAdapter? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Check if NFC is available on the device
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not available on this device.", Toast.LENGTH_LONG).show()
            return
        }

        // Check if NFC is enabled
        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC is disabled. Please enable it in settings.", Toast.LENGTH_LONG).show()
        }

    }
}