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
    private val onEditClick: (DataItem) -> Unit,
    // fragment akan melakukan update ke KeranjangManager melalui callback ini
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

        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_barang_admin, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listBarang.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listBarang[position]

        val hargaInt = toHargaInt(item.hargaJual)
        holder.tvHarga.text = formatRupiah(hargaInt)

        val stokToko = item.stokToko ?: 0

        // SET DATA SESUAI XML
        holder.tvName.text = item.namaBarang ?: "-"
        holder.tvKode.text = item.kodeBarang ?: "-"
        holder.tvStok.text = "Stok: $stokToko"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.ivLogo)

        // Klik item → detail barang
        holder.itemView.setOnClickListener { onItemClick(item) }

        // Klik edit → pindah halaman edit
        holder.btnEdit.setOnClickListener { onEditClick(item) }

        // ambil jumlah dalam keranjang (selalu baca dari KeranjangManager)
        val jumlahKeranjang = KeranjangManager.getJumlahForBarang(item.id ?: 0)

        if (jumlahKeranjang > 0) {
            holder.btnTambahAwal.visibility = View.GONE
            holder.layoutCounter.visibility = View.VISIBLE
            holder.tvJumlahKlik.text = jumlahKeranjang.toString()
        } else {
            holder.btnTambahAwal.visibility = View.VISIBLE
            holder.layoutCounter.visibility = View.GONE
            holder.tvJumlahKlik.text = "0"
        }

        // --- ACTIONS ---
        // tambah pertama
        holder.btnTambahAwal.setOnClickListener {
            if (stokToko <= 0) {
                Toast.makeText(holder.itemView.context, "Stok barang tidak cukup", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // posisi aman
            val pos = try { holder.adapterPosition } catch (e: Throwable) { holder.adapterPosition }
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            onAddClick(item.id!!, 1)
            // UI di adapter akan di-refresh dari fragment (fragment memanggil notifyItemChanged atau notifyDataSetChanged)
            // tapi untuk safety kita panggil notifyItemChanged juga di sini
            notifyItemChanged(pos)
        }

        // tombol +
        holder.btnTambah.setOnClickListener {
            val pos = try { holder.adapterPosition } catch (e: Throwable) { holder.adapterPosition }
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val now = KeranjangManager.getJumlahForBarang(item.id ?: 0)
            val next = now + 1

            if (next > stokToko) {
                Toast.makeText(holder.itemView.context, "Stok barang tidak cukup", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onAddClick(item.id!!, next)
            notifyItemChanged(pos)
        }

        // tombol -
        holder.btnKurang.setOnClickListener {
            val pos = try { holder.adapterPosition } catch (e: Throwable) { holder.adapterPosition }
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val now = KeranjangManager.getJumlahForBarang(item.id ?: 0)

            if (now > 1) {
                val prev = now - 1
                onAddClick(item.id!!, prev)
            } else {
                // jika sekarang 1 -> setelah dikurang akan jadi 0 => hapus dari keranjang
                onAddClick(item.id!!, 0)
            }
            notifyItemChanged(pos)
        }
    }

    fun updateData(newList: List<DataItem>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}
