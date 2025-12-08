package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListKelolaMerek
import androidx.navigation.fragment.findNavController
import com.example.inventrix.Model.*
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KelolaMerekFragment : Fragment() {

    private lateinit var adapter: ListKelolaMerek

    // Daftar merek (diambil dari API)
    private val merekList = mutableListOf<MerekData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_kelola_merek, container, false)
        val btnBack = view.findViewById<View>(R.id.btnback)
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        /** --------------------------------
         *  SETUP RECYCLER VIEW
         * --------------------------------*/
        val recyclerMerek = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerMerek)
        recyclerMerek.layoutManager = LinearLayoutManager(requireContext())

        adapter = ListKelolaMerek(
            merekList,
            onEdit = { merek -> showEditDialog(merek) },
            onDelete = { merek -> showDeleteDialog(merek) }
        )
        recyclerMerek.adapter = adapter

        /** --------------------------------
         *  TOMBOL TAMBAH
         * --------------------------------*/
        val btnTambahMerek = view.findViewById<View>(R.id.btnTambahMerek)
        val iconTambah = view.findViewById<ImageView>(R.id.rvtambah)

        btnTambahMerek.setOnClickListener { showAddDialog() }
        iconTambah.setOnClickListener { showAddDialog() }

        /** --------------------------------
         *  LOAD MEREK DARI SERVER
         * --------------------------------*/
        loadMerek()

        return view
    }

    // ========================================================================
    //  LOAD MEREK (GET merek/list)
    // ========================================================================
    private fun loadMerek() {

        ApiClinet.instance.getMerekList()
            .enqueue(object : Callback<ResTampilMerek> {

                override fun onResponse(
                    call: Call<ResTampilMerek>,
                    response: Response<ResTampilMerek>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val listData = response.body()!!.data ?: emptyList()

                        merekList.clear()

                        for (item in listData) {
                            if (item != null) {
                                merekList.add(
                                    MerekData(
                                        id = item.id ?: -1,
                                        namaMerek = item.namaMerek ?: ""
                                    )
                                )
                            }
                        }

                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Data merek dimuat", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat daftar merek", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                    Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ========================================================================
    //  TAMBAH MEREK (Dialog)
    // ========================================================================
    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_merek, null)
        val inputMerek = dialogView.findViewById<EditText>(R.id.inputMerek)
        dialogView.findViewById<TextView>(R.id.titleDialog)?.text = "Tambah Merek"

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Tambahkan") { dialog, _ ->

                val nama = inputMerek.text.toString().trim()
                if (nama.isEmpty()) {
                    Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                tambahMerekKeServer(nama)
                dialog.dismiss()
            }
            .show()
    }

    // ========================================================================
    //  REQUEST API : TAMBAH MEREK
    // ========================================================================
    private fun tambahMerekKeServer(nama: String) {

        val req = ReqTambahMerek(nama)

        ApiClinet.instance.tambahMerek(nama)
            .enqueue(object : Callback<ResTambahMerek> {

                override fun onResponse(
                    call: Call<ResTambahMerek>,
                    response: Response<ResTambahMerek>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val res = response.body()

                        val newMerek = MerekData(
                            id = res?.data?.id ?: -1,
                            namaMerek = res?.data?.namaMerek ?: nama
                        )

                        adapter.addMerek(newMerek)

                        Toast.makeText(requireContext(), "Merek berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Gagal menambah merek", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResTambahMerek>, t: Throwable) {
                    Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ========================================================================
    //  EDIT MEREK (Dialog)
    // ========================================================================
    private fun showEditDialog(merek: MerekData) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_merek, null)
        val inputMerek = dialogView.findViewById<EditText>(R.id.inputMerek)
        val title = dialogView.findViewById<TextView>(R.id.titleDialog)
        title.text = "Edit Merek"
        inputMerek.setText(merek.namaMerek)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Ubah") { dialog, _ ->

                val newName = inputMerek.text.toString().trim()
                if (newName.isEmpty()) return@setPositiveButton

                editMerekKeServer(merek.id ?: -1, newName)

                dialog.dismiss()
            }
            .show()
    }

    // ========================================================================
    //  REQUEST API : EDIT MEREK (PUT)
    // ========================================================================
    private fun editMerekKeServer(id: Int, newName: String) {

        ApiClinet.instance.editMerek(id, newName)
            .enqueue(object : Callback<ResEditMerek> {

                override fun onResponse(
                    call: Call<ResEditMerek>,
                    response: Response<ResEditMerek>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val res = response.body()

                        val updated = MerekData(
                            id = res?.data?.id ?: id,
                            namaMerek = res?.data?.namaMerek ?: newName
                        )

                        adapter.updateMerek(updated)

                        Toast.makeText(requireContext(), "Merek berhasil diperbarui", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Gagal mengubah merek", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResEditMerek>, t: Throwable) {
                    Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ========================================================================
    //  HAPUS MEREK (Dialog)
    // ========================================================================
    private fun showDeleteDialog(merek: MerekData) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Merek")
            .setMessage("Yakin ingin menghapus merek \"${merek.namaMerek}\"?")
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Hapus") { dialog, _ ->
                hapusMerekKeServer(merek.id ?: -1)
                dialog.dismiss()
            }
            .show()
    }

    // ========================================================================
    //  REQUEST API : HAPUS MEREK (DELETE)
    // ========================================================================
    private fun hapusMerekKeServer(id: Int) {

        ApiClinet.instance.hapusMerek(id)
            .enqueue(object : Callback<ResHapusMerek> {

                override fun onResponse(
                    call: Call<ResHapusMerek>,
                    response: Response<ResHapusMerek>
                ) {
                    if (response.isSuccessful) {

                        adapter.removeMerek(id)

                        Toast.makeText(requireContext(), "Merek berhasil dihapus", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Gagal menghapus merek", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResHapusMerek>, t: Throwable) {
                    Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
