package com.example.inventrix.UI.Admin.ui.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ContentItem
import com.example.inventrix.databinding.ItemLaporanBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ==========================
// FUNGSI FORMAT TANGGAL
// ==========================
fun formatTanggal(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    return try {
        val date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    } catch (e: Exception) {
        raw
    }
}

class LaporanAdapter(
    private var list: ArrayList<ContentItem>,
    private val onItemClick: (ContentItem) -> Unit
) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLaporanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ContentItem) {
            binding.txtJenis.text = data.jenis
            binding.txtTanggal.text = formatTanggal(data.tanggal)
            binding.txtTotalItem.text = "${data.totalItem} item"

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLaporanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun update(newList: List<ContentItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
