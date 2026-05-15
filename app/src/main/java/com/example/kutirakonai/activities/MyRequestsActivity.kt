package com.example.kutirakonai.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kutirakonai.R
import com.example.kutirakonai.models.SwapRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyRequestsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val requests = mutableListOf<SwapRequest>()
    private var currentTab = "Received"
    private lateinit var adapter: RequestsAdapter
    private lateinit var tvNoRequests: TextView
    private lateinit var rvRequests: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requests)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Requests"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvNoRequests = findViewById(R.id.tvNoRequests)
        rvRequests = findViewById(R.id.rvRequests)

        val btnSent = findViewById<Button>(R.id.btnSent)
        val btnReceived = findViewById<Button>(R.id.btnReceived)
        val btnSwap = findViewById<Button>(R.id.btnSwap)

        rvRequests.layoutManager = LinearLayoutManager(this)
        adapter = RequestsAdapter(requests, currentTab) { request, action ->
            handleAction(request, action)
        }
        rvRequests.adapter = adapter

        btnSent.setOnClickListener {
            currentTab = "Sent"
            adapter = RequestsAdapter(requests, currentTab) { request, action ->
                handleAction(request, action)
            }
            rvRequests.adapter = adapter
            loadRequests()
        }

        btnReceived.setOnClickListener {
            currentTab = "Received"
            adapter = RequestsAdapter(requests, currentTab) { request, action ->
                handleAction(request, action)
            }
            rvRequests.adapter = adapter
            loadRequests()
        }

        btnSwap.setOnClickListener {
            currentTab = "Swap"
            adapter = RequestsAdapter(requests, currentTab) { request, action ->
                handleAction(request, action)
            }
            rvRequests.adapter = adapter
            loadRequests()
        }

        loadRequests()
    }

    private fun loadRequests() {
        val uid = auth.currentUser?.uid ?: return
        requests.clear()

        val query = when (currentTab) {
            "Sent" -> db.collection("swapRequests").whereEqualTo("requesterId", uid)
            "Received" -> db.collection("swapRequests").whereEqualTo("scrapOwnerId", uid)
            "Swap" -> db.collection("swapRequests").whereEqualTo("type", "SWAP")
            else -> db.collection("swapRequests")
        }

        query.addSnapshotListener { snap, error ->
            if (error != null) return@addSnapshotListener

            snap?.let {
                requests.clear()
                for (doc in it.documents) {
                    val req = doc.toObject(SwapRequest::class.java)
                    if (req != null) {
                        requests.add(req)
                    }
                }
                adapter.notifyDataSetChanged()

                if (requests.isEmpty()) {
                    tvNoRequests.visibility = View.VISIBLE
                    rvRequests.visibility = View.GONE
                } else {
                    tvNoRequests.visibility = View.GONE
                    rvRequests.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleAction(request: SwapRequest, action: String) {
        when (action) {
            "ACCEPT" -> {
                db.collection("swapRequests").document(request.id).update("status", "accepted")
                Toast.makeText(this, "Accepted!", Toast.LENGTH_SHORT).show()
                loadRequests()
            }
            "REJECT" -> {
                db.collection("swapRequests").document(request.id).update("status", "rejected")
                Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                loadRequests()
            }
            "CHAT" -> {
                val otherUid = if (currentTab == "Sent") request.scrapOwnerId else request.requesterId
                val otherName = if (currentTab == "Sent") request.scrapOwnerName else request.requesterName
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("requestId", request.id)
                    putExtra("otherUserId", otherUid)
                    putExtra("otherUserName", otherName)
                })
            }
        }
    }

    inner class RequestsAdapter(
        private val items: List<SwapRequest>,
        private val tabType: String,
        private val onAction: (SwapRequest, String) -> Unit
    ) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(request: SwapRequest) {
                val container = itemView as LinearLayout
                container.removeAllViews()

                // Main info container
                val mainLayout = LinearLayout(itemView.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 12) }
                    setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
                    elevation = 2f
                    setPadding(14, 14, 14, 14)
                }

                // From/To info
                val tvInfo = TextView(itemView.context).apply {
                    text = if (tabType == "Sent")
                        "To: ${request.scrapOwnerName}"
                    else
                        "From: ${request.requesterName}"
                    textSize = 15f
                    setTextColor(android.graphics.Color.parseColor("#333333"))
                    setPadding(0, 0, 0, 8)
                }

                // Material info
                val tvMaterial = TextView(itemView.context).apply {
                    text = "${request.scrapMaterial} • ${request.scrapColor} • ${request.scrapSize}m"
                    textSize = 13f
                    setTextColor(android.graphics.Color.parseColor("#666666"))
                    setPadding(0, 0, 0, 8)
                }

                // Status
                val tvStatus = TextView(itemView.context).apply {
                    text = "Status: ${request.status.uppercase()}"
                    textSize = 12f
                    setTextColor(
                        when (request.status) {
                            "accepted" -> android.graphics.Color.parseColor("#4CAF50")
                            "rejected" -> android.graphics.Color.parseColor("#F44336")
                            else -> android.graphics.Color.parseColor("#FF9800")
                        }
                    )
                    setPadding(0, 0, 0, 12)
                }

                mainLayout.addView(tvInfo)
                mainLayout.addView(tvMaterial)
                mainLayout.addView(tvStatus)
                container.addView(mainLayout)

                // Action buttons - LARGER SIZE
                val btnLayout = LinearLayout(itemView.context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                if (tabType == "Received" && request.status == "pending") {
                    val btnAccept = Button(itemView.context).apply {
                        text = "✅ Accept"
                        layoutParams = LinearLayout.LayoutParams(0, 65, 1f).apply { setMargins(0, 0, 8, 0) }
                        textSize = 14f
                        setOnClickListener { onAction(request, "ACCEPT") }
                    }
                    val btnReject = Button(itemView.context).apply {
                        text = "❌ Reject"
                        layoutParams = LinearLayout.LayoutParams(0, 65, 1f).apply { setMargins(8, 0, 0, 0) }
                        textSize = 14f
                        setOnClickListener { onAction(request, "REJECT") }
                    }
                    btnLayout.addView(btnAccept)
                    btnLayout.addView(btnReject)
                }

                val btnChat = Button(itemView.context).apply {
                    text = "💬 Chat"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        68
                    ).apply { setMargins(0, if (tabType == "Received" && request.status == "pending") 12 else 0, 0, 0) }
                    textSize = 15f
                    setOnClickListener { onAction(request, "CHAT") }
                }

                container.addView(btnLayout)
                container.addView(btnChat)
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = LinearLayout(parent.context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 8, 8, 8)
            }
            return ViewHolder(view)
        }

        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}