package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.screens.LoginScreen
import com.example.todolist.screens.MainScreen
import com.example.todolist.screens.RegisterScreen
import com.example.todolist.ui.theme.ToDoListTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavScreen()
        }
    }
}

@Composable
fun NavScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Register") {
        composable("Register") {
            RegisterScreen(navController)
        }
        composable("Login") {
            LoginScreen(navController)
        }
        composable("Main/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainScreen(email, navController)
        }
    }
}




