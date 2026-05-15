package com.example.kutirakonai.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kutirakonai.R
import com.example.kutirakonai.adapters.ScrapAdapter
import com.example.kutirakonai.databinding.ActivityMainBinding
import com.example.kutirakonai.models.ScrapItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScrapAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var allItems = listOf<ScrapItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "KutiraKoneAI 🧵"
        showUserProfile()

        adapter = ScrapAdapter(
            emptyList(),
            onSwapClick = { item ->
                val intent = Intent(this, SwapRequestActivity::class.java)
                intent.putExtra("scrapId", item.id)
                intent.putExtra("ownerName", item.ownerName)
                intent.putExtra("ownerUid", item.userId)
                intent.putExtra("scrapMaterial", item.material)
                intent.putExtra("scrapColor", item.color)
                intent.putExtra("scrapSize", item.sizeMeters)
                intent.putExtra("requestType", "SWAP")
                startActivity(intent)
            },
            onBuyClick = { item ->
                val intent = Intent(this, SwapRequestActivity::class.java)
                intent.putExtra("scrapId", item.id)
                intent.putExtra("ownerName", item.ownerName)
                intent.putExtra("ownerUid", item.userId)
                intent.putExtra("scrapMaterial", item.material)
                intent.putExtra("scrapColor", item.color)
                intent.putExtra("scrapSize", item.sizeMeters)
                intent.putExtra("requestType", "BUY")
                startActivity(intent)
            },
            onAIClick = { item ->
                val message = if (item.aiSuggestions.isNotEmpty())
                    item.aiSuggestions
                else
                    "No AI suggestions yet"
                MaterialAlertDialogBuilder(this)
                    .setTitle("✨ AI Design Ideas")
                    .setMessage(message)
                    .setPositiveButton("Got it!") { d, _ -> d.dismiss() }
                    .show()
            }
        )

        binding.rvScraps.layoutManager = GridLayoutManager(this, 2)
        binding.rvScraps.adapter = adapter

        binding.chipAll.setOnClickListener { adapter.updateList(allItems) }
        binding.chipSilk.setOnClickListener {
            adapter.updateList(allItems.filter { it.material.contains("Silk", ignoreCase = true) })
        }
        binding.chipCotton.setOnClickListener {
            adapter.updateList(allItems.filter { it.material.contains("Cotton", ignoreCase = true) })
        }
        binding.chipWool.setOnClickListener {
            adapter.updateList(allItems.filter { it.material.contains("Wool", ignoreCase = true) })
        }

        // GREEN FAB CLICK - UPLOAD SCRAP
        binding.fabUpload.setOnClickListener {
            Toast.makeText(this, "Opening Upload Screen", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UploadScrapActivity::class.java))
        }

        // ORANGE FAB CLICK - MY REQUESTS
        binding.fabRequests.setOnClickListener {
            Toast.makeText(this, "Opening My Requests", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MyRequestsActivity::class.java))
        }

        loadScraps()
    }

    private fun showUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "User"
                binding.toolbar.subtitle = "Welcome, $name!"
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadScraps() {
        db.collection("scraps")
            .whereEqualTo("available", true)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading scraps", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                snap?.let {
                    allItems = it.documents.mapNotNull { doc ->
                        doc.toObject(ScrapItem::class.java)
                    }
                    adapter.updateList(allItems)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        loadScraps()
        showUserProfile()
    }
}