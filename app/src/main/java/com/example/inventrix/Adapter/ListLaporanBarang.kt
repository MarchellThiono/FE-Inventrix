package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ReqPermintaanBarang
import com.example.inventrix.R

class ListLaporanBarang(
    private val listBarang: List<ReqPermintaanBarang>
) : RecyclerView.Adapter<ListLaporanBarang.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaBarang: TextView = view.findViewById(R.id.tvNamaBarang)
        val jumlahBarang: EditText = view.findViewById(R.id.etJumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val barang = listBarang[position]
        holder.namaBarang.text = "${barang.nama} (${barang.kodeBarang})"
    }

    override fun getItemCount(): Int = listBarang.size
}
