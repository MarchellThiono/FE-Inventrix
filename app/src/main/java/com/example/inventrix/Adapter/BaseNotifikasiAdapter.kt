package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.R

abstract class BaseNotifikasiAdapter(
    private var dataList: MutableList<Notifikasi>,
    private val layoutId: Int,
    private val tvJudulId: Int,
    private val tvIsiId: Int,
    private val tvTanggalId: Int,
    private val onDelete: (Notifikasi) -> Unit
) : RecyclerView.Adapter<BaseNotifikasiAdapter.NotifikasiViewHolder>() {

    inner class NotifikasiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        val tvJudul: TextView = view.findViewById(tvJudulId)
        val tvIsi: TextView = view.findViewById(tvIsiId)
        val tvTanggal: TextView = view.findViewById(tvTanggalId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return NotifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val item = dataList[position]

        holder.tvJudul.text = item.judul
        holder.tvIsi.text = item.pesan
        holder.tvTanggal.text = item.tanggal

        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun removeItem(item: Notifikasi) {
        val index = dataList.indexOf(item)
        if (index != -1) {
            dataList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
