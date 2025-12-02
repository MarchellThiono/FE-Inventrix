package com.example.inventrix.Server

import com.example.inventrix.Model.LoginReq
import com.example.inventrix.Model.LoginRes
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.Model.ReqCreateLaporan
import com.example.inventrix.Model.ReqFilterLaporan
import com.example.inventrix.Model.ReqTransaksiKeluar
import com.example.inventrix.Model.ResDetailRiwayat
import com.example.inventrix.Model.ResEditBarang
import com.example.inventrix.Model.ResEditMerek
import com.example.inventrix.Model.ResHapusMerek
import com.example.inventrix.Model.ResLaporanResponse
import com.example.inventrix.Model.ResListPermintaanItem
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.Model.ResTambahBarang
import com.example.inventrix.Model.ResTambahMerek
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.ResTransaksiKeluar
import com.example.inventrix.Model.TampilBarangRes
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

    // Tambah barang
    @Multipart
    @POST("barang/tambah")
    fun tambahBarang(
        @Part("kodeBarang") kodeBarang: RequestBody,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("merekId") merekId: RequestBody,
        @Part("hargaBeli") hargaBeli: RequestBody,
        @Part("hargaJual") hargaJual: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ResTambahBarang>

    // List barang
    @GET("barang/list")
    fun getBarangList(): Call<TampilBarangRes>

    // DETAIL barang (Edit)
    @GET("barang/detail/{id}")
    fun getDetailBarang(
        @Path("id") id: Int
    ): Call<ResEditBarang>

    // UPDATE BARANG (EDIT)
    @Multipart
    @PUT("barang/edit/{id}")
    fun editBarang(
        @Path("id") id: Int,
        @Part("kodeBarang") kodeBarang: RequestBody,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("merekId") merekId: RequestBody,
        @Part("hargaBeli") hargaBeli: RequestBody,
        @Part("hargaJual") hargaJual: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ResEditBarang>

    @POST("transaksi/keluar")
    fun createTransaksi(
        @Body body: ReqTransaksiKeluar
    ): Call<ResTransaksiKeluar>



    // ======================================================
    //                     MEREK
    // ======================================================

    @GET("merek/list")
    fun getMerekList(): Call<ResTampilMerek>

    @FormUrlEncoded
    @POST("merek/tambah")
    fun tambahMerek(
        @Field("namaMerek") nama: String
    ): Call<ResTambahMerek>


    @FormUrlEncoded
    @PUT("merek/edit/{id}")
    fun editMerek(
        @Path("id") id: Int,
        @Field("namaBaru") namaBaru: String
    ): Call<ResEditMerek>

    @DELETE("merek/hapus/{id}")
    fun hapusMerek(
        @Path("id") id: Int
    ): Call<ResHapusMerek>

    // ======================================================
    //                     Laporan
    // ======================================================

    @POST("laporan/create")
    fun createLaporan(
        @Body body: ReqCreateLaporan
    ): Call<ResPesan>

    @POST("laporan/list")
    fun filterLaporan(
        @Body req: ReqFilterLaporan
    ): Call<ResLaporanResponse>

    @GET("laporan/detail/{id}")
    fun getDetailRiwayat(
        @Path("id") id: Long
    ): Call<ResDetailRiwayat>
// ======================================================
//                     PERMINTAAN
// ======================================================

    // 1. Admin kirim permintaan ke gudang
    @POST("permintaan/create")
    fun kirimPermintaanGudang(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<ResPesan>

    // 2. Gudang melihat permintaan status DIPINTA
    @GET("permintaan/warehouse/diminta")
    fun getPermintaanGudangDiminta(): Call<List<ResListPermintaanItem>>

    // 3. Gudang melihat permintaan status DIKIRIM
    @GET("permintaan/dikirim")
    fun getPermintaanGudangDikirim(): Call<List<ResListPermintaanItem>>

    // 4. Gudang konfirmasi & kirim barang
    @POST("permintaan/kirim/{id}")
    fun kirimBarang(
        @Path("id") id: Int
    ): Call<ResPesan>



// =======================
// ADMIN - LIHAT PERMINTAAN
// =======================
    // Admin lihat permintaan yang masih DIPINTA (baru dibuat admin)
    @GET("permintaan/owner/diminta")
    fun getAdminPermintaanDiminta(): Call<List<ResListPermintaanItem>>

    // Admin lihat permintaan yang SUDAH DIKIRIM oleh gudang
    @GET("permintaan/dikirim")
    fun getAdminPermintaanDikirim(): Call<List<ResListPermintaanItem>>

    @POST("permintaan/selesai/{id}")
    fun konfirmasiPermintaanSelesai(
        @Path("id") id: Int
    ): Call<ResPesan>




// ======================================================
//                  NOTIFIKASI
// ======================================================

    @GET("api/notifikasi/list/{userId}")
    fun getNotifikasi(
        @Path("userId") userId: Long
    ): Call<List<Notifikasi>>

    @DELETE("api/notifikasi/delete/{id}")
    fun deleteNotifikasi(
        @Path("id") id: Long
    ): Call<ResPesan>

}
