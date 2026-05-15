package com.example.kutirakonai.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kutirakonai.databinding.ActivityUploadScrapBinding
import com.example.kutirakonai.models.ScrapItem
import com.example.kutirakonai.utils.HuggingFaceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class UploadScrapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadScrapBinding
    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Image picker
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.ivPreview.setImageURI(it)
            }
        }

    // Voice input result
    private val voiceInputLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val spokenText = data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrEmpty()) {
                binding.etClothType.setText(spokenText)
                Toast.makeText(this, "Heard: $spokenText", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadScrapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pick image
        binding.btnPickImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // 🎤 Voice input for cloth type
        binding.btnVoiceInput.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Say the cloth type, e.g. Cotton, Silk, Wool...")
            }
            try {
                voiceInputLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this,
                    "Voice input not available on this device", Toast.LENGTH_SHORT).show()
            }
        }

        // Quick-select chips fill the text field
        binding.chipSilk.setOnClickListener { binding.etClothType.setText("Silk") }
        binding.chipCotton.setOnClickListener { binding.etClothType.setText("Cotton") }
        binding.chipWool.setOnClickListener { binding.etClothType.setText("Wool") }
        binding.chipPolyester.setOnClickListener { binding.etClothType.setText("Polyester") }
        binding.chipJute.setOnClickListener { binding.etClothType.setText("Jute") }

        // Get AI suggestions button
        binding.btnGetAI.setOnClickListener {
            val clothType = binding.etClothType.text.toString().trim()
            val color = binding.etColor.text.toString().trim()
            val size = binding.etSize.text.toString().toDoubleOrNull()

            if (clothType.isEmpty()) {
                Toast.makeText(this, "Please enter or speak the cloth type first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading("Getting AI suggestions from Hugging Face...")
            lifecycleScope.launch {
                val suggestions = HuggingFaceHelper.getDesignSuggestions(
                    clothType = clothType,
                    color = color.ifEmpty { "unknown" },
                    sizeMeters = size ?: 0.5
                )
                hideLoading()
                binding.cardAISuggestions.visibility = View.VISIBLE
                binding.tvAISuggestions.text = suggestions
            }
        }

        // Upload
        binding.btnUpload.setOnClickListener { uploadScrap() }
    }

    private fun uploadScrap() {
        val clothType = binding.etClothType.text.toString().trim()
        val color = binding.etColor.text.toString().trim()
        val size = binding.etSize.text.toString().toDoubleOrNull()
        val desc = binding.etDescription.text.toString().trim()
        val aiSuggestions = binding.tvAISuggestions.text.toString()

        if (clothType.isEmpty()) {
            Toast.makeText(this, "Please enter the cloth type (type or speak it)", Toast.LENGTH_SHORT).show()
            return
        }
        if (color.isEmpty() || size == null) {
            Toast.makeText(this, "Please fill Color and Size", Toast.LENGTH_SHORT).show()
            return
        }
        val imgUri = selectedImageUri ?: run {
            Toast.makeText(this, "Please pick a fabric photo", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading("Uploading photo...")

        val storageRef = storage.reference.child("scraps/${UUID.randomUUID()}.jpg")
        storageRef.putFile(imgUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            val ownerName = userDoc.getString("name") ?: "Artisan"
                            val scrapId = UUID.randomUUID().toString()
                            val scrap = ScrapItem(
                                id = scrapId,
                                userId = uid,
                                ownerName = ownerName,
                                material = clothType,
                                color = color,
                                sizeMeters = size,
                                imageUrl = downloadUrl.toString(),
                                description = desc,
                                isAvailable = true,
                                timestamp = System.currentTimeMillis(),
                                aiSuggestions = aiSuggestions
                            )
                            db.collection("scraps").document(scrapId).set(scrap)
                                .addOnSuccessListener {
                                    hideLoading()
                                    Toast.makeText(this,
                                        "Scrap uploaded successfully! ✅",
                                        Toast.LENGTH_LONG).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    hideLoading()
                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(msg: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.visibility = View.VISIBLE
        binding.tvStatus.text = msg
        binding.btnUpload.isEnabled = false
        binding.btnGetAI.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
        binding.btnUpload.isEnabled = true
        binding.btnGetAI.isEnabled = true
    }
}