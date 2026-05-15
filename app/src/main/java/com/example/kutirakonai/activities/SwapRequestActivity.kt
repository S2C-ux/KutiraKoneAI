package com.example.kutirakonai.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutirakonai.models.SwapRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class SwapRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrapId = intent.getStringExtra("scrapId") ?: ""
        val ownerName = intent.getStringExtra("ownerName") ?: ""
        val ownerUid = intent.getStringExtra("ownerUid") ?: ""
        val requestType = intent.getStringExtra("requestType") ?: "SWAP"
        val scrapMaterial = intent.getStringExtra("scrapMaterial") ?: ""
        val scrapColor = intent.getStringExtra("scrapColor") ?: ""
        val scrapSize = intent.getDoubleExtra("scrapSize", 0.0)

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val requesterName = userDoc.getString("name") ?: "Unknown"
                val requestId = UUID.randomUUID().toString()

                val request = SwapRequest(
                    id = requestId,
                    scrapId = scrapId,
                    scrapMaterial = scrapMaterial,
                    scrapColor = scrapColor,
                    scrapSize = scrapSize,
                    scrapOwnerId = ownerUid,
                    scrapOwnerName = ownerName,
                    requesterId = uid,
                    requesterName = requesterName,
                    type = requestType,
                    status = "pending",
                    timestamp = System.currentTimeMillis()
                )

                db.collection("swapRequests").document(requestId).set(request)
                    .addOnSuccessListener {
                        val msg = if (requestType == "BUY")
                            "Buy request sent to $ownerName! ✅"
                        else
                            "Swap request sent to $ownerName! ✅"
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
    }
}