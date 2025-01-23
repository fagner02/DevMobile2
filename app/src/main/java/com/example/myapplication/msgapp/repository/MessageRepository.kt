package com.example.myapplication.msgapp.repository

import com.example.myapplication.msgapp.data.local.dao.MessageDao
import com.example.myapplication.msgapp.model.Message
import kotlinx.coroutines.flow.Flow


class MessageRepository(private val dao: MessageDao) {
    val allMessages: Flow<List<Message>> = dao.getAllMessages()

    suspend fun addMessage(content: String) {
        val message = Message(content = content, timestamp = System.currentTimeMillis())

        dao.insertMessage(message)
    }
}