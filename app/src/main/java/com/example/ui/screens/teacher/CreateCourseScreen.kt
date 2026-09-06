package com.example.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseLanguage
import com.example.data.model.DifficultyLevel
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    onCourseCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()

    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "cat_tajweed") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(DifficultyLevel.BEGINNER) }
    var language by remember { mutableStateOf(CourseLanguage.SOMALI) }
    var duration by remember { mutableStateOf("12 Saacadood") }
    var requirements by remember { mutableStateOf("Aqriska xarfaha Carabiga") }
    var learningObjectives by remember { mutableStateOf("Fahamka xeerarka iyo ku dhaqanka aayadaha") }
    var tags by remember { mutableStateOf("Diini, Tajweed, Quran") }
    var isDraft by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abuur Koors Cusub (Create Course)", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Macluumaadka Koorsada (Course Information)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Cinwaanka Koorsada (Title)") },
                        placeholder = { Text("Tusaale: Barashada Naxwaha Al-Aajurroomiyyah") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("course_title_input")
                    )

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Cinwaan-hoosaadka (Subtitle)") },
                        placeholder = { Text("Dulmar kooban oo koorsada ah") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selection Chips
                    Text("Qeybta Diiniga ah (Category):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOfFirst { it.id == selectedCategoryId }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = HighDensitySurface,
                        divider = {}
                    ) {
                        categories.forEach { cat ->
                            Tab(
                                selected = selectedCategoryId == cat.id,
                                onClick = { selectedCategoryId = cat.id },
                                text = { Text("${cat.icon} ${cat.name}", fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Sharaxaadda Buuxda ee Koorsada") },
                        placeholder = { Text("Ujeeddooyinka, waxa ardaygu baranayo...") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    // Difficulty & Language
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Heerka (Level):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                DifficultyLevel.values().forEach { level ->
                                    FilterChip(
                                        selected = difficulty == level,
                                        onClick = { difficulty = level },
                                        label = { Text(level.name.take(3), fontSize = 10.sp) }
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Luuqadda:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                CourseLanguage.values().forEach { lang ->
                                    FilterChip(
                                        selected = language == lang,
                                        onClick = { language = lang },
                                        label = { Text(lang.name.take(3), fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Muddada Koorsada (Estimated Duration)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = requirements,
                        onValueChange = { requirements = it },
                        label = { Text("Shuruudaha Ardayga looga Baahan Yahay") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = learningObjectives,
                        onValueChange = { learningObjectives = it },
                        label = { Text("Waxyaabaha uu Baran Doono (Learning Objectives)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Calaamadaha (Tags)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Direct Cover Image Selection
                    Surface(
                        color = SoftBeige,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sawirka Koorsada (Cover Image)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IslamicGreenDark)
                                Text("Toos ugu kaydi Mandeq Storage (JPG, PNG)", fontSize = 10.sp, color = HighDensitySlate500)
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Sawirka koorsada waa la doortay!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Text("Dooro Sawir", fontSize = 11.sp)
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isDraft,
                            onCheckedChange = { isDraft = it }
                        )
                        Text("Ku keydi Qabyo ahaan (Save as Draft)", fontSize = 12.sp)
                    }

                    // Create Course Action Button
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.createCourse(
                                    title = title,
                                    subtitle = subtitle,
                                    categoryId = selectedCategoryId,
                                    description = description,
                                    difficulty = difficulty,
                                    language = language,
                                    duration = duration,
                                    requirements = requirements,
                                    learningObjectives = learningObjectives,
                                    tags = tags,
                                    isDraft = isDraft
                                )
                                Toast.makeText(context, "Masha Allah! Koorsada cusub si guul leh ayaa loo abuuray Part 01 wata.", Toast.LENGTH_LONG).show()
                                onCourseCreated()
                            } else {
                                Toast.makeText(context, "Fadlan gali cinwaanka koorsada.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_create_course_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isDraft) "Keydi Qabyo (Save Draft)" else "Daabac Koorsada (Publish Course)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
