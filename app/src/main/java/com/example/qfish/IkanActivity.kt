package com.example.qfish

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.qfish.databinding.ActivityHomeBinding
import com.example.qfish.databinding.ActivityIkanBinding
import java.io.File

class IkanActivity : AppCompatActivity() {
    private lateinit var bind: ActivityIkanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityIkanBinding.inflate(layoutInflater)
        setContentView(bind.root)

        var selectedImg: Uri? = intent.getParcelableExtra("SELECTED_IMAGE")
        bind.imageView2.setImageURI(selectedImg)

        var label: String? = intent.getStringExtra("LABEL")
        var score: String? = intent.getStringExtra("CONFIDENCE")
        bind.presentase.text = score
        bind.textView.text = label

        if(label != "DITERIMA") {
            bind.textView4.setBackgroundResource(R.drawable.merahmuda)
        }

        bind.scanulang.setOnClickListener {
            val goToScanPage = Intent(this, ScanPage::class.java)
            startActivity(goToScanPage)
        }
    }

}
