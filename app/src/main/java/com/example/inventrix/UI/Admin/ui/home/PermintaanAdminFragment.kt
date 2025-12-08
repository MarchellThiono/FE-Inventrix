package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaanAdmin
import com.example.inventrix.Model.BarangLaporanResult
import com.example.inventrix.Model.ReqLaporanItem
import com.example.inventrix.Model.ReqCreateLaporan
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.ui.aktivitas.AktivitasFragment
import com.example.inventrix.databinding.FragmentPermintaanAdminBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PermintaanAdminFragment : Fragment() {

    private var _binding: FragmentPermintaanAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListPermintaanAdmin
    private var modeDipilih: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPermintaanAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        modeDipilih = arguments?.getString("mode")

        // =========================================
        //          SETUP UI BERDASARKAN MODE
        // =========================================
        if (modeDipilih == "Penyesuaian Stok") {
            binding.textJenisLaporan.text = "Penyesuaian Stok"
            binding.spinnerJenisLaporan.visibility = View.VISIBLE
            binding.layoutSupplier.visibility = View.GONE

            val jenisList = listOf("Rusak", "Hilang")
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, jenisList)
            binding.spinnerJenisLaporan.adapter = spinnerAdapter

        } else {
            binding.textJenisLaporan.text = "Permintaan Stok"
            binding.spinnerJenisLaporan.visibility = View.GONE
            binding.layoutSupplier.visibility = View.GONE
        }

        // =========================================
        //             SETUP RECYCLER VIEW
        // =========================================
        val dataDipilih = PermintaanManager.getList()

        adapter = ListPermintaanAdmin(
            dataDipilih,
            onJumlahEdit = { id, jumlah ->
                PermintaanManager.updateJumlah(id, jumlah)
            },
            isReadOnly = false
        )

        binding.rvBarangDipilih.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarangDipilih.adapter = adapter


        // =========================================
        //                BACK BUTTON
        // =========================================
        binding.icBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // =========================================
        //          KONFIRMASI (FUNGSI UTAMA)
        // =========================================
        binding.btnKonfirmasi.setOnClickListener {
            prosesKonfirmasi()
        }
    }

    // ============================================================
    //                       FUNGSI KONFIRMASI
    // ============================================================
    private fun prosesKonfirmasi() {

        val hasilAkhir: List<BarangLaporanResult> = adapter.getListJumlah()

        if (hasilAkhir.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada barang dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        // ============================================================
        //                  MODE 1 — PENYESUAIAN STOK
        // ============================================================
        if (modeDipilih == "Penyesuaian Stok") {

            val jenis = binding.spinnerJenisLaporan.selectedItem.toString().uppercase()

            val items = hasilAkhir.map {
                ReqLaporanItem(
                    barangId = it.barangId,
                    jumlah = it.jumlah,
                    keterangan = ""
                )
            }

            val body = ReqCreateLaporan(
                jenis = jenis,
                supplier = "",
                items = items
            )

            ApiClinet.instance.createLaporan(body)
                .enqueue(object : Callback<ResPesan> {
                    override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                        Toast.makeText(requireContext(), "Laporan Penyesuaian berhasil dibuat", Toast.LENGTH_SHORT).show()

                        // kembali ke Home Stok Toko
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }

                    override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                })

            return
        }


        // ============================================================
        //                  MODE 2 — PERMINTAAN STOK
        // ============================================================
        if (modeDipilih == "Permintaan Stok") {

            val body = mapOf(
                "keterangan" to "Permintaan stok toko",
                "items" to hasilAkhir.map {
                    mapOf(
                        "barangId" to it.barangId,
                        "jumlah" to it.jumlah,
                        "keterangan" to ""
                    )
                }
            )

            ApiClinet.instance.kirimPermintaanGudang(body)
                .enqueue(object : Callback<ResPesan> {
                    override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                        Toast.makeText(requireContext(), "Permintaan berhasil dikirim", Toast.LENGTH_SHORT).show()

                        // MASUK KE HALAMAN AKTIVITAS
                        val frag = AktivitasFragment()

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.containerStokToko, frag)
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
