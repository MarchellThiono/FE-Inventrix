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
    private var merekMap: Map<String, Int> = emptyMap()

    private var isEdit = false
    private var editId = -1
    private var originalImageUrl: String? = null

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_tamabah_barang, container, false)
        val btnBack = view.findViewById<ImageView>(R.id.btnback)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_tambahBarangFragment_to_navigation_home)
        }

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

        btnDeleteImage.visibility = View.GONE
        progressBar.visibility = View.GONE

        tvPilihMerek.setOnClickListener { tampilkanBottomSheetMerek() }
        imageView.setOnClickListener { openGallery() }
        btnDeleteImage.setOnClickListener { hapusGambar() }

        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        if (isEdit) {
            editId = arguments?.getInt("id") ?: -1
            if (editId != -1) loadDetailBarang(editId)
            btnSimpan.text = "Update"
        } else {
            btnSimpan.text = "Simpan"
        }

        btnSimpan.setOnClickListener {
            if (isEdit) updateBarang() else tambahBarang()
        }

        return view
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun hapusGambar() {
        selectedImageUri = null
        originalImageUrl = null
        imageView.setImageResource(R.drawable.ic_image)
        btnDeleteImage.visibility = View.GONE
    }

    private fun loadDetailBarang(id: Int) {
        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(id).enqueue(object : Callback<ResEditBarang> {
            override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.data != null) {
                    val d = response.body()!!.data!!

                    etNamaBarang.setText(d.namaBarang ?: "")
                    etKodeBarang.setText(d.kodeBarang ?: "")
                    d.hargaBeli?.let { etHargaBeli.setText(it.toString()) }
                    d.hargaJual?.let { etHargaJual.setText(it.toString()) }
                    etDeskripsi.setText(d.deskripsi ?: "")

                    // 🔥 Karena merek sekarang STRING
                    tvPilihMerek.text = d.merek ?: "Pilih Merek"

                    // ❗ kamu perlu atur selectedMerekId berdasarkan nama merek
                    if (merekMap.containsKey(d.merek)) {
                        selectedMerekId = merekMap[d.merek]
                    }

                    originalImageUrl = d.imageUrl
                    if (!originalImageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(originalImageUrl).into(imageView)
                        btnDeleteImage.visibility = View.VISIBLE
                    }

                } else {
                    Toast.makeText(requireContext(), "Gagal memuat detail barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun tambahBarang() {
        prosesBarang(isUpdate = false)
    }

    private fun updateBarang() {
        prosesBarang(isUpdate = true)
    }

    private fun prosesBarang(isUpdate: Boolean) {
        val nama = etNamaBarang.text.toString().trim()
        val kode = etKodeBarang.text.toString().trim()
        val hargaBeli = etHargaBeli.text.toString().trim()
        val hargaJual = etHargaJual.text.toString().trim()
        val deskripsi = etDeskripsi.text.toString().trim()

        if (nama.isEmpty() || kode.isEmpty() || hargaBeli.isEmpty() ||
            hargaJual.isEmpty() || deskripsi.isEmpty() || (selectedMerekId == null)
        ) {
            Toast.makeText(requireContext(), "Lengkapi semua data (termasuk merek)!", Toast.LENGTH_SHORT).show()
            return
        }

        val kodeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), kode)
        val namaBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
        val merekBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedMerekId.toString())
        val hargaBeliBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaBeli)
        val hargaJualBody = RequestBody.create("text/plain".toMediaTypeOrNull(), hargaJual)
        val deskripsiBody = RequestBody.create("text/plain".toMediaTypeOrNull(), deskripsi)

        var imagePart: MultipartBody.Part? = null
        if (selectedImageUri != null) {
            val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
            val fileBytes = inputStream?.readBytes()
            inputStream?.close()
            if (fileBytes != null) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
                imagePart = MultipartBody.Part.createFormData("image", "barang.jpg", requestFile)
            }
        }

        progressBar.visibility = View.VISIBLE
        btnSimpan.isEnabled = false

        if (isUpdate) {
            ApiClinet.instance.updateBarang(
                editId,
                kodeBody, namaBody, merekBody,
                hargaBeliBody, hargaJualBody, deskripsiBody,
                imagePart
            ).enqueue(object : Callback<ResEditBarang> {
                override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
                    progressBar.visibility = View.GONE
                    btnSimpan.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Berhasil update barang!", Toast.LENGTH_SHORT).show()
                        // kembali dan refresh list di HomeFragment
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Gagal update (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnSimpan.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            ApiClinet.instance.tambahBarang(
                kodeBody, namaBody, merekBody,
                hargaBeliBody, hargaJualBody, deskripsiBody,
                imagePart
            ).enqueue(object : Callback<ResTambahBarang> {
                override fun onResponse(call: Call<ResTambahBarang>, response: Response<ResTambahBarang>) {
                    progressBar.visibility = View.GONE
                    btnSimpan.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Gagal tambah (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResTambahBarang>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnSimpan.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun tampilkanBottomSheetMerek() {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rvMerek = dialogView.findViewById<RecyclerView>(R.id.rvMerek)
        val tvTitle = dialogView.findViewById<TextView>(R.id.titleMerek)

        rvMerek.layoutManager = LinearLayoutManager(requireContext())
        val sheet = BottomSheetDialog(requireContext())
        sheet.setContentView(dialogView)
        sheet.show()

        val merekAdapter = ListMerek(emptyList()) { merekNama ->
            selectedMerekId = merekMap[merekNama]
            tvPilihMerek.text = merekNama
            sheet.dismiss()
        }
        rvMerek.adapter = merekAdapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val data = response.body()!!.data!!.filterNotNull()
                    merekMap = data.associate { it.namaMerek!! to it.id!! }
                    merekAdapter.updateData(data.map { it.namaMerek!! })
                    tvTitle.text = "Pilih Merek"
                } else {
                    tvTitle.text = "Gagal memuat merek"
                }
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                tvTitle.text = "Gagal: ${t.message}"
            }
        })
    }
}
