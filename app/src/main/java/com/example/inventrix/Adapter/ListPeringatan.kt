package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.Peringatan
import com.example.inventrix.R

class ListPeringatan(
    private val dataList: List<Peringatan>
) : RecyclerView.Adapter<ListPeringatan.PeringatanViewHolder>() {

    inner class PeringatanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconPeringatan)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsiPeringatan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeringatanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_peringatan, parent, false)
        return PeringatanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeringatanViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvJudul.text = "Peringatan"
        holder.tvIsi.text = "Stok ${item.namaBarang} (${item.kodeBarang}) tinggal ${item.stokTersisa}"
        holder.tvTanggal.text = item.tanggal
        holder.tvWaktu.text = item.waktu
    }

    override fun getItemCount(): Int = dataList.size
}
