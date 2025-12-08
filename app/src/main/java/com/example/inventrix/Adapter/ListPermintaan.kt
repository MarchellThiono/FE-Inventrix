package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ResListPermintaanItem
import com.example.inventrix.R

class ListPermintaanAdapter(
    private val onItemClick: (ResListPermintaanItem) -> Unit,
    private val onDeleteClick: (ResListPermintaanItem) -> Unit = {},
    var showDelete: Boolean = false
) : RecyclerView.Adapter<ListPermintaanAdapter.ViewHolder>() {

    private val items = mutableListOf<ResListPermintaanItem>()

    fun updateData(newList: List<ResListPermintaanItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvPermintaan: TextView = itemView.findViewById(R.id.tvPermintaan)
        private val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        fun bind(item: ResListPermintaanItem) {

            tvPermintaan.text = "Permintaan"

            // =============================
            // FORMAT TANGGAL + JAM
            // =============================
            val raw = item.tanggal ?: "-"
            var tanggalFinal = "-"

            if (raw.contains("T") && raw.length >= 16) {
                // Contoh raw: 2025-12-08T21:43:16
                val tgl = raw.substring(0, 10)
                val jam = raw.substring(11, 16) // AMBIL HH:mm

                val (yyyy, mm, dd) = tgl.split("-")

                tanggalFinal = "$dd-$mm-$yyyy $jam"
            }

            tvTanggal.text = "Tanggal : $tanggalFinal"

            // TOTAL ITEM
            tvTotal.text = "Total item : ${item.totalItem ?: 0}"

            // DELETE BUTTON
            btnDelete.visibility = if (showDelete) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onItemClick(item) }
            btnDelete.setOnClickListener { onDeleteClick(item) }
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
