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
import com.example.cbt.api.TokenRequest // Import model request baru
import kotlinx.coroutines.launch

class DetailUjianActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_ujian)

        // 1. Insets Handling (Sesuaikan ID root layout XML lo)
        val rootView = findViewById<View>(R.id.activity_detail_ujian)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 2. Inisialisasi View
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnMulai = findViewById<Button>(R.id.btn_submit_exam)
        val et1 = findViewById<EditText>(R.id.et_code_1)
        val et2 = findViewById<EditText>(R.id.et_code_2)
        val et3 = findViewById<EditText>(R.id.et_code_3)
        val et4 = findViewById<EditText>(R.id.et_code_4)
        val tvMapel = findViewById<TextView>(R.id.tv_mapel_detail)

        btnBack.setOnClickListener { finish() }

        // 3. Logic Auto-Move Focus
        setupTokenAutoMove(et1, et2, et3, et4)

        // 4. Logic Tombol Mulai
        btnMulai.setOnClickListener {
            val inputToken = "${et1.text}${et2.text}${et3.text}${et4.text}".trim()
            android.util.Log.d("CBT_DEBUG", "Token yang dikirim: '$inputToken'")

            if (inputToken.length < 4) {
                Toast.makeText(this, "Masukkan kode ujian lengkap!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnMulai.isEnabled = false
                btnMulai.text = "Memverifikasi..."

                try {
                    // PANGGIL TANPA JWT (Sesuai Routing.kt dummy kita tadi)
                    val request = TokenRequest(token = inputToken)
                    val response = RetrofitClient.instance.checkExamToken(request)

                    if (response.isSuccessful && response.body() != null) {
                        val exam = response.body()!!

                        Toast.makeText(this@DetailUjianActivity, "Berhasil: ${exam.judul}", Toast.LENGTH_SHORT).show()

                        // Pindah ke SoalUjianActivity
                        val intent = Intent(this@DetailUjianActivity, SoalUjianActivity::class.java)
                        intent.putExtra("EXAM_ID", exam.id)
                        intent.putExtra("EXAM_TITLE", exam.judul)
                        startActivity(intent)
                        finish()
                    } else {
                        // Jika 400 (Token Salah)
                        Toast.makeText(this@DetailUjianActivity, "Kode Ujian Salah!", Toast.LENGTH_SHORT).show()
                        clearTokenFields(et1, et2, et3, et4)
                    }
                } catch (e: Exception) {
                    // Jika Server Mati / IP Salah
                    Toast.makeText(this@DetailUjianActivity, "Server Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnMulai.isEnabled = true
                    btnMulai.text = "Mulai Ujian Sekarang →"
                }
            }
        }
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

    private fun clearTokenFields(vararg editTexts: EditText) {
        for (et in editTexts) {
            et.text.clear()
        }
        editTexts[0].requestFocus()
    }
}