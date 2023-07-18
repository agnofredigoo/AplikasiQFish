package com.example.qfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qfish.databinding.ActivityHalamanPembukaBinding
import com.example.qfish.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var bind: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.buttonpanduan.setOnClickListener {
            val goToPanduan = Intent(this, panduan::class.java)
            startActivity(goToPanduan)
        }

        bind.buttonscan.setOnClickListener {
            val goToScanPage = Intent(this, ScanPage::class.java)
            startActivity(goToScanPage)
        }
    }
}