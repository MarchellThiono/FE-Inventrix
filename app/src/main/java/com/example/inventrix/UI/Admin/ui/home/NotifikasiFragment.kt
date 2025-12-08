package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPemberitahuan
import androidx.navigation.fragment.findNavController
import com.example.inventrix.Adapter.ListPeringatan
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentNotifikasiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiFragment : Fragment() {

    private var _binding: FragmentNotifikasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var peringatanAdapter: ListPeringatan
    private lateinit var pemberitahuanAdapter: ListPemberitahuan

    private val listPeringatanData = mutableListOf<Notifikasi>()
    private val listPemberitahuanData = mutableListOf<Notifikasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rvPeringatan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPemberitahuan.layoutManager = LinearLayoutManager(requireContext())

        peringatanAdapter = ListPeringatan(listPeringatanData) { notif ->
            confirmDelete(notif)
        }
        pemberitahuanAdapter = ListPemberitahuan(listPemberitahuanData) { notif ->
            confirmDelete(notif)
        }

        binding.rvPeringatan.adapter = peringatanAdapter
        binding.rvPemberitahuan.adapter = pemberitahuanAdapter

        loadData()
//        binding.btnback.setOnClickListener {
//            findNavController().navigate(R.id.action_notifikasiFragment_to_navigation_home)
//        }
    }

    private fun loadData() {

        val prefs = requireContext().getSharedPreferences("APP_PREF", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getLong("USER_ID", 0)

        if (userId == 0L) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // ===================== LOAD PERINGATAN =====================
        ApiClinet.instance.getPeringatan(userId)
            .enqueue(object : Callback<List<Notifikasi>> {
                override fun onResponse(call: Call<List<Notifikasi>>, response: Response<List<Notifikasi>>) {
                    val data = response.body() ?: emptyList()
                    listPeringatanData.clear()
                    listPeringatanData.addAll(data)
                    peringatanAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<Notifikasi>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal load peringatan", Toast.LENGTH_SHORT).show()
                }
            })

        // ===================== LOAD PEMBERITAHUAN =====================
        ApiClinet.instance.getPemberitahuan(userId)
            .enqueue(object : Callback<List<Notifikasi>> {
                override fun onResponse(call: Call<List<Notifikasi>>, response: Response<List<Notifikasi>>) {
                    val data = response.body() ?: emptyList()
                    listPemberitahuanData.clear()
                    listPemberitahuanData.addAll(data)
                    pemberitahuanAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<Notifikasi>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal load pemberitahuan", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun confirmDelete(notif: Notifikasi) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Notifikasi")
            .setMessage("Apakah Anda yakin ingin menghapus notifikasi ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteNotif(notif)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteNotif(notif: Notifikasi) {
        ApiClinet.instance.deleteNotifikasi(notif.id)
            .enqueue(object : Callback<com.example.inventrix.Model.ResPesan> {

                override fun onResponse(
                    call: Call<com.example.inventrix.Model.ResPesan>,
                    response: Response<com.example.inventrix.Model.ResPesan>
                ) {
                    Toast.makeText(requireContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show()

                    if (notif.tipe == "STOK_MINIM") {
                        peringatanAdapter.removeItem(notif)
                    } else {
                        pemberitahuanAdapter.removeItem(notif)
                    }
                }

                override fun onFailure(call: Call<com.example.inventrix.Model.ResPesan>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
