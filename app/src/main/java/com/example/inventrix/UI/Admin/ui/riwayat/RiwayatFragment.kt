package com.example.inventrix.UI.Admin.ui.riwayat

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Model.ReqFilterLaporan
import com.example.inventrix.Model.ResLaporanResponse
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentRiwayatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LaporanAdapter
    private var selectedDate: String = ""  // yyyy-MM-dd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ================================
        // SETUP ADAPTER + KLIK ITEM
        // ================================
        adapter = LaporanAdapter(arrayListOf()) { laporan ->

            val bundle = Bundle()
            bundle.putLong("laporanId", laporan.id?.toLong() ?: 0L)

            findNavController().navigate(
                R.id.action_navigation_riwayat_to_navigation_detail_riwayat,
                bundle
            )
        }


        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter

        setupDatePicker()
        setupSpinner()
        loadLaporan()
    }

    private fun setupDatePicker() {
        binding.pilihTgl.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val monthFormat = String.format("%02d", m + 1)
                    val dayFormat = String.format("%02d", d)

                    selectedDate = "$y-$monthFormat-$dayFormat"
                    binding.pilihTgl.text = selectedDate

                    loadLaporan()
                },
                year, month, day
            )

            datePicker.show()
        }
    }

    private fun setupSpinner() {
        val listJenis = listOf(
            "SEMUA",
            "MASUK",
            "RUSAK",
            "HILANG",
            "MUTASI TOKO",
            "KELUAR"
        )

        val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listJenis)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJenisLaporan.adapter = spAdapter

        binding.spinnerJenisLaporan.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loadLaporan()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun loadLaporan() {

        val jenis = if (binding.spinnerJenisLaporan.selectedItem == "SEMUA")
            ""
        else binding.spinnerJenisLaporan.selectedItem.toString()

        val req = ReqFilterLaporan(
            jenis = jenis,
            tanggalMulai = selectedDate,
            tanggalSelesai = selectedDate,
            page = 0,
            size = 50
        )

        ApiClinet.instance.filterLaporan(req)
            .enqueue(object : Callback<ResLaporanResponse> {
                override fun onResponse(
                    call: Call<ResLaporanResponse>,
                    response: Response<ResLaporanResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.content?.filterNotNull() ?: emptyList()
                        adapter.update(data)
                    }
                }

                override fun onFailure(call: Call<ResLaporanResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
