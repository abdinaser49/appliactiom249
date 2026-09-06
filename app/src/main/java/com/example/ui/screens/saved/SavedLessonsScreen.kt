package com.example.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.ui.components.IslamicLessonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@Composable
fun SavedLessonsScreen(
    viewModel: MandeqViewModel,
    onNavigateToLesson: (Lesson) -> Unit,
    onExploreLessons: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedLessons by viewModel.savedLessons.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityBg)
            .testTag("saved_lessons_screen")
    ) {
        // Header
        Surface(
            color = HighDensitySurface,
            shadowElevation = 0.dp,
            modifier = Modifier.border(0.dp, HighDensityBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Saved Lessons (Casharrada La Keydiyey)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = IslamicGreenDark
                )
                Text(
                    text = "${savedLessons.size} cashar ayaa kuu keydsan",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = HighDensitySlate500
                )
            }
        }

        if (savedLessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = EmeraldContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = IslamicGreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Text(
                        text = "Ma jiraan casharro aad keydsatay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = HighDensitySlate900
                    )

                    Text(
                        text = "Markaad cashar daawanayso, riix badhanka 'Save' si aad hadhow si fudud ugu hesho halkan.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = HighDensitySlate500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = onExploreLessons,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("saved_explore_button")
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Baadh Casharada Hadda", fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedLessons) { lesson ->
                    IslamicLessonCard(
                        lesson = lesson,
                        isBookmarked = true,
                        onBookmarkToggle = { viewModel.toggleBookmark(lesson.id, lesson.courseId, true) },
                        onClick = {
                            viewModel.setPlayingLesson(lesson)
                            onNavigateToLesson(lesson)
                        }
                    )
                }
            }
        }
    }
}
