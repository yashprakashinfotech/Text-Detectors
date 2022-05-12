package com.example.textdetectors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnCaptureImage : Button
    private lateinit var txtDetectText : TextView
    private lateinit var imgCapture : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        btnCaptureImage.setOnClickListener {
            val i = Intent(this,ScannerActivity::class.java)
            startActivity(i)
        }
    }

    private fun initView(){
        btnCaptureImage = findViewById(R.id.btnCaptureImage)

        txtDetectText = findViewById(R.id.txtWelcome)
        imgCapture = findViewById(R.id.imgDetect)
    }
}