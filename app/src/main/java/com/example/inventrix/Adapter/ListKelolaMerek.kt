package com.example.inventrix.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.R

class ListKelolaMerek(
    private val merekList: MutableList<String>,
    private val onEdit: (String) -> Unit,
    private val onDelete: (String) -> Unit
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
        holder.namaMerek.text = merek

        holder.btnEdit.setOnClickListener { onEdit(merek) }
        holder.btnDelete.setOnClickListener { onDelete(merek) }
    }

    override fun getItemCount(): Int = merekList.size

    fun addMerek(nama: String) {
        merekList.add(nama)
        notifyItemInserted(merekList.size - 1)
    }

    fun updateMerek(oldName: String, newName: String) {
        val index = merekList.indexOf(oldName)
        if (index != -1) {
            merekList[index] = newName
            notifyItemChanged(index)
        }
    }

    fun removeMerek(nama: String) {
        val index = merekList.indexOf(nama)
        if (index != -1) {
            merekList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
