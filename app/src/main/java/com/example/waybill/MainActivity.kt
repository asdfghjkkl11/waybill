package com.example.waybill

import android.Manifest
import android.R
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startButton = findViewById(R.id.startButton) as Button // Button Boilerplate
        getCameraPermission()

        // 다음 Activity로 넘어가기 위한 onClickListener
        // 이렇게 한 이유는 Permission Check 가
        // 기본적으로 UI Thread가 아닌 다른 Thread 에서 동시에 실행되기 때문에
        // 첫 실행 때, 권한이 없어서 SurfaceView 에서 addCallback 처리를 제대로 못하는 상황이 생긴다.
        // 그래서 검은 화면이 나온다. 고로, 아예 Activity를 다르게 해줬다.
        startButton.setOnClickListener(object : DialogInterface.OnClickListener() {
            fun onClick(v: View?) {
                val goNextActivity = Intent(applicationContext, QRCodeScan::class.java)
                startActivity(goNextActivity)
            }
        })
    }
    private fun getCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            // 권한이 왜 필요한지 설명이 필요한가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                Toast.makeText(this, "카메라 사용을 위해 확인버튼을 눌러주세요!", Toast.LENGTH_SHORT).show()
                true
            } else {
                // 설명이 필요하지 않음.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSIONS_GRANTED
                )
                true
            }
        }
    }
}