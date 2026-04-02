package com.example.cbt

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailHasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hasil)

        // --- KODE LAMA LO (Inisialisasi View) ---
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvDetailMapel = findViewById<TextView>(R.id.tvDetailMapel)
        val progressCircle = findViewById<ProgressBar>(R.id.progressCircle)
        // ... (lanjutkan inisialisasi view lainnya sesuai kode lo sebelumnya)

        // 5. Click Listeners
        btnBack.setOnClickListener { finish() }

        // CONTOH: Trigger munculin filter (misal klik tvDetailMapel)
        tvDetailMapel.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_history, null)

        // 1. Inisialisasi View dari layout dialog (Harus pakai view.findViewById)
        val layoutLulus = view.findViewById<RelativeLayout>(R.id.layoutLulus)
        val layoutGagal = view.findViewById<RelativeLayout>(R.id.layoutGagal)
        val layoutRemed = view.findViewById<RelativeLayout>(R.id.layoutRemed)

        val checkLulus = view.findViewById<ImageView>(R.id.checkLulus)
        val checkGagal = view.findViewById<ImageView>(R.id.checkGagal)
        val checkRemed = view.findViewById<ImageView>(R.id.checkRemed)

        val uncheckGagal = view.findViewById<View>(R.id.uncheckGagal)
        val uncheckRemed = view.findViewById<View>(R.id.uncheckRemed)

        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)

        // 2. Fungsi Update Centang
        fun updateStatus(pilihan: String) {
            // Reset: Sembunyiin semua centang, tampilin semua bulatan kosong
            checkLulus.visibility = View.INVISIBLE

            checkGagal.visibility = View.GONE
            uncheckGagal.visibility = View.VISIBLE

            checkRemed.visibility = View.GONE
            uncheckRemed.visibility = View.VISIBLE

            when (pilihan) {
                "lulus" -> checkLulus.visibility = View.VISIBLE
                "gagal" -> {
                    checkGagal.visibility = View.VISIBLE
                    uncheckGagal.visibility = View.GONE
                }
                "remed" -> {
                    checkRemed.visibility = View.VISIBLE
                    uncheckRemed.visibility = View.GONE
                }
            }
        }

        // 3. Set Klik Listener pada Baris
        layoutLulus.setOnClickListener { updateStatus("lulus") }
        layoutGagal.setOnClickListener { updateStatus("gagal") }
        layoutRemed.setOnClickListener { updateStatus("remed") }

        btnTerapkan.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(view)
        dialog.show()
    }
}