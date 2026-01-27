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

        // 2. Mengatur padding sistem
        val mainView = findViewById<View>(R.id.main_dashboard)
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 3. Inisialisasi Komponen UI Dasar
        val btnMulai = findViewById<Button>(R.id.btnMulaiUjian)
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navHistory = findViewById<ImageView>(R.id.navHistory)
        val navProfile = findViewById<ImageView>(R.id.navProfile)

        // 4. Inisialisasi Ikon Titik Tiga (More Vert)
        // TAMBAHKAN menuMatematika DI SINI
        val menuMatematika = findViewById<ImageView>(R.id.menu_matematika)
        val menuSejarah = findViewById<ImageView>(R.id.menu_sejarah)
        val menuInggris = findViewById<ImageView>(R.id.menu_inggris)
        val menuIndonesia = findViewById<ImageView>(R.id.menu_indonesia)

        // 5. Logika Klik Menu Titik Tiga (Popup Menu)
        // TAMBAHKAN LOGIKA UNTUK menuMatematika DI SINI
        menuMatematika.setOnClickListener { view ->
            tampilkanMenu(view, "Matematika")
        }
        menuSejarah.setOnClickListener { view ->
            tampilkanMenu(view, "Sejarah")
        }
        menuInggris.setOnClickListener { view ->
            tampilkanMenu(view, "Bahasa Inggris")
        }
        menuIndonesia.setOnClickListener { view ->
            tampilkanMenu(view, "Bahasa Indonesia")
        }

        // 6. Logika Tombol Utama (Banner Matematika Atas)
        btnMulai.setOnClickListener {
            Toast.makeText(this, "Mempersiapkan Soal Matematika...", Toast.LENGTH_SHORT).show()
        }

        // 7. Navigasi Bottom Bar
        navHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            Toast.makeText(this, "Fitur Profil akan segera datang", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fungsi bantuan untuk menampilkan Popup Menu "Kerjakan"
     */
    private fun tampilkanMenu(view: View, namaMapel: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_kerjakan -> {
                    Toast.makeText(this, "Membuka ujian $namaMapel", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}