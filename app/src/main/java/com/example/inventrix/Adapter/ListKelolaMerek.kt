package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.MerekData
import com.example.inventrix.R

class ListKelolaMerek(
    private val merekList: MutableList<MerekData>,
    private val onEdit: (MerekData) -> Unit,
    private val onDelete: (MerekData) -> Unit
) : RecyclerView.Adapter<ListKelolaMerek.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMerek: TextView = itemView.findViewById(R.id.tvNamaMerek)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelola_merek, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val merek = merekList[position]

        holder.namaMerek.text = merek.namaMerek

        holder.btnEdit.setOnClickListener { onEdit(merek) }
        holder.btnDelete.setOnClickListener { onDelete(merek) }
    }

    override fun getItemCount(): Int = merekList.size

    /** ------------------------------------------------------------------
     *  TAMBAH MEREK (result dari API)
     * ------------------------------------------------------------------ */
    fun addMerek(data: MerekData) {
        merekList.add(data)
        notifyItemInserted(merekList.size - 1)
    }

    /** ------------------------------------------------------------------
     *  EDIT MEREK
     * ------------------------------------------------------------------ */
    fun updateMerek(updated: MerekData) {
        val index = merekList.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            merekList[index] = updated
            notifyItemChanged(index)
        }
    }

    /** ------------------------------------------------------------------
     *  DELETE MEREK
     * ------------------------------------------------------------------ */
    fun removeMerek(id: Int) {
        val index = merekList.indexOfFirst { it.id == id }
        if (index != -1) {
            merekList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
