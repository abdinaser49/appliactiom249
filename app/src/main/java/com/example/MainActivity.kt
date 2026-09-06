package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.certificates.CertificatesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.lessons.LessonsScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.player.LessonPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.quran.QuranHifdhScreen
import com.example.ui.screens.saved.SavedLessonsScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.theme.MandeqIslamicTheme
import com.example.ui.viewmodel.MandeqViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Lessons : Screen("lessons", "Casharada", Icons.Filled.PlayLesson, Icons.Outlined.PlayLesson)
    object Quran : Screen("quran", "Qur'aan", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Saved : Screen("saved", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    // Secondary destinations
    object Player : Screen("player", "Player", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Notifications : Screen("notifications", "Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications)
    object Certificates : Screen("certificates", "Certificates", Icons.Filled.WorkspacePremium, Icons.Outlined.WorkspacePremium)
    object Admin : Screen("admin", "Admin CMS", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MandeqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MandeqIslamicTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MandeqViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Lessons,
        Screen.Quran,
        Screen.Saved,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val currentPlayingLesson by viewModel.currentPlayingLesson.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = com.example.ui.theme.HighDensitySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.HighDensityBorder),
                    shadowElevation = 4.dp
                ) {
                    NavigationBar(
                        containerColor = com.example.ui.theme.HighDensitySurface,
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenDark,
                                    indicatorColor = com.example.ui.theme.EmeraldContainerLight,
                                    selectedTextColor = IslamicGreenDark,
                                    unselectedIconColor = com.example.ui.theme.HighDensitySlate500,
                                    unselectedTextColor = com.example.ui.theme.HighDensitySlate500
                                ),
                                modifier = Modifier.testTag("nav_item_${screen.route}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home Destination
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lesson ->
                        viewModel.setPlayingLesson(lesson)
                        navController.navigate(Screen.Player.route)
                    },
                    onNavigateToCategory = { category ->
                        viewModel.selectCategory(category.id)
                        navController.navigate(Screen.Lessons.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToHifdh = {
                        navController.navigate(Screen.Quran.route)
                    }
                )
            }

            // 2. Lessons Destination
            composable(Screen.Lessons.route) {
                LessonsScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lesson ->
                        viewModel.setPlayingLesson(lesson)
                        navController.navigate(Screen.Player.route)
                    }
                )
            }

            // 3. Qur'aan & Hifdh Destination
            composable(Screen.Quran.route) {
                QuranHifdhScreen(
                    viewModel = viewModel
                )
            }

            // 4. Saved Lessons Destination
            composable(Screen.Saved.route) {
                SavedLessonsScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lesson ->
                        viewModel.setPlayingLesson(lesson)
                        navController.navigate(Screen.Player.route)
                    },
                    onExploreLessons = {
                        navController.navigate(Screen.Lessons.route)
                    }
                )
            }

            // 5. User Profile Destination
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSaved = {
                        navController.navigate(Screen.Saved.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToCertificates = {
                        navController.navigate(Screen.Certificates.route)
                    },
                    onNavigateToAdminDashboard = {
                        navController.navigate(Screen.Admin.route)
                    }
                )
            }

            // 6. Lesson Player Destination
            composable(Screen.Player.route) {
                val lesson = currentPlayingLesson
                if (lesson != null) {
                    LessonPlayerScreen(
                        lesson = lesson,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToLesson = { nextLesson ->
                            viewModel.setPlayingLesson(nextLesson)
                        }
                    )
                } else {
                    // Fallback to lessons screen if null
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            // 7. Global Search Destination
            composable(Screen.Search.route) {
                GlobalSearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLesson = { lesson ->
                        viewModel.setPlayingLesson(lesson)
                        navController.navigate(Screen.Player.route)
                    }
                )
            }

            // 8. Notifications Destination
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 9. Certificates Destination
            composable(Screen.Certificates.route) {
                CertificatesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 10. Admin Content Discovery & CMS Destination
            composable(Screen.Admin.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
