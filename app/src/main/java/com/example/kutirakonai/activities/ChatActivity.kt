package com.example.kutirakonai.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kutirakonai.R
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val otherUserName = intent.getStringExtra("otherUserName") ?: "User"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chat with $otherUserName"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSend = findViewById<Button>(R.id.btnSend)
        val etMessage = findViewById<EditText>(R.id.etMessage)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isNotEmpty()) {
                Toast.makeText(this, "Message: $text", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}