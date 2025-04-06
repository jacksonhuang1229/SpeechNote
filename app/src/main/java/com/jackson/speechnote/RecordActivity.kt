package com.jackson.speechnote

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jackson.speechnote.databinding.ActivityRecordBinding
import java.io.File
import java.io.IOException

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isRecording = false
    private var isPlaying = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 確保錄音檔案目錄存在
        val audioDir = File(externalCacheDir, "audio").apply { 
            if (!exists()) mkdirs() 
        }
        audioFilePath = File(audioDir, "audiorecord.mp3").absolutePath

        setupButtons()
        updateButtonStates()
    }

    private fun setupButtons() {
        binding.btnRecord.setOnClickListener {
            if (checkPermission()) {
                if (!isRecording) startRecording() else stopRecording()
            } else {
                requestPermission()
            }
        }

        binding.btnPlay.setOnClickListener {
            if (!isPlaying) startPlaying() else stopPlaying()
        }

        binding.btnStop.setOnClickListener {
            if (isRecording) stopRecording()
            if (isPlaying) stopPlaying()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                
                try {
                    prepare()
                    start()
                    isRecording = true
                    binding.btnRecord.text = "停止錄音"
                    binding.chronometer.base = SystemClock.elapsedRealtime()
                    binding.chronometer.start()
                    Toast.makeText(this@RecordActivity, "開始錄音", Toast.LENGTH_SHORT).show()
                    android.util.Log.d("RecordActivity", "錄音開始，檔案路徑: $audioFilePath")
                } catch (e: IOException) {
                    e.printStackTrace()
                    android.util.Log.e("RecordActivity", "錄音準備失敗: ${e.message}")
                    Toast.makeText(this@RecordActivity, "錄音準備失敗: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("RecordActivity", "錄音初始化失敗: ${e.message}")
            Toast.makeText(this, "錄音初始化失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        updateButtonStates()
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            binding.btnRecord.text = "開始錄音"
            binding.chronometer.stop()
            Toast.makeText(this, "錄音已儲存", Toast.LENGTH_SHORT).show()
            android.util.Log.d("RecordActivity", "錄音已儲存到: $audioFilePath")
            
            // 檢查檔案是否存在和大小
            val file = File(audioFilePath.toString())
            if (file.exists()) {
                android.util.Log.d("RecordActivity", "錄音檔案大小: ${file.length()} bytes")
            } else {
                android.util.Log.e("RecordActivity", "錄音檔案不存在")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("RecordActivity", "停止錄音失敗: ${e.message}")
            Toast.makeText(this, "停止錄音失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        updateButtonStates()
    }

    private fun startPlaying() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
                this@RecordActivity.isPlaying = true
                binding.btnPlay.text = "暫停播放"
                Toast.makeText(this@RecordActivity, "開始播放", Toast.LENGTH_SHORT).show()
            }
            mediaPlayer?.setOnCompletionListener {
                stopPlaying()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "播放失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        updateButtonStates()
    }

    private fun stopPlaying() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            this@RecordActivity.isPlaying = false
            binding.btnPlay.text = "播放錄音"
            Toast.makeText(this, "播放停止", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "停止播放失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        updateButtonStates()
    }

    private fun updateButtonStates() {
        binding.btnPlay.isEnabled = !isRecording && File(audioFilePath.toString()).exists()
        binding.btnStop.isEnabled = isRecording || isPlaying
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "錄音權限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "請授予錄音權限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) stopRecording()
        if (isPlaying) stopPlaying()
    }
} 