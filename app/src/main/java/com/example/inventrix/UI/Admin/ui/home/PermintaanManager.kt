package com.example.inventrix.UI.Admin.ui.home

import com.example.inventrix.Model.BarangDipilihAdmin

object PermintaanManager {

    private val listPermintaan = mutableListOf<BarangDipilihAdmin>()

    // ✅ Mode global agar Home tahu apakah harus tampil mode
    var modeAktif: String? = null

    // ✅ Listener sinkron realtime ke Home
    var onChange: ((Int, Int) -> Unit)? = null

    fun setMode(mode: String?) {
        modeAktif = mode
    }

    fun tambahBarang(item: BarangDipilihAdmin) {
        if (listPermintaan.any { it.barangId == item.barangId }) return

        listPermintaan.add(item)
        onChange?.invoke(item.barangId, item.stokToko)
    }

    fun updateJumlah(barangId: Int, jumlahBaru: Int) {
        val item = listPermintaan.find { it.barangId == barangId } ?: return

        if (jumlahBaru <= 0) {
            listPermintaan.remove(item)
            onChange?.invoke(barangId, 0)
            return
        }

        val newItem = BarangDipilihAdmin(
            barangId = item.barangId,
            nama = item.nama,
            merek = item.merek,
            kodeBarang = item.kodeBarang,
            stokToko = jumlahBaru
        )

        listPermintaan[listPermintaan.indexOf(item)] = newItem
        onChange?.invoke(barangId, jumlahBaru)
    }

    fun getList(): List<BarangDipilihAdmin> = listPermintaan.toList()

    // ✅ ✅ ✅ INI YANG PALING PENTING (FIX BUG UTAMA)
    fun clearAll() {
        modeAktif = null
        listPermintaan.clear()

        // ❗ JANGAN PERNAH MATIKAN onChange
        onChange?.invoke(-1, 0) // trigger reset UI Home
    }
}
