package com.example.inventrix.UI

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.inventrix.Model.ResTampilDetail
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment() {

    private var role: String? = null

    // View references
    private lateinit var ivLogo: ImageView
    private lateinit var tvKodeBarang: TextView
    private lateinit var tvNamaBarang: TextView
    private lateinit var tvMerek: TextView
    private lateinit var tvTipe: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvStok: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var layoutDeskripsi: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var menuBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = getRoleFromPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // 🔹 Inisialisasi semua View
        ivLogo = view.findViewById(R.id.ivLogo)
        tvKodeBarang = view.findViewById(R.id.Kode_barang)
        tvNamaBarang = view.findViewById(R.id.Name_barang)
        tvMerek = view.findViewById(R.id.tvMerek)
        tvTipe = view.findViewById(R.id.tvTipe)
        tvHarga = view.findViewById(R.id.tvHarga)
        tvStok = view.findViewById(R.id.tvStok)
        tvDeskripsi = view.findViewById(R.id.tvDescription)
        layoutDeskripsi = view.findViewById(R.id.tvdeslabel)
        progressBar = view.findViewById(R.id.progressBar)
        menuBack = view.findViewById(R.id.menu_back)

        // 🔹 Ambil ID dari arguments
        val idBarang = arguments?.getInt("id", -1) ?: -1
        if (idBarang != -1) {
            loadDetailBarang(idBarang)
        } else {
            Toast.makeText(requireContext(), "ID barang tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Tombol back
        menuBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadDetailBarang(id: Int) {
        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(id).enqueue(object : Callback<ResTampilDetail> {
            override fun onResponse(
                call: Call<ResTampilDetail>,
                response: Response<ResTampilDetail>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.data != null) {
                    val data = response.body()!!.data

                    tvNamaBarang.text = "Nama Barang : ${data?.namaBarang ?: "-"}"
                    tvKodeBarang.text = "Kode Barang : ${data?.kodeBarang ?: "-"}"
                    tvMerek.text = "Merek : ${data?.merek ?: "-"}"
                    tvTipe.text = "Tipe : ${data?.id ?: "-"}"
                    tvHarga.text = "Harga : Rp ${data?.hargaJual ?: "-"}"

                    // 🔹 Tampilkan stok sesuai role
                    val stokText = if (role == "gudang") {
                        "Stok Gudang : ${data?.stokGudang ?: 0}"
                    } else {
                        "Stok Toko : ${data?.stokToko ?: 0}"
                    }
                    tvStok.text = stokText

                    // 🔹 Tampilkan deskripsi kalau bukan gudang
                    if (role == "gudang") {
                        layoutDeskripsi.visibility = View.GONE
                    } else {
                        layoutDeskripsi.visibility = View.VISIBLE
                        tvDeskripsi.text = data?.deskripsi ?: "-"
                    }

                    // 🔹 Load gambar dari backend
                    Glide.with(requireContext())
                        .load(data?.imageUrl)
                        .into(ivLogo)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat detail barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResTampilDetail>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Kesalahan koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRoleFromPreferences(): String? {
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sharedPref.getString("role", null)
    }
}
