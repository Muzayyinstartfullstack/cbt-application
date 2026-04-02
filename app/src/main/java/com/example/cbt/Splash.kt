package com.example.cbt

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {

    // Kita gunakan List untuk menampung animator agar mudah dibersihkan di onDestroy
    private val animators = mutableListOf<ObjectAnimator>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 1. Inisialisasi 3 titik dari XML
        val dot1 = findViewById<View>(R.id.dot1)
        val dot2 = findViewById<View>(R.id.dot2)
        val dot3 = findViewById<View>(R.id.dot3)

        // 2. Jalankan animasi untuk masing-masing titik dengan delay
        startDotAnimation(dot1, 0)
        startDotAnimation(dot2, 200)
        startDotAnimation(dot3, 400)

        // 3. Pindah ke LoginActivity setelah 3 detik (3000ms)
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun startDotAnimation(view: View, delay: Long) {
        // Gabungan animasi Skala (Membesar) dan Alpha (Transparansi)
        val anim = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.3f)
        )

        anim.duration = 600
        anim.startDelay = delay
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.repeatMode = ObjectAnimator.REVERSE

        anim.start()
        animators.add(anim) // Masukkan ke daftar untuk di-cancel nanti
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan semua animasi agar tidak terjadi memory leak
        for (anim in animators) {
            anim.cancel()
        }
        handler.removeCallbacksAndMessages(null)
    }
}