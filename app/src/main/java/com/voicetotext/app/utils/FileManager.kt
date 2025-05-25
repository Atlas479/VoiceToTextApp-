package com.voicetotext.app.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileManager(private val context: Context) {
    
    companion object {
        private const val FOLDER_NAME = "CourseRecordings"
        private const val FILE_PREFIX = "course_"
        private const val FILE_EXTENSION = ".txt"
    }
    
    private val voiceTextFolder: File by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    private var currentFile: File? = null
    
    /**
     * 创建新的录音会话文件
     */
    fun createNewSessionFile(fileName: String): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val safeFileName = fileName.replace(Regex("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s\\-_]"), "")
        val fullFileName = "${FILE_PREFIX}${safeFileName}_$timestamp$FILE_EXTENSION"
        val file = File(voiceTextFolder, fullFileName)
        
        try {
            val header = buildString {
                append("=== 课程录音转文字 ===\n")
                append("文件名：$fileName\n")
                append("开始时间：${SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(Date())}\n")
                append("=" * 50)
                append("\n\n")
            }
            
            FileWriter(file).use { writer ->
                writer.write(header)
            }
            currentFile = file
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        return file
    }
    
    /**
     * 获取当前日期的文件
     */
    private fun getCurrentDateFile(): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val fileName = "${FILE_PREFIX}daily_$currentDate$FILE_EXTENSION"
        return File(voiceTextFolder, fileName)
    }
    
    /**
     * 追加文本到当前文件
     */
    fun appendToCurrentFile(text: String) {
        try {
            val file = currentFile ?: getCurrentDateFile()
            
            // 如果是日常文件且不存在，添加文件头
            if (currentFile == null && !file.exists()) {
                val header = "=== 语音转文字记录 ${SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date())} ===\n\n"
                FileWriter(file, true).use { writer ->
                    writer.append(header)
                }
            }
            
            // 追加文本内容
            FileWriter(file, true).use { writer ->
                writer.append(text)
            }
            
            if (currentFile == null) {
                currentFile = file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 重命名当前文件
     */
    fun renameCurrentFile(newFileName: String): Boolean {
        return try {
            currentFile?.let { oldFile ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
                val safeFileName = newFileName.replace(Regex("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s\\-_]"), "")
                val newFullFileName = "${FILE_PREFIX}${safeFileName}_$timestamp$FILE_EXTENSION"
                val newFile = File(voiceTextFolder, newFullFileName)
                
                if (oldFile.renameTo(newFile)) {
                    currentFile = newFile
                    true
                } else {
                    false
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取所有文本文件
     */
    fun getAllTextFiles(): List<File> {
        return voiceTextFolder.listFiles { file ->
            file.isFile && file.name.endsWith(FILE_EXTENSION)
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * 读取文件内容
     */
    fun readFileContent(file: File): String {
        return try {
            file.readText()
        } catch (e: IOException) {
            "读取文件失败: ${e.message}"
        }
    }
    
    /**
     * 删除文件
     */
    fun deleteFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取文件大小（格式化）
     */
    fun getFormattedFileSize(file: File): String {
        val sizeInBytes = file.length()
        return when {
            sizeInBytes < 1024 -> "${sizeInBytes}B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024}KB"
            else -> "${sizeInBytes / (1024 * 1024)}MB"
        }
    }
    
    /**
     * 获取文件修改时间（格式化）
     */
    fun getFormattedModifiedTime(file: File): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(Date(file.lastModified()))
    }
    
    /**
     * 获取文件的课程名称（从文件名提取）
     */
    fun getCourseName(file: File): String {
        val fileName = file.nameWithoutExtension
        return when {
            fileName.startsWith("${FILE_PREFIX}daily_") -> "日常记录"
            fileName.startsWith(FILE_PREFIX) -> {
                val parts = fileName.removePrefix(FILE_PREFIX).split("_")
                if (parts.size >= 2) {
                    parts.dropLast(1).joinToString("_")
                } else {
                    "未命名课程"
                }
            }
            else -> "未知文件"
        }
    }
    
    /**
     * 导出文件到下载目录
     */
    fun exportToDownloads(file: File): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val courseName = getCourseName(file)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(file.lastModified()))
            val exportFileName = "课程录音_${courseName}_$timestamp.txt"
            val exportFile = File(downloadsDir, exportFileName)
            file.copyTo(exportFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取存储目录路径
     */
    fun getStoragePath(): String {
        return voiceTextFolder.absolutePath
    }
    
    /**
     * 清理旧文件（保留最近30天）
     */
    fun cleanOldFiles() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        getAllTextFiles().forEach { file ->
            if (file.lastModified() < thirtyDaysAgo) {
                file.delete()
            }
        }
    }
    
    /**
     * 获取文件统计信息
     */
    fun getFileStats(): Map<String, Int> {
        val files = getAllTextFiles()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val thisWeek = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        
        return mapOf(
            "total" to files.size,
            "today" to files.count { file ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.lastModified())) == today
            },
            "thisWeek" to files.count { it.lastModified() > thisWeek }
        )
    }
} 