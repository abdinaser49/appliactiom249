package com.example.ui.screens.courses

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.ui.components.CoursePartsSyllabus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    onPlayLesson: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val course by viewModel.selectedCourse.collectAsState()
    val parts by viewModel.selectedCourseParts.collectAsState()
    val lessons by viewModel.selectedCourseLessons.collectAsState()
    val isEnrolled by viewModel.isEnrolledInSelectedCourse.collectAsState()
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()

    if (course == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = IslamicGreenPrimary)
        }
        return
    }

    val currentCourse = course!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentCourse.categoryName, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensitySurface)
            )
        },
        bottomBar = {
            Surface(
                color = HighDensitySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEnrolled) "Waxaad tahay Arday" else "Koorso Bilaash ah",
                            fontSize = 11.sp,
                            color = HighDensitySlate500
                        )
                        Text(
                            text = if (isEnrolled) "Diiwaangashan" else "100% Free Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IslamicGreenDark
                        )
                    }

                    Button(
                        onClick = {
                            if (!isEnrolled) {
                                viewModel.enrollInCourse(currentCourse.id)
                                Toast.makeText(context, "Masha Allah! Waxaad si guul leh ugu biirtay koorsada.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Start or continue playing first lesson
                                lessons.firstOrNull()?.let { onPlayLesson(it) }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("enroll_course_button")
                    ) {
                        Icon(
                            imageVector = if (isEnrolled) Icons.Default.PlayArrow else Icons.Default.HowToReg,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnrolled) "Bilow Casharada" else "Isku Qor Koorsada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Course Hero Cover Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IslamicGreenDark)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(currentCourse.coverImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = currentCourse.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                    ) {
                        Text(
                            text = "${currentCourse.language.name} • ${currentCourse.difficulty.name} • ${currentCourse.duration}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Title & Teacher Card
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = currentCourse.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = HighDensitySlate900
                    )

                    if (currentCourse.subtitle.isNotBlank()) {
                        Text(
                            text = currentCourse.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HighDensitySlate700
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
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
                                    text = currentCourse.teacherName.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentCourse.teacherName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HighDensitySlate900
                                )
                                Text(
                                    text = "Macallin Diineed la Aqoonsan yahay • Mandeq Faculty",
                                    fontSize = 11.sp,
                                    color = HighDensitySlate500
                                )
                            }

                            Surface(color = EmeraldContainerLight, shape = RoundedCornerShape(6.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = SoftGold, modifier = Modifier.size(13.dp))
                                    Text("${currentCourse.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IslamicGreenDark)
                                }
                            }
                        }
                    }
                }
            }

            // Description
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sharaxaadda Koorsada", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = currentCourse.description,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = HighDensitySlate700
                        )

                        if (currentCourse.learningObjectives.isNotBlank()) {
                            HorizontalDivider(color = HighDensityBorder)
                            Text("Waxyaabaha Aad Baran Doonto:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = currentCourse.learningObjectives,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = HighDensitySlate700
                            )
                        }
                    }
                }
            }

            // Course Syllabus / Parts
            item {
                Text(
                    text = "Qorshaha Duruusta (Course Syllabus • ${parts.size} Qeybood)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = HighDensitySlate900
                )
            }

            item {
                CoursePartsSyllabus(
                    parts = parts,
                    lessons = lessons,
                    completedLessonIds = completedLessonIds,
                    currentPlayingLessonId = null,
                    isEnrolled = isEnrolled,
                    onLessonClick = onPlayLesson
                )
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
