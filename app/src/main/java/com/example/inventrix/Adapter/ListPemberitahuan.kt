package com.example.inventrix.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.Model.ReqPemberitahuan
import com.example.inventrix.R

class ListPemberitahuan(
    private val dataList: List<Notifikasi>,
    private val onDelete: (Notifikasi) -> Unit
) : RecyclerView.Adapter<ListPemberitahuan.PemberitahuanViewHolder>() {

    inner class PemberitahuanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudulPemberitahuan)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsiPemberitahuan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggalPemberitahuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PemberitahuanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pemberitahuan, parent, false)
        return PemberitahuanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PemberitahuanViewHolder, position: Int) {
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

