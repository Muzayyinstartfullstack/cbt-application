package com.example.cbt

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {

    private lateinit var tvDots: TextView
    private lateinit var animator: ObjectAnimator
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvDots = findViewById(R.id.tvDots)

        // Animasi fade in - fade out untuk "..."
        animator = ObjectAnimator.ofFloat(tvDots, "alpha", 0.2f, 1f)
        animator.duration = 500
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()

        // Pindah ke LoginActivity setelah 3 detik
        handler.postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (::animator.isInitialized) {
            animator.cancel()
        }
    }
}
