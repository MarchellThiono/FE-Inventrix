package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListKelolaMerek
import com.example.inventrix.R


class KelolaMerekFragment : Fragment() {

    private lateinit var adapter: ListKelolaMerek
    private val merekList = mutableListOf("Panasonic", "LG", "Maspion", "Miyako", "Polytron")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kelola_merek, container, false)

        // 🔹 Setup RecyclerView
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerMerek)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ListKelolaMerek(merekList,
            onEdit = { merek -> showEditDialog(merek) },
            onDelete = { merek -> showDeleteDialog(merek) }
        )
        recyclerView.adapter = adapter

        // 🔹 Tombol tambah
        val btnTambah = view.findViewById<View>(R.id.btnTambahMerek)
        btnTambah.setOnClickListener { showAddDialog() }

        return view
    }

    /** -------------------------------
     *  TAMBAH MEREK
     *  ------------------------------*/
    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_merek, null)
        val inputMerek = dialogView.findViewById<EditText>(R.id.inputMerek)
        dialogView.findViewById<TextView>(R.id.titleDialog)?.text = "Tambah Merek"

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Tambahkan") { dialog, _ ->
                val nama = inputMerek.text.toString().trim()
                if (nama.isNotEmpty()) {
                    adapter.addMerek(nama)
                    Toast.makeText(requireContext(), "Merek $nama ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .show()
    }

    /** -------------------------------
     *  EDIT MEREK
     *  ------------------------------*/
    private fun showEditDialog(oldName: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_merek, null)
        val inputMerek = dialogView.findViewById<EditText>(R.id.inputMerek)
        val title = dialogView.findViewById<TextView>(R.id.titleDialog)
        title.text = "Edit Merek"
        inputMerek.setText(oldName)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Ubah") { dialog, _ ->
                val newName = inputMerek.text.toString().trim()
                if (newName.isNotEmpty()) {
                    adapter.updateMerek(oldName, newName)
                    Toast.makeText(requireContext(), "Merek diubah menjadi $newName", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .show()
    }

    /** -------------------------------
     *  HAPUS MEREK
     *  ------------------------------*/
    private fun showDeleteDialog(nama: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Merek")
            .setMessage("Apakah Anda yakin ingin menghapus merek \"$nama\"?")
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Hapus") { dialog, _ ->
                adapter.removeMerek(nama)
                Toast.makeText(requireContext(), "Merek $nama dihapus", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }
}
