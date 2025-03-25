package com.example.todolist.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.components.TaskItemComponent
import com.example.todolist.models.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun MainScreen(){
    val db = Firebase.firestore
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var newTaskText by remember { mutableStateOf("")}
    var isImportant by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("tasks").addSnapshotListener{
            snapshot, _ -> snapshot?.let {
                tasks = it.documents.map { doc ->
                    Task(id = doc.id,
                        title = doc.getString("title") ?: "",
                        isImportant = doc.getBoolean("isImportant") ?: false)
                }
        }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(text="ToDo List")})})
    {
        paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)){
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it},
                label = { Text("NewTask")},
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(text = "Important", modifier = Modifier.weight(1f))
                OutlinedIconToggleButton(
                    checked = isImportant,
                    onCheckedChange = { isImportant = it }
                ) {
                    if (isImportant) {
                        Icon(Icons.Default.Star, contentDescription = "Important")
                    } else {
                        Icon(Icons.Default.Warning, contentDescription = "Not Important")
                    }
                }
            }

            Button(
                onClick = {
                    if (newTaskText.isNotBlank()){
                        val newTask = Task(title = newTaskText)
                        db.collection("tasks").document(newTask.id).set(mapOf("title" to newTask.title, "isImportant" to newTask.isImportant))
                        newTaskText = ""
                        isImportant = false
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Add")
            }

            LazyColumn(modifier = Modifier.padding(top = 16.dp))
            {
                items(tasks) { task ->
                    TaskItemComponent(task) {
                        db.collection("task").document(task.id).delete()
                    }
                }
            }
        }
    }
}