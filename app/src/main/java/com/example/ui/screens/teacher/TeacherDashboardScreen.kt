package com.example.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

enum class TeacherTab(val title: String, val icon: ImageVector) {
    MY_COURSES("Koorsooyinkayga", Icons.Default.MenuBook),
    STUDENTS("Ardayda & Natiijooyinka", Icons.Default.People),
    ASSIGNMENTS("Layliyada & Saxidda", Icons.Default.Assignment),
    PROFILE("Xogtayda", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    viewModel: MandeqViewModel,
    onNavigateToCreateCourse: () -> Unit,
    onNavigateToCourseManager: (Course) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTeacher by viewModel.currentTeacher.collectAsState()
    val teacherCourses by viewModel.teacherCourses.collectAsState()
    val stats by viewModel.teacherStats.collectAsState()

    var selectedTab by remember { mutableStateOf(TeacherTab.MY_COURSES) }
    var showGradingDialog by remember { mutableStateOf(false) }
    var gradingScore by remember { mutableStateOf("95") }
    var gradingFeedback by remember { mutableStateOf("Masha Allah! Dhawaaqaaga xarafka Qaaf aad buu u hagaagsan yahay.") }

    if (showGradingDialog) {
        AlertDialog(
            onDismissRequest = { showGradingDialog = false },
            title = {
                Text(
                    text = "Saxidda Layliga Ardayga",
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ardayga: Cabdiraxmaan Cali\nLayliga: Duubista Suuradda Al-Ikhlaas",
                        fontSize = 12.sp,
                        color = HighDensitySlate700
                    )
                    OutlinedTextField(
                        value = gradingScore,
                        onValueChange = { gradingScore = it },
                        label = { Text("Dhibcaha (Out of 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gradingFeedback,
                        onValueChange = { gradingFeedback = it },
                        label = { Text("Talooyinka Macallinka (Feedback)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGradingDialog = false
                        Toast.makeText(context, "Layligii waa la saxay! Natiijada waxaa loo diray ardayga.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                ) {
                    Text("Keydi Natiijada")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGradingDialog = false }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = HighDensitySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentTeacher?.fullName?.take(2)?.uppercase() ?: "UM",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Column {
                                Text(
                                    text = "MANDEQ ISLAMIC",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = currentTeacher?.fullName ?: "Ustaad Maxamed Cabdullaahi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensitySlate900
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = EmeraldSecondary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = EmeraldSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "APPROVED",
                                        color = EmeraldSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = HighDensitySlate500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateCourse,
                containerColor = IslamicGreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_course")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Abuur Koors Cusub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(HighDensityBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KPI Metrics Row
            item {
                Text(
                    text = "Xogta Guud ee Macallinka (Analytics Overview)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = HighDensitySlate900
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TeacherKpiCard(
                            title = "Koorsooyinka",
                            value = "${stats.totalCourses}",
                            subtitle = "${stats.publishedCourses} Faafay • ${stats.draftCourses} Qabyo",
                            icon = Icons.Default.AutoStories,
                            color = IslamicGreenPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        TeacherKpiCard(
                            title = "Ardayda Diiwaangashan",
                            value = "${stats.totalStudents}",
                            subtitle = "+140 usbuucan",
                            icon = Icons.Default.People,
                            color = EmeraldSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TeacherKpiCard(
                            title = "Wadarta Casharada",
                            value = "${stats.totalLessons}",
                            subtitle = "Muuqaal, Cod, PDF, Quiz",
                            icon = Icons.Default.PlayLesson,
                            color = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                        TeacherKpiCard(
                            title = "Heerka Dhameynta",
                            value = "${stats.averageCompletion}%",
                            subtitle = "Celcelis wanaagsan",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Tab Selector Chips
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = HighDensitySurface,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    TeacherTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(tab.icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Text(tab.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        )
                    }
                }
            }

            // Tab 1: My Courses & Parts Manager
            if (selectedTab == TeacherTab.MY_COURSES) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Koorsooyinka aad Bixiso (${teacherCourses.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = HighDensitySlate900
                        )
                    }
                }

                items(teacherCourses) { course ->
                    TeacherCourseCard(
                        course = course,
                        onManageClick = {
                            viewModel.selectCourse(course.id)
                            onNavigateToCourseManager(course)
                        }
                    )
                }
            }

            // Tab 2: Students & Performance
            if (selectedTab == TeacherTab.STUDENTS) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Horumarka Ardayda Ugu Dambeysay", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            listOf(
                                Triple("Cabdiraxmaan Cali", "Tajweedka Aasaasiga ah", 85),
                                Triple("Faadumo Xasan", "Tajweedka Aasaasiga ah", 100),
                                Triple("Khaalid Cumar", "Fiqiga Daahorada", 45),
                                Triple("Zaynab Yuusuf", "Tafsiirka Al-Baqarah", 70)
                            ).forEach { (student, courseTitle, progress) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HighDensitySurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(student, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(courseTitle, fontSize = 11.sp, color = HighDensitySlate500)
                                        }
                                        Text(
                                            text = "$progress%",
                                            fontWeight = FontWeight.Bold,
                                            color = if (progress == 100) EmeraldSecondary else IslamicGreenDark,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 3: Assignments & Grading
            if (selectedTab == TeacherTab.ASSIGNMENTS) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Layliyada Sugaya Saxidda (Pending Grading)", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HighDensitySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Cabdiraxmaan Cali", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Surface(color = SoftBeige, shape = RoundedCornerShape(4.dp)) {
                                            Text("Part 02 Layli", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IslamicGreenDark, modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                    Text(
                                        text = "Akhriska Suuradda Al-Ikhlaas iyo tusaalooyinka makhaarijka...",
                                        fontSize = 11.sp,
                                        color = HighDensitySlate700
                                    )
                                    Button(
                                        onClick = { showGradingDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sax oo Qiimee Layliga (Grade)", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 4: Teacher Profile Details
            if (selectedTab == TeacherTab.PROFILE) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Faahfaahinta Macallinka", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Email: ${currentTeacher?.email}", fontSize = 12.sp)
                            Text("Taleefan: ${currentTeacher?.phone}", fontSize = 12.sp)
                            Text("Maadooyinka: ${currentTeacher?.teachingSubjects}", fontSize = 12.sp)
                            Text("Khibradda: ${currentTeacher?.experienceYears} Sano", fontSize = 12.sp)
                            Text("Bio: ${currentTeacher?.bio}", fontSize = 12.sp, color = HighDensitySlate700)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun TeacherKpiCard(
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
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = HighDensitySlate500, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier.size(26.dp).clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                }
            }
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HighDensitySlate900)
            Text(subtitle, fontSize = 10.sp, color = HighDensitySlate500, maxLines = 1)
        }
    }
}

@Composable
fun TeacherCourseCard(
    course: Course,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (course.status == CourseStatus.PUBLISHED) EmeraldSecondary.copy(alpha = 0.15f) else SoftBeige,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = course.status.name,
                        color = if (course.status == CourseStatus.PUBLISHED) EmeraldSecondary else Color(0xFFD97706),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${course.studentsCount} Arday",
                    fontSize = 11.sp,
                    color = HighDensitySlate500,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = HighDensitySlate900
            )

            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall,
                color = HighDensitySlate700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = HighDensityBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Qeybaha (Parts): ${course.totalPartsCount} • Casharada: ${course.totalLessonsCount}",
                    fontSize = 11.sp,
                    color = HighDensitySlate500
                )

                Button(
                    onClick = onManageClick,
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("manage_course_button_${course.id}")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Maamul (Manage)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
