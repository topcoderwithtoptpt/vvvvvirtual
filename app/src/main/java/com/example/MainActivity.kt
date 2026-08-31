package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingOverlayController
import com.example.ui.screens.ControllerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VLiveTheme
import com.example.ui.viewmodel.VLiveViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : Screen("home", "首页", Icons.Default.Home)
    data object Controller : Screen("controller", "控制", Icons.Default.Tune)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: VLiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            VLiveTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: VLiveViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val floatingWindow by viewModel.floatingWindow.collectAsState()
    val isFloatingExpanded by viewModel.isFloatingExpanded.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoop by viewModel.isLoop.collectAsState()
    val statusNotice by viewModel.statusNotice.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                modifier = Modifier
                    .border(1.dp, SurfaceCardBorder)
                    .testTag("bottom_nav_bar")
            ) {
                val screens = listOf(Screen.Home, Screen.Controller, Screen.Settings)
                screens.forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepNavy,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToController = { currentScreen = Screen.Controller },
                    onNavigateToSettings = { currentScreen = Screen.Settings }
                )
                Screen.Controller -> ControllerScreen(
                    viewModel = viewModel
                )
                Screen.Settings -> SettingsScreen(
                    viewModel = viewModel
                )
            }

            // Draggable Floating Overlay Window
            FloatingOverlayController(
                isVisible = floatingWindow,
                isExpanded = isFloatingExpanded,
                isPlaying = isPlaying,
                isLoop = isLoop,
                onToggleExpanded = { viewModel.toggleFloatingExpanded() },
                onClose = { viewModel.toggleFloatingWindow() },
                onTriggerAction = { actionId -> viewModel.triggerFacialAction(actionId) },
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onToggleLoop = { viewModel.toggleLoop() },
                onRotate = { viewModel.rotate90() },
                onToggleMirror = { viewModel.toggleMirror() },
                onSwitchVideo = { currentScreen = Screen.Controller },
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Status Notice Toast Popup
            AnimatedVisibility(
                visible = statusNotice != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                statusNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xF0101828))
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = notice,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}
