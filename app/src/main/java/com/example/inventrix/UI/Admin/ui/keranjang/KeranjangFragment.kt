package com.example.inventrix.UI.Admin.ui.keranjang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListKeranjang
import com.example.inventrix.Model.ReqKeranjang
import com.example.inventrix.databinding.FragmentKeranjangBinding

class KeranjangFragment : Fragment() {

    private var _binding: FragmentKeranjangBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListKeranjang
    private var totalHarga = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeranjangBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        setupButton()

        return view
    }

    private fun setupRecyclerView() {
        val dummyKeranjang = mutableListOf(
            ReqKeranjang("Kipas Angin", "Maspion", "BRG-001", 250000),
            ReqKeranjang("Setrika", "Philips", "BRG-002", 180000)
        )

        adapter = ListKeranjang(dummyKeranjang) { total ->
            totalHarga = total
            updateTotalHarga()
        }

        binding.rvpilihbarang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvpilihbarang.adapter = adapter

        totalHarga = adapter.getTotalHarga()
        updateTotalHarga()
    }

    private fun updateTotalHarga() {
        binding.btntmbh.text = "Total: Rp$totalHarga"
    }

    private fun setupButton() {
        binding.btntmbh.setOnClickListener {
            // Nanti diganti API call ke backend
            // sementara tampilkan konfirmasi dummy
            android.widget.Toast.makeText(requireContext(),
                "Konfirmasi pembelian berhasil!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
