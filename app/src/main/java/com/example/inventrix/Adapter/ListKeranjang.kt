package com.example.inventrix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Model.ReqKeranjang
import com.example.inventrix.R

class ListKeranjang(
    private var listKeranjang: MutableList<ReqKeranjang>,
    private val onJumlahChange: (Int) -> Unit
) : RecyclerView.Adapter<ListKeranjang.KeranjangViewHolder>() {

    inner class KeranjangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMerek: TextView = itemView.findViewById(R.id.tvMerek)
        val tvKodeBarang: TextView = itemView.findViewById(R.id.tvKodeBarang)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlahKlik)
        val btnTambahAwal: ImageButton = itemView.findViewById(R.id.btnTambahAwal)
        val layoutCounter: View = itemView.findViewById(R.id.layoutCounter)
        val btnTambah: ImageButton = itemView.findViewById(R.id.btnTambah)
        val btnKurang: ImageButton = itemView.findViewById(R.id.btnKurang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        return KeranjangViewHolder(view)
    }

    override fun getItemCount(): Int = listKeranjang.size

    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        val item = listKeranjang[position]

        holder.tvName.text = item.nama
        holder.tvMerek.text = item.merek
        holder.tvKodeBarang.text = item.kodeBarang
        holder.tvHarga.text = "Rp${item.harga}"
        holder.tvJumlah.text = item.jumlah.toString()

        // 🔹 Atur visibilitas awal
        if (item.jumlah > 0) {
            holder.btnTambahAwal.visibility = View.GONE
            holder.layoutCounter.visibility = View.VISIBLE
        } else {
            holder.btnTambahAwal.visibility = View.VISIBLE
            holder.layoutCounter.visibility = View.GONE
        }

        // 🔹 Klik tombol + awal → muncul layout counter dgn efek zoom-in
        holder.btnTambahAwal.setOnClickListener {
            val zoomIn = ScaleAnimation(
                0f, 1f,  // From X: 0 → To X: full size
                0f, 1f,  // From Y: 0 → To Y: full size
                Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot X: center
                Animation.RELATIVE_TO_SELF, 0.5f   // Pivot Y: center
            ).apply {
                duration = 200
                fillAfter = true
            }

            holder.btnTambahAwal.visibility = View.GONE
            holder.layoutCounter.visibility = View.VISIBLE
            holder.layoutCounter.startAnimation(zoomIn)

            item.jumlah = 1
            holder.tvJumlah.text = "1"
            onJumlahChange.invoke(getTotalHarga())
        }

        // 🔹 Tombol tambah di counter
        holder.btnTambah.setOnClickListener {
            item.jumlah++
            holder.tvJumlah.text = item.jumlah.toString()

            // Efek zoom kecil di angka biar interaktif
            val pulse = ScaleAnimation(
                0.9f, 1f,
                0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 100
                fillAfter = true
            }
            holder.tvJumlah.startAnimation(pulse)

            onJumlahChange.invoke(getTotalHarga())
        }

        // 🔹 Tombol kurang di counter → kalau jumlah = 0, sembunyikan counter (zoom-out)
        holder.btnKurang.setOnClickListener {
            if (item.jumlah > 1) {
                item.jumlah--
                holder.tvJumlah.text = item.jumlah.toString()
                onJumlahChange.invoke(getTotalHarga())
            } else {
                val zoomOut = ScaleAnimation(
                    1f, 0f,
                    1f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 200
                    fillAfter = true
                }

                holder.layoutCounter.startAnimation(zoomOut)

                zoomOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        holder.layoutCounter.visibility = View.GONE
                        holder.btnTambahAwal.visibility = View.VISIBLE
                    }
                    override fun onAnimationRepeat(animation: Animation) {}
                })

                item.jumlah = 0
                holder.tvJumlah.text = "0"
                onJumlahChange.invoke(getTotalHarga())
            }
        }
    }

    fun updateData(newList: MutableList<ReqKeranjang>) {
        listKeranjang = newList
        notifyDataSetChanged()
    }

    fun getTotalHarga(): Int {
        return listKeranjang.sumOf { it.harga * it.jumlah }
    }
}
