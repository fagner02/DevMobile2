package com.example.myapplication.msgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.msgapp.data.local.database.AppDatabase
import com.example.myapplication.msgapp.repository.MessageRepository
import com.example.myapplication.msgapp.ui.theme.MsgAppTheme
import com.example.myapplication.msgapp.ui.theme.view.MessageApp
import com.example.myapplication.msgapp.viewmodel.MessageViewModel
import com.example.myapplication.msgapp.viewmodel.MessageViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this@MainActivity.applicationContext)
        val repository = MessageRepository(db.messageDao())

        setContent {
            MsgAppTheme {
                val viewModel: MessageViewModel =
                    viewModel(factory = MessageViewModelFactory(repository))
                MessageApp(viewModel)
            }
        }
    }
}
