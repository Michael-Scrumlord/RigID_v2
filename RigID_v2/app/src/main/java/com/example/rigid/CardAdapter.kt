package com.example.rigid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val context: Context, private val cardEntries: List<CardEntry>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val entry = cardEntries[position]
        //holder.bind(entry)
    }

    override fun getItemCount(): Int = cardEntries.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
/*
        private val image: ImageView = itemView.findViewById(R.id.card_image)
        private val title: TextView = itemView.findViewById(R.id.card_title)
        private val text: TextView = itemView.findViewById(R.id.card_text)

        fun bind(entry: CardEntry) {
            image.setImageResource(entry.imageResId)
            title.text = entry.title
            text.text = entry.text
        }

 */
    }
    fun addCardEntry(cardEntry: CardEntry) {
        cardEntries.plus(cardEntry)
        notifyItemInserted(cardEntries.size - 1)
    }

    data class CardEntry(val imageResId: Int, val title: String, val text: String)
}