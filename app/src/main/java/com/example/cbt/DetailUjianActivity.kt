package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_detail_ujian)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Terima data dari Dashboard
        val namaMapel = intent.getStringExtra("NAMA_MAPEL") ?: "Ujian"

        // Setup input kode otomatis pindah focus
        val et1 = findViewById<EditText>(R.id.et_code_1)
        val et2 = findViewById<EditText>(R.id.et_code_2)
        val et3 = findViewById<EditText>(R.id.et_code_3)
        val et4 = findViewById<EditText>(R.id.et_code_4)

        autoMoveToNext(et1, et2)
        autoMoveToNext(et2, et3)
        autoMoveToNext(et3, et4)
        autoMoveToNext(et4, null)

        // Tombol back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Tombol mulai ujian
        findViewById<Button>(R.id.btn_submit_exam).setOnClickListener {
            val kode = "${et1.text}${et2.text}${et3.text}${et4.text}"

            if (kode.length < 4) {
                Toast.makeText(this, "Masukkan kode ujian lengkap!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: validasi kode ke backend, untuk sekarang langsung pindah
            Toast.makeText(this, "Memulai ujian $namaMapel...", Toast.LENGTH_SHORT).show()

            // Kalau udah ada ExamActivity, uncomment ini:
            // val intent = Intent(this, ExamActivity::class.java)
            // intent.putExtra("NAMA_MAPEL", namaMapel)
            // intent.putExtra("KODE_UJIAN", kode)
            // startActivity(intent)
        }
    }

    private fun autoMoveToNext(current: EditText, next: EditText?) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    next?.requestFocus()
                }
            }
        })
    }
}