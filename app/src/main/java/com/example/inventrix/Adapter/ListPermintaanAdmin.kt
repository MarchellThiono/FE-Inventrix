package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.BarangDipilihAdmin
import com.example.inventrix.Model.BarangLaporanResult
import com.example.inventrix.R

class ListPermintaanAdmin(
    private val listBarang: List<BarangDipilihAdmin>,
    private val isReadOnly: Boolean = false,
    private val onJumlahEdit: (barangId: Int, jumlahBaru: Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<ListPermintaanAdmin.ViewHolder>() {

    private val jumlahMap = HashMap<Int, Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaBarang: TextView = view.findViewById(R.id.tvNamaBarang)
        val etJumlah: EditText = view.findViewById(R.id.etJumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_perimintaan_barang_admin, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val barang = listBarang[pos]
        val id = barang.barangId

        val jumlah = jumlahMap[id] ?: barang.stokToko

        holder.namaBarang.text = "${barang.nama} (${barang.kodeBarang})"
        holder.etJumlah.setText(jumlah.toString())

        if (isReadOnly) {
            holder.etJumlah.isEnabled = false
            holder.etJumlah.isFocusable = false
            holder.etJumlah.isClickable = false
            return
        }

        holder.etJumlah.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = holder.etJumlah.text.toString().toIntOrNull() ?: 0
                jumlahMap[id] = input
                onJumlahEdit(id, input)
            }
        }
    }

    override fun getItemCount(): Int = listBarang.size

    // ===========================================
    //  TAMBAHKAN FUNGSI INI (WAJIB)
    // ===========================================
    fun getListJumlah(): List<BarangLaporanResult> {
        return listBarang.map { barang ->
            val jumlahAkhir = jumlahMap[barang.barangId] ?: barang.stokToko
            BarangLaporanResult(barang.barangId, jumlahAkhir)
        }
    }
}
