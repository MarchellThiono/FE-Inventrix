package com.example.inventrix.Adapter

import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.R

class ListPeringatan(
    dataList: MutableList<Notifikasi>,
    onDelete: (Notifikasi) -> Unit
) : BaseNotifikasiAdapter(
    dataList = dataList,
    layoutId = R.layout.item_peringatan,
    tvJudulId = R.id.tvJudul,
    tvIsiId = R.id.tvIsiPeringatan,
    tvTanggalId = R.id.tvTanggal,
    onDelete = onDelete
)
