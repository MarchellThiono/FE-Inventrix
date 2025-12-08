package com.example.inventrix.UI.Admin.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListRiwayatBarang
import com.example.inventrix.Adapter.ListStokAkhir
import com.example.inventrix.Model.ResDetailRiwayat
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentDetailRiwayatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRiwayatFragment : Fragment() {

    private var _binding: FragmentDetailRiwayatBinding? = null
    private val binding get() = _binding!!

    private var laporanId: Long = 0L

    private lateinit var barangAdapter: ListRiwayatBarang
    private lateinit var stokAdapter: ListStokAkhir

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✔️ Ambil ID laporan dari Bundle
        laporanId = arguments?.getLong("laporanId") ?: 0L

        setupRecyclerView()
        loadDetailRiwayat()

        binding.menuBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun setupRecyclerView() {
        barangAdapter = ListRiwayatBarang(arrayListOf())
        stokAdapter = ListStokAkhir(arrayListOf())

        binding.rvRiwayatbarang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatbarang.adapter = barangAdapter

        binding.rvStokAkhir.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStokAkhir.adapter = stokAdapter
    }

    private fun loadDetailRiwayat() {
        ApiClinet.instance.getDetailRiwayat(laporanId)
            .enqueue(object : Callback<ResDetailRiwayat> {
                override fun onResponse(
                    call: Call<ResDetailRiwayat>,
                    response: Response<ResDetailRiwayat>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body() ?: return
                        setData(data)
                    }
                }

                override fun onFailure(call: Call<ResDetailRiwayat>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun setData(data: ResDetailRiwayat) {

        val tanggalFull = data.tanggal ?: ""

        val split = tanggalFull.split("T")
        val tanggal = split.getOrNull(0) ?: "-"
        var waktu = split.getOrNull(1) ?: "-"
        waktu = waktu.split(".").getOrNull(0) ?: waktu
        waktu = waktu.replace("Z", "")

        binding.tgl.text = tanggal
        binding.tvwaktu.text = waktu.trim()

        binding.rvTotal.text = data.totalHargaFormatted ?: "0"
        binding.rvJenis.text = data.jenis ?: "-"

        // ✔️ Bersihkan list dari null
        val listItems = data.items?.filterNotNull() ?: emptyList()

        barangAdapter.update(listItems)
        stokAdapter.update(listItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
