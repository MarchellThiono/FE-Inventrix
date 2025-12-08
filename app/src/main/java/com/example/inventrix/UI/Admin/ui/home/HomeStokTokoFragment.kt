package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListBarangStokToko
import com.example.inventrix.Adapter.ListKategori
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.ResKategoriList
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.*

class HomeStokTokoFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var chipKategori: TextView
    private lateinit var chipMerek: TextView
    private lateinit var rvMode: ImageView
    private lateinit var modeTitle: TextView
    private lateinit var btnKonfirmasi: Button
    private lateinit var adapter: ListBarangStokToko

    private var allBarangList = listOf<DataItem>()
    private var kategoriTerpilih: String? = null
    private var merekTerpilih: String? = null
    private var modeDipilih: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val v = inflater.inflate(R.layout.fragment_home_stok_toko, container, false)

        progressBar = v.findViewById(R.id.progressBar)
        recyclerView = v.findViewById(R.id.rvbarangadmin)
        searchView = v.findViewById(R.id.searchViewBarang)
        chipKategori = v.findViewById(R.id.chipKategori)
        chipMerek = v.findViewById(R.id.chipSemua)
        rvMode = v.findViewById(R.id.rvMode)
        modeTitle = v.findViewById(R.id.mode)
        btnKonfirmasi = v.findViewById(R.id.btnKofirmasi)

        setupRecyclerView()
        setupModeChooser()
        setupSearchBar()
        setupChipMerek()
        setupChipKategori()
        setupBackButton(v)
        setupKonfirmasi()
        loadBarang()

        return v
    }

    override fun onResume() {
        super.onResume()

        if (modeDipilih != null) {
            restoreModeUI()
            adapter.syncFromManager(PermintaanManager.getList())
        } else {
            setInitialUI()
        }
    }

    private fun setInitialUI() {
        adapter.modeDipilih = null
        adapter.resetAllCounters()

        rvMode.visibility = View.VISIBLE
        modeTitle.visibility = View.GONE
        btnKonfirmasi.visibility = View.GONE

        val navBar = requireActivity().findViewById<View>(R.id.navBar)
        navBar?.visibility = View.VISIBLE
    }

    private fun restoreModeUI() {
        rvMode.visibility = View.GONE
        modeTitle.visibility = View.VISIBLE
        modeTitle.text = modeDipilih

        adapter.modeDipilih = modeDipilih
        adapter.notifyDataSetChanged()

        val navBar = requireActivity().findViewById<View>(R.id.navBar)
        navBar?.visibility = View.GONE

        btnKonfirmasi.visibility =
            if (PermintaanManager.getList().isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupBackButton(view: View) {
        val btnBack = view.findViewById<ImageView>(R.id.btnback)
        btnBack.setOnClickListener {
            if (modeDipilih != null) {
                modeDipilih = null
                PermintaanManager.clearAll()
                setInitialUI()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupModeChooser() {
        rvMode.setOnClickListener { tampilkanBottomSheetMode() }
    }

    private fun tampilkanBottomSheetMode() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_mode, null)

        val listView = view.findViewById<ListView>(R.id.listMode)
        val modeList = listOf("Permintaan Stok", "Penyesuaian Stok")

        val adapterMode = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modeList)
        listView.adapter = adapterMode

        listView.setOnItemClickListener { _, _, pos, _ ->

            val modeBaru = modeList[pos]

            // ✅ FIX DATA NEMPEL SAAT GANTI MODE
            if (modeDipilih != modeBaru) {
                PermintaanManager.clearAll()
                adapter.resetAllCounters()
            }

            modeDipilih = modeBaru
            restoreModeUI()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupRecyclerView() {
        adapter = ListBarangStokToko(
            emptyList(),

            onRequestCountChanged = { total ->
                btnKonfirmasi.visibility =
                    if (modeDipilih != null && total > 0) View.VISIBLE else View.GONE
            },

            onDetailClick = { barang ->   // ⬅️ KLIK CARD → DETAIL
                val frag = DetailAdminFragment()
                val b = Bundle()
                b.putInt("id", barang.id!!)
                frag.arguments = b

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.containerStokToko, frag)
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }


    private fun setupKonfirmasi() {
        btnKonfirmasi.setOnClickListener {
            val frag = PermintaanAdminFragment()
            val b = Bundle()
            b.putString("mode", modeDipilih)
            frag.arguments = b

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.containerStokToko, frag)
                .addToBackStack(null)
                .commit()
        }
    }

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

    private fun setupSearchBar() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { filterBarang(q) }
            override fun onQueryTextChange(q: String?) = true.also { filterBarang(q) }
        })
    }

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

    private fun setupChipKategori() {
        chipKategori.setOnClickListener {
            tampilkanBottomSheetKategori()
        }
    }
    private fun tampilkanBottomSheetKategori() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_kategori, null)

        val rv = view.findViewById<RecyclerView>(R.id.rvKategori)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Tampilkan loading sementara (opsional)
        val loadingAdapter = ListKategori(listOf("Memuat...")) { }
        rv.adapter = loadingAdapter

        // Ambil data kategori dari server
        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(
                    call: Call<ResKategoriList>,
                    res: Response<ResKategoriList>
                ) {
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

        adapter.updateData(filtered)
    }
}
