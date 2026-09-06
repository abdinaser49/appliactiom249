package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.MandeqViewModel

@Composable
fun ProfileScreen(
    viewModel: MandeqViewModel,
    onNavigateToSaved: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToTeacherPortal: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var showSafetyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    // Islamic Trust & Safety Dialog
    if (showSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = IslamicGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Amiinnimada & Badbaadada Diinta",
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Dhammaan koorsooyinka iyo duruusta waxaa bixiya culimo iyo macallimiin la aqoonsan yahay.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "2. Nidaamka LMS ma laha wax xayeysiis ah, heeso ama muuqaallo aan diiniga ahayn.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "3. Macallin walba wuxuu leeyahay shahaado rasmi ah iyo Ijaazo la xaqiijiyey.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSafetyDialog = false }) {
                    Text("Waan Fahmay")
                }
            }
        )
    }

    // Role Switcher Dialog
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Dooro Doorkaaga (Role-Based Access)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRole.values().forEach { role ->
                        Surface(
                            onClick = {
                                viewModel.switchRole(role)
                                showRoleDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (activeRole == role) EmeraldContainerLight else HighDensitySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (activeRole == role) IslamicGreenPrimary else HighDensityBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(selected = activeRole == role, onClick = null)
                                Column {
                                    Text(
                                        text = when (role) {
                                            UserRole.SUPER_ADMIN -> "Super Admin (Maamulaha Guud)"
                                            UserRole.TEACHER -> "Teacher / Ustaad (Macallinka)"
                                            UserRole.STUDENT -> "Student / Arday (Waxbarasho)"
                                            UserRole.GUEST -> "Guest / Marti (Booqude)"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HighDensitySlate900
                                    )
                                    Text(
                                        text = role.description,
                                        fontSize = 10.sp,
                                        color = HighDensitySlate500
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("Xir")
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Dooro Luuqadda / Choose Language", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.values().forEach { lang ->
                        Surface(
                            onClick = {
                                viewModel.setLanguage(lang)
                                showLanguageDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (appLanguage == lang) EmeraldContainerLight else Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(lang.title, fontWeight = FontWeight.Bold)
                                if (appLanguage == lang) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = IslamicGreenDark)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Xir") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityBg),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header
        item {
            val user = userProfile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(IslamicGreenPrimary, IslamicGreenDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, SoftGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.fullName?.take(2)?.uppercase() ?: "CA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGreenDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = user?.fullName ?: "Cabdiraxmaan Cali Maxamed",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = user?.email ?: "student@mandeqislamic.so",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Active Role Badge (Clickable to switch demo role)
                    Surface(
                        onClick = { showRoleDialog = true },
                        color = SoftGold,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when (activeRole) {
                                    UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                                    UserRole.TEACHER -> Icons.Default.School
                                    UserRole.STUDENT -> Icons.Default.Person
                                    UserRole.GUEST -> Icons.Default.Visibility
                                },
                                contentDescription = null,
                                tint = IslamicGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Doorka: ${activeRole.name} (Beddel)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreenDark
                            )
                        }
                    }
                }
            }
        }

        // 2. Stats Grid
        item {
            val user = userProfile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Courses",
                        value = "${user?.coursesEnrolled ?: 3}",
                        subtitle = "Koorsooyin",
                        icon = Icons.Default.MenuBook,
                        color = IslamicGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completed",
                        value = "${user?.completedCourses ?: 1}",
                        subtitle = "Dhammaystiran",
                        icon = Icons.Default.CheckCircle,
                        color = EmeraldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Lessons",
                        value = "${user?.completedLessons ?: 14}",
                        subtitle = "Casharro la bartay",
                        icon = Icons.Default.PlayLesson,
                        color = SoftGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Streak",
                        value = "${user?.streakDays ?: 18} Days",
                        subtitle = "Maalmood xiriir ah 🔥",
                        icon = Icons.Default.LocalFireDepartment,
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Portals Quick Links
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Teacher Portal Card
                Surface(
                    onClick = onNavigateToTeacherPortal,
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldContainerLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_teacher_portal_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "Bawaabada Macallimiinta (Teacher Portal)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HighDensitySlate900
                                )
                                Text(
                                    text = "Abuur koorsooyin, qeybo (Part 1..100) & casharro",
                                    fontSize = 11.sp,
                                    color = HighDensitySlate500
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = IslamicGreenDark)
                    }
                }

                // Super Admin CMS Card
                Surface(
                    onClick = onNavigateToAdminDashboard,
                    shape = RoundedCornerShape(12.dp),
                    color = GoldContainerLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_admin_dashboard_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SoftGoldDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "Admin Panel & Governance",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HighDensitySlate900
                                )
                                Text(
                                    text = "Ansixinta macallimiinta, koorsooyinka & warbixinnada",
                                    fontSize = 11.sp,
                                    color = HighDensitySlate500
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SoftGoldDark)
                    }
                }
            }
        }

        // 4. Menu Items
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ProfileMenuItem(
                        title = "Saved Lessons (Casharrada La Keydiyey)",
                        icon = Icons.Outlined.BookmarkBorder,
                        onClick = onNavigateToSaved
                    )
                    HorizontalDivider(color = HighDensityBorder)

                    ProfileMenuItem(
                        title = "Certificates (Shahaadooyinka)",
                        icon = Icons.Outlined.WorkspacePremium,
                        onClick = onNavigateToCertificates
                    )
                    HorizontalDivider(color = HighDensityBorder)

                    ProfileMenuItem(
                        title = "Notifications (Ogeysiisyada)",
                        icon = Icons.Outlined.Notifications,
                        onClick = onNavigateToNotifications
                    )
                    HorizontalDivider(color = HighDensityBorder)

                    ProfileMenuItem(
                        title = "Luuqadda (Language: ${appLanguage.title})",
                        icon = Icons.Outlined.Translate,
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(color = HighDensityBorder)

                    ProfileMenuItem(
                        title = "Badbaadada & Shuruucda Diiniga ah",
                        icon = Icons.Outlined.VerifiedUser,
                        onClick = { showSafetyDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = HighDensitySlate500, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HighDensitySlate900)
            Text(subtitle, fontSize = 10.sp, color = HighDensitySlate500)
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(20.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HighDensitySlate900)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = HighDensitySlate500, modifier = Modifier.size(14.dp))
        }
    }
}
