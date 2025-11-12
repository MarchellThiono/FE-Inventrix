package com.example.inventrix.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MerekBottomSheet(
    private val onMerekSelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_merek, container, false)

        val rvMerek = view.findViewById<RecyclerView>(R.id.rvMerek)
        val tvTitle = view.findViewById<TextView>(R.id.titleMerek)

        rvMerek.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ListMerek(emptyList()) { merekTerpilih ->
            onMerekSelected(merekTerpilih)
            dismiss()
        }
        rvMerek.adapter = adapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val merekList = response.body()?.data
                        ?.filterNotNull()
                        ?.mapNotNull { it.namaMerek }
                        ?: emptyList()

                    if (merekList.isEmpty()) {
                        tvTitle.text = "Tidak ada merek tersedia"
                    } else {
                        adapter.updateData(merekList)
                        tvTitle.text = "Pilih Merek"
                    }
                } else {
                    tvTitle.text = "Gagal memuat merek"
                    Toast.makeText(requireContext(), "Gagal memuat data merek", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                tvTitle.text = "Gagal terhubung"
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}
