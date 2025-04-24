package common

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import screens.HomeScreen
import viewmodels.HomeViewModel

@Composable
fun App() {
    val homeViewModel: HomeViewModel = koinInject()
    HomeScreen(homeViewModel)
} 