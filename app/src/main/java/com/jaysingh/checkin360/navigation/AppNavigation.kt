package com.jaysingh.checkin360.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jaysingh.checkin360.ui.auth.LoginScreen
import com.jaysingh.checkin360.ui.history.AttendanceHistoryScreen
import com.jaysingh.checkin360.ui.home.HomeScreen
import com.jaysingh.checkin360.ui.leave.LeaveScreen
import com.jaysingh.checkin360.ui.profile.ProfileScreen


// Routes
object Routes{
    const val LOGIN = "login"
    const val HOME = "home"
    const val HISTORY = "history"
    const val LEAVE = "leave"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ){
        composable(Routes.LOGIN){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    Firebase.auth.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onLeaveClick = {
                    navController.navigate(Routes.LEAVE)
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        composable(Routes.HISTORY) {
            AttendanceHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LEAVE) {
            LeaveScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogOut = {
                    Firebase.auth.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}