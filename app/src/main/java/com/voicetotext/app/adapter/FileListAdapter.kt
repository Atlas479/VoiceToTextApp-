package com.voicetotext.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voicetotext.app.R
import com.voicetotext.app.utils.FileManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileListAdapter(
    private val files: List<File>,
    private val onItemAction: (File, Action) -> Unit
) : RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {
    
    enum class Action {
        VIEW, SHARE, DELETE, EXPORT
    }
    
    private val fileManager = FileManager(null) // 只用于工具方法
    
    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvFileInfo: TextView = itemView.findViewById(R.id.tvFileInfo)
        val tvFileDate: TextView = itemView.findViewById(R.id.tvFileDate)
        val btnView: ImageButton = itemView.findViewById(R.id.btnView)
        val btnShare: ImageButton = itemView.findViewById(R.id.btnShare)
        val btnExport: ImageButton = itemView.findViewById(R.id.btnExport)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        
        // 显示文件名（去掉扩展名和前缀）
        val displayName = getDisplayName(file)
        holder.tvFileName.text = displayName
        
        // 显示文件信息
        val fileSize = getFormattedFileSize(file)
        holder.tvFileInfo.text = "大小: $fileSize"
        
        // 显示修改时间
        val modifiedTime = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            .format(Date(file.lastModified()))
        holder.tvFileDate.text = modifiedTime
        
        // 设置按钮点击事件
        holder.btnView.setOnClickListener {
            onItemAction(file, Action.VIEW)
        }
        
        holder.btnShare.setOnClickListener {
            onItemAction(file, Action.SHARE)
        }
        
        holder.btnExport.setOnClickListener {
            onItemAction(file, Action.EXPORT)
        }
        
        holder.btnDelete.setOnClickListener {
            onItemAction(file, Action.DELETE)
        }
        
        // 整个项目点击查看文件
        holder.itemView.setOnClickListener {
            onItemAction(file, Action.VIEW)
        }
    }
    
    override fun getItemCount(): Int = files.size
    
    private fun getDisplayName(file: File): String {
        val fileName = file.nameWithoutExtension
        return when {
            fileName.startsWith("course_daily_") -> {
                val date = fileName.removePrefix("course_daily_")
                "日常记录 $date"
            }
            fileName.startsWith("course_") -> {
                val parts = fileName.removePrefix("course_").split("_")
                if (parts.size >= 2) {
                    parts.dropLast(1).joinToString("_")
                } else {
                    "未命名课程"
                }
            }
            else -> fileName
        }
    }
    
    private fun getFormattedFileSize(file: File): String {
        val sizeInBytes = file.length()
        return when {
            sizeInBytes < 1024 -> "${sizeInBytes}B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024}KB"
            else -> "${sizeInBytes / (1024 * 1024)}MB"
        }
    }
} 