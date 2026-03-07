package com.sg.simplekanban.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun startLoading(){
        _isLoading.value = true
    }

    fun stopLoading(){
        _isLoading.value = false
    }

    protected fun launchWithLoading(block: suspend () -> Unit) = viewModelScope.launch {
        startLoading()
        try {
            block()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}