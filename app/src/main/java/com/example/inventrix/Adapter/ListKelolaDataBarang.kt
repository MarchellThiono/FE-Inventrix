package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.DataItem
import com.example.inventrix.R
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt

class ListKelolaDataBarang(
    private var data: List<DataItem>,
    private val listenerEdit: (DataItem) -> Unit,
    private val listenerDetail: (DataItem) -> Unit    // ⬅️ Listener Detail
) : RecyclerView.Adapter<ListKelolaDataBarang.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivLogo: ImageView = v.findViewById(R.id.ivLogo)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvKodeBarang: TextView = v.findViewById(R.id.tvKodeBarang)
        val tvHarga: TextView = v.findViewById(R.id.tvHarga)

        val tvStok: TextView = v.findViewById(R.id.tvStok)
        val btnTambahAwal: ImageButton = v.findViewById(R.id.btnTambahAwal)
        val layoutCounter: LinearLayout = v.findViewById(R.id.layoutCounter)

        val btnEdit: ImageButton = v.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_admin, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(holder.ivLogo)

        holder.tvName.text = item.namaBarang
        holder.tvKodeBarang.text = item.kodeBarang
        holder.tvHarga.text = formatRupiah(toHargaInt(item.hargaJual))

        // Admin tidak pakai stok & counter
        holder.tvStok.visibility = View.GONE
        holder.btnTambahAwal.visibility = View.GONE
        holder.layoutCounter.visibility = View.GONE

        // === Klik tombol edit ===
        holder.btnEdit.setOnClickListener {
            listenerEdit(item)
        }

        // === Klik card untuk detail ===
        holder.itemView.setOnClickListener {
            listenerDetail(item)
        }
    }

    fun updateData(newList: List<DataItem>) {
        data = newList
        notifyDataSetChanged()
    }
}
