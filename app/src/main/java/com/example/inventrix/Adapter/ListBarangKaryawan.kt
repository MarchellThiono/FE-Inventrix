package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.DataItem
import com.example.inventrix.R
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt

class ListBarangKaryawan(
    private val onItemClick: (DataItem) -> Unit
) : RecyclerView.Adapter<ListBarangKaryawan.ViewHolder>() {

    private var listBarang: List<DataItem> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvStok: TextView = itemView.findViewById(R.id.tvStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_karyawan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listBarang.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listBarang[position]

        val hargaInt = toHargaInt(item.hargaJual)

        holder.tvName.text = item.namaBarang ?: "-"
        holder.tvHarga.text = formatRupiah(hargaInt)
        holder.tvStok.text = "Stok: ${item.stokToko ?: 0}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.ivLogo)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }


    fun updateData(newList: List<DataItem>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}
