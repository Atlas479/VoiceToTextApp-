package com.voicetotext.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.voicetotext.app.databinding.ActivityMainBinding
import com.voicetotext.app.service.VoiceRecognitionService
import com.voicetotext.app.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var recordingState = RecordingState.STOPPED
    private var currentFileName = ""
    
    enum class RecordingState {
        STOPPED, RECORDING, PAUSED
    }
    
    private val textUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val text = intent?.getStringExtra("text") ?: ""
            viewModel.updateRecognizedText(text)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        setupUI()
        setupObservers()
        checkPermissions()
        
        // 注册广播接收器
        val filter = IntentFilter("com.voicetotext.app.TEXT_RECOGNIZED")
        registerReceiver(textUpdateReceiver, filter, RECEIVER_NOT_EXPORTED)
    }
    
    private fun setupUI() {
        binding.apply {
            // 开始录音按钮
            btnStart.setOnClickListener {
                when (recordingState) {
                    RecordingState.STOPPED -> showFileNameDialog()
                    RecordingState.PAUSED -> resumeRecording()
                    else -> {}
                }
            }
            
            // 暂停录音按钮
            btnPause.setOnClickListener {
                if (recordingState == RecordingState.RECORDING) {
                    pauseRecording()
                }
            }
            
            // 停止并保存按钮
            btnStopSave.setOnClickListener {
                if (recordingState != RecordingState.STOPPED) {
                    showSaveDialog()
                }
            }
            
            // 查看文件按钮
            btnViewFiles.setOnClickListener {
                startActivity(Intent(this@MainActivity, FileManagerActivity::class.java))
            }
            
            // 清除当前文本按钮
            btnClearText.setOnClickListener {
                clearCurrentText()
            }
        }
        
        updateUI()
    }
    
    private fun setupObservers() {
        viewModel.recognizedText.observe(this) { text ->
            binding.tvRecognizedText.text = text
        }
        
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showFileNameDialog() {
        val editText = EditText(this).apply {
            hint = "请输入文件名（如：数学课第一章）"
            setText("课程录音_${System.currentTimeMillis()}")
        }
        
        AlertDialog.Builder(this)
            .setTitle("新建录音文件")
            .setMessage("请为这次录音命名：")
            .setView(editText)
            .setPositiveButton("开始录音") { _, _ ->
                val fileName = editText.text.toString().trim()
                if (fileName.isNotEmpty()) {
                    currentFileName = fileName
                    startRecording()
                } else {
                    Toast.makeText(this, "请输入文件名", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showSaveDialog() {
        val editText = EditText(this).apply {
            setText(currentFileName)
        }
        
        AlertDialog.Builder(this)
            .setTitle("保存录音文件")
            .setMessage("确认文件名：")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val finalFileName = editText.text.toString().trim()
                if (finalFileName.isNotEmpty()) {
                    stopAndSaveRecording(finalFileName)
                } else {
                    Toast.makeText(this, "请输入文件名", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("继续录音", null)
            .show()
    }
    
    private fun startRecording() {
        if (hasPermissions()) {
            val intent = Intent(this, VoiceRecognitionService::class.java)
            intent.action = VoiceRecognitionService.ACTION_START_RECORDING
            intent.putExtra("file_name", currentFileName)
            startForegroundService(intent)
            
            recordingState = RecordingState.RECORDING
            viewModel.clearCurrentText()
            updateUI()
        } else {
            checkPermissions()
        }
    }
    
    private fun pauseRecording() {
        val intent = Intent(this, VoiceRecognitionService::class.java)
        intent.action = VoiceRecognitionService.ACTION_PAUSE_RECORDING
        startService(intent)
        
        recordingState = RecordingState.PAUSED
        updateUI()
    }
    
    private fun resumeRecording() {
        val intent = Intent(this, VoiceRecognitionService::class.java)
        intent.action = VoiceRecognitionService.ACTION_RESUME_RECORDING
        startService(intent)
        
        recordingState = RecordingState.RECORDING
        updateUI()
    }
    
    private fun stopAndSaveRecording(fileName: String) {
        val intent = Intent(this, VoiceRecognitionService::class.java)
        intent.action = VoiceRecognitionService.ACTION_STOP_RECORDING
        intent.putExtra("final_file_name", fileName)
        startService(intent)
        
        recordingState = RecordingState.STOPPED
        currentFileName = ""
        updateUI()
        
        Toast.makeText(this, "录音已保存为：$fileName", Toast.LENGTH_LONG).show()
    }
    
    private fun clearCurrentText() {
        AlertDialog.Builder(this)
            .setTitle("清除文本")
            .setMessage("确定要清除当前显示的文本吗？（不会影响已保存的内容）")
            .setPositiveButton("清除") { _, _ ->
                binding.tvRecognizedText.text = ""
                viewModel.clearCurrentText()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun updateUI() {
        binding.apply {
            when (recordingState) {
                RecordingState.STOPPED -> {
                    btnStart.text = "开始新录音"
                    btnStart.isEnabled = true
                    btnStart.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
                    
                    btnPause.text = "暂停"
                    btnPause.isEnabled = false
                    btnPause.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    
                    btnStopSave.text = "停止并保存"
                    btnStopSave.isEnabled = false
                    btnStopSave.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    
                    tvStatus.text = "准备就绪，点击开始新录音"
                    tvFileName.text = "当前文件：无"
                    progressBar.visibility = android.view.View.GONE
                }
                
                RecordingState.RECORDING -> {
                    btnStart.text = "录音中..."
                    btnStart.isEnabled = false
                    btnStart.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    
                    btnPause.text = "暂停"
                    btnPause.isEnabled = true
                    btnPause.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_orange_dark))
                    
                    btnStopSave.text = "停止并保存"
                    btnStopSave.isEnabled = true
                    btnStopSave.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
                    
                    tvStatus.text = "正在录音中..."
                    tvFileName.text = "当前文件：$currentFileName"
                    progressBar.visibility = android.view.View.VISIBLE
                }
                
                RecordingState.PAUSED -> {
                    btnStart.text = "继续录音"
                    btnStart.isEnabled = true
                    btnStart.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
                    
                    btnPause.text = "已暂停"
                    btnPause.isEnabled = false
                    btnPause.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    
                    btnStopSave.text = "停止并保存"
                    btnStopSave.isEnabled = true
                    btnStopSave.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
                    
                    tvStatus.text = "录音已暂停"
                    tvFileName.text = "当前文件：$currentFileName"
                    progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    private fun checkPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(this@MainActivity, "权限已授予，可以开始使用", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "需要权限才能使用录音功能", Toast.LENGTH_LONG).show()
                    }
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
    
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(textUpdateReceiver)
        if (recordingState != RecordingState.STOPPED) {
            val intent = Intent(this, VoiceRecognitionService::class.java)
            intent.action = VoiceRecognitionService.ACTION_STOP_RECORDING
            startService(intent)
        }
    }
} 