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
import com.example.inventrix.Model.DataItem
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.ui.keranjang.KeranjangManager
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt

class ListBarang(
    private val onItemClick: (DataItem) -> Unit,
    private val onAddClick: (barangId: Int, jumlah: Int) -> Unit
) : RecyclerView.Adapter<ListBarang.ViewHolder>() {

    private var listBarang: List<DataItem> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvKode: TextView = itemView.findViewById(R.id.tvKodeBarang)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvStok: TextView = itemView.findViewById(R.id.tvStok)

        val btnTambahAwal: ImageButton = itemView.findViewById(R.id.btnTambahAwal)
        val layoutCounter: View = itemView.findViewById(R.id.layoutCounter)
        val btnTambah: ImageButton = itemView.findViewById(R.id.btnTambah)
        val btnKurang: ImageButton = itemView.findViewById(R.id.btnKurang)
        val tvJumlahKlik: TextView = itemView.findViewById(R.id.tvJumlahKlik)

        // btnEdit optional: jika id ada di layout maka akan di-bind, kalau tidak -> null
        val btnEdit: ImageButton? = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_admin, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = listBarang.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listBarang[position]

        // Harga
        val harga = toHargaInt(item.hargaJual)
        holder.tvHarga.text = formatRupiah(harga)

        // Stok
        val stokToko = item.stokToko ?: 0
        holder.tvStok.text = "Stok: $stokToko"

        // Nama dan kode
        holder.tvName.text = item.namaBarang ?: "-"
        holder.tvKode.text = item.kodeBarang ?: "-"

        // Gambar
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.ivLogo)

        // Jika layout punya btnEdit, sembunyikan (karena HomeFragment hanya kasir)
        holder.btnEdit?.visibility = View.GONE

        // Klik item buka detail barang
        holder.itemView.setOnClickListener { onItemClick(item) }

        // Ambil jumlah di keranjang
        val jumlahKeranjang = KeranjangManager.getJumlahForBarang(item.id ?: 0)

        // Setup UI counter kasir
        if (jumlahKeranjang > 0) {
            holder.layoutCounter.visibility = View.VISIBLE
            holder.btnTambahAwal.visibility = View.GONE
            holder.tvJumlahKlik.text = jumlahKeranjang.toString()
        } else {
            holder.layoutCounter.visibility = View.GONE
            holder.btnTambahAwal.visibility = View.VISIBLE
        }

        // =============================================================
        // 🔥 TOMBOL TAMBAH AWAL (+)
        // =============================================================
        holder.btnTambahAwal.setOnClickListener {
            if (stokToko <= 0) {
                Toast.makeText(
                    holder.itemView.context,
                    "Stok barang tidak cukup",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            onAddClick(item.id!!, 1)
            notifyItemChanged(position)
        }

        // =============================================================
        // 🔥 TOMBOL TAMBAH (+)
        // =============================================================
        holder.btnTambah.setOnClickListener {
            val now = KeranjangManager.getJumlahForBarang(item.id!!)
            val next = now + 1

            if (next > stokToko) {
                Toast.makeText(
                    holder.itemView.context,
                    "Stok tidak cukup",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            onAddClick(item.id!!, next)
            notifyItemChanged(position)
        }

        // =============================================================
        // 🔥 TOMBOL KURANG (-)
        // =============================================================
        holder.btnKurang.setOnClickListener {
            val now = KeranjangManager.getJumlahForBarang(item.id!!)

            if (now > 1) {
                onAddClick(item.id!!, now - 1)
            } else {
                onAddClick(item.id!!, 0)
            }

            notifyItemChanged(position)
        }
    }

    fun updateData(newList: List<DataItem>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}
