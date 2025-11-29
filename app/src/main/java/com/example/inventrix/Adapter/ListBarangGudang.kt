package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.DataItem
import com.example.inventrix.R

class ListBarangGudang(
    private val onItemClick: (DataItem) -> Unit,
    private val onJumlahChange: (barangId: Int, jumlah: Int) -> Unit
) : RecyclerView.Adapter<ListBarangGudang.ViewHolder>() {

    private var listBarang: List<DataItem> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvMerek: TextView = view.findViewById(R.id.tvMerek)
        val tvKode: TextView = view.findViewById(R.id.tvKodeBarang)
        val tvStok: TextView = view.findViewById(R.id.tvStok)

        val btnTambahAwal: ImageButton = view.findViewById(R.id.btnTambahAwal)
        val layoutCounter: View = view.findViewById(R.id.layoutCounter)
        val btnTambah: ImageButton = view.findViewById(R.id.btnTambah)
        val btnKurang: ImageButton = view.findViewById(R.id.btnKurang)
        val tvJumlahKlik: TextView = view.findViewById(R.id.tvJumlahKlik)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_gudang, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listBarang.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val barang = listBarang[position]

        holder.tvName.text = barang.namaBarang ?: "-"
        holder.tvMerek.text = barang.merek ?: "-"
        holder.tvKode.text = barang.kodeBarang ?: "-"
        holder.tvStok.text = "Stok: ${barang.stokGudang ?: 0}"

        Glide.with(holder.itemView.context)
            .load(barang.imageUrl)
            .into(holder.ivLogo)

        // klik item → detail
        holder.itemView.setOnClickListener {
            onItemClick(barang)
        }

        // default
        holder.btnTambahAwal.visibility = View.VISIBLE
        holder.layoutCounter.visibility = View.GONE
        holder.tvJumlahKlik.text = "0"

        // tombol tambah awal
        holder.btnTambahAwal.setOnClickListener {
            holder.btnTambahAwal.visibility = View.GONE
            holder.layoutCounter.visibility = View.VISIBLE
            holder.tvJumlahKlik.text = "1"

            onJumlahChange(barang.id ?: 0, 1)
        }

        // tombol +
        holder.btnTambah.setOnClickListener {
            val now = holder.tvJumlahKlik.text.toString().toInt()
            val next = now + 1
            holder.tvJumlahKlik.text = next.toString()

            onJumlahChange(barang.id ?: 0, next)
        }

        // tombol -
        holder.btnKurang.setOnClickListener {
            val now = holder.tvJumlahKlik.text.toString().toInt()

            if (now > 1) {
                val next = now - 1
                holder.tvJumlahKlik.text = next.toString()
                onJumlahChange(barang.id ?: 0, next)

            } else {
                // kembali ke tombol awal
                holder.layoutCounter.visibility = View.GONE
                holder.btnTambahAwal.visibility = View.VISIBLE
                holder.tvJumlahKlik.text = "0"
                onJumlahChange(barang.id ?: 0, 0)
            }
        }
    }

    fun updateData(newList: List<DataItem>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}
