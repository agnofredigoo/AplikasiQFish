package com.example.qfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qfish.databinding.ActivityHalamanPembukaBinding
import com.example.qfish.databinding.ActivityMainBinding

class HalamanPembuka : AppCompatActivity() {

    private lateinit var bind: ActivityHalamanPembukaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHalamanPembukaBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.button.setOnClickListener {
            val goToHomeActivity = Intent(this, HomeActivity::class.java)
            startActivity(goToHomeActivity)
        }
    }
}