package com.example.inventrix.UI.Admin.ui.keranjang

import com.example.inventrix.Model.ReqKeranjang

object KeranjangManager {

    private val keranjangMap = mutableMapOf<Int, ReqKeranjang>()

    // tambahAtauUpdate: tambahkan atau update jumlah
    fun tambahAtauUpdate(barang: ReqKeranjang, qty: Int) {
        if (qty <= 0) {
            keranjangMap.remove(barang.barangId)
            return
        }

        val exist = keranjangMap[barang.barangId]

        if (exist != null) {
            // update jumlah (mutasi pada object yang tersimpan)
            exist.jumlah = qty
        } else {
            // copy untuk menghindari referensi luar bermasalah
            keranjangMap[barang.barangId] = barang.copy(jumlah = qty)
        }
    }

    fun hapus(barangId: Int) {
        keranjangMap.remove(barangId)
    }

    fun getKeranjang(): List<ReqKeranjang> {
        return keranjangMap.values.toList()
    }

    // Ambil jumlah (dipakai di adapter ListBarang agar counter sinkron)
    fun getJumlahForBarang(barangId: Int): Int {
        return keranjangMap[barangId]?.jumlah ?: 0
    }

    fun getTotalHarga(): Int {
        return keranjangMap.values.sumOf { it.harga * it.jumlah }
    }

    fun clear() {
        keranjangMap.clear()
    }
}
