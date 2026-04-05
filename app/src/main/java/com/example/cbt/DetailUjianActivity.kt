package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cbt.api.RetrofitClient
import kotlinx.coroutines.launch

class DetailUjianActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_ujian)

        // 1. Handling Window Insets (Agar UI tidak tertutup status bar)
        val rootView = findViewById<View>(R.id.activity_detail_ujian)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 2. Inisialisasi Komponen UI
        val et1 = findViewById<EditText>(R.id.et_code_1)
        val et2 = findViewById<EditText>(R.id.et_code_2)
        val et3 = findViewById<EditText>(R.id.et_code_3)
        val et4 = findViewById<EditText>(R.id.et_code_4)
        val btnMulai = findViewById<Button>(R.id.btn_submit_exam)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar) // Pastikan ada di XML

        // 3. Logika Tombol Back
        btnBack?.setOnClickListener {
            finish()
        }

        // 4. Logika Submit & Validasi Token ke Ktor Backend
        btnMulai?.setOnClickListener {
            val inputToken = "${et1.text}${et2.text}${et3.text}${et4.text}"

            if (inputToken.length < 4) {
                Toast.makeText(this, "Silakan masukkan token 4 digit!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil JWT dari SharedPreferences (disimpan saat Login)
            val sharedPref = getSharedPreferences("CBT_PREF", MODE_PRIVATE)
            val tokenJWT = sharedPref.getString("jwt_token", "") ?: ""

            if (tokenJWT.isEmpty()) {
                Toast.makeText(this, "Sesi habis, silakan login kembali", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jalankan Request API
            lifecycleScope.launch {
                // Tampilkan Loading
                progressBar.visibility = View.VISIBLE
                btnMulai.isEnabled = false

                try {
                    val response = RetrofitClient.instance.checkExamToken("Bearer $tokenJWT", inputToken)

                    if (response.isSuccessful) {
                        val exam = response.body()
                        Toast.makeText(this@DetailUjianActivity, "Berhasil masuk: ${exam?.judul}", Toast.LENGTH_SHORT).show()

                        // PINDAH KE SOAL & Kirim Data
                        val intent = Intent(this@DetailUjianActivity, SoalUjianActivity::class.java)
                        intent.putExtra("EXAM_ID", exam?.id)
                        intent.putExtra("EXAM_TITLE", exam?.judul)
                        startActivity(intent)
                        finish() // Agar user tidak bisa balik ke halaman token saat ujian
                    } else {
                        // Jika token salah (404) atau tidak diizinkan (401/403)
                        Toast.makeText(this@DetailUjianActivity, "Token Ujian Tidak Valid!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Jika Server Ktor mati atau masalah internet
                    Toast.makeText(this@DetailUjianActivity, "Gagal koneksi: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    // Sembunyikan Loading kembali
                    progressBar.visibility = View.GONE
                    btnMulai.isEnabled = true
                }
            }
        }

        // 5. Fungsi Auto-Move Focus Token (User Experience)
        setupTokenAutoMove(et1, et2, et3, et4)
    }

    private fun setupTokenAutoMove(vararg editTexts: EditText) {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
            })
        }
    }
}