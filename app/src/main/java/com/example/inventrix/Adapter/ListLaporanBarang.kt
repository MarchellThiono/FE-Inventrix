package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.BarangDipilihGudang
import com.example.inventrix.Model.BarangLaporanResult
import com.example.inventrix.R

class ListLaporanBarang(
    private val listBarang: List<BarangDipilihGudang>,
    private val onJumlahEdit: (barangId: Int, jumlahBaru: Int) -> Unit
) : RecyclerView.Adapter<ListLaporanBarang.ViewHolder>() {

    private val jumlahMap = HashMap<Int, Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaBarang: TextView = view.findViewById(R.id.tvNamaBarang)
        val etJumlah: EditText = view.findViewById(R.id.etJumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_laporan, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val barang = listBarang[pos]

        holder.namaBarang.text = "${barang.nama} (${barang.kodeBarang})"

        // default jumlah
        holder.etJumlah.setText(barang.stokGudang.toString())
        jumlahMap[barang.barangId] = barang.stokGudang

        holder.etJumlah.setOnKeyListener { _, _, _ ->
            val jumlahInput = holder.etJumlah.text.toString().toIntOrNull() ?: 0

            jumlahMap[barang.barangId] = jumlahInput

            // UPDATE HOME VIEWMODEL
            onJumlahEdit(barang.barangId, jumlahInput)

            false
        }
    }

    override fun getItemCount(): Int = listBarang.size

    fun getListJumlah(): List<BarangLaporanResult> {
        return jumlahMap.map { (id, jml) ->
            BarangLaporanResult(id, jml)
        }
    }
}
