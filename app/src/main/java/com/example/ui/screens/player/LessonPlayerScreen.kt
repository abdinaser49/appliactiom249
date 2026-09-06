package com.example.ui.screens.player

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.data.model.LessonType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

enum class PlayerTab(val title: String) {
    OVERVIEW("Casharka"),
    SYLLABUS("Qorshaha"),
    NOTES("Qoraallada"),
    QUIZ_OR_ASSIGNMENT("Imtixaan / Layli")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonPlayerScreen(
    lesson: Lesson,
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentLesson by remember(lesson.id) { mutableStateOf(lesson) }

    val courseParts by viewModel.selectedCourseParts.collectAsState()
    val courseLessons by viewModel.selectedCourseLessons.collectAsState()
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()
    val isCompleted = completedLessonIds.contains(currentLesson.id)

    val isBookmarked by viewModel.isLessonBookmarked(currentLesson.id).collectAsState(initial = false)
    val quiz by viewModel.currentLessonQuiz.collectAsState()
    val quizQuestions by viewModel.currentLessonQuestions.collectAsState()
    val assignment by viewModel.currentLessonAssignment.collectAsState()

    var selectedTab by remember { mutableStateOf(PlayerTab.OVERVIEW) }
    var userNoteText by remember(currentLesson.id) {
        mutableStateOf(viewModel.getLessonNote(currentLesson.id))
    }

    LaunchedEffect(currentLesson.id) {
        viewModel.setPlayingLesson(currentLesson)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentLesson.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentLesson.teacherName,
                            fontSize = 11.sp,
                            color = HighDensitySlate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleBookmark(currentLesson.id, currentLesson.courseId, isBookmarked)
                            Toast.makeText(
                                context,
                                if (!isBookmarked) "Casharka waa la keydiyey (Saved to Bookmarks)" else "Casharka waa laga saaray keydka",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.testTag("bookmark_lesson_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Lesson",
                            tint = if (isBookmarked) SoftGold else HighDensitySlate700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensitySurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(HighDensityBg)
                .padding(innerPadding)
        ) {
            // Native Direct Media Player (Zero YouTube!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (currentLesson.lessonType) {
                    LessonType.VIDEO -> {
                        LmsVideoPlayer(
                            lesson = currentLesson,
                            onProgressUpdate = { progress, completed ->
                                viewModel.markLessonCompleted(currentLesson.id, currentLesson.courseId)
                            }
                        )
                    }
                    LessonType.AUDIO -> {
                        LmsAudioPlayer(lesson = currentLesson)
                    }
                    LessonType.PDF -> {
                        LmsPdfViewer(lesson = currentLesson)
                    }
                    LessonType.TEXT -> {
                        LmsTextLessonReader(lesson = currentLesson)
                    }
                    LessonType.QUIZ -> {
                        LmsQuizComponent(
                            quiz = quiz,
                            questions = quizQuestions,
                            onCompleteQuiz = { score, passed ->
                                viewModel.submitQuizScore(quiz?.id ?: "quiz_1", score, passed)
                            }
                        )
                    }
                    LessonType.ASSIGNMENT -> {
                        LmsAssignmentComponent(
                            assignment = assignment,
                            onSubmit = { answer ->
                                viewModel.submitAssignmentAnswer(assignment?.id ?: "assign_1", answer)
                            }
                        )
                    }
                }
            }

            // Tab Navigation Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = HighDensitySurface,
                divider = {}
            ) {
                PlayerTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    PlayerTab.OVERVIEW -> {
                        // Lesson Meta & Actions
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = EmeraldContainerLight,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${currentLesson.lessonType.name} • ${currentLesson.duration}",
                                                color = IslamicGreenDark,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        // Mark Complete Button
                                        Button(
                                            onClick = {
                                                viewModel.markLessonCompleted(currentLesson.id, currentLesson.courseId)
                                                Toast.makeText(context, "Masha Allah! Casharka waa la dhammeeyey.", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isCompleted) EmeraldSecondary else IslamicGreenPrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("mark_complete_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isCompleted) "Waa Dhameystiran yahay" else "Calaamadee inuu Dhamaaday",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = currentLesson.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = HighDensitySlate900
                                    )

                                    if (currentLesson.arabicTitle.isNotBlank()) {
                                        Text(
                                            text = currentLesson.arabicTitle,
                                            fontFamily = ArabicFontFamily,
                                            fontSize = 16.sp,
                                            color = IslamicGreenPrimary
                                        )
                                    }

                                    HorizontalDivider(color = HighDensityBorder)

                                    Text(
                                        text = "Sharaxaadda Casharka:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HighDensitySlate900
                                    )

                                    Text(
                                        text = currentLesson.description,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = HighDensitySlate700
                                    )
                                }
                            }
                        }

                        // Teacher Details Card
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
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
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(IslamicGreenDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentLesson.teacherName.take(2).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = currentLesson.teacherName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = HighDensitySlate900
                                        )
                                        Text(
                                            text = "Macallinka Koorsada • Mandeq Islamic",
                                            fontSize = 11.sp,
                                            color = HighDensitySlate500
                                        )
                                    }
                                }
                            }
                        }
                    }

                    PlayerTab.SYLLABUS -> {
                        item {
                            Text(
                                text = "Dhammaan Duruusta Koorsada",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = HighDensitySlate900
                            )
                        }

                        item {
                            CoursePartsSyllabus(
                                parts = courseParts,
                                lessons = courseLessons,
                                completedLessonIds = completedLessonIds,
                                currentPlayingLessonId = currentLesson.id,
                                isEnrolled = true,
                                onLessonClick = { selectedLesson ->
                                    currentLesson = selectedLesson
                                }
                            )
                        }
                    }

                    PlayerTab.NOTES -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "Qoraalladaada Gaarka ah (Study Notes)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = HighDensitySlate900
                                    )
                                    Text(
                                        text = "Halkan ku qoro faallooyinka iyo nuxurka aad ka faa'iidaysatay casharkan:",
                                        fontSize = 11.sp,
                                        color = HighDensitySlate500
                                    )

                                    OutlinedTextField(
                                        value = userNoteText,
                                        onValueChange = { userNoteText = it },
                                        placeholder = { Text("Qor qoraal halkan...") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .testTag("lesson_note_input")
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.saveLessonNote(currentLesson.id, userNoteText)
                                            Toast.makeText(context, "Qoraalkaagii si guul leh ayaa loo keydiyey!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Keydi Qoraalka", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    PlayerTab.QUIZ_OR_ASSIGNMENT -> {
                        item {
                            if (quiz != null) {
                                LmsQuizComponent(
                                    quiz = quiz,
                                    questions = quizQuestions,
                                    onCompleteQuiz = { score, passed ->
                                        viewModel.submitQuizScore(quiz?.id ?: "", score, passed)
                                    }
                                )
                            } else if (assignment != null) {
                                LmsAssignmentComponent(
                                    assignment = assignment,
                                    onSubmit = { ans ->
                                        viewModel.submitAssignmentAnswer(assignment?.id ?: "", ans)
                                    }
                                )
                            } else {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(36.dp))
                                        Text("Casharkan malaha imtixaan gaar ah.", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Fadlan sii wad qeybaha xiga si aad u hesho imtixaanka qeybta.", fontSize = 11.sp, color = HighDensitySlate500)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
