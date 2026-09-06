package com.example.ui.screens.teacher

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCourseManagerScreen(
    course: Course,
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    onPreviewLesson: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parts by viewModel.selectedCourseParts.collectAsState()
    val lessons by viewModel.selectedCourseLessons.collectAsState()

    var showAddPartDialog by remember { mutableStateOf(false) }
    var newPartTitle by remember { mutableStateOf("") }
    var newPartNumber by remember { mutableStateOf("${parts.size + 1}") }
    var newPartDesc by remember { mutableStateOf("") }

    var showAddLessonDialog by remember { mutableStateOf(false) }
    var targetPartIdForLesson by remember { mutableStateOf("") }
    var newLessonTitle by remember { mutableStateOf("") }
    var newLessonType by remember { mutableStateOf(LessonType.VIDEO) }
    var newLessonContentUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var newLessonTextContent by remember { mutableStateOf("") }
    var newLessonDuration by remember { mutableStateOf("15:00") }
    var newLessonIsFree by remember { mutableStateOf(false) }

    var expandedPartId by remember { mutableStateOf<String?>(parts.firstOrNull()?.id) }

    // Dialog: Add Part (Supports unlimited dynamic parts: Part 1, Part 2, ... Part 100!)
    if (showAddPartDialog) {
        AlertDialog(
            onDismissRequest = { showAddPartDialog = false },
            title = {
                Text(
                    text = "Ku dar Qeyb Cusub (Add Part)",
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Waxaad koorsada ku dari kartaa qeybo aan xad lahayn (Part 1 ilaa Part 100+).",
                        fontSize = 12.sp,
                        color = HighDensitySlate700
                    )
                    OutlinedTextField(
                        value = newPartNumber,
                        onValueChange = { newPartNumber = it },
                        label = { Text("Lambarka Qeybta (Part Number)") },
                        placeholder = { Text("Tusaale: 5 ama 100") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPartTitle,
                        onValueChange = { newPartTitle = it },
                        label = { Text("Cinwaanka Qeybta (Part Title)") },
                        placeholder = { Text("Tusaale: Part 05 — Axkaamta Madd-ka") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPartDesc,
                        onValueChange = { newPartDesc = it },
                        label = { Text("Sharaxaadda Qeybta (Description)") },
                        placeholder = { Text("Faahfaahin kooban...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = newPartNumber.toIntOrNull() ?: (parts.size + 1)
                        val title = if (newPartTitle.isNotBlank()) newPartTitle else "Part $num"
                        viewModel.addCoursePart(course.id, num, title, newPartDesc)
                        showAddPartDialog = false
                        newPartTitle = ""
                        newPartDesc = ""
                        Toast.makeText(context, "Part $num si guul leh ayaa loogu daray!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                ) {
                    Text("Abuur Qeybta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPartDialog = false }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    // Dialog: Add Lesson inside selected Part (NO YOUTUBE!)
    if (showAddLessonDialog) {
        AlertDialog(
            onDismissRequest = { showAddLessonDialog = false },
            title = {
                Text(
                    text = "Ku dar Cashar Cusub (Add Lesson)",
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Dooro nooca casharka iyo xogtiisa tooska ah (Direct Upload to Supabase Storage, No YouTube):",
                        fontSize = 11.sp,
                        color = HighDensitySlate700
                    )

                    OutlinedTextField(
                        value = newLessonTitle,
                        onValueChange = { newLessonTitle = it },
                        label = { Text("Cinwaanka Casharka (Title)") },
                        placeholder = { Text("Tusaale: Sharaxaadda Maddul Asli") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Lesson Type Dropdown / Chips
                    Text("Nooca Casharka (Lesson Type):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(LessonType.VIDEO, LessonType.AUDIO, LessonType.PDF).forEach { type ->
                            FilterChip(
                                selected = newLessonType == type,
                                onClick = { newLessonType = type },
                                label = { Text(type.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(LessonType.TEXT, LessonType.QUIZ, LessonType.ASSIGNMENT).forEach { type ->
                            FilterChip(
                                selected = newLessonType == type,
                                onClick = { newLessonType = type },
                                label = { Text(type.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Direct Upload Simulation
                    Surface(
                        color = SoftBeige,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📁 Soo geli Faylka (Direct Upload)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGreenDark
                                )
                                Text(
                                    text = "MP4, MOV, MP3, ama PDF toos loogu keydinayo Mandeq Storage",
                                    fontSize = 10.sp,
                                    color = HighDensitySlate500
                                )
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Faylka si toos ah ayaa looga doortay aaladda (Direct Upload Ready)", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Text("Dooro Fayl", fontSize = 10.sp)
                            }
                        }
                    }

                    if (newLessonType == LessonType.TEXT) {
                        OutlinedTextField(
                            value = newLessonTextContent,
                            onValueChange = { newLessonTextContent = it },
                            label = { Text("Qoraalka Casharka (Rich Text)") },
                            placeholder = { Text("Halkan ku qor nuxurka casharka oo Soomaali & Carabi ah...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        )
                    }

                    OutlinedTextField(
                        value = newLessonDuration,
                        onValueChange = { newLessonDuration = it },
                        label = { Text("Waqtiga (Duration)") },
                        placeholder = { Text("15:00 ama 10 Bog") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = newLessonIsFree,
                            onCheckedChange = { newLessonIsFree = it }
                        )
                        Text("Cashar Bilaash ah (Free Preview)", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val partId = if (targetPartIdForLesson.isNotBlank()) targetPartIdForLesson else parts.firstOrNull()?.id ?: ""
                        val title = if (newLessonTitle.isNotBlank()) newLessonTitle else "Cashar Cusub"
                        val lessonNum = lessons.filter { it.partId == partId }.size + 1

                        viewModel.addLesson(
                            courseId = course.id,
                            partId = partId,
                            lessonNumber = lessonNum,
                            title = title,
                            description = "Cashar kooban oo lagu soo kordhiyey koorsada.",
                            lessonType = newLessonType,
                            contentUrl = newLessonContentUrl,
                            textContent = newLessonTextContent,
                            duration = newLessonDuration,
                            isFree = newLessonIsFree,
                            isPublished = true
                        )
                        showAddLessonDialog = false
                        newLessonTitle = ""
                        newLessonTextContent = ""
                        Toast.makeText(context, "Casharka si toos ah ayaa loogu daray!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                ) {
                    Text("Keydi Casharka")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLessonDialog = false }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Course Content & Part Manager",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = course.title,
                            fontSize = 11.sp,
                            color = HighDensitySlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddPartDialog = true }) {
                        Icon(Icons.Default.AddBox, contentDescription = "Add Part", tint = IslamicGreenDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensitySurface)
            )
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
            // Course Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = HighDensitySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nidaamka Qeybaha (Dynamic Parts Architecture)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IslamicGreenDark
                            )
                            Surface(color = EmeraldContainerLight, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "${parts.size} Qeybood (Parts)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGreenDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Macallin kasta wuxuu abuuri karaa qeybo aan xad lahayn (Part 1 ilaa Part 100+). Qeyb walba waxay xambaarsan kartaa casharro Video, Audio, PDF, Text, Quiz, iyo Assignment ah.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = HighDensitySlate700
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddPartDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ku dar Qeyb (Add Part)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // List of Parts & their Lessons
            items(parts) { part ->
                val isExpanded = expandedPartId == part.id
                val partLessons = lessons.filter { it.partId == part.id }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Part Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedPartId = if (isExpanded) null else part.id
                                }
                                .padding(12.dp),
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
                                        .size(34.dp)
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
                                        text = "${partLessons.size} Cashar ku jira",
                                        fontSize = 11.sp,
                                        color = HighDensitySlate500
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        targetPartIdForLesson = part.id
                                        showAddLessonDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Add Lesson",
                                        tint = IslamicGreenPrimary
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = HighDensitySlate500
                                )
                            }
                        }

                        // Expanded Part Content
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = HighDensityBorder)

                                if (partLessons.isEmpty()) {
                                    Text(
                                        text = "Qeybtan wali wax cashar ah kuma jiraan. Riix '+' si aad ugu darto cashar.",
                                        fontSize = 11.sp,
                                        color = HighDensitySlate500,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                partLessons.forEach { lesson ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = HighDensitySurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Surface(
                                                        color = EmeraldContainerLight,
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = lesson.lessonType.name,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = IslamicGreenDark,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = "Lesson ${lesson.lessonNumber}: ${lesson.title}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = HighDensitySlate900,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Text(
                                                    text = lesson.duration,
                                                    fontSize = 10.sp,
                                                    color = HighDensitySlate500
                                                )
                                            }

                                            // Lesson Actions: Preview, Duplicate, Delete
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Preview Lesson
                                                TextButton(
                                                    onClick = { onPreviewLesson(lesson) },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Fiiri (Preview)", fontSize = 10.sp)
                                                }

                                                // Duplicate Lesson (Section 31 of prompt!)
                                                TextButton(
                                                    onClick = {
                                                        viewModel.duplicateLesson(lesson.id)
                                                        Toast.makeText(context, "Casharkii si guul leh ayaa loo duplicate gareeyey!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Nuqul (Duplicate)", fontSize = 10.sp)
                                                }

                                                // Delete Lesson
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteLesson(lesson)
                                                        Toast.makeText(context, "Casharkii waa la tirtiray.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = Color(0xFFE53935),
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Quick Add Lesson Button at bottom of part
                                OutlinedButton(
                                    onClick = {
                                        targetPartIdForLesson = part.id
                                        showAddLessonDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ku dar Cashar Qeybta ${part.partNumber}", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
