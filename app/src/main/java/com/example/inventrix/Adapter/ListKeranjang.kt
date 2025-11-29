package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.ReqKeranjang
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.ui.keranjang.KeranjangManager
import com.example.inventrix.formatRupiah

class ListKeranjang(
    private var listKeranjang: MutableList<ReqKeranjang>,
    private val onJumlahChange: (Int) -> Unit
) : RecyclerView.Adapter<ListKeranjang.KeranjangViewHolder>() {

    inner class KeranjangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMerek: TextView = itemView.findViewById(R.id.tvMerek)
        val tvKode: TextView = itemView.findViewById(R.id.tvKodeBarang)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlahKlik)

        val btnTambahAwal: ImageButton = itemView.findViewById(R.id.btnTambahAwal)
        val layoutCounter: View = itemView.findViewById(R.id.layoutCounter)
        val btnTambah: ImageButton = itemView.findViewById(R.id.btnTambah)
        val btnKurang: ImageButton = itemView.findViewById(R.id.btnKurang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        return KeranjangViewHolder(view)
    }

    override fun getItemCount(): Int = listKeranjang.size

    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        val item = listKeranjang[position]

        holder.tvName.text = item.namaBarang
        holder.tvMerek.text = item.merek ?: ""
        holder.tvKode.text = item.kodeBarang ?: ""

        val totalHargaItem = item.harga * item.jumlah
        holder.tvHarga.text = formatRupiah(totalHargaItem)

        holder.tvJumlah.text = item.jumlah.toString()

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.ivLogo)

        if (item.jumlah > 0) {
            holder.btnTambahAwal.visibility = View.GONE
            holder.layoutCounter.visibility = View.VISIBLE
        } else {
            holder.btnTambahAwal.visibility = View.VISIBLE
            holder.layoutCounter.visibility = View.GONE
        }

        val stokToko = item.stokToko ?: 0

        // ================================================
        // 🔥 TOMBOL TAMBAH AWAL — CEK STOK
        // ================================================
        holder.btnTambahAwal.setOnClickListener {
            if (stokToko <= 0) {
                Toast.makeText(holder.itemView.context, "Stok barang tidak cukup", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            KeranjangManager.tambahAtauUpdate(item, 1)
            refreshFromManager()
        }

        // ================================================
        // 🔥 TOMBOL TAMBAH (+) — CEK STOK
        // ================================================
        holder.btnTambah.setOnClickListener {
            val now = KeranjangManager.getJumlahForBarang(item.barangId)
            val next = now + 1

            if (next > stokToko) {
                Toast.makeText(holder.itemView.context, "Stok barang tidak cukup", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            KeranjangManager.tambahAtauUpdate(item, next)
            refreshFromManager()
        }

        // ================================================
        // TOMBOL KURANG
        // ================================================
        holder.btnKurang.setOnClickListener {
            val now = KeranjangManager.getJumlahForBarang(item.barangId)

            if (now > 1) {
                KeranjangManager.tambahAtauUpdate(item, now - 1)
            } else {
                KeranjangManager.hapus(item.barangId) // hapus jika sudah 0
            }
            refreshFromManager()
        }
    }

    private fun refreshFromManager() {
        val newList = KeranjangManager.getKeranjang().toMutableList()
        updateData(newList)
        onJumlahChange(KeranjangManager.getTotalHarga())
    }

    fun updateData(newList: MutableList<ReqKeranjang>) {
        listKeranjang = newList
        notifyDataSetChanged()
    }
}
