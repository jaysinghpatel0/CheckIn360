package com.example.checkin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkin360.ui.theme.CheckIn360Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckIn360Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "CheckIn360",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color = Color.Cyan) {
        Box {
            Text(
                text = "Hello $name!",
                modifier = modifier
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CheckIn360Theme {
            Greeting("Android")
        }
    }
}

@Composable
fun EmployeeScreen() {
    val emp = Employee(1, "John Doe", "HR", "Manager")

    Box(
        modifier = Modifier
            .fillMaxSize() // Makes the Box fill the whole screen
//            .padding(top = 20.dp) // Add top padding to move the content down
    ) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp),    // Centers the text
                text = buildAnnotatedString {
                    // Employee Name in bold and italic
                    append("Employee Name: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(emp.name)
                    }
                    append("\n")
                    append("Department: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(emp.department)
                    }
                    append("\n")
                    append("Designation: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(emp.designation)
                    }
                    append("\n")
                }
            )
//        Text(                                                                 //2nd & Simple View Option
//            text = "Employee Name: ${emp.name}" +
//                    " Department: ${emp.department}" +
//                    " Designation: ${emp.designation}\n${greetEmployee(emp)}"
//        )
        }
    }

@Preview(showBackground = true)
@Composable
fun EmployeeScreenPreview() {
    EmployeeScreen()
}