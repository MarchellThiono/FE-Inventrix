package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.R

class ListMerek(
    private var merekList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ListMerek.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaMerek: TextView = itemView.findViewById(R.id.tvNamaMerek)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merek, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nama = merekList[position]
        holder.tvNamaMerek.text = nama
        holder.itemView.setOnClickListener {
            onItemClick(nama)
        }
    }

    override fun getItemCount(): Int = merekList.size

    fun updateData(newList: List<String>) {
        merekList = newList
        notifyDataSetChanged()
    }
}
