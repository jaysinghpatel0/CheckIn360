package com.jaysingh.checkin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jaysingh.checkin360.navigation.AppNavigation
import com.jaysingh.checkin360.ui.theme.CheckIn360Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckIn360Theme {
                AppNavigation()
            }
        }
    }
}
