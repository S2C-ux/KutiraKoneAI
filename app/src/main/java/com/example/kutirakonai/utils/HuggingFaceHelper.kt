package com.example.kutirakonai.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object HuggingFaceHelper {

    // Paste your free Hugging Face token here
    private const val HF_TOKEN = "YOUR_HUGGING_FACE_TOKEN_HERE"

    // Free model: Mistral 7B Instruct (best free text model on HF)
    private const val MODEL_URL =
        "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Main function: get AI design suggestions for a fabric scrap
     * Takes the cloth type (typed or spoken by user) + color + size
     * Returns 3 practical DIY project ideas
     */
    suspend fun getDesignSuggestions(
        clothType: String,
        color: String,
        sizeMeters: Double
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """<s>[INST]
You are a helpful assistant for Indian village artisans.
A tailor has a leftover fabric scrap with these details:
- Cloth type: $clothType
- Color: $color
- Size: $sizeMeters meters

Suggest exactly 3 simple DIY project ideas that an Indian village artisan can make with this scrap.
Each idea should be practical, low-cost, and suitable for village crafts.
Format your response as:
1. [Project Name]: [2 simple steps to make it]
2. [Project Name]: [2 simple steps to make it]
3. [Project Name]: [2 simple steps to make it]
Keep each point under 30 words. Reply only with the 3 ideas.[/INST]</s>"""

            val json = JSONObject().apply {
                put("inputs", prompt)
                put("parameters", JSONObject().apply {
                    put("max_new_tokens", 250)
                    put("temperature", 0.7)
                    put("return_full_text", false)
                })
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(MODEL_URL)
                .addHeader("Authorization", "Bearer $HF_TOKEN")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext fallbackSuggestions(clothType)

            // Parse the response
            val jsonArray = JSONArray(responseBody)
            val generatedText = jsonArray.getJSONObject(0).getString("generated_text").trim()

            if (generatedText.isNotEmpty()) generatedText
            else fallbackSuggestions(clothType)

        } catch (e: Exception) {
            // If model is loading (cold start), return helpful fallback
            fallbackSuggestions(clothType)
        }
    }

    // Fallback suggestions if AI is loading (HF free tier has cold start ~20 sec)
    private fun fallbackSuggestions(clothType: String): String {
        return when (clothType.lowercase()) {
            "silk" -> "1. Hair Scrunchie: Cut strip, fold and stitch ends together, insert elastic.\n" +
                    "2. Decorative Pouch: Fold in half, stitch 2 sides, add ribbon drawstring.\n" +
                    "3. Saree Border Trim: Iron flat, sew along edges of another garment."
            "cotton" -> "1. Face Mask: Cut rectangle, fold twice, add ear loops.\n" +
                    "2. Reusable Bag: Fold double, stitch sides, fold top for handle.\n" +
                    "3. Patchwork Cushion Cover: Stitch multiple pieces together, stuff with cotton."
            "wool" -> "1. Finger Puppet: Wrap around finger, stitch base, add eyes.\n" +
                    "2. Coaster Set: Cut circles, blanket-stitch the edges.\n" +
                    "3. Bottle Cover: Wrap around bottle, stitch side seam, add tie."
            else -> "1. Fabric Bookmark: Cut strip, fold edges in, press flat.\n" +
                    "2. Key Holder Pouch: Fold small square, stitch 3 sides, add snap button.\n" +
                    "3. Gift Wrapping: Use as eco-friendly cloth gift wrap with ribbon."
        }
    }
}