package com.example.todolist.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.components.TaskItemComponent
import com.example.todolist.models.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private fun SignOut(auth: FirebaseAuth, navController: NavController) {
    auth.signOut()
    navController.navigate("Login")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(email: String, navController: NavController) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid ?: return

    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var newTaskText by remember { mutableStateOf("") }
    var isImportant by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    tasks = it.documents.map { doc ->
                        Task(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            isImportant = doc.getBoolean("isImportant") ?: false,
                            userId = doc.getString("userId") ?: ""
                        )
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = email)
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { SignOut(auth, navController) }) {
                            Text(text = "Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        ) {
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                label = { Text("New Task") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
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
                    if (newTaskText.isNotBlank()) {
                        val newTask = Task(title = newTaskText, isImportant = isImportant, userId = userId)
                        db.collection("tasks").document(newTask.id).set(
                            mapOf(
                                "title" to newTask.title,
                                "isImportant" to newTask.isImportant,
                                "userId" to newTask.userId
                            )
                        )
                        newTaskText = ""
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Add")
            }

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(tasks) { task ->
                    TaskItemComponent(task) {
                        db.collection("tasks").document(task.id).delete()
                    }
                }
            }
        }
    }
}