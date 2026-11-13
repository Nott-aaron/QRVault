package com.example.qrvault.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qrvault.R
import com.example.qrvault.databinding.ActivityScannerBinding
import com.example.qrvault.utils.QRCodeGenerator
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding
    private var isTorchOn = false
    private var isScanned = false

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startScanning()
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startScanning() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (result != null && !isScanned) {
                    val rawText = result.text
                    if (!rawText.isNull_or_blank()) {
                        isScanned = true
                        handleScannedResult(rawText)
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    private fun handleScannedResult(rawText: String) {
        val scannedContact = QRCodeGenerator.parseVCard(rawText)

        val intent = Intent(this, ScannedResultActivity::class.java).apply {
            putExtra("SCANNED_CONTACT_EXTRA", scannedContact)
            putExtra("RAW_CONTENT_EXTRA", rawText)
        }
        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnFlash.setOnClickListener {
            if (isTorchOn) {
                binding.barcodeScanner.setTorchOff()
                binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                isTorchOn = false
            } else {
                binding.barcodeScanner.setTorchOn()
                binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                isTorchOn = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.barcodeScanner.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }
}