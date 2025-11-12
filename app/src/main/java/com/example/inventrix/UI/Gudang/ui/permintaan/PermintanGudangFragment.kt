package com.example.inventrix.UI.Gudang.ui.permintaan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaan
import com.example.inventrix.Model.ReqPermintaanBarang
import com.example.inventrix.R
import com.example.inventrix.databinding.FragmentPermintaanGudangBinding

class PermintanGudangFragment : Fragment() {

    private var _binding: FragmentPermintaanGudangBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListPermintaan

    private val listDiminta = listOf(
        ReqPermintaanBarang("Kipas Angin", "Maspion", "BRG001", 10, null, "diminta"),
        ReqPermintaanBarang("Setrika", "LG", "BRG002", 5, null, "diminta"),
        ReqPermintaanBarang("Dispenser", "Miyako", "BRG003", 8, null, "diminta")
    )

    private val listDikirim = listOf(
        ReqPermintaanBarang("TV LED", "Polytron", "BRG004", 3, null, "dikirim"),
        ReqPermintaanBarang("Kulkas", "Toshiba", "BRG005", 2, null, "dikirim")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermintaanGudangBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupListeners()
        highlightTab(true)

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ListPermintaan { item ->
            showConfirmDialog(item)
        }

        binding.rvPermintaan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaan.adapter = adapter
        adapter.updateData(listDiminta)
    }

    private fun setupListeners() {
        binding.diminta.setOnClickListener {
            adapter.updateData(listDiminta)
            highlightTab(true)
        }
        binding.dikirim.setOnClickListener {
            adapter.updateData(listDikirim)
            highlightTab(false)
        }
    }

    private fun showConfirmDialog(item: ReqPermintaanBarang) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Pengiriman")
        builder.setMessage("Apakah Anda ingin mengirim barang '${item.nama}' (${item.kodeBarang}) ke toko?")
        builder.setPositiveButton("Kirim Barang") { dialog, _ ->
            dialog.dismiss()
            // TODO: nanti tambahkan aksi kirim ke backend atau update status
            showSuccessDialog("Barang '${item.nama}' berhasil dikirim.")
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sukses")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun highlightTab(isDimintaActive: Boolean) {
        val activeColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val inactiveColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        val activeBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_selected)
        val inactiveBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip)

        binding.diminta.background = if (isDimintaActive) activeBg else inactiveBg
        binding.dikirim.background = if (!isDimintaActive) activeBg else inactiveBg

        binding.diminta.setTextColor(if (isDimintaActive) activeColor else inactiveColor)
        binding.dikirim.setTextColor(if (!isDimintaActive) activeColor else inactiveColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
