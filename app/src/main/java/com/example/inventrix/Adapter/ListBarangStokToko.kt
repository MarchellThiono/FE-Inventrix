package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Model.BarangDipilihAdmin
import com.example.inventrix.Model.DataItem
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.ui.home.PermintaanManager
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt

class ListBarangStokToko(
    private var listBarang: List<DataItem>,
    private val onRequestCountChanged: (Int) -> Unit,
    private val onDetailClick: (DataItem) -> Unit
) : RecyclerView.Adapter<ListBarangStokToko.ViewHolder>() {

    var modeDipilih: String? = null
    private val jumlahMap = mutableMapOf<Int, Int>()

    // ✅ ✅ ✅ LISTENER GLOBAL – TIDAK BOLEH DITIMPA SEMBARANGAN
    init {
        PermintaanManager.onChange = { barangId, jumlah ->

            // ✅ RESET TOTAL SAAT CLEAR
            if (barangId == -1) {
                jumlahMap.clear()
                notifyDataSetChanged()
                onRequestCountChanged(0)
            } else {

                if (jumlah <= 0) {
                    jumlahMap.remove(barangId)
                } else {
                    jumlahMap[barangId] = jumlah
                }

                notifyDataSetChanged()
                onRequestCountChanged(jumlahMap.values.sum())
            }
        }
    }



    // ✅ LABEL LAMBDA RESMI (AGAR return TIDAK ERROR)
    private val onChangeSafe: (Int, Int) -> Unit = { _, _ -> }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivLogo: ImageView = v.findViewById(R.id.ivLogo)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvKode: TextView = v.findViewById(R.id.tvKodeBarang)
        val tvHarga: TextView = v.findViewById(R.id.tvHarga)
        val tvStok: TextView = v.findViewById(R.id.tvStok)

        val btnEdit: ImageButton = v.findViewById(R.id.btnEdit)
        val btnTambahAwal: ImageButton = v.findViewById(R.id.btnTambahAwal)
        val layoutCounter: View = v.findViewById(R.id.layoutCounter)
        val tvJumlahKlik: TextView = v.findViewById(R.id.tvJumlahKlik)
        val btnTambah: ImageButton = v.findViewById(R.id.btnTambah)
        val btnKurang: ImageButton = v.findViewById(R.id.btnKurang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_admin, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listBarang.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = listBarang[pos]
        val id = item.id ?: 0
        val stokGudang = item.stokGudang ?: Int.MAX_VALUE
        val jumlah = jumlahMap[id] ?: 0

        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.ivLogo)

        holder.tvName.text = item.namaBarang.orEmpty()
        holder.tvKode.text = item.kodeBarang.orEmpty()
        holder.tvHarga.text = formatRupiah(toHargaInt(item.hargaJual))
        holder.tvStok.text = "Stok toko: ${item.stokToko ?: 0}"

        holder.btnEdit.visibility = View.GONE

        holder.layoutCounter.visibility = if (jumlah > 0) View.VISIBLE else View.GONE
        holder.btnTambahAwal.visibility = if (jumlah == 0 && modeDipilih != null) View.VISIBLE else View.GONE
        holder.tvJumlahKlik.text = jumlah.toString()

        holder.itemView.setOnClickListener {
            onDetailClick(item)
        }


        if (modeDipilih == null) {
            holder.layoutCounter.visibility = View.GONE
            holder.btnTambahAwal.visibility = View.GONE
            return
        }

        holder.btnTambahAwal.setOnClickListener {
            val target = if (modeDipilih == "Permintaan Stok") minOf(1, stokGudang) else 1
            if (target <= 0) return@setOnClickListener
            jumlahMap[id] = target

            PermintaanManager.tambahBarang(
                BarangDipilihAdmin(
                    barangId = id,
                    nama = item.namaBarang.orEmpty(),
                    merek = item.merek.orEmpty(),
                    kodeBarang = item.kodeBarang.orEmpty(),
                    stokToko = target
                )
            )

            notifyItemChanged(pos)
            onRequestCountChanged(jumlahMap.values.sum())
        }

        holder.btnTambah.setOnClickListener {
            val current = jumlahMap[id] ?: 0
            val newValue = if (modeDipilih == "Permintaan Stok") {
                if (current >= stokGudang) current else current + 1
            } else current + 1

            jumlahMap[id] = newValue
            holder.tvJumlahKlik.text = newValue.toString()
            PermintaanManager.updateJumlah(id, newValue)
            onRequestCountChanged(jumlahMap.values.sum())
        }

        holder.btnKurang.setOnClickListener {
            val current = jumlahMap[id] ?: 0

            if (current > 1) {
                val newValue = current - 1
                jumlahMap[id] = newValue
                holder.tvJumlahKlik.text = newValue.toString()
                PermintaanManager.updateJumlah(id, newValue)
            } else {
                jumlahMap.remove(id)
                PermintaanManager.updateJumlah(id, 0)

                holder.layoutCounter.visibility = View.GONE
                holder.btnTambahAwal.visibility = View.VISIBLE
            }

            onRequestCountChanged(jumlahMap.values.sum())
        }
    }

    fun updateData(newList: List<DataItem>) {
        listBarang = newList
        notifyDataSetChanged()
    }

    fun resetAllCounters() {
        jumlahMap.clear()
        notifyDataSetChanged()
        onRequestCountChanged(0)
    }

    fun syncFromManager(list: List<BarangDipilihAdmin>) {
        jumlahMap.clear()
        list.forEach { jumlahMap[it.barangId] = it.stokToko }
        notifyDataSetChanged()
    }
}
