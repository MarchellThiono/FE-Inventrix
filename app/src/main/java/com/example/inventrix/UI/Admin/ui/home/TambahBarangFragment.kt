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
import com.bumptech.glide.Glide
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.*
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

    private lateinit var etNama: EditText
    private lateinit var etHargaBeli: EditText
    private lateinit var etHargaJual: EditText
    private lateinit var etStokToko: EditText
    private lateinit var etStokGudang: EditText
    private lateinit var etStokMinimum: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var tvKategori: TextView
    private lateinit var tvMerek: TextView
    private lateinit var tvHeader: TextView
    private lateinit var btnSimpan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var btnDeleteImage: ImageButton
    private lateinit var tvKodeBarang: TextView

    private var selectedKategoriId: Int? = null
    private var selectedMerekId: Int? = null
    private var selectedImageUri: Uri? = null
    private var merekMap: Map<String, Int> = emptyMap()

    private var isEdit = false
    private var barangId: Int? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                selectedImageUri = it.data?.data
                imageView.setImageURI(selectedImageUri)
                btnDeleteImage.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_tambah_barang, container, false)

        // BIND UI
        etNama = view.findViewById(R.id.rvNamaBarang)
        etHargaBeli = view.findViewById(R.id.rvHargaBeli)
        etHargaJual = view.findViewById(R.id.rvHargaJual)
        etStokToko = view.findViewById(R.id.rvStokToko)
        etStokGudang = view.findViewById(R.id.rvStokGudang)
        etStokMinimum = view.findViewById(R.id.rvStokMinimum)
        etDeskripsi = view.findViewById(R.id.tvDescription)
        tvKategori = view.findViewById(R.id.rvKategori)
        tvMerek = view.findViewById(R.id.chipSemua)
        tvHeader = view.findViewById(R.id.nama_sekolah)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        progressBar = view.findViewById(R.id.loadingBar)
        imageView = view.findViewById(R.id.image)
        tvKodeBarang = view.findViewById(R.id.rvKodeBarang)


        btnDeleteImage = view.findViewById(R.id.btnDeleteImage)

        // MODE EDIT
        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        barangId = arguments?.getInt("id", -1).takeIf { it != -1 }

        if (isEdit) {
            tvHeader.text = "Edit Barang"
            btnSimpan.text = "Simpan Perubahan"
            etStokGudang.isEnabled = false
            loadDetailBarang(barangId!!)
        } else {
            tvHeader.text = "Tambah Barang"
        }

        view.findViewById<View>(R.id.btnback).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        imageView.setOnClickListener { pilihGambar() }
        btnDeleteImage.setOnClickListener { resetGambar() }
        tvKategori.setOnClickListener { pilihKategori() }
        tvMerek.setOnClickListener { pilihMerek() }

        btnSimpan.setOnClickListener {
            if (isEdit) updateBarang()
            else tambahBarang()
        }

        return view
    }

    // -----------------------------------------
    //      LOAD DETAIL BARANG UNTUK EDIT
    // -----------------------------------------
    private fun loadDetailBarang(id: Int) {
        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(id)
            .enqueue(object : Callback<ResEditBarang> {
                override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
                    progressBar.visibility = View.GONE
                    val data = response.body()?.data ?: return

                    // --- SET VALUE KE INPUT ---
                    etNama.setText(data.namaBarang)
                    etHargaBeli.setText(data.hargaBeli.toString())
                    etHargaJual.setText(data.hargaJual.toString())
                    etStokMinimum.setText(data.stokMinimum.toString())
                    etDeskripsi.setText(data.deskripsi)

                    // TAMPILKAN STOK
                    etStokToko.setText(data.stokToko.toString())
                    etStokGudang.setText(data.stokGudang.toString()) // READ ONLY

                    // TAMPILKAN KATEGORI
                    tvKategori.text = data.kategori
                    // BE TIDAK KIRIM KATEGORI ID → tidak bisa update kategori
                    // Jika mau, ubah BE kirim kategoriId

                    // TAMPILKAN MEREK
                    tvMerek.text = data.merek?.namaMerek
                    selectedMerekId = data.merek?.id

                    // TAMPILKAN KODE BARANG (READ ONLY)
                    tvKodeBarang.text = data.kodeBarang

                    // GAMBAR
                    if (!data.imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(data.imageUrl).into(imageView)
                        btnDeleteImage.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    toast("Gagal memuat data barang")
                }
            })
    }


    // -----------------------------------------
    //             TAMBAH BARANG
    // -----------------------------------------
    private fun tambahBarang() {

        if (selectedKategoriId == null) {
            toast("Pilih kategori dulu")
            return
        }
        if (selectedMerekId == null) {
            toast("Pilih merek dulu")
            return
        }

        val kategoriBody = rb(selectedKategoriId.toString())
        val namaBody = rb(etNama.text.toString())
        val merekBody = rb(selectedMerekId.toString())
        val hargaBeliBody = rb(etHargaBeli.text.toString())
        val hargaJualBody = rb(etHargaJual.text.toString())
        val stokTokoBody = rb(etStokToko.text.toString())
        val stokGudangBody = rb(etStokGudang.text.toString())
        val stokMinimumBody = rb(etStokMinimum.text.toString())
        val deskripsiBody = rb(etDeskripsi.text.toString())

        val imagePart = createImagePart()

        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.tambahBarang(
            kategoriBody,
            namaBody,
            merekBody,
            hargaBeliBody,
            hargaJualBody,
            stokTokoBody,
            stokGudangBody,
            stokMinimumBody,
            deskripsiBody,
            imagePart
        ).enqueue(baseTambahCallback)
    }

    // -----------------------------------------
    //             UPDATE BARANG
    // -----------------------------------------
    private fun updateBarang() {

        val namaBody = rbOrNull(etNama.text.toString())
        val merekBody = rbOrNull(selectedMerekId?.toString())
        val hargaBeliBody = rbOrNull(etHargaBeli.text.toString())
        val hargaJualBody = rbOrNull(etHargaJual.text.toString())
        val stokMinimumBody = rbOrNull(etStokMinimum.text.toString())
        val kategoriBody = rbOrNull(selectedKategoriId?.toString())
        val deskripsiBody = rbOrNull(etDeskripsi.text.toString())

        val imagePart = createImagePart()

        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.editBarang(
            barangId!!,
            namaBody,
            merekBody,
            hargaBeliBody,
            hargaJualBody,
            stokMinimumBody,
            deskripsiBody,
            kategoriBody,
            imagePart
        ).enqueue(baseEditCallback)
    }


    // -----------------------------------------
    //           BOTTOMSHEET KATEGORI
    // -----------------------------------------
    private fun pilihKategori() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_kategori, null)
        val rv = view.findViewById<RecyclerView>(R.id.rvKategori)
        rv.layoutManager = LinearLayoutManager(requireContext())

        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(call: Call<ResKategoriList>, response: Response<ResKategoriList>) {
                    val list = response.body()?.data?.filterNotNull() ?: emptyList()

                    rv.adapter = object : RecyclerView.Adapter<KategoriVH>() {
                        override fun onCreateViewHolder(p: ViewGroup, v: Int) =
                            KategoriVH(LayoutInflater.from(p.context)
                                .inflate(android.R.layout.simple_list_item_1, p, false))

                        override fun getItemCount() = list.size

                        override fun onBindViewHolder(h: KategoriVH, pos: Int) {
                            val item = list[pos]
                            h.text.text = item.nama
                            h.itemView.setOnClickListener {
                                selectedKategoriId = item.id
                                tvKategori.text = item.nama

                                autoGenerateKodeBarang()   // WAJIB! => KODE BARANG AKAN MUNCUL DI TEXTVIEW

                                dialog.dismiss()

                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResKategoriList>, t: Throwable) {}
            })

        dialog.setContentView(view)
        dialog.show()
    }

    class KategoriVH(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(android.R.id.text1)
    }
    private fun autoGenerateKodeBarang() {
        if (selectedKategoriId == null) return

        ApiClinet.instance.generateKode(selectedKategoriId!!)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val kode = response.body()?.get("kodeBarang") as? String
                        if (!kode.isNullOrEmpty()) {
                            tvKodeBarang.text = kode
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    toast("Gagal mengambil kode barang")
                }
            })
    }


    // -----------------------------------------
    //             BOTTOMSHEET MEREK
    // -----------------------------------------
    private fun pilihMerek() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ListMerek(emptyList()) {
            selectedMerekId = merekMap[it]
            tvMerek.text = it
            dialog.dismiss()
        }

        rv.adapter = adapter

        ApiClinet.instance.getMerekList()
            .enqueue(object : Callback<ResTampilMerek> {
                override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                    val list = response.body()?.data?.filterNotNull() ?: emptyList()
                    merekMap = list.associate { it.namaMerek!! to it.id!! }
                    adapter.updateData(list.map { it.namaMerek!! })
                }
                override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {}
            })

        dialog.setContentView(view)
        dialog.show()
    }

    // -----------------------------------------
    //           IMAGE HANDLER
    // -----------------------------------------
    private fun pilihGambar() {
        pickImageLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private fun resetGambar() {
        selectedImageUri = null
        imageView.setImageResource(R.drawable.ic_image)
        btnDeleteImage.visibility = View.GONE
    }

    private fun createImagePart(): MultipartBody.Part? {
        return selectedImageUri?.let { uri ->
            val bytes = requireContext().contentResolver.openInputStream(uri)?.readBytes()
            val req = RequestBody.create("image/*".toMediaTypeOrNull(), bytes!!)
            MultipartBody.Part.createFormData("image", "barang.jpg", req)
        }
    }

    // -----------------------------------------
    //           CALLBACKS
    // -----------------------------------------
    private val baseTambahCallback = object : Callback<ResTambahBarang> {
        override fun onResponse(call: Call<ResTambahBarang>, response: Response<ResTambahBarang>) {
            progressBar.visibility = View.GONE
            if (response.isSuccessful) {
                toast("Barang berhasil ditambahkan")
                requireActivity().supportFragmentManager.popBackStack()
            } else toast("Gagal menambah barang")
        }
        override fun onFailure(call: Call<ResTambahBarang>, t: Throwable) {
            progressBar.visibility = View.GONE
            toast("Error: ${t.message}")
        }
    }

    private val baseEditCallback = object : Callback<ResEditBarang> {
        override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
            progressBar.visibility = View.GONE
            if (response.isSuccessful) {
                toast("Barang berhasil diperbarui")
                requireActivity().supportFragmentManager.popBackStack()
            } else toast("Gagal update barang")
        }
        override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
            progressBar.visibility = View.GONE
            toast("Error: ${t.message}")
        }
    }

    // -----------------------------------------
    //     HELPER EXTENSIONS
    // -----------------------------------------
    private fun rb(text: String) =
        RequestBody.create("text/plain".toMediaTypeOrNull(), text)

    private fun rbOrNull(text: String?) =
        text?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
