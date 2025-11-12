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

class ListBarang(
    private val role: String,
    private val onItemClick: (DataItem) -> Unit
) : RecyclerView.Adapter<ListBarang.ListViewHolder>() {

    private val items = mutableListOf<DataItem>()
    private val jumlahKlik = mutableListOf<Int>()

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvStok: TextView? = itemView.findViewById(R.id.tvStok)
        private val tvKodeBarang: TextView? = itemView.findViewById(R.id.tvKodeBarang)
        private val tvHarga: TextView? = itemView.findViewById(R.id.tvHarga)
        private val tvJumlahKlik: TextView? = itemView.findViewById(R.id.tvJumlahKlik)
        private val btnTambahAwal: ImageButton? = itemView.findViewById(R.id.btnTambahAwal)
        private val btnTambah: ImageButton? = itemView.findViewById(R.id.btnTambah)
        private val btnKurang: ImageButton? = itemView.findViewById(R.id.btnKurang)
        private val layoutCounter: View? = itemView.findViewById(R.id.layoutCounter)

        fun bind(item: DataItem, position: Int) {
            // Nama, kode, harga
            tvName.text = item.namaBarang ?: "-"
            tvKodeBarang?.text = "${item.kodeBarang ?: "-"}"
            tvHarga?.text = "Rp${item.hargaJual ?: "-"}"

            // Stok (pilih stok toko atau gudang)
            val stok = item.stokToko ?: item.stokGudang ?: 0
            tvStok?.text = "Stok: $stok"

            // Jumlah klik counter (jika ada)
            if (position < jumlahKlik.size) {
                tvJumlahKlik?.text = jumlahKlik[position].toString()
            }

            // ✅ Pastikan URL valid
            val imageUrl = item.imageUrl?.trim()

            Glide.with(itemView.context)
                .load(imageUrl)
                .into(ivLogo)

            // Tombol "+" pertama kali ditekan
            btnTambahAwal?.setOnClickListener {
                btnTambahAwal.visibility = View.GONE
                layoutCounter?.apply {
                    visibility = View.VISIBLE
                    alpha = 0f
                    animate().alpha(1f).setDuration(200).start()
                }
                jumlahKlik[position] = 1
                tvJumlahKlik?.text = "1"
            }

            // Tombol tambah di counter
            btnTambah?.setOnClickListener {
                jumlahKlik[position]++
                tvJumlahKlik?.text = jumlahKlik[position].toString()
            }

            // Tombol kurang di counter
            btnKurang?.setOnClickListener {
                if (jumlahKlik[position] > 1) {
                    jumlahKlik[position]--
                    tvJumlahKlik?.text = jumlahKlik[position].toString()
                } else {
                    layoutCounter?.animate()?.alpha(0f)?.setDuration(200)
                        ?.withEndAction {
                            layoutCounter.visibility = View.GONE
                            btnTambahAwal?.visibility = View.VISIBLE
                        }?.start()
                    jumlahKlik[position] = 0
                    tvJumlahKlik?.text = "0"
                }
            }

            // Klik seluruh item → kirim ke Fragment
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (role.lowercase()) {
            "owner", "admin" -> 2 // layout item_barang_admin.xml
            "gudang" -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layout = when (viewType) {
            2 -> R.layout.item_barang_admin
            1 -> R.layout.item_barang_gudang
            else -> R.layout.item_barang_karyawan
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DataItem>) {
        items.clear()
        items.addAll(newItems)
        jumlahKlik.clear()
        jumlahKlik.addAll(List(newItems.size) { 0 })
        notifyDataSetChanged()
    }
}
