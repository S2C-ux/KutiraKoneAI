package com.example.kutirakonai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kutirakonai.R
import com.example.kutirakonai.models.ScrapItem

class ScrapAdapter(
    private var items: List<ScrapItem>,
    private val onSwapClick: (ScrapItem) -> Unit,
    private val onBuyClick: (ScrapItem) -> Unit,
    private val onAIClick: (ScrapItem) -> Unit
) : RecyclerView.Adapter<ScrapAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFabric: ImageView = view.findViewById(R.id.ivFabric)
        val tvMaterial: TextView = view.findViewById(R.id.tvMaterial)
        val tvColor: TextView = view.findViewById(R.id.tvColor)
        val tvSize: TextView = view.findViewById(R.id.tvSize)
        val btnSwap: Button = view.findViewById(R.id.btnSwap)
        val btnBuy: Button = view.findViewById(R.id.btnBuy)
        val btnViewAI: Button = view.findViewById(R.id.btnViewAI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scrap, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvMaterial.text = "🧵 ${item.material}"
        holder.tvColor.text = "Color: ${item.color}"
        holder.tvSize.text = "Size: ${item.sizeMeters}m"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivFabric)

        // SWAP button - Create swap request
        holder.btnSwap.setOnClickListener { onSwapClick(item) }

        // BUY button - Create buy request
        holder.btnBuy.setOnClickListener { onBuyClick(item) }

        // AI button - Show AI design ideas
        holder.btnViewAI.setOnClickListener { onAIClick(item) }
    }

    fun updateList(newItems: List<ScrapItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}