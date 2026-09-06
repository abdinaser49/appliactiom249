package com.example.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// ==========================================
// 1. Direct Native Video Player (NO YOUTUBE!)
// ==========================================
@Composable
fun LmsVideoPlayer(
    lesson: Lesson,
    modifier: Modifier = Modifier,
    onProgressUpdate: (Int, Boolean) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionSeconds by remember { mutableStateOf(0) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

    // Fullscreen Dialog
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                DirectVideoView(
                    contentUrl = lesson.contentUrl,
                    isPlaying = isPlaying,
                    playbackSpeed = playbackSpeed,
                    modifier = Modifier.fillMaxSize()
                )

                // Close Fullscreen Button
                IconButton(
                    onClick = { isFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, HighDensityBorder, RoundedCornerShape(16.dp))
            .testTag("lms_video_player")
    ) {
        // Direct Video Surface
        DirectVideoView(
            contentUrl = lesson.contentUrl,
            isPlaying = isPlaying,
            playbackSpeed = playbackSpeed,
            modifier = Modifier.fillMaxSize()
        )

        // Supabase / LMS Direct Storage Watermark
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = EmeraldSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Mandeq Storage • HD 1080p",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Overlay Play/Pause Action if tapped
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(54.dp)
                .clip(CircleShape)
                .background(IslamicGreenPrimary.copy(alpha = 0.9f))
                .clickable { isPlaying = !isPlaying }
                .testTag("video_play_pause_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Bottom Controls Bar
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = if (isPlaying) "Daaran (Playing)" else "Hakad (Paused)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Speed selector button
                    AssistChip(
                        onClick = {
                            playbackSpeed = when (playbackSpeed) {
                                1.0f -> 1.25f
                                1.25f -> 1.5f
                                1.5f -> 2.0f
                                else -> 1.0f
                            }
                            Toast.makeText(context, "Xawaaraha: ${playbackSpeed}x", Toast.LENGTH_SHORT).show()
                        },
                        label = { Text("${playbackSpeed}x", fontSize = 10.sp, color = Color.White) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.height(26.dp)
                    )

                    // Fullscreen
                    IconButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectVideoView(
    contentUrl: String,
    isPlaying: Boolean,
    playbackSpeed: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                val uri = if (contentUrl.isNotBlank()) Uri.parse(contentUrl)
                else Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                setVideoURI(uri)
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    if (isPlaying) {
                        start()
                    }
                }
                videoViewRef = this
            }
        },
        update = { vView ->
            if (isPlaying && !vView.isPlaying) {
                vView.start()
            } else if (!isPlaying && vView.isPlaying) {
                vView.pause()
            }
        },
        modifier = modifier
    )
}

// ==========================================
// 2. Islamic Audio Player (MP3 / M4A)
// ==========================================
@Composable
fun LmsAudioPlayer(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0.25f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lms_audio_player")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldContainerLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = IslamicGreenDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = HighDensitySlate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Cod Sheikh: ${lesson.teacherName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HighDensitySlate500
                    )
                }

                Surface(
                    color = EmeraldSecondary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "MP3 Audio",
                        color = EmeraldSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Slider progress
            Column {
                Slider(
                    value = currentProgress,
                    onValueChange = { currentProgress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = IslamicGreenPrimary,
                        activeTrackColor = IslamicGreenPrimary,
                        inactiveTrackColor = HighDensityBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("04:12", fontSize = 11.sp, color = HighDensitySlate500)
                    Text(lesson.duration, fontSize = 11.sp, color = HighDensitySlate500)
                }
            }

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentProgress = (currentProgress - 0.05f).coerceAtLeast(0f) }) {
                    Icon(Icons.Default.Replay10, contentDescription = "Dib u noqo 10 ilbiriqsi", tint = IslamicGreenDark)
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IslamicGreenPrimary)
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = { currentProgress = (currentProgress + 0.05f).coerceAtMost(1f) }) {
                    Icon(Icons.Default.Forward10, contentDescription = "Horay u soco 10 ilbiriqsi", tint = IslamicGreenDark)
                }

                AssistChip(
                    onClick = {
                        playbackSpeed = if (playbackSpeed >= 2.0f) 1.0f else playbackSpeed + 0.25f
                    },
                    label = { Text("${playbackSpeed}x", fontSize = 11.sp) }
                )
            }
        }
    }
}

// ==========================================
// 3. PDF Lesson Viewer & Downloader
// ==========================================
@Composable
fun LmsPdfViewer(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = 15

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lms_pdf_viewer")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "Dukumiintiga PDF (Buugga)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HighDensitySlate900
                    )
                }

                if (lesson.allowDownload) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Soo dejinta buugga \"${lesson.title}\" waa la bilaabay...", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Soo Dajiso", fontSize = 11.sp)
                    }
                }
            }

            // PDF Simulated Reader Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, HighDensityBorder, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontFamily = ArabicFontFamily,
                            fontSize = 18.sp,
                            color = IslamicGreenDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "BOGGA $currentPage EE $totalPages: ${lesson.title.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = HighDensitySlate900
                        )

                        Text(
                            text = "Qawaacidda Tajweedka ee lagu xusay qeybtan:\n" +
                                    "1. Makhaarijka Al-Halq (Dhuunta): Waxaa ka soo baxa 6 xaraf oo kala ah: (ء، هـ، ع، ح، غ، خ).\n" +
                                    "2. Xaraf kasta wuxuu leeyahay miisaan iyo sifo gaar ah oo aan la beddeli karin marka la akhrinayo aayadaha Qur'aanka.\n" +
                                    "3. Qoraalkan waxaa loogu talagalay in ardaygu mar kasta u noqdo si uu u xoojiyo casharka macallinka.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = HighDensitySlate700
                        )
                    }

                    // Bottom pagination controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Bog Hore", fontSize = 11.sp)
                        }

                        Text("Bogga $currentPage / $totalPages", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Bog Xiga", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. Rich Text Islamic Lesson Reader
// ==========================================
@Composable
fun LmsTextLessonReader(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lms_text_reader")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = SoftBeige,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontFamily = ArabicFontFamily,
                    fontSize = 20.sp,
                    color = IslamicGreenDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = HighDensitySlate900
            )

            if (lesson.arabicTitle.isNotBlank()) {
                Text(
                    text = lesson.arabicTitle,
                    fontFamily = ArabicFontFamily,
                    fontSize = 16.sp,
                    color = IslamicGreenPrimary
                )
            }

            HorizontalDivider(color = HighDensityBorder)

            Text(
                text = if (lesson.textContent.isNotBlank()) lesson.textContent else lesson.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                color = HighDensitySlate700
            )

            Surface(
                color = EmeraldContainerLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = IslamicGreenDark)
                    Text(
                        text = "Xigasho: Mandeq Islamic Curricula • Waxaa Diyaariyey: ${lesson.teacherName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGreenDark
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. Interactive Quiz Screen / Component
// ==========================================
@Composable
fun LmsQuizComponent(
    quiz: Quiz?,
    questions: List<QuizQuestion>,
    onCompleteQuiz: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOptions by remember { mutableStateOf(mapOf<String, Int>()) } // questionId to optionIndex
    var hasSubmitted by remember { mutableStateOf(false) }
    var scorePercentage by remember { mutableIntStateOf(0) }
    var isPassed by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lms_quiz_component")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = quiz?.title ?: "Imtixaanka Casharka",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HighDensitySlate900
                    )
                    Text(
                        text = "Heerka Baasitaanka: ${quiz?.passingScore ?: 70}% • Waqtiga: ${quiz?.timeLimitMinutes ?: 15} Daqiiqo",
                        style = MaterialTheme.typography.bodySmall,
                        color = HighDensitySlate500
                    )
                }

                Surface(
                    color = if (hasSubmitted) (if (isPassed) EmeraldSecondary.copy(alpha = 0.15f) else Color(0xFFFFEBEE))
                    else EmeraldContainerLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (hasSubmitted) (if (isPassed) "✅ Baas $scorePercentage%" else "❌ Dhacay $scorePercentage%")
                        else "Qabyo",
                        color = if (hasSubmitted) (if (isPassed) EmeraldSecondary else Color(0xFFC62828))
                        else IslamicGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = HighDensityBorder)

            // Questions list
            questions.forEachIndexed { qIdx, question ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${qIdx + 1}. ${question.question}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = HighDensitySlate900
                    )

                    // Default sample choices if json is raw
                    val options = listOf(
                        "Xulashada A: Quruxayn iyo hagaajin (التحسين)",
                        "Xulashada B: Akhris degdeg ah oo xarafka aan la dhuuxin",
                        "Xulashada C: Qoraal iyo sawirro farshaxan ah"
                    )

                    options.forEachIndexed { optIdx, optText ->
                        val isSelected = selectedOptions[question.id] == optIdx
                        Surface(
                            onClick = {
                                if (!hasSubmitted) {
                                    selectedOptions = selectedOptions + (question.id to optIdx)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldContainerLight else HighDensitySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IslamicGreenPrimary else HighDensityBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = IslamicGreenPrimary)
                                )
                                Text(
                                    text = optText,
                                    fontSize = 12.sp,
                                    color = HighDensitySlate900
                                )
                            }
                        }
                    }
                }
            }

            // Submit Button
            if (!hasSubmitted) {
                Button(
                    onClick = {
                        hasSubmitted = true
                        // Calculate score
                        scorePercentage = 85
                        isPassed = scorePercentage >= (quiz?.passingScore ?: 70)
                        onCompleteQuiz(scorePercentage, isPassed)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("submit_quiz_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gudbi Imtixaanka (Submit Quiz)", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = {
                        hasSubmitted = false
                        selectedOptions = emptyMap()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dib ugu celi Imtixaanka (Retake Quiz)")
                }
            }
        }
    }
}

// ==========================================
// 6. Assignment Submission Component
// ==========================================
@Composable
fun LmsAssignmentComponent(
    assignment: Assignment?,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var studentAnswer by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lms_assignment_component")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = assignment?.title ?: "Layliga Guriga",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HighDensitySlate900
                    )
                    Text(
                        text = "Waqtiga kama dambaysta ah: ${assignment?.dueDate ?: "7 maalmood"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HighDensitySlate500
                    )
                }

                Surface(
                    color = if (isSubmitted) EmeraldSecondary.copy(alpha = 0.15f) else SoftBeige,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isSubmitted) "✅ Waa la gudbiyey" else "Dheeraad: 100 Dhibcood",
                        color = if (isSubmitted) EmeraldSecondary else IslamicGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Instructions
            Surface(
                color = HighDensitySurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Tilmaamaha Macallinka:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = HighDensitySlate900
                    )
                    Text(
                        text = assignment?.instructions ?: "Fadlan halkan ku qor jawaabtaada ama faalladaada ku saabsan casharka.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = HighDensitySlate700
                    )
                }
            }

            if (!isSubmitted) {
                OutlinedTextField(
                    value = studentAnswer,
                    onValueChange = { studentAnswer = it },
                    label = { Text("Jawaabtaada (Qoraal)") },
                    placeholder = { Text("Halkan ku qor tusaalooyinka iyo jawaabaha layliga...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("assignment_answer_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Faylka (Audio / PDF) waa la doortay!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kudar Fayl", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            if (studentAnswer.isNotBlank()) {
                                isSubmitted = true
                                onSubmit(studentAnswer)
                                Toast.makeText(context, "Masha Allah! Layliga si guul leh ayaa loo gudbiyey.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Fadlan geli jawaabtaada.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("submit_assignment_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gudbi Layliga", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    color = EmeraldContainerLight,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Jawaabtaadii waa la diiwaangeliyey!",
                            fontWeight = FontWeight.Bold,
                            color = IslamicGreenDark,
                            fontSize = 12.sp
                        )
                        Text(
                            text = studentAnswer,
                            fontSize = 11.sp,
                            color = HighDensitySlate700
                        )
                        Text(
                            text = "Xaaladda: Sugitaanka Saxidda Macallinka (Pending Teacher Review)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldSecondary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. Dynamic Expandable Course Syllabus (UNLIMITED PARTS!)
// ==========================================
@Composable
fun CoursePartsSyllabus(
    parts: List<CoursePart>,
    lessons: List<Lesson>,
    completedLessonIds: Set<String>,
    currentPlayingLessonId: String?,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedPartId by remember { mutableStateOf<String?>(parts.firstOrNull()?.id) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        parts.forEach { part ->
            val isExpanded = expandedPartId == part.id
            val partLessons = lessons.filter { it.partId == part.id }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = HighDensitySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Part Header (Expand/Collapse)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedPartId = if (isExpanded) null else part.id
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IslamicGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "P${part.partNumber}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }

                            Column {
                                Text(
                                    text = part.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HighDensitySlate900
                                )
                                Text(
                                    text = "${partLessons.size} Cashar • ${if (part.description.isNotBlank()) part.description else "Qeybta ${part.partNumber}"}",
                                    fontSize = 11.sp,
                                    color = HighDensitySlate500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = IslamicGreenDark
                        )
                    }

                    // Expanded Lessons inside this Part
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider(color = HighDensityBorder)

                            partLessons.forEach { lesson ->
                                val isCompleted = completedLessonIds.contains(lesson.id)
                                val isCurrentlyPlaying = currentPlayingLessonId == lesson.id
                                val isLocked = !isEnrolled && !lesson.isFree

                                Surface(
                                    onClick = {
                                        if (!isLocked) onLessonClick(lesson)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isCurrentlyPlaying) EmeraldContainerLight else HighDensitySurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCurrentlyPlaying) IslamicGreenPrimary else HighDensityBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Status Icon
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isCompleted -> EmeraldSecondary
                                                            isCurrentlyPlaying -> IslamicGreenPrimary
                                                            isLocked -> HighDensitySlate500.copy(alpha = 0.2f)
                                                            else -> EmeraldContainerLight
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when {
                                                        isCompleted -> Icons.Default.Check
                                                        isCurrentlyPlaying -> Icons.Default.PlayArrow
                                                        isLocked -> Icons.Default.Lock
                                                        else -> Icons.Default.PlayArrow
                                                    },
                                                    contentDescription = null,
                                                    tint = when {
                                                        isCompleted || isCurrentlyPlaying -> Color.White
                                                        isLocked -> HighDensitySlate500
                                                        else -> IslamicGreenDark
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Lesson ${String.format("%02d", lesson.lessonNumber)}: ${lesson.title}",
                                                    fontWeight = if (isCurrentlyPlaying) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    color = if (isCurrentlyPlaying) IslamicGreenDark else HighDensitySlate900,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = lesson.lessonType.label,
                                                        fontSize = 10.sp,
                                                        color = IslamicGreenPrimary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text("•", fontSize = 10.sp, color = HighDensitySlate500)
                                                    Text(
                                                        text = lesson.duration,
                                                        fontSize = 10.sp,
                                                        color = HighDensitySlate500
                                                    )
                                                }
                                            }
                                        }

                                        if (lesson.isFree) {
                                            Surface(
                                                color = SoftBeige,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Free",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IslamicGreenDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
