package com.example.inventrix.UI.Admin.ui.keranjang

import com.example.inventrix.Model.ReqKeranjang

object KeranjangManager {

    private val keranjangList = mutableListOf<ReqKeranjang>()

    fun tambahBarang(barang: ReqKeranjang) {
        val existing = keranjangList.find { it.kodeBarang == barang.kodeBarang }
        if (existing != null) {
            existing.jumlah++
        } else {
            keranjangList.add(barang)
        }
    }

    fun hapusBarang(barang: ReqKeranjang) {
        keranjangList.remove(barang)
    }

    fun getKeranjang(): MutableList<ReqKeranjang> = keranjangList

    fun getTotalHarga(): Int = keranjangList.sumOf { it.harga * it.jumlah }

    fun clear() {
        keranjangList.clear()
    }
}
