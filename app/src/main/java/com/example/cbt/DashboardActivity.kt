package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Mengaktifkan fitur tampilan penuh (Edge-to-Edge)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // 2. Mengatur padding sistem agar tidak tertutup status bar
        val mainView = findViewById<View>(R.id.main_dashboard)
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 3. Inisialisasi Komponen UI
        val btnMulai = findViewById<Button>(R.id.btnMulaiUjian)
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navHistory = findViewById<ImageView>(R.id.navHistory)
        val navProfile = findViewById<ImageView>(R.id.navProfile)

        // 5. Logika Tombol Utama (Banner Matematika) -> PINDAH KE DETAIL
        btnMulai.setOnClickListener {
            val intent = Intent(this, DetailUjianActivity::class.java)
            startActivity(intent)
        }

        // 6. Navigasi Bottom Bar
        navHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fungsi untuk menampilkan Popup Menu dan Navigasi ke Detail
     */
    private fun tampilkanMenu(view: View, namaMapel: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_kerjakan -> {
                    // Pindah ke halaman Detail Ujian
                    val intent = Intent(this, DetailUjianActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}