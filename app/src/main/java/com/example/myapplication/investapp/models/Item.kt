package com.example.myapplication.investapp.models

data class Item (
    val id: String = generateId().toString(),
    var title: String= "",
    var description: String = "")
    {
    companion object {
        private var currentId = 0

        fun generateId(): Int {
            currentId ++
            return currentId
        }
    }
}