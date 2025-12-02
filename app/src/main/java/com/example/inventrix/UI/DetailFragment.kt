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
import com.example.inventrix.Model.ResEditBarang
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DetailFragment : Fragment() {

    private var role: String? = null

    private lateinit var ivLogo: ImageView
    private lateinit var tvKodeBarang: TextView
    private lateinit var tvNamaBarang: TextView
    private lateinit var tvMerek: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvStok: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var tvText : TextView
    private lateinit var layoutDeskripsi: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var menuBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Baca ROLE dari SharedPreferences yang sama seperti di LoginFragment ("APP_PREF")
        role = getRoleFromPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // Inisialisasi Views
        ivLogo = view.findViewById(R.id.ivLogo)
        tvKodeBarang = view.findViewById(R.id.Kode_barang)
        tvNamaBarang = view.findViewById(R.id.Name_barang)
        tvMerek = view.findViewById(R.id.tvMerek)
        tvHarga = view.findViewById(R.id.tvHarga)
        tvStok = view.findViewById(R.id.tvStok)
        tvDeskripsi = view.findViewById(R.id.tvDescription)
        layoutDeskripsi = view.findViewById(R.id.tvdeslabel)
        progressBar = view.findViewById(R.id.progressBar)
        menuBack = view.findViewById(R.id.menu_back)
        tvText = view.findViewById(R.id.text)

        menuBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Ambil ID dari bundle
        val idBarang = arguments?.getInt("id", -1) ?: -1
        if (idBarang != -1) {
            loadDetailBarang(idBarang)
        }

        return view
    }

    private fun loadDetailBarang(id: Int) {
        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(id).enqueue(object : Callback<ResEditBarang> {
            override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
                progressBar.visibility = View.GONE

                if (!response.isSuccessful || response.body()?.data == null) {
                    Toast.makeText(requireContext(), "Gagal memuat detail barang", Toast.LENGTH_SHORT).show()
                    return
                }

                val data = response.body()!!.data!!
                val r = role?.lowercase()?.trim()

                // Data dasar
                tvNamaBarang.text = "Nama Barang : ${data.namaBarang ?: "-"}"
                tvKodeBarang.text = "Kode Barang : ${data.kodeBarang ?: "-"}"
                tvMerek.text = "Merek : ${data.merek?.namaMerek}"

                // Role-based UI
                if (r == "gudang" || r == "warehouse" || r == "role_gudang") {
                    // Gudang: sembunyikan harga & deskripsi, tunjukkan stok gudang
                    tvHarga.visibility = View.GONE
                    tvText.visibility =View.GONE
                    layoutDeskripsi.visibility = View.GONE
                    tvStok.text = "Stok Gudang : ${data.stokGudang ?: 0}"
                } else {
                    // Karyawan / tamu: tunjukkan harga, stok toko, deskripsi
                    tvHarga.visibility = View.VISIBLE
                    // Harga: pilih yang sudah diformat dari backend jika ada, kalau tidak format sendiri
                    val hargaFix = when {
                        !data.hargaJualFormatted.isNullOrEmpty() -> data.hargaJualFormatted
                        data.hargaJual != null -> formatHarga(data.hargaJual)
                        else -> "-"
                    }
                    tvHarga.text = "Harga : Rp $hargaFix"

                    tvStok.text = "Stok Toko : ${data.stokToko ?: 0}"
                    layoutDeskripsi.visibility = View.VISIBLE
                    tvDeskripsi.text = data.deskripsi ?: "-"
                }

                // Gambar (boleh berupa path relatif atau absolute)
                val imageUrl = data.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    // jika imageUrl dimulai dgn '/', mungkin server menaruh relatif; Glide masih bisa menangani
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(ivLogo)
                } else {
                    ivLogo.setImageResource(R.drawable.ic_image)
                }
            }

            override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Kesalahan koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Mengubah Any? (angka dari backend bisa berupa Int / Double / String) -> String terformat
     * Contoh hasil: 12.345 -> "12.345"
     */
    private fun formatHarga(harga: Any?): String {
        if (harga == null) return "-"
        return try {
            // coba jadi Long atau Double, fallback ke parsing String
            val angkaLong = when (harga) {
                is Number -> harga.toLong()
                is String -> {
                    // hapus .0 kalau ada
                    val cleaned = harga.replace(".0", "")
                    cleaned.toLongOrNull() ?: cleaned.toDoubleOrNull()?.toLong() ?: 0L
                }
                else -> harga.toString().replace(".0", "").toLongOrNull() ?: 0L
            }
            // format dengan pemisah ribuan menggunakan locale (ganti '.' sebagai pemisah)
            val nf = NumberFormat.getIntegerInstance(Locale.US) // gunakan US untuk get grouping
            nf.format(angkaLong).replace(",", ".")
        } catch (e: Exception) {
            "-"
        }
    }

    private fun getRoleFromPreferences(): String? {
        // Gunakan PREF yang sama seperti di LoginFragment: "APP_PREF"
        val prefs = requireContext().getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        return prefs.getString("ROLE", null)
    }
}
