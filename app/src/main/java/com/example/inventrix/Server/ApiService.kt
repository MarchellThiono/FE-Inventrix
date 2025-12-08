package com.example.inventrix.Server

import com.example.inventrix.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ======================================================
    //                     AUTH
    // ======================================================
    @POST("auth/login")
    fun login(@Body loginRequest: LoginReq): Call<LoginRes>

    @POST("auth/guest")
    fun guestLogin(): Call<LoginRes>


    // ======================================================
    //                     BARANG
    // ======================================================
    @Multipart
    @POST("barang/tambah")
    fun tambahBarang(
        @Part("kategoriId") kategoriId: RequestBody,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("merekId") merekId: RequestBody,
        @Part("hargaBeli") hargaBeli: RequestBody,
        @Part("hargaJual") hargaJual: RequestBody,
        @Part("stokToko") stokToko: RequestBody,
        @Part("stokGudang") stokGudang: RequestBody,
        @Part("stokMinimum") stokMinimum: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ResTambahBarang>

    @GET("barang/list")
    fun getBarangList(): Call<TampilBarangRes>

    @GET("barang/detail/{id}")
    fun getDetailBarang(@Path("id") id: Int): Call<ResEditBarang>

    @Multipart
    @PUT("barang/edit/{id}")
    fun editBarang(
        @Path("id") id: Int,
        @Part("namaBarang") namaBarang: RequestBody?,
        @Part("merekId") merekId: RequestBody?,
        @Part("hargaBeli") hargaBeli: RequestBody?,
        @Part("hargaJual") hargaJual: RequestBody?,
        @Part("stokMinimum") stokMinimum: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part("kategoriId") kategoriId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ResEditBarang>

    @DELETE("barang/hapus/{id}")
    fun hapusBarang(@Path("id") id: Int): Call<ResPesan>

    @GET("barang/generate-kode/{kategoriId}")
    fun generateKode(@Path("kategoriId") kategoriId: Int): Call<Map<String, Any>>

    @POST("transaksi/keluar")
    fun createTransaksi(
        @Body body: ReqTransaksiKeluar
    ): Call<ResTransaksiKeluar>


    // ======================================================
    //                     MEREK & KATEGORI
    // ======================================================
    @GET("merek/list")
    fun getMerekList(): Call<ResTampilMerek>

    @FormUrlEncoded
    @POST("merek/tambah")
    fun tambahMerek(@Field("namaMerek") nama: String): Call<ResTambahMerek>

    @FormUrlEncoded
    @PUT("merek/edit/{id}")
    fun editMerek(@Path("id") id: Int, @Field("namaBaru") namaBaru: String): Call<ResEditMerek>

    @DELETE("merek/hapus/{id}")
    fun hapusMerek(@Path("id") id: Int): Call<ResHapusMerek>

    @GET("kategori/list")
    fun getKategoriList(): Call<ResKategoriList>

    @DELETE("kategori/hapus/{id}")
    fun deleteKategori(@Path("id") id: Int): Call<Map<String, Any>>

    @FormUrlEncoded
    @POST("kategori/tambah")
    fun tambahKategori(
        @Field("nama") nama: String,
        @Field("kodeAwal") kodeAwal: String
    ): Call<Map<String, Any>>

    @FormUrlEncoded
    @PUT("kategori/edit/{id}")
    fun updateKategori(
        @Path("id") id: Int,
        @Field("nama") nama: String?,
        @Field("kodeAwal") kodeAwal: String?
    ): Call<Map<String, Any>>

    @GET("kategori/{id}")
    fun getKategoriDetail(@Path("id") id: Int): Call<Map<String, Any>>


    // ======================================================
    //                     LAPORAN
    // ======================================================
    @POST("laporan/create")
    fun createLaporan(@Body body: ReqCreateLaporan): Call<ResPesan>

    @POST("laporan/list")
    fun filterLaporan(@Body req: ReqFilterLaporan): Call<ResLaporanResponse>

    @GET("laporan/detail/{id}")
    fun getDetailRiwayat(@Path("id") id: Long): Call<ResDetailRiwayat>


    // ======================================================
    //                     PERMINTAAN (FINAL)
    // ======================================================

    // ============= OWNER (ADMIN TOKO) =============
    @POST("permintaan/create")
    fun kirimPermintaanGudang(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<ResPesan>

    @GET("permintaan/owner/diminta")
    fun getAdminPermintaanDiminta(): Call<List<ResListPermintaanItem>>

    @GET("permintaan/diproses")
    fun getAdminPermintaanDikirim(): Call<List<ResListPermintaanItem>>

    @GET("permintaan/selesai")
    fun getAdminPermintaanSelesai(): Call<List<ResListPermintaanItem>>

    @POST("permintaan/selesai/{id}")
    fun konfirmasiPermintaanSelesai(@Path("id") id: Int): Call<ResPesan>

    @DELETE("permintaan/hapus/{id}")
    fun hapusPermintaan(@Path("id") id: Int): Call<ResPesan>

    @GET("permintaan/detail/{id}")
    fun getDetailPermintaan(@Path("id") id: Int): Call<ResDetailPermintaan>


    // ============= WAREHOUSE (GUDANG) =============
    @GET("permintaan/warehouse/diminta")
    fun getPermintaanGudangDiminta(): Call<List<ResListPermintaanItem>>

    @POST("permintaan/kirim/{id}")
    fun kirimBarang(@Path("id") id: Int): Call<ResPesan>

    @GET("permintaan/diproses")
    fun getPermintaanGudangDikirim(): Call<List<ResListPermintaanItem>>


    // ======================================================
    //                     NOTIFIKASI
    // ======================================================
    @GET("api/notifikasi/peringatan/{userId}")
    fun getPeringatan(@Path("userId") userId: Long): Call<List<Notifikasi>>

    @GET("api/notifikasi/pemberitahuan/{userId}")
    fun getPemberitahuan(@Path("userId") userId: Long): Call<List<Notifikasi>>

    @DELETE("api/notifikasi/delete/{id}")
    fun deleteNotifikasi(@Path("id") id: Long): Call<ResPesan>
}
