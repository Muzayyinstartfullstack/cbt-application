package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailUjianActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_ujian)

        // 1. Handling Window Insets (Biar gak kepotong status bar)
        val rootView = findViewById<View>(R.id.activity_detail_ujian)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 2. Inisialisasi Komponen Token
        val et1 = findViewById<EditText>(R.id.et_code_1)
        val et2 = findViewById<EditText>(R.id.et_code_2)
        val et3 = findViewById<EditText>(R.id.et_code_3)
        val et4 = findViewById<EditText>(R.id.et_code_4)

        // 3. Inisialisasi Tombol Navigasi
        val btnMulai = findViewById<Button>(R.id.btn_submit_exam)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        // 4. Logika Pindah ke SoalUjianActivity
        btnMulai?.setOnClickListener {
            val token = "${et1?.text}${et2?.text}${et3?.text}${et4?.text}"

            if (token.length < 4) {
                Toast.makeText(this, "Silakan masukkan token dengan lengkap!", Toast.LENGTH_SHORT).show()
            } else {
                // PINDAH KE SOAL
                val intent = Intent(this, SoalUjianActivity::class.java)
                startActivity(intent)
                // finish() // Aktifkan ini jika ingin user tidak bisa kembali ke halaman token
            }
        }

        btnBack?.setOnClickListener {
            finish()
        }

        // 5. Fungsi Auto-Move Focus Token
        fun autoMoveToNext(current: EditText?, next: EditText?) {
            current?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        next?.requestFocus()
                    }
                }
            })
        }

        autoMoveToNext(et1, et2)
        autoMoveToNext(et2, et3)
        autoMoveToNext(et3, et4)
        autoMoveToNext(et4, null)
    }
}