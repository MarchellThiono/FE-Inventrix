package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ItemsItem
import com.example.inventrix.databinding.ItemStokAkhirBinding

class ListStokAkhir(
    private var list: List<ItemsItem?>
) : RecyclerView.Adapter<ListStokAkhir.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStokAkhirBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStokAkhirBinding.inflate(
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
            etstok.text = "${item.stokGudangAfter ?: 0} / ${item.stokTokoAfter ?: 0}"
        }
    }

    override fun getItemCount(): Int = list.size

    fun update(newList: List<ItemsItem?>) {
        list = newList
        notifyDataSetChanged()
    }
}
