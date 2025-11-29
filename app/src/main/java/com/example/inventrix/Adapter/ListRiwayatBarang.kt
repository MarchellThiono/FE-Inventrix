package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ItemsItem
import com.example.inventrix.databinding.ItemRiwayatBarangBinding

class ListRiwayatBarang(
    private var list: List<ItemsItem?>
) : RecyclerView.Adapter<ListRiwayatBarang.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRiwayatBarangBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBarangBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position] ?: return

        with(holder.binding) {
            tvNamaBarang.text = item.namaBarang ?: "-"
            tvjumlah.text = "x${item.qty ?: 0}"
            etHarga.text = item.subtotalFormatted ?: "0"
        }
    }

    override fun getItemCount(): Int = list.size

    fun update(newList: List<ItemsItem?>) {
        list = newList
        notifyDataSetChanged()
    }
}
