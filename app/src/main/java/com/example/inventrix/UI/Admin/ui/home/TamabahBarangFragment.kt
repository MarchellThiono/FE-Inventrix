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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.ResTambahBarang
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

class TamabahBarangFragment : Fragment() {

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

    private var selectedImageUri: Uri? = null
    private var selectedMerekId: Int? = null
    private var merekMap: Map<String, Int> = emptyMap() // simpan pasangan nama -> id

    // launcher untuk buka galeri
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
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
        val view = inflater.inflate(R.layout.fragment_tamabah_barang, container, false)

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

        progressBar.visibility = View.GONE
        btnDeleteImage.visibility = View.GONE

        tvPilihMerek.setOnClickListener { tampilkanBottomSheetMerek() }
        imageView.setOnClickListener { openGallery() }
        btnDeleteImage.setOnClickListener { hapusGambar() }
        btnSimpan.setOnClickListener { tambahBarang() }

        return view
    }

    // ================== Fungsi =====================

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun hapusGambar() {
        selectedImageUri = null
        imageView.setImageResource(R.drawable.ic_image)
        btnDeleteImage.visibility = View.GONE
    }

    /** ======================
     * BOTTOM SHEET PILIH MEREK
     * =====================*/
    private fun tampilkanBottomSheetMerek() {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rvMerek = dialogView.findViewById<RecyclerView>(R.id.rvMerek)
        val tvTitle = dialogView.findViewById<TextView>(R.id.titleMerek)

        rvMerek.layoutManager = LinearLayoutManager(requireContext())
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()

        val merekAdapter = ListMerek(emptyList()) { merekNama ->
            selectedMerekId = merekMap[merekNama] // ambil id berdasarkan nama
            tvPilihMerek.text = merekNama
            bottomSheetDialog.dismiss()
        }
        rvMerek.adapter = merekAdapter

        // Ambil data merek dari backend
        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val dataMerek = response.body()?.data?.filterNotNull() ?: emptyList()
                    val namaList = dataMerek.mapNotNull { it.namaMerek }
                    merekMap = dataMerek.associate { it.namaMerek!! to it.id!! }

                    merekAdapter.updateData(namaList)
                    tvTitle.text = "Pilih Merek"
                } else {
                    tvTitle.text = "Gagal memuat merek"
                }
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                tvTitle.text = "Gagal terhubung: ${t.message}"
            }
        })
    }

    /** ======================
     * SIMPAN / TAMBAH BARANG
     * =====================*/
    private fun tambahBarang() {
        val nama = etNamaBarang.text.toString().trim()
        val kode = etKodeBarang.text.toString().trim()
        val hargaBeli = etHargaBeli.text.toString().trim()
        val hargaJual = etHargaJual.text.toString().trim()
        val deskripsi = etDeskripsi.text.toString().trim()

        if (nama.isEmpty() || kode.isEmpty() || selectedMerekId == null ||
            hargaBeli.isEmpty() || hargaJual.isEmpty() || deskripsi.isEmpty()
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
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()
                fileBytes?.let {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                    imagePart = MultipartBody.Part.createFormData("image", "barang.jpg", requestFile)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal membaca gambar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        progressBar.visibility = View.VISIBLE
        btnSimpan.isEnabled = false

        ApiClinet.instance.tambahBarang(
            kodeBody, namaBody, merekBody, hargaBeliBody, hargaJualBody, deskripsiBody, imagePart
        ).enqueue(object : Callback<ResTambahBarang> {
            override fun onResponse(call: Call<ResTambahBarang>, response: Response<ResTambahBarang>) {
                progressBar.visibility = View.GONE
                btnSimpan.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(requireContext(), "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(requireContext(), "Gagal menambah barang (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResTambahBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnSimpan.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** ======================
     * RESET FORM
     * =====================*/
    private fun clearForm() {
        etNamaBarang.text.clear()
        etKodeBarang.text.clear()
        etHargaBeli.text.clear()
        etHargaJual.text.clear()
        etDeskripsi.text.clear()
        selectedImageUri = null
        selectedMerekId = null
        tvPilihMerek.text = "Pilih Merek"
        imageView.setImageResource(R.drawable.ic_image)
        btnDeleteImage.visibility = View.GONE
    }
}
