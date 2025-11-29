package com.example.inventrix.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.Model.Peringatan
import com.example.inventrix.R

class ListPeringatan(
    private val dataList: List<Notifikasi>,
    private val onDelete: (Notifikasi) -> Unit
) : RecyclerView.Adapter<ListPeringatan.PeringatanViewHolder>() {

    inner class PeringatanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsiPeringatan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeringatanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_peringatan, parent, false)
        return PeringatanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeringatanViewHolder, position: Int) {
        val item = dataList[position]

        holder.tvJudul.text = item.judul
        holder.tvIsi.text = item.pesan
        holder.tvTanggal.text = item.tanggal

        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount() = dataList.size
}
