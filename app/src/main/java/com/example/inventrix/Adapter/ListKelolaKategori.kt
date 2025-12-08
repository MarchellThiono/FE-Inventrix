package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.KategoriData
import com.example.inventrix.R

class ListKelolaKategori(
    private val kategoriList: MutableList<KategoriData>,
    private val onEdit: (KategoriData) -> Unit,
    private val onDelete: (KategoriData) -> Unit
) : RecyclerView.Adapter<ListKelolaKategori.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKategori: TextView = itemView.findViewById(R.id.tvNamaKategori)
        val kodeAwal: TextView = itemView.findViewById(R.id.idKategori)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelola_kategori, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = kategoriList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = kategoriList[position]

        holder.namaKategori.text = kategori.nama
        holder.kodeAwal.text = kategori.kodeAwal

        holder.btnEdit.setOnClickListener { onEdit(kategori) }
        holder.btnDelete.setOnClickListener { onDelete(kategori) }
    }

    fun setData(newList: List<KategoriData>) {
        kategoriList.clear()
        kategoriList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(id: Int) {
        val index = kategoriList.indexOfFirst { it.id == id }
        if (index != -1) {
            kategoriList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
