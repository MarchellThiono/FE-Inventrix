package com.example.inventrix.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventrix.R
import com.example.inventrix.Model.LoginReq
import com.example.inventrix.Model.LoginRes
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.MainAdminActivity
import com.example.inventrix.UI.Gudang.MainActivityGudang
import com.example.inventrix.UI.Karyawan.HomeKaryawanFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textTamu: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        ApiClinet.init(requireContext())

        inputUsername = view.findViewById(R.id.input_username)
        inputPassword = view.findViewById(R.id.input_KataSandi)
        buttonLogin = view.findViewById(R.id.button_login)
        textTamu = view.findViewById(R.id.tamu)

        // 🔹 Login normal (admin/gudang)
        buttonLogin.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        // 🔹 Login sebagai tamu (karyawan)
        textTamu.setOnClickListener {
            loginAsGuest()
        }

        return view
    }

    // 🧩 Fungsi login normal
    private fun loginUser(username: String, password: String) {
        val loginRequest = LoginReq(username, password)
        val call = ApiClinet.instance.login(loginRequest)

        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                if (response.isSuccessful) {
                    val loginRes = response.body()

                    if (loginRes != null && loginRes.token != null) {
                        val prefs = requireContext().getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
                        with(prefs.edit()) {
                            putString("TOKEN", loginRes.token)
                            putString("ROLE", loginRes.role)
                            apply()
                        }

                        Log.d("LOGIN_DEBUG", "Role: ${loginRes.role}, Token: ${loginRes.token}")
                        Toast.makeText(requireContext(), loginRes.pesan ?: "Login berhasil", Toast.LENGTH_SHORT).show()

                        when (loginRes.role?.trim()?.lowercase()) {
                            "owner", "admin", "role_admin" -> {
                                startActivity(Intent(requireContext(), MainAdminActivity::class.java))
                                requireActivity().finish()
                            }
                            "warehouse", "gudang", "role_gudang" -> {
                                startActivity(Intent(requireContext(), MainActivityGudang::class.java))
                                requireActivity().finish()
                            }
                            else -> {
                                Toast.makeText(requireContext(), "Peran tidak dikenali: ${loginRes.role}", Toast.LENGTH_SHORT).show()
                                Log.w("LOGIN_DEBUG", "Peran tidak dikenali: ${loginRes.role}")
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login gagal: data kosong", Toast.LENGTH_SHORT).show()
                        Log.e("LOGIN_DEBUG", "Response kosong atau token null")
                    }
                } else {
                    Toast.makeText(requireContext(), "Username atau password salah", Toast.LENGTH_SHORT).show()
                    Log.e("LOGIN_DEBUG", "Response gagal: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LOGIN_DEBUG", "onFailure: ${t.message}", t)
            }
        })
    }
    private fun loginAsGuest() {
        val prefs = requireContext().getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        prefs.edit().clear().apply() // 🔥 pastikan token lama dihapus

        val call = ApiClinet.instance.guestLogin()
        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                if (response.isSuccessful) {
                    val loginRes = response.body()
                    if (loginRes != null && loginRes.token != null) {
                        val editor = prefs.edit()
                        editor.putString("TOKEN", loginRes.token)
                        editor.putString("ROLE", loginRes.role)
                        editor.apply()

                        Log.d("LOGIN_DEBUG", "Token guest disimpan: ${loginRes.token}")

                        Toast.makeText(requireContext(), loginRes.pesan ?: "Login tamu berhasil", Toast.LENGTH_SHORT).show()
                        val homeFragment = com.example.inventrix.UI.Karyawan.HomeKaryawanFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame_container, homeFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Login tamu gagal: token kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal login tamu (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LOGIN_DEBUG", "onFailure: ${t.message}", t)
            }
        })
    }

}
