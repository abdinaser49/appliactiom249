package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.Lesson
import com.example.ui.components.IslamicLessonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchCategoryFilter by viewModel.searchCategoryFilter.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Raadi cashar, macallin, tafsiir...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = HighDensitySurface,
                            unfocusedContainerColor = HighDensitySurface,
                            focusedBorderColor = IslamicGreenPrimary,
                            unfocusedBorderColor = HighDensityBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(end = 6.dp)
                            .testTag("global_search_input")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IslamicGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighDensitySurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(HighDensityBg)
                .padding(innerPadding)
                .testTag("global_search_screen")
        ) {
            // Category Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = searchCategoryFilter == null,
                        onClick = { viewModel.setSearchCategoryFilter(null) },
                        label = { Text("Dhammaan", fontSize = 11.sp) }
                    )
                }

                items(categories) { cat ->
                    FilterChip(
                        selected = searchCategoryFilter == cat.id,
                        onClick = {
                            viewModel.setSearchCategoryFilter(if (searchCategoryFilter == cat.id) null else cat.id)
                        },
                        label = { Text("${cat.iconEmoji} ${cat.name}", fontSize = 11.sp) }
                    )
                }
            }

            // Results List
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = HighDensitySlate500,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Wax casharro ah lama helin",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = HighDensitySlate900
                        )
                        Text(
                            text = "Isku day inaad baarto eray kale sida 'Tajweed', 'Mustafe', 'Salaad'",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = HighDensitySlate500
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Natiijada Raadinta (${searchResults.size} cashar)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = IslamicGreenDark
                        )
                    }

                    items(searchResults) { lesson ->
                        val isBookmarked by viewModel.isLessonBookmarked(lesson.id).collectAsState(initial = false)
                        LessonCard(
                            lesson = lesson,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = { viewModel.toggleBookmark(lesson.id, isBookmarked) },
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
