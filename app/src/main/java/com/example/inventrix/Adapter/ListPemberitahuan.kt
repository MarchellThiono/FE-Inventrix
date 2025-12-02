package com.example.inventrix.Adapter

import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.R

class ListPemberitahuan(
    dataList: MutableList<Notifikasi>,
    onDelete: (Notifikasi) -> Unit
) : BaseNotifikasiAdapter(
    dataList = dataList,
    layoutId = R.layout.item_pemberitahuan,
    tvJudulId = R.id.tvJudulPemberitahuan,
    tvIsiId = R.id.tvIsiPemberitahuan,
    tvTanggalId = R.id.tvTanggalPemberitahuan,
    onDelete = onDelete
)
