package com.voicetotext.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    
    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText
    
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> = _isRecording
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        _recognizedText.value = ""
        _isRecording.value = false
        _errorMessage.value = null
    }
    
    fun updateRecognizedText(text: String) {
        _recognizedText.value = text
    }
    
    fun appendRecognizedText(text: String) {
        val currentText = _recognizedText.value ?: ""
        _recognizedText.value = currentText + text
    }
    
    fun clearCurrentText() {
        _recognizedText.value = ""
    }
    
    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
    }
    
    fun setError(message: String?) {
        _errorMessage.value = message
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
} 