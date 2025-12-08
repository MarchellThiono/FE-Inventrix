package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListKategori
import com.example.inventrix.Adapter.ListKelolaDataBarang
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.ResKategoriList
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.*

class KelolaDataBarangFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var chipKategori: TextView
    private lateinit var chipMerek: TextView
    private lateinit var btnTambah: ImageView
    private lateinit var btnMenu: ImageView
    private lateinit var adapter: ListKelolaDataBarang

    private var allBarangList = listOf<DataItem>()
    private var kategoriTerpilih: String? = null
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_kelola_data_barang, container, false)

        progressBar = v.findViewById(R.id.progressBar)
        recyclerView = v.findViewById(R.id.rvbarangadmin)
        searchView = v.findViewById(R.id.searchViewBarang)
        chipKategori = v.findViewById(R.id.chipKategori)
        chipMerek = v.findViewById(R.id.chipSemua)
        btnTambah = v.findViewById(R.id.btnTmbh)
        btnMenu = v.findViewById(R.id.menu)

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        setupBackButton(v)
        setupChipKategori()
        setupTambahButton()

        btnMenu.setOnClickListener { showPopupMenu(it) }

        loadBarang()
        return v
    }

    private fun setupBackButton(view: View) {
        view.findViewById<ImageView>(R.id.btnback).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // =======================================
    //           SETUP RECYCLER VIEW
    // =======================================
    private fun setupRecyclerView() {
        adapter = ListKelolaDataBarang(
            emptyList(),

            // === EDIT BARANG ===
            listenerEdit = { barang ->
                val frag = TambahBarangFragment()
                val bundle = Bundle()
                bundle.putBoolean("isEdit", true)
                bundle.putInt("id", barang.id!!)
                frag.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container_main_menu, frag)
                    .addToBackStack(null)
                    .commit()
            },

            // === DETAIL BARANG ===
            listenerDetail = { barang ->
                val frag = DetailAdminFragment()
                val bundle = Bundle()
                bundle.putInt("id", barang.id!!)
                frag.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container_main_menu, frag)
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    // =======================================
    //             TAMBAH BARANG
    // =======================================
    private fun setupTambahButton() {
        btnTambah.setOnClickListener {
            val frag = TambahBarangFragment()
            val bundle = Bundle()
            bundle.putBoolean("isEdit", false)
            frag.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main_menu, frag)
                .addToBackStack(null)
                .commit()
        }
    }

    // =======================================
    //             LOAD BARANG
    // =======================================
    private fun loadBarang() {
        progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getBarangList()
            .enqueue(object : Callback<TampilBarangRes> {
                override fun onResponse(call: Call<TampilBarangRes>, res: Response<TampilBarangRes>) {
                    progressBar.visibility = View.GONE
                    res.body()?.data?.let {
                        allBarangList = it.filterNotNull()
                        adapter.updateData(allBarangList)
                    }
                }

                override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // =======================================
    //             SEARCH BAR
    // =======================================
    private fun setupSearchBar() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { filterBarang(q) }
            override fun onQueryTextChange(q: String?) = true.also { filterBarang(q) }
        })
    }

    // =======================================
    //             BOTTOMSHEET MEREK
    // =======================================
    private fun setupChipMerek() {
        chipMerek.setOnClickListener { tampilkanBottomSheetMerek() }
    }

    private fun tampilkanBottomSheetMerek() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)

        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        val title = view.findViewById<TextView>(R.id.titleMerek)

        rv.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Loading sementara
        val loadingAdapter = ListMerek(listOf("Memuat...")) { }
        rv.adapter = loadingAdapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(
                call: Call<ResTampilMerek>,
                res: Response<ResTampilMerek>
            ) {
                val list = mutableListOf("Semua Merek")
                list += res.body()?.data?.mapNotNull { it?.namaMerek } ?: emptyList()

                val adapterMerek = ListMerek(list) { merek ->
                    merekTerpilih = if (merek == "Semua Merek") null else merek
                    chipMerek.text = merek
                    filterBarang(searchView.query.toString())
                    dialog.dismiss()
                }

                rv.adapter = adapterMerek
                title.text = "Pilih Merek"
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                title.text = "Gagal memuat"
                Toast.makeText(requireContext(), "Gagal memuat merek", Toast.LENGTH_SHORT).show()
            }
        })

        dialog.setContentView(view)
        dialog.show()
    }


    // =======================================
    //           BOTTOMSHEET KATEGORI
    // =======================================
    private fun setupChipKategori() {
        chipKategori.setOnClickListener { tampilkanBottomSheetKategori() }
    }

    private fun tampilkanBottomSheetKategori() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_kategori, null)

        val rv = view.findViewById<RecyclerView>(R.id.rvKategori)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val loadingAdapter = ListKategori(listOf("Memuat...")) { }
        rv.adapter = loadingAdapter

        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(call: Call<ResKategoriList>, res: Response<ResKategoriList>) {

                    val list = mutableListOf("Semua Kategori")
                    list += res.body()?.data?.mapNotNull { it?.nama } ?: emptyList()

                    val kategoriAdapter = ListKategori(list) { kategori ->
                        kategoriTerpilih = if (kategori == "Semua Kategori") null else kategori
                        chipKategori.text = kategori
                        filterBarang(searchView.query.toString())
                        dialog.dismiss()
                    }

                    rv.adapter = kategoriAdapter
                }

                override fun onFailure(call: Call<ResKategoriList>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                }
            })

        dialog.setContentView(view)
        dialog.show()
    }

    // =======================================
    //              FILTER BARANG
    // =======================================
    private fun filterBarang(query: String?) {
        var filtered = allBarangList

        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.namaBarang.orEmpty().contains(query, true) ||
                        it.kodeBarang.orEmpty().contains(query, true)
            }
        }

        merekTerpilih?.let { m ->
            filtered = filtered.filter { it.merek == m }
        }

        kategoriTerpilih?.let { k ->
            filtered = filtered.filter { it.kategori == k }
        }

        adapter.updateData(filtered)
    }

    // =======================================
    //              POPUP MENU
    // =======================================
    private fun showPopupMenu(anchor: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_kelola, null)

        val popupWindow = PopupWindow(
            popupView,
            250.dp(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation = 20f
        popupWindow.isOutsideTouchable = true

        popupView.findViewById<TextView>(R.id.kelolaMerek).setOnClickListener {
            popupWindow.dismiss()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main_menu, KelolaMerekFragment())
                .addToBackStack(null)
                .commit()
        }

        popupView.findViewById<TextView>(R.id.kelolaKategori).setOnClickListener {
            popupWindow.dismiss()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main_menu, KelolaKategoriFragment())
                .addToBackStack(null)
                .commit()
        }

        popupWindow.showAsDropDown(anchor, 20.dp(), 10.dp())
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
