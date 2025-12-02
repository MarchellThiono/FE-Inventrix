package com.example.inventrix.UI.Admin.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.ResEditBarang
import com.example.inventrix.Model.ResTambahBarang
import com.example.inventrix.Model.ResTampilDetail
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahBarangFragment : Fragment() {

    private lateinit var etNamaBarang: EditText
    private lateinit var etKodeBarang: EditText
    private lateinit var etHargaBeli: EditText
    private lateinit var etHargaJual: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var btnSimpan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var btnDeleteImage: ImageButton
    private lateinit var tvPilihMerek: TextView
    private lateinit var tvHeader: TextView

    private var selectedImageUri: Uri? = null
    private var selectedMerekId: Int? = null
    private var merekMap: Map<String, Int> = emptyMap()

    private var isEdit = false
    private var barangId: Int? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    imageView.setImageURI(it)
                    btnDeleteImage.visibility = View.VISIBLE
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_tambah_barang, container, false)

        // Bind UI
        etNamaBarang = view.findViewById(R.id.rvNamaBarang)
        etKodeBarang = view.findViewById(R.id.rvKodeBarang)
        etHargaBeli = view.findViewById(R.id.rvHargaBeli)
        etHargaJual = view.findViewById(R.id.rvHargaJual)
        etDeskripsi = view.findViewById(R.id.tvDescription)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        imageView = view.findViewById(R.id.image)
        btnDeleteImage = view.findViewById(R.id.btnDeleteImage)
        progressBar = view.findViewById(R.id.loadingBar)
        tvPilihMerek = view.findViewById(R.id.chipSemua)
        tvHeader = view.findViewById(R.id.nama_sekolah)

        btnDeleteImage.visibility = View.GONE
        progressBar.visibility = View.GONE

        // Mode Edit atau Tambah
        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        barangId = arguments?.getInt("id", -1).takeIf { it != -1 }

        if (isEdit && barangId != null) {
            tvHeader.text = "Edit Barang"
            btnSimpan.text = "Simpan Perubahan"
            loadDetailBarang(barangId!!)
        } else {
            tvHeader.text = "Tambah Barang"
            btnSimpan.text = "Tambah Barang"
        }

        // Back button
        view.findViewById<ImageView>(R.id.btnback).setOnClickListener {
            findNavController().popBackStack()
        }

        // Events
        imageView.setOnClickListener { openGallery() }
        btnDeleteImage.setOnClickListener { resetGambar() }
        tvPilihMerek.setOnClickListener { tampilkanBottomSheetMerek() }

        btnSimpan.setOnClickListener {
            if (isEdit) updateBarang()
            else tambahBarang()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun resetGambar() {
        selectedImageUri = null
        imageView.setImageResource(R.drawable.ic_image)
        btnDeleteImage.visibility = View.GONE
    }

    // ================= LOAD DETAIL BARANG =================
    private fun loadDetailBarang(id: Int) {

        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(id).enqueue(object : Callback<ResEditBarang> {
            override fun onResponse(
                call: Call<ResEditBarang>,
                response: Response<ResEditBarang>
            ) {
                progressBar.visibility = View.GONE

                val data = response.body()?.data ?: return

                etNamaBarang.setText(data.namaBarang)
                etKodeBarang.setText(data.kodeBarang)
                etDeskripsi.setText(data.deskripsi)
                etHargaBeli.setText(data.hargaBeli.toString())
                etHargaJual.setText(data.hargaJual.toString())

                // FIX DI SINI
                tvPilihMerek.text = data.merek?.namaMerek

                ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
                    override fun onResponse(
                        call: Call<ResTampilMerek>,
                        response: Response<ResTampilMerek>
                    ) {
                        val list = response.body()?.data ?: return
                        merekMap = list.filterNotNull().associate { it.namaMerek!! to it.id!! }

                        // FIX DI SINI
                        selectedMerekId = merekMap[data.merek?.namaMerek]
                    }

                    override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {}
                })

                if (!data.imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(data.imageUrl)
                        .into(imageView)
                    btnDeleteImage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // ================= TAMBAH BARANG =================
    private fun tambahBarang() {
        val kode = etKodeBarang.text.toString()
        val nama = etNamaBarang.text.toString()
        val hargaBeli = etHargaBeli.text.toString()
        val hargaJual = etHargaJual.text.toString()
        val deskripsi = etDeskripsi.text.toString()

        if (kode.isEmpty() || nama.isEmpty() ||
            hargaBeli.isEmpty() || hargaJual.isEmpty() ||
            deskripsi.isEmpty() || selectedMerekId == null
        ) {
            Toast.makeText(requireContext(), "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val kodeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), kode)
        val namaBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
        val merekBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedMerekId.toString())
        val hargaBeliBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaBeli)
        val hargaJualBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaJual)
        val deskripsiBody = RequestBody.create("text/plain".toMediaTypeOrNull(), deskripsi)

        var imagePart: MultipartBody.Part? = null

        selectedImageUri?.let { uri ->
            val input = requireContext().contentResolver.openInputStream(uri)
            val bytes = input?.readBytes()
            val req = RequestBody.create("image/*".toMediaTypeOrNull(), bytes!!)
            imagePart = MultipartBody.Part.createFormData("image", "barang.jpg", req)
        }

        progressBar.visibility = View.VISIBLE
        btnSimpan.isEnabled = false

        ApiClinet.instance.tambahBarang(
            kodeBody, namaBody, merekBody, hargaBeliBody,
            hargaJualBody, deskripsiBody, imagePart
        ).enqueue(object : Callback<ResTambahBarang> {
            override fun onResponse(
                call: Call<ResTambahBarang>,
                response: Response<ResTambahBarang>
            ) {
                progressBar.visibility = View.GONE
                btnSimpan.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Barang ditambahkan!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Gagal (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResTambahBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnSimpan.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ================= UPDATE BARANG =================
    private fun updateBarang() {
        if (barangId == null) return

        val kode = etKodeBarang.text.toString()
        val nama = etNamaBarang.text.toString()
        val hargaBeli = etHargaBeli.text.toString()
        val hargaJual = etHargaJual.text.toString()
        val deskripsi = etDeskripsi.text.toString()

        val kodeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), kode)
        val namaBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
        val merekBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedMerekId.toString())
        val hargaBeliBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaBeli)
        val hargaJualBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaJual)
        val deskripsiBody = RequestBody.create("text/plain".toMediaTypeOrNull(), deskripsi)

        var imagePart: MultipartBody.Part? = null

        selectedImageUri?.let { uri ->
            val input = requireContext().contentResolver.openInputStream(uri)
            val bytes = input?.readBytes()
            val req = RequestBody.create("image/*".toMediaTypeOrNull(), bytes!!)
            imagePart = MultipartBody.Part.createFormData("image", "barang.jpg", req)
        }

        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.editBarang(
            barangId!!,
            kodeBody, namaBody, merekBody,
            hargaBeliBody, hargaJualBody, deskripsiBody, imagePart
        ).enqueue(object : Callback<ResEditBarang> {
            override fun onResponse(
                call: Call<ResEditBarang>,
                response: Response<ResEditBarang>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Perubahan disimpan", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Gagal simpan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ================= MEREK =================
    private fun tampilkanBottomSheetMerek() {
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        val title = view.findViewById<TextView>(R.id.titleMerek)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        val adapter = ListMerek(emptyList()) { nama ->
            selectedMerekId = merekMap[nama]
            tvPilihMerek.text = nama
            dialog.dismiss()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                val list = response.body()?.data?.filterNotNull() ?: emptyList()
                merekMap = list.associate { it.namaMerek!! to it.id!! }
                adapter.updateData(list.map { it.namaMerek!! })
                title.text = "Pilih Merek"
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                title.text = "Gagal memuat merek"
            }
        })
    }
}
