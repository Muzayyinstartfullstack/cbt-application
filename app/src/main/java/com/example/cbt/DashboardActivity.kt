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
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val mainView = findViewById<View>(R.id.main_dashboard)
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val btnMulai = findViewById<Button>(R.id.btnMulaiUjian)
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navHistory = findViewById<ImageView>(R.id.navHistory)
        val navProfile = findViewById<ImageView>(R.id.navProfile)
        val menuMatematika = findViewById<ImageView>(R.id.menu_matematika)

        menuMatematika.setOnClickListener { view ->
            tampilkanMenu(view, "Matematika")
        }

        // Tombol banner atas juga langsung ke DetailUjianActivity
        btnMulai.setOnClickListener {
            bukaDetailUjian("Matematika")
        }

        navHistory.setOnClickListener {
            startActivity(Intent(this, FilterHistoryActivity::class.java))
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            Toast.makeText(this, "Fitur Profil akan segera datang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bukaDetailUjian(namaMapel: String) {
        val intent = Intent(this, DetailUjianActivity::class.java)
        intent.putExtra("NAMA_MAPEL", namaMapel)
        startActivity(intent)
    }

    private fun tampilkanMenu(view: View, namaMapel: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_kerjakan -> {
                    bukaDetailUjian(namaMapel)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}