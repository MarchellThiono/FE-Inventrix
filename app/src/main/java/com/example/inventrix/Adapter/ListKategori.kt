package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.R

class ListKategori(
    private var list: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ListKategori.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNama: TextView = v.findViewById(R.id.tvNamaKategori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kategori, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = list[position]
        holder.tvNama.text = kategori

        holder.itemView.setOnClickListener {
            onClick(kategori)
        }
    }

    override fun getItemCount(): Int = list.size
}
