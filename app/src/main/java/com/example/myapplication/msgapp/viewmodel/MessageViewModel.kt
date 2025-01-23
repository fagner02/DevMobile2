package com.example.myapplication.msgapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.msgapp.model.Message
import com.example.myapplication.msgapp.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageViewModel(private val repository: MessageRepository): ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages : StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allMessages.collect { _messages.value = it }
        }
    }


}