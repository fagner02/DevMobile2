package com.example.myapplication.investapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.investapp.models.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class ItemViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    init {
        listen()
    }

    var items = mutableStateOf(listOf<Item>())

    private fun listen() {
        listener = db.collection("items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val res = snapshot.documents.mapNotNull { document ->
                        document.toObject<Item>()?.copy(id = document.id)
                    }

                    items.value = res
                }
            }
    }

    fun add(item: Item){
        db.collection("items").add(item)
    }

    fun delete(id: String){
        db.collection("items").document(id).delete()
    }

    fun update(item: Item){
        db.collection("items").document(item.id).set(item).addOnSuccessListener {
            println("deu certo")
        }.addOnFailureListener {
            println("deu erro")
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}