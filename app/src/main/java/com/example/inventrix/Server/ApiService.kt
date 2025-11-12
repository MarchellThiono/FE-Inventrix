package com.example.inventrix.Server

import com.example.inventrix.Model.LoginReq
import com.example.inventrix.Model.LoginRes
import com.example.inventrix.Model.ReqTambahBarang
import com.example.inventrix.Model.ResTambahBarang
import com.example.inventrix.Model.ResTampilDetail
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginReq): Call<LoginRes>

    @POST("auth/guest")
    fun guestLogin(): Call<LoginRes>

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

    @GET("barang/list")
    fun getBarangList(): Call<TampilBarangRes>

    @GET("barang/detail/{id}")
    fun getDetailBarang(
        @Path("id") id: Int
    ): Call<ResTampilDetail>

    @GET ("merek/list")
    fun getMerekList(): Call<ResTampilMerek>
}