package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog


class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_history)

        // --- 1. INISIALISASI CARDVIEW ---
        val cardMath = findViewById<CardView>(R.id.cardMath)
        val cardHistory = findViewById<CardView>(R.id.cardHistory)
        val cardEnglish = findViewById<CardView>(R.id.cardEnglish)
        val cardIndo = findViewById<CardView>(R.id.cardIndo) // <-- Tambah ini

        // --- 2. LOGIKA KLIK PINDAH KE DETAIL ---

        cardMath?.setOnClickListener {
            pindahKeDetail("Matematika", 86)
        }

        cardHistory?.setOnClickListener {
            pindahKeDetail("Sejarah", 75)
        }

        cardEnglish?.setOnClickListener {
            pindahKeDetail("B. Inggris", 43)
        }

        cardIndo?.setOnClickListener { // <-- Tambah blok ini
            pindahKeDetail("B. Indonesia", 92)
        }

        // --- 3. LOGIKA FILTER & NAVIGASI ---

        val btnFilter = findViewById<ImageView>(R.id.btnFilter)
        btnFilter?.setOnClickListener {
            showFilterDialog()
        }

        val btnHome = findViewById<ImageView>(R.id.btnHomeNav)
        btnHome?.setOnClickListener {
            finish()
        }
    }

    private fun pindahKeDetail(namaMapel: String, skor: Int) {
        val intent = Intent(this, DetailHasilActivity::class.java)
        intent.putExtra("MAPEL", namaMapel)
        intent.putExtra("SKOR", skor)
        startActivity(intent)
    }

    private fun showFilterDialog() {
        try {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.layout_filter_history, null)

<<<<<<< HEAD
            val btnClose = view.findViewById<ImageView>(R.id.btnClose)
            val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)
            val tvAturUlang = view.findViewById<TextView>(R.id.tvAturUlangAtas)
=======
        val btnHariIni = view.findViewById<Button>(R.id.btnHariIni)
        val btn7Hari = view.findViewById<Button>(R.id.btn7Hari)
        val btn30Hari = view.findViewById<Button>(R.id.btn30Hari)
        val btnKustom = view.findViewById<Button>(R.id.btnKustom)
        //val chipGroupMapel = view.findViewById<ChipGroup>(R.id.chipGroupMapel)
        val radioGroupStatus = view.findViewById<RadioGroup>(R.id.radioGroupStatus)
        val rangeSlider = view.findViewById<RangeSlider>(R.id.rangeSliderNilai)
        val txtRangeValue = view.findViewById<TextView>(R.id.txtRangeValue)
        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)
        val btnResetBawah = view.findViewById<Button>(R.id.btnResetBawah)
>>>>>>> c0231c1a8f1813dfe9d308953b8d81351764f836

            btnClose?.setOnClickListener { dialog.dismiss() }
            tvAturUlang?.setOnClickListener {
                Toast.makeText(this, "Filter diatur ulang", Toast.LENGTH_SHORT).show()
            }
            btnTerapkan?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat filter", Toast.LENGTH_SHORT).show()
        }
<<<<<<< HEAD
=======

        // 3. Reset Function
        btnResetBawah.setOnClickListener {
            dateButtons.forEach {
                it.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                it.setTextColor(Color.BLACK)
            }
            btnHariIni.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0097B2")))
            btnHariIni.setTextColor(Color.WHITE)
            view.findViewById<Chip>(R.id.chipSemua).isChecked = true
            radioGroupStatus.clearCheck()
            rangeSlider.setValues(0f, 100f)
            btnKustom.text = "Kustom"
        }

        // 4. Terapkan
        //btnTerapkan.setOnClickListener {
          //  val mapel = view.findViewById<Chip>(chipGroupMapel.checkedChipId)?.text ?: "Semua"
          //  val status = when(radioGroupStatus.checkedRadioButtonId) {
            //    R.id.rbLulus -> "Lulus"
            //    R.id.rbGagal -> "Gagal"
            //    R.id.rbRemedial -> "Remedial"
            //    else -> "None"
            //}
            //Toast.makeText(this, "Filter: $mapel, Status: $status", Toast.LENGTH_SHORT).show()
            //dialog.dismiss()
        //}

        dialog.setContentView(view)
        dialog.show()
>>>>>>> c0231c1a8f1813dfe9d308953b8d81351764f836
    }
}