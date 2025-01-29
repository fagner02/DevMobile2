package com.example.myapplication.investapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.investapp.models.Item

@Composable
fun ItemScreen(modifier: Modifier, viewModel: ItemViewModel = viewModel()) {
    val items = viewModel.items
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    Column(modifier=Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        TextField(
            title,
            onValueChange = { title = it },
            label = { Text("title") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(description,
            onValueChange = { description = it },
            label = { Text("description") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
            if (title.text.isNotEmpty() && description.text.isNotEmpty()) {
                viewModel.add(Item(title = title.text, description = description.text))
                title = TextFieldValue("")
                description = TextFieldValue("")
            }
        }
        ){
            Text("Add")
        }

        LazyColumn (verticalArrangement = Arrangement.spacedBy(5.dp)) {
            itemsIndexed(items.value) { _, item ->
                Card {
                    Column(Modifier.padding(5.dp)) {
                        Text(item.title)
                        Text(item.description)

                        Row{

                        }
                    }
                }
            }
        }
        Button(onClick = {

        }) {
            Text("Update")
        }
    }
}