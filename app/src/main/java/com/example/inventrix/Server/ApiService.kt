package com.example.inventrix.Server

import com.example.inventrix.Model.LoginReq
import com.example.inventrix.Model.LoginRes
import com.example.inventrix.Model.ResEditBarang
import com.example.inventrix.Model.ResEditMerek
import com.example.inventrix.Model.ResHapusMerek
import com.example.inventrix.Model.ResTambahBarang
import com.example.inventrix.Model.ResTambahMerek
import com.example.inventrix.Model.ResTampilMerek
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

    // DETAIL barang (untuk DetailFragment & Edit)
    // PENTING: gunakan ResEditBarang, bukan ResTampilDetail!
    @GET("barang/detail/{id}")
    fun getDetailBarang(
        @Path("id") id: Int
    ): Call<ResEditBarang>

    // Edit barang / update
    @Multipart
    @PUT("barang/edit/{id}")
    fun updateBarang(
        @Path("id") id: Int,
        @Part("kodeBarang") kode: RequestBody,
        @Part("namaBarang") nama: RequestBody,
        @Part("merekId") merekId: RequestBody,
        @Part("hargaBeli") hargaBeli: RequestBody,
        @Part("hargaJual") hargaJual: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Call<ResEditBarang>


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
}
