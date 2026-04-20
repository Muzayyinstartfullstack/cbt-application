package com.example.cbt



import android.os.Bundle

import android.widget.ImageButton

import android.widget.ProgressBar

import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import android.widget.LinearLayout



class DetailHasilActivity : AppCompatActivity() {



    private lateinit var tvSubject: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var tvBenar: TextView
    private lateinit var tvSalah: TextView
    private lateinit var tvPeringkat: TextView
    private lateinit var circularProgress: ProgressBar
    // View analisisContainer tidak ada di layout - di-comment out
    // private var analisisContainer: LinearLayout? = null
    private lateinit var btnKembali: android.widget.Button



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_hasil)



        // Inisialisasi views
        tvSubject = findViewById(R.id.tvSubject)
        tvPercent = findViewById(R.id.tvPercent)
        tvWaktu = findViewById(R.id.tvWaktu)
        circularProgress = findViewById(R.id.circularProgress)
        btnKembali = findViewById(R.id.btnKembali)



        // Ambil data dari Intent

        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Matematika"

        val percent = intent.getIntExtra("PERCENT", 0)

        val duration = intent.getStringExtra("DURATION") ?: "0m"



        // Set data ke tampilan

        tvSubject.text = subjectName

        tvPercent.text = percent.toString()

        tvWaktu.text = duration

        circularProgress.progress = percent



        // Tombol kembali

        btnKembali.setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }

    }



    // Data class untuk analisis topik (disediakan untuk fitur mendatang)
    data class TopicAnalysis(
        val topicName: String,
        val percentage: Int
    ) : java.io.Serializable

}