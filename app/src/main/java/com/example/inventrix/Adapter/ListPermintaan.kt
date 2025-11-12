package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.ReqPermintaanBarang
import com.example.inventrix.R

class ListPermintaan(
    private val onItemClick: (ReqPermintaanBarang) -> Unit
) : RecyclerView.Adapter<ListPermintaan.ViewHolder>() {

    private val items = mutableListOf<ReqPermintaanBarang>()

    fun updateData(newList: List<ReqPermintaanBarang>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvKodeBarang: TextView = itemView.findViewById(R.id.tvKodeBarang)
        private val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        private val tvStok: TextView = itemView.findViewById(R.id.tvStok)

        fun bind(item: ReqPermintaanBarang) {
            tvName.text = item.nama
            tvKodeBarang.text = "${item.kodeBarang}"
            tvHarga.text = "${item.merek}"
            tvStok.text = "Jumlah Permintaan: ${item.Stok}"

            Glide.with(itemView.context)
                .load(item.imageUrl ?: R.drawable.ic_launcher_background)
                .into(ivLogo)

            // panggil callback ke fragment
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_permintaan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
