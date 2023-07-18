package com.example.qfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qfish.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.logo.alpha = 0f
        bind.logo.animate().setDuration(1000).alpha(1f).withEndAction {
            val goToHalamanPembuka = Intent(this, HalamanPembuka::class.java)
            startActivity(goToHalamanPembuka)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}