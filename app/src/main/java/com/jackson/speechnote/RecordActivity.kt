package com.jackson.speechnote

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jackson.speechnote.databinding.ActivityRecordBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sin

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isRecording = false
    private var isPlaying = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
        private const val SAMPLE_RATE = 44100
        private const val DURATION_SECONDS = 2
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

        // 生成測試音頻
        generateTestAudio()

        setupButtons()
        updateButtonStates()
    }

    private fun generateTestAudio() {
        try {
            val testAudioFile = File(externalCacheDir, "test_audio.wav")  // 改用WAV格式
            if (!testAudioFile.exists()) {
                // 生成一個簡單的正弦波音頻
                val numSamples = SAMPLE_RATE * DURATION_SECONDS
                val sample = DoubleArray(numSamples)
                val frequency = 440.0 // 440 Hz (A4音)

                for (i in 0 until numSamples) {
                    sample[i] = sin(2.0 * Math.PI * i.toDouble() * frequency / SAMPLE_RATE)
                }

                // 轉換為16位PCM
                val pcmData = ByteArray(numSamples * 2)
                for (i in 0 until numSamples) {
                    val value = (sample[i] * 32767).toInt()
                    pcmData[i * 2] = (value and 0xFF).toByte()
                    pcmData[i * 2 + 1] = (value shr 8).toByte()
                }

                // 寫入WAV文件
                FileOutputStream(testAudioFile).use { output ->
                    // 寫入WAV文件頭
                    writeWavHeader(output, numSamples)
                    // 寫入PCM數據
                    output.write(pcmData)
                }
                android.util.Log.d("RecordActivity", "測試音頻已生成: ${testAudioFile.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("RecordActivity", "生成測試音頻失敗: ${e.message}")
        }
    }

    private fun writeWavHeader(output: FileOutputStream, numSamples: Int) {
        // WAV文件頭
        val header = ByteArray(44)
        
        // RIFF頭
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        
        // 文件大小 (不包括頭部44字節)
        val fileSize = numSamples * 2 + 36
        header[4] = (fileSize and 0xFF).toByte()
        header[5] = (fileSize shr 8 and 0xFF).toByte()
        header[6] = (fileSize shr 16 and 0xFF).toByte()
        header[7] = (fileSize shr 24 and 0xFF).toByte()
        
        // WAVE標識
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        
        // fmt子塊
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        
        // fmt子塊大小
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        
        // 音頻格式 (1 = PCM)
        header[20] = 1
        header[21] = 0
        
        // 聲道數 (1 = 單聲道)
        header[22] = 1
        header[23] = 0
        
        // 採樣率
        header[24] = (SAMPLE_RATE and 0xFF).toByte()
        header[25] = (SAMPLE_RATE shr 8 and 0xFF).toByte()
        header[26] = (SAMPLE_RATE shr 16 and 0xFF).toByte()
        header[27] = (SAMPLE_RATE shr 24 and 0xFF).toByte()
        
        // 字節率
        val byteRate = SAMPLE_RATE * 2
        header[28] = (byteRate and 0xFF).toByte()
        header[29] = (byteRate shr 8 and 0xFF).toByte()
        header[30] = (byteRate shr 16 and 0xFF).toByte()
        header[31] = (byteRate shr 24 and 0xFF).toByte()
        
        // 塊對齊
        header[32] = 2
        header[33] = 0
        
        // 位深度
        header[34] = 16
        header[35] = 0
        
        // data子塊
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        
        // 數據大小
        val dataSize = numSamples * 2
        header[40] = (dataSize and 0xFF).toByte()
        header[41] = (dataSize shr 8 and 0xFF).toByte()
        header[42] = (dataSize shr 16 and 0xFF).toByte()
        header[43] = (dataSize shr 24 and 0xFF).toByte()
        
        output.write(header)
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

        binding.btnTestAudio.setOnClickListener {
            playTestAudio()
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

    private fun playTestAudio() {
        try {
            val testAudioFile = File(externalCacheDir, "test_audio.wav")  // 改用WAV格式
            if (testAudioFile.exists()) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(testAudioFile.absolutePath)
                    prepare()
                    start()
                    this@RecordActivity.isPlaying = true
                    binding.btnPlay.text = "暫停播放"
                    Toast.makeText(this@RecordActivity, "開始播放測試音檔", Toast.LENGTH_SHORT).show()
                }
                mediaPlayer?.setOnCompletionListener {
                    stopPlaying()
                }
            } else {
                Toast.makeText(this, "測試音檔不存在", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "播放測試音檔失敗: ${e.message}", Toast.LENGTH_SHORT).show()
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