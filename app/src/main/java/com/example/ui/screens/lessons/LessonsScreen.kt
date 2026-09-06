package com.example.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.data.model.LessonType
import com.example.ui.components.IslamicCourseCard
import com.example.ui.components.IslamicLessonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

enum class LessonsViewTab(val title: String) {
    COURSES("Koorsooyinka"),
    ALL_LESSONS("Duruusta Tooska ah")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    viewModel: MandeqViewModel,
    onNavigateToLesson: (Lesson) -> Unit,
    onNavigateToCourse: (Course) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val filteredLessons by viewModel.filteredLessons.collectAsState()
    val publishedCourses by viewModel.publishedCourses.collectAsState()

    var activeTab by remember { mutableStateOf(LessonsViewTab.COURSES) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("lessons_screen_content")
    ) {
        // Top Header
        Surface(
            color = HighDensitySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Manaahijta & Koorsooyinka Diiniga",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = IslamicGreenDark
                        )
                        Text(
                            text = "Koorsooyin qeybo badan leh oo ay bixinayaan culimo la xaqiijiyey",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = HighDensitySlate500
                        )
                    }

                    // Count Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = if (activeTab == LessonsViewTab.COURSES) "${publishedCourses.size} Koorso" else "${filteredLessons.size} Cashar",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = IslamicGreenDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Sub-tabs: Courses vs All Lessons
                TabRow(
                    selectedTabIndex = activeTab.ordinal,
                    containerColor = HighDensitySurface,
                    divider = {}
                ) {
                    LessonsViewTab.values().forEach { tab ->
                        Tab(
                            selected = activeTab == tab,
                            onClick = { activeTab = tab },
                            text = {
                                Text(
                                    tab.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { viewModel.selectCategory(null) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            label = { Text("Dhammaan", fontSize = 12.sp) },
                            leadingIcon = {
                                if (selectedCategoryId == null) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = HighDensitySurfaceVariant,
                                labelColor = HighDensitySlate700
                            )
                        )
                    }

                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = {
                                viewModel.selectCategory(if (selectedCategoryId == cat.id) null else cat.id)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            label = { Text("${cat.iconEmoji} ${cat.name}", fontSize = 12.sp) },
                            leadingIcon = {
                                if (selectedCategoryId == cat.id) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = HighDensitySurfaceVariant,
                                labelColor = HighDensitySlate700
                            )
                        )
                    }
                }

                // Media Type Filter Chips (Zero YouTube!)
                if (activeTab == LessonsViewTab.ALL_LESSONS) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            AssistChip(
                                onClick = { viewModel.setTypeFilter(null) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                label = { Text("Dhammaan Noocyada", fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (typeFilter == null) EmeraldContainerLight else HighDensitySurfaceVariant,
                                    labelColor = if (typeFilter == null) IslamicGreenDark else HighDensitySlate700
                                )
                            )
                        }

                        items(LessonType.values()) { type ->
                            AssistChip(
                                onClick = { viewModel.setTypeFilter(if (typeFilter == type) null else type) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                label = { Text(type.name, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (typeFilter == type) EmeraldContainerLight else HighDensitySurfaceVariant,
                                    labelColor = if (typeFilter == type) IslamicGreenDark else HighDensitySlate700
                                )
                            )
                        }
                    }
                }
            }
        }

        // Body Content
        if (activeTab == LessonsViewTab.COURSES) {
            val coursesToDisplay = if (selectedCategoryId == null) {
                publishedCourses
            } else {
                publishedCourses.filter { it.categoryId == selectedCategoryId }
            }

            if (coursesToDisplay.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HighDensityBg)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ma jiraan koorsooyin qeybtaan ku jira hadda.", fontWeight = FontWeight.Bold, color = HighDensitySlate700)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HighDensityBg),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(coursesToDisplay) { course ->
                        IslamicCourseCard(
                            course = course,
                            onClick = {
                                viewModel.selectCourse(course)
                                onNavigateToCourse(course)
                            }
                        )
                    }
                }
            }
        } else {
            // Lessons list
            if (filteredLessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HighDensityBg)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ma jiraan casharro qeybtaan ku jira hadda.", fontWeight = FontWeight.Bold, color = HighDensitySlate700)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HighDensityBg),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLessons) { lesson ->
                        val isBookmarked by viewModel.isLessonBookmarked(lesson.id).collectAsState(initial = false)
                        IslamicLessonCard(
                            lesson = lesson,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(lesson.id, lesson.courseId, isBookmarked)
                            },
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
}
