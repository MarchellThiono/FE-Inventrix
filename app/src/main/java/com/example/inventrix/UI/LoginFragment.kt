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

        // LOGIN NORMAL
        buttonLogin.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        // LOGIN TAMU
        textTamu.setOnClickListener {
            loginAsGuest()
        }

        return view
    }

    // ---------------------------------------------------------------
    // LOGIN USER (ADMIN / OWNER / GUDANG)
    // ---------------------------------------------------------------
    private fun loginUser(username: String, password: String) {
        val loginRequest = LoginReq(username, password)
        val call = ApiClinet.instance.login(loginRequest)

        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                if (response.isSuccessful) {
                    val loginRes = response.body()

                    if (loginRes != null && loginRes.token != null) {

                        // SIMPAN TOKEN, ROLE, USER_ID
                        val prefs = requireContext()
                            .getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)

                        prefs.edit().apply {
                            putString("TOKEN", loginRes.token)
                            putString("ROLE", loginRes.role)
                            putLong("USER_ID", loginRes.id?.toLong() ?: 0L)   // <--- FIX UTAMA
                            apply()
                        }

                        Log.d("LOGIN_DEBUG", "Token: ${loginRes.token}")
                        Log.d("LOGIN_DEBUG", "Role: ${loginRes.role}")
                        Log.d("LOGIN_DEBUG", "UserID: ${loginRes.id}")

                        Toast.makeText(
                            requireContext(),
                            loginRes.pesan ?: "Login berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        val role = loginRes.role?.trim()?.lowercase()

                        when (role) {
                            "owner", "admin", "role_admin" -> {
                                startActivity(Intent(requireContext(), MainAdminActivity::class.java))
                                requireActivity().finish()
                            }

                            "warehouse", "gudang", "role_gudang" -> {
                                startActivity(Intent(requireContext(), MainActivityGudang::class.java))
                                requireActivity().finish()
                            }

                            else -> {
                                Toast.makeText(requireContext(), "Peran tidak dikenali: $role", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(requireContext(), "Login gagal: token kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    // ---------------------------------------------------------------
    // LOGIN TAMU → MASUK SEBAGAI KARYAWAN
    // ---------------------------------------------------------------
    private fun loginAsGuest() {
        val prefs = requireContext().getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        prefs.edit().clear().apply() // hapus token lama

        val call = ApiClinet.instance.guestLogin()

        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                if (response.isSuccessful) {
                    val loginRes = response.body()

                    if (loginRes != null && loginRes.token != null) {

                        prefs.edit().apply {
                            putString("TOKEN", loginRes.token)
                            putString("ROLE", loginRes.role)
                            putLong("USER_ID", loginRes.id?.toLong() ?: 0L)   // Tamu juga harus punya userId
                            apply()
                        }


                        Log.d("LOGIN_DEBUG", "TAMU Token: ${loginRes.token}")
                        Log.d("LOGIN_DEBUG", "TAMU Role: ${loginRes.role}")

                        Toast.makeText(requireContext(), "Masuk sebagai tamu", Toast.LENGTH_SHORT).show()

                        // pindah ke home karyawan
                        val homeFragment = HomeKaryawanFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame_container, homeFragment)
                            .addToBackStack(null)
                            .commit()

                    } else {
                        Toast.makeText(requireContext(), "Token tamu kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal login tamu (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
