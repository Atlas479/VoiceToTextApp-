package com.voicetotext.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.voicetotext.app.adapter.FileListAdapter
import com.voicetotext.app.databinding.ActivityFileManagerBinding
import com.voicetotext.app.utils.FileManager
import java.io.File

class FileManagerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFileManagerBinding
    private lateinit var fileManager: FileManager
    private lateinit var adapter: FileListAdapter
    private var fileList = mutableListOf<File>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        fileManager = FileManager(this)
        setupUI()
        loadFiles()
    }
    
    private fun setupUI() {
        // 设置标题栏
        supportActionBar?.apply {
            title = "文件管理"
            setDisplayHomeAsUpEnabled(true)
        }
        
        // 设置RecyclerView
        adapter = FileListAdapter(fileList) { file, action ->
            when (action) {
                FileListAdapter.Action.VIEW -> viewFile(file)
                FileListAdapter.Action.SHARE -> shareFile(file)
                FileListAdapter.Action.DELETE -> deleteFile(file)
                FileListAdapter.Action.EXPORT -> exportFile(file)
            }
        }
        
        binding.recyclerViewFiles.apply {
            layoutManager = LinearLayoutManager(this@FileManagerActivity)
            adapter = this@FileManagerActivity.adapter
        }
        
        // 刷新按钮
        binding.btnRefresh.setOnClickListener {
            loadFiles()
        }
        
        // 清理旧文件按钮
        binding.btnCleanOld.setOnClickListener {
            cleanOldFiles()
        }
    }
    
    private fun loadFiles() {
        fileList.clear()
        fileList.addAll(fileManager.getAllTextFiles())
        adapter.notifyDataSetChanged()
        
        binding.tvFileCount.text = "共 ${fileList.size} 个文件"
        
        if (fileList.isEmpty()) {
            binding.tvEmptyMessage.visibility = android.view.View.VISIBLE
            binding.recyclerViewFiles.visibility = android.view.View.GONE
        } else {
            binding.tvEmptyMessage.visibility = android.view.View.GONE
            binding.recyclerViewFiles.visibility = android.view.View.VISIBLE
        }
    }
    
    private fun viewFile(file: File) {
        val intent = Intent(this, FileViewActivity::class.java)
        intent.putExtra("file_path", file.absolutePath)
        startActivity(intent)
    }
    
    private fun shareFile(file: File) {
        val content = fileManager.readFileContent(file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "语音转文字 - ${file.name}")
        }
        startActivity(Intent.createChooser(shareIntent, "分享文件"))
    }
    
    private fun deleteFile(file: File) {
        AlertDialog.Builder(this)
            .setTitle("删除文件")
            .setMessage("确定要删除文件 \"${file.name}\" 吗？")
            .setPositiveButton("删除") { _, _ ->
                if (fileManager.deleteFile(file)) {
                    Toast.makeText(this, "文件已删除", Toast.LENGTH_SHORT).show()
                    loadFiles()
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun exportFile(file: File) {
        if (fileManager.exportToDownloads(file)) {
            Toast.makeText(this, "文件已导出到下载目录", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun cleanOldFiles() {
        AlertDialog.Builder(this)
            .setTitle("清理旧文件")
            .setMessage("将删除30天前的文件，确定继续吗？")
            .setPositiveButton("确定") { _, _ ->
                fileManager.cleanOldFiles()
                Toast.makeText(this, "清理完成", Toast.LENGTH_SHORT).show()
                loadFiles()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    override fun onResume() {
        super.onResume()
        loadFiles() // 返回时刷新文件列表
    }
} 