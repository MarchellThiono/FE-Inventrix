package com.example.inventrix.UI.Gudang.ui.beranda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeGudangViewModel : ViewModel() {

    // barangId -> jumlah dipilih
    private val _selectedBarang = MutableLiveData<HashMap<Int, Int>>(HashMap())
    val selectedBarang: LiveData<HashMap<Int, Int>> get() = _selectedBarang

    fun setJumlah(barangId: Int, jumlah: Int) {
        val map = _selectedBarang.value ?: HashMap()

        if (jumlah > 0) map[barangId] = jumlah
        else map.remove(barangId)

        _selectedBarang.value = map
    }

    fun getJumlah(barangId: Int): Int {
        return _selectedBarang.value?.get(barangId) ?: 0
    }

    fun clear() {
        _selectedBarang.value = HashMap()
    }
}
