package com.example.waybill

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException


class QRCodeScan : Activity() {
    var cameraSource: CameraSource? = null
    var cameraSurface: SurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scan)
        cameraSurface =
            findViewById<View>(R.id.cameraSurface) as SurfaceView // SurfaceView 선언 :: Boilerplate
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE) // QR_CODE로 설정하면 좀더 빠르게 인식할 수 있습니다.
            .build()
        Log.d("NowStatus", "BarcodeDetector Build Complete")
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedFps(29.8f) // 프레임 높을 수록 리소스를 많이 먹겠죠
            .setRequestedPreviewSize(1080, 1920) // 확실한 용도를 잘 모르겠음. 필자는 핸드폰 크기로 설정
            .setAutoFocusEnabled(true) // AutoFocus를 안하면 초점을 못 잡아서 화질이 많이 흐립니다.
            .build()
        Log.d("NowStatus", "CameraSource Build Complete")

        // Callback을 이용해서 SurfaceView를 실시간으로 Mobile Vision API와 연결
        cameraSurface!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {   // try-catch 문은 Camera 권한획득을 위한 권장사항
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(cameraSurface!!.holder) // Mobile Vision API 시작
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop() // SurfaceView가 종료되었을 때, Mobile Vision API 종료
                Log.d("NowStatus", "SurfaceView Destroyed and CameraSource Stopped")
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Log.d("NowStatus", "BarcodeDetector SetProcessor Released")
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                // 바코드가 인식되었을 때 무슨 일을 할까?
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    val barcodeContents =
                        barcodes.valueAt(0).displayValue // 바코드 인식 결과물
                    Log.d("Detection", barcodeContents)
                }
            }
        })
    }
}