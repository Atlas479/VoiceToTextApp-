package com.voicetotext.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import com.voicetotext.app.MainActivity
import com.voicetotext.app.R
import com.voicetotext.app.utils.FileManager
import java.text.SimpleDateFormat
import java.util.*

class VoiceRecognitionService : Service() {
    
    companion object {
        const val ACTION_START_RECORDING = "START_RECORDING"
        const val ACTION_PAUSE_RECORDING = "PAUSE_RECORDING"
        const val ACTION_RESUME_RECORDING = "RESUME_RECORDING"
        const val ACTION_STOP_RECORDING = "STOP_RECORDING"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "VoiceRecognitionChannel"
    }
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecording = false
    private var isPaused = false
    private lateinit var fileManager: FileManager
    private var currentSessionText = StringBuilder()
    private var currentFileName = ""
    private var sessionStartTime = ""
    
    override fun onCreate() {
        super.onCreate()
        fileManager = FileManager(this)
        createNotificationChannel()
        initializeSpeechRecognizer()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                val fileName = intent.getStringExtra("file_name") ?: "未命名录音"
                startRecording(fileName)
            }
            ACTION_PAUSE_RECORDING -> pauseRecording()
            ACTION_RESUME_RECORDING -> resumeRecording()
            ACTION_STOP_RECORDING -> {
                val finalFileName = intent.getStringExtra("final_file_name") ?: currentFileName
                stopRecording(finalFileName)
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "课程录音服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "课程录音转文字后台服务"
                setSound(null, null)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(status: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("课程录音转文字")
            .setContentText("$status - $currentFileName")
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
    
    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 准备就绪
            }
            
            override fun onBeginningOfSpeech() {
                // 开始说话
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // 音量变化
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // 接收到音频数据
            }
            
            override fun onEndOfSpeech() {
                // 说话结束
                if (isRecording && !isPaused) {
                    // 重新开始监听
                    restartListening()
                }
            }
            
            override fun onError(error: Int) {
                // 发生错误时重新开始监听
                if (isRecording && !isPaused) {
                    restartListening()
                }
            }
            
            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                    if (matches.isNotEmpty() && !isPaused) {
                        val recognizedText = matches[0]
                        handleRecognizedText(recognizedText)
                    }
                }
                
                if (isRecording && !isPaused) {
                    restartListening()
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                // 部分结果
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                // 其他事件
            }
        })
    }
    
    private fun startRecording(fileName: String) {
        if (!isRecording) {
            isRecording = true
            isPaused = false
            currentFileName = fileName
            currentSessionText.clear()
            sessionStartTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            
            // 创建新的会话文件
            fileManager.createNewSessionFile(fileName)
            
            startForeground(NOTIFICATION_ID, createNotification("正在录音"))
            startListening()
        }
    }
    
    private fun pauseRecording() {
        if (isRecording && !isPaused) {
            isPaused = true
            speechRecognizer?.stopListening()
            
            // 添加暂停标记
            val pauseText = "\n[${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}] === 录音暂停 ===\n"
            currentSessionText.append(pauseText)
            fileManager.appendToCurrentFile(pauseText)
            
            // 更新通知
            val notification = createNotification("已暂停")
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
    
    private fun resumeRecording() {
        if (isRecording && isPaused) {
            isPaused = false
            
            // 添加继续标记
            val resumeText = "[${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}] === 录音继续 ===\n"
            currentSessionText.append(resumeText)
            fileManager.appendToCurrentFile(resumeText)
            
            // 更新通知
            val notification = createNotification("正在录音")
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
            
            startListening()
        }
    }
    
    private fun stopRecording(finalFileName: String) {
        if (isRecording) {
            isRecording = false
            isPaused = false
            speechRecognizer?.stopListening()
            
            // 保存最终文件
            saveSessionFile(finalFileName)
            
            stopForeground(true)
            stopSelf()
        }
    }
    
    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.startListening(intent)
    }
    
    private fun restartListening() {
        if (isRecording && !isPaused) {
            // 延迟重新开始，避免过于频繁
            android.os.Handler(mainLooper).postDelayed({
                if (isRecording && !isPaused) {
                    startListening()
                }
            }, 500)
        }
    }
    
    private fun handleRecognizedText(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val formattedText = "[$timestamp] $text\n"
        
        currentSessionText.append(formattedText)
        
        // 实时保存到文件
        fileManager.appendToCurrentFile(formattedText)
        
        // 发送广播更新UI
        val intent = Intent("com.voicetotext.app.TEXT_RECOGNIZED")
        intent.putExtra("text", currentSessionText.toString())
        sendBroadcast(intent)
    }
    
    private fun saveSessionFile(finalFileName: String) {
        if (currentSessionText.isNotEmpty()) {
            val sessionEndTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val sessionFooter = "\n=== 录音结束 $sessionEndTime ===\n" +
                    "录音时长：从 $sessionStartTime 到 $sessionEndTime\n" +
                    "文件名：$finalFileName\n\n"
            
            fileManager.appendToCurrentFile(sessionFooter)
            
            // 如果最终文件名与当前文件名不同，重命名文件
            if (finalFileName != currentFileName) {
                fileManager.renameCurrentFile(finalFileName)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        isRecording = false
        isPaused = false
    }
} 