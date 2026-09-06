package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.SourcePlatformBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

enum class AdminTab(val title: String) {
    OVERVIEW("Dashboard"),
    DISCOVERY("Discovery"),
    CONTENT("Casharada"),
    CATEGORIES("Qeybaha"),
    STUDENTS("Ardayda")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AdminTab.DISCOVERY) }
    val adminStats by viewModel.adminStats.collectAsState()
    val allAdminLessons by viewModel.allAdminLessons.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Discovery States
    val discoveryUrl by viewModel.discoveryUrl.collectAsState()
    val extractedMetadata by viewModel.extractedMetadata.collectAsState()
    val isExtracting by viewModel.isExtracting.collectAsState()

    // Form inputs for editing / publishing discovered content
    var editTitle by remember { mutableStateOf("") }
    var editTeacher by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editSourceName by remember { mutableStateOf("") }
    var editDuration by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(LessonStatus.PUBLISHED) }
    var isFeatured by remember { mutableStateOf(false) }
    var tagsInput by remember { mutableStateOf("Islamic, Tajweed, Lesson") }

    // Sync form inputs when metadata extracted
    LaunchedEffect(extractedMetadata) {
        extractedMetadata?.let { meta ->
            editTitle = meta.title
            editTeacher = meta.teacherName
            editDescription = meta.description
            editSourceName = meta.sourceName
            editDuration = meta.duration
            if (categories.isNotEmpty() && selectedCategoryId.isEmpty()) {
                selectedCategoryId = categories.first().id
            }
        }
    }

    // Category Creation Dialog
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCatName by remember { mutableStateOf("") }
    var newCatArabic by remember { mutableStateOf("") }
    var newCatEmoji by remember { mutableStateOf("📖") }
    var newCatDesc by remember { mutableStateOf("") }
    var newCatSubs by remember { mutableStateOf("") }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Ku Dar Qeyb Cusub (New Category)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Magaca (Somali)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCatArabic,
                        onValueChange = { newCatArabic = it },
                        label = { Text("Magaca Carabiga (Arabic)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCatEmoji,
                        onValueChange = { newCatEmoji = it },
                        label = { Text("Astaanta (Emoji)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCatSubs,
                        onValueChange = { newCatSubs = it },
                        label = { Text("Qeybaha hoose (Subcategories)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            viewModel.addCategory(
                                name = newCatName,
                                arabicName = newCatArabic,
                                description = newCatDesc.ifBlank { newCatName },
                                iconEmoji = newCatEmoji.ifBlank { "📖" },
                                subcategories = newCatSubs
                            )
                            newCatName = ""
                            newCatArabic = ""
                            newCatSubs = ""
                            showAddCategoryDialog = false
                        }
                    }
                ) {
                    Text("Ku Dar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
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
                            text = "Admin Dashboard & CMS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = IslamicGreenDark
                        )
                        Text(
                            text = "Maamulka Casharada & Ansixinta",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = HighDensitySlate500
                        )
                    }
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
                actions = {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Super Admin",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = SoftGoldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighDensitySurface
                ),
                modifier = Modifier.border(0.dp, HighDensityBorder)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(HighDensityBg)
                .padding(innerPadding)
                .testTag("admin_dashboard_screen")
        ) {
            // Admin Tabs Scrollable Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 12.dp,
                containerColor = HighDensitySurface,
                divider = { HorizontalDivider(color = HighDensityBorder) }
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == tab) IslamicGreenDark else HighDensitySlate500
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                // TAB 1: OVERVIEW
                AdminTab.OVERVIEW -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Xaaladda Guud ee Akadeemiyada (Analytics)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = HighDensitySlate900
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatBox(
                                    title = "Total Students",
                                    value = "${adminStats?.totalStudents ?: 1420}",
                                    color = IslamicGreenDark,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatBox(
                                    title = "Total Lessons",
                                    value = "${allAdminLessons.size}",
                                    color = EmeraldSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatBox(
                                    title = "Published",
                                    value = "${allAdminLessons.count { it.status == LessonStatus.PUBLISHED }}",
                                    color = SoftGoldDark,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatBox(
                                    title = "Pending Review",
                                    value = "${allAdminLessons.count { it.status == LessonStatus.PENDING }}",
                                    color = Color(0xFFE65100),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Qeybta Ugu Caansan (Most Popular Category)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                        color = HighDensitySlate500
                                    )
                                    Text(
                                        text = "📖 Tajweed & Qur'aan (4,210 views)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = HighDensitySlate900
                                    )
                                    HorizontalDivider(color = HighDensityBorder)
                                    Text(
                                        text = "Casharka Ugu Badan ee La Daawaday (Most Watched)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                        color = HighDensitySlate500
                                    )
                                    Text(
                                        text = "Makhaarijul Xuruuf — Casharka 01 (Sheikh Cabdirashiid)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        ),
                                        color = HighDensitySlate900
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 2: CONTENT DISCOVERY (YouTube / Facebook URL extraction & Review)
                AdminTab.DISCOVERY -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Online Content Discovery (Soo Helitaanka Casharada)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = IslamicGreenDark
                                )
                                Text(
                                    text = "Geli linkiga casharka YouTube ama Facebook si xogta looga soo saaro.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = HighDensitySlate500
                                )
                            }
                        }

                        // Quick Topic Suggestions for Discovery
                        item {
                            Text(
                                text = "Baadh Mowduucyada Diiniga ah:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = HighDensitySlate700
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SuggestionChip(
                                    onClick = { viewModel.setDiscoveryUrl("https://www.youtube.com/watch?v=cI_gcMnlMwY") },
                                    label = { Text("Tajweed", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = { viewModel.setDiscoveryUrl("https://www.youtube.com/watch?v=BxG64B3pjlI") },
                                    label = { Text("Tafsir", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = { viewModel.setDiscoveryUrl("https://www.youtube.com/watch?v=mZc1bwFIhwI") },
                                    label = { Text("Fiqh", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = { viewModel.setDiscoveryUrl("https://www.youtube.com/watch?v=SNnF5gHFbMA") },
                                    label = { Text("Qur'aan", fontSize = 11.sp) }
                                )
                            }
                        }

                        // URL Input & Extract Action
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = discoveryUrl,
                                        onValueChange = { viewModel.setDiscoveryUrl(it) },
                                        label = { Text("Geli YouTube URL ama Facebook URL", fontSize = 12.sp) },
                                        placeholder = { Text("https://www.youtube.com/watch?v=...", fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Link, contentDescription = null, tint = IslamicGreenPrimary, modifier = Modifier.size(18.dp))
                                        },
                                        trailingIcon = {
                                            if (discoveryUrl.isNotBlank()) {
                                                IconButton(onClick = { viewModel.clearExtractedMetadata() }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_url_input")
                                    )

                                    Button(
                                        onClick = { viewModel.extractMetadataForUrl(discoveryUrl) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                        contentPadding = PaddingValues(vertical = 8.dp),
                                        enabled = discoveryUrl.isNotBlank() && !isExtracting,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                            .testTag("admin_extract_button")
                                    ) {
                                        if (isExtracting) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Soo Saarid Xogta...", fontSize = 12.sp)
                                        } else {
                                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Extract Metadata (Soo Saari Xogta)", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Extracted Metadata & Review Form
                        extractedMetadata?.let { meta ->
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    modifier = Modifier.testTag("admin_review_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Dib-u-eegis & Ansixin (Review & Publish)",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                ),
                                                color = IslamicGreenDark
                                            )
                                            SourcePlatformBadge(platform = meta.sourcePlatform)
                                        }

                                        HorizontalDivider(color = HighDensityBorder)

                                        OutlinedTextField(
                                            value = editTitle,
                                            onValueChange = { editTitle = it },
                                            label = { Text("Cinwaanka Casharka (Title)", fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        OutlinedTextField(
                                            value = editTeacher,
                                            onValueChange = { editTeacher = it },
                                            label = { Text("Magaca Sheekha / Macallinka (Teacher)", fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        OutlinedTextField(
                                            value = editSourceName,
                                            onValueChange = { editSourceName = it },
                                            label = { Text("Kanaalka / Bogga Asalka ah (Source Channel/Page)", fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        OutlinedTextField(
                                            value = editDuration,
                                            onValueChange = { editDuration = it },
                                            label = { Text("Dhererka Casharka (Duration e.g. 25:30)", fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // Category Picker
                                        Text("Dooro Qeybta (Category):", style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp), color = HighDensitySlate700)
                                        LazyRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            items(categories) { cat ->
                                                FilterChip(
                                                    selected = selectedCategoryId == cat.id,
                                                    onClick = { selectedCategoryId = cat.id },
                                                    label = { Text("${cat.iconEmoji} ${cat.name}", fontSize = 10.sp) }
                                                )
                                            }
                                        }

                                        OutlinedTextField(
                                            value = editDescription,
                                            onValueChange = { editDescription = it },
                                            label = { Text("Faahfaahinta (Description)", fontSize = 11.sp) },
                                            maxLines = 3,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // Status Selector
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Xaaladda (Status):", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp), fontWeight = FontWeight.SemiBold, color = HighDensitySlate900)
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                FilterChip(
                                                    selected = selectedStatus == LessonStatus.PUBLISHED,
                                                    onClick = { selectedStatus = LessonStatus.PUBLISHED },
                                                    label = { Text("Published", fontSize = 10.sp) }
                                                )
                                                FilterChip(
                                                    selected = selectedStatus == LessonStatus.PENDING,
                                                    onClick = { selectedStatus = LessonStatus.PENDING },
                                                    label = { Text("Pending", fontSize = 10.sp) }
                                                )
                                            }
                                        }

                                        // Feature Lesson Switch
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Cashar Gaar ah (Featured Lesson):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HighDensitySlate900)
                                            Switch(checked = isFeatured, onCheckedChange = { isFeatured = it })
                                        }

                                        // Mandatory Islamic Content Validation Checklist
                                        var isConfirmedIslamic by remember { mutableStateOf(false) }
                                        var isConfirmedAuthentic by remember { mutableStateOf(false) }
                                        var isConfirmedNoMusic by remember { mutableStateOf(false) }
                                        var isConfirmedEducational by remember { mutableStateOf(false) }

                                        val allVerificationPassed = isConfirmedIslamic && isConfirmedAuthentic && isConfirmedNoMusic && isConfirmedEducational

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (allVerificationPassed) EmeraldContainerLight else GoldContainerLight,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "Xaqiijinta Ansixinta (Mandatory Verification):",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = IslamicGreenDark
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(checked = isConfirmedIslamic, onCheckedChange = { isConfirmedIslamic = it }, modifier = Modifier.size(26.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Muuqaalku waa mid diini ah (Video is Islamic)", fontSize = 11.sp)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(checked = isConfirmedAuthentic, onCheckedChange = { isConfirmedAuthentic = it }, modifier = Modifier.size(26.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Isha laga soo xigtay waa sugan tahay (Source is authentic)", fontSize = 11.sp)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(checked = isConfirmedNoMusic, onCheckedChange = { isConfirmedNoMusic = it }, modifier = Modifier.size(26.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Ma jiro wax heeso ama muusig ah (No music included)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(checked = isConfirmedEducational, onCheckedChange = { isConfirmedEducational = it }, modifier = Modifier.size(26.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Wuxuu leeyahay qiime waxbarasho (Educational value confirmed)", fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        // Publish / Save Button
                                        Button(
                                            onClick = {
                                                val catId = selectedCategoryId.ifBlank { categories.firstOrNull()?.id ?: "cat_quran" }
                                                viewModel.publishDiscoveredLesson(
                                                    title = editTitle.ifBlank { meta.title },
                                                    description = editDescription.ifBlank { meta.description },
                                                    categoryId = catId,
                                                    teacherName = editTeacher.ifBlank { meta.teacherName },
                                                    sourceUrl = meta.url,
                                                    sourcePlatform = meta.sourcePlatform,
                                                    sourceName = editSourceName.ifBlank { meta.sourceName },
                                                    duration = editDuration.ifBlank { meta.duration },
                                                    thumbnailUrl = meta.thumbnailUrl,
                                                    status = selectedStatus,
                                                    featured = isFeatured,
                                                    tags = tagsInput
                                                )
                                                selectedTab = AdminTab.CONTENT
                                            },
                                            enabled = allVerificationPassed,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                            contentPadding = PaddingValues(vertical = 8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(38.dp)
                                                .testTag("admin_save_lesson_button")
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                if (allVerificationPassed) "Ansixi oo Daabac Casharka (Approve & Publish)" else "Fadlan xaqiiji 4-ta shuruudood ee sare",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: CONTENT MANAGEMENT (List of all lessons, approve/reject/delete)
                AdminTab.CONTENT -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Maamulka Casharada (${allAdminLessons.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = HighDensitySlate900
                            )
                        }

                        items(allAdminLessons) { lesson ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (lesson.status) {
                                                LessonStatus.PUBLISHED -> EmeraldContainerLight
                                                LessonStatus.PENDING -> GoldContainerLight
                                                LessonStatus.REJECTED -> Color(0xFFFFEBEE)
                                                else -> HighDensitySurface
                                            },
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                when (lesson.status) {
                                                    LessonStatus.PUBLISHED -> IslamicGreenDark.copy(alpha = 0.3f)
                                                    LessonStatus.PENDING -> SoftGoldDark.copy(alpha = 0.3f)
                                                    LessonStatus.REJECTED -> Color.Red.copy(alpha = 0.3f)
                                                    else -> HighDensityBorder
                                                }
                                            )
                                        ) {
                                            Text(
                                                text = lesson.status.name,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = when (lesson.status) {
                                                    LessonStatus.PUBLISHED -> IslamicGreenDark
                                                    LessonStatus.PENDING -> SoftGoldDark
                                                    LessonStatus.REJECTED -> Color.Red
                                                    else -> HighDensitySlate900
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        SourcePlatformBadge(platform = lesson.sourcePlatform)
                                    }

                                    Text(
                                        text = lesson.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = HighDensitySlate900
                                    )

                                    Text(
                                        text = "Macallinka: ${lesson.teacherName} • ${lesson.duration}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = HighDensitySlate500
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (lesson.status == LessonStatus.PENDING) {
                                            TextButton(
                                                onClick = { viewModel.updateLessonStatus(lesson.id, LessonStatus.PUBLISHED) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = IslamicGreenDark)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve", color = IslamicGreenDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            TextButton(
                                                onClick = { viewModel.updateLessonStatus(lesson.id, LessonStatus.REJECTED) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reject", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteLesson(lesson) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = HighDensitySlate500, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 4: CATEGORIES MANAGEMENT
                AdminTab.CATEGORIES -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Qeybaha Diiniga ah (${categories.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = HighDensitySlate900
                                )
                                Button(
                                    onClick = { showAddCategoryDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Qeyb Cusub", fontSize = 11.sp)
                                }
                            }
                        }

                        items(categories) { cat ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HighDensitySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                shadowElevation = 0.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(cat.iconEmoji, fontSize = 22.sp)
                                        Column {
                                            Text(
                                                text = "${cat.name} ${if (cat.arabicName.isNotBlank()) "(${cat.arabicName})" else ""}",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                ),
                                                color = HighDensitySlate900
                                            )
                                            Text(
                                                text = cat.description,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = HighDensitySlate500
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 5: STUDENTS MANAGEMENT
                AdminTab.STUDENTS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Ardayda Diiwaangashan (Total: 1,420)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = HighDensitySlate900
                            )
                        }

                        val demoStudents = listOf(
                            Triple("Cabdiraxmaan Cali", "student@mandeqislamic.so", "Active • 37 lessons completed"),
                            Triple("Faadumo Xasan", "fadumo.hassan@email.com", "Active • 22 lessons completed"),
                            Triple("Maxamed Nuur", "m.nuur99@email.com", "Active • 45 lessons completed"),
                            Triple("Khadra Axmed", "khadra.a@email.com", "Active • 12 lessons completed")
                        )

                        items(demoStudents) { student ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HighDensitySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                shadowElevation = 0.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldContainerLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = student.first.first().toString(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = IslamicGreenDark
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = student.first,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                ),
                                                color = HighDensitySlate900
                                            )
                                            Text(
                                                text = student.second,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = HighDensitySlate500
                                            )
                                            Text(
                                                text = student.third,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = EmeraldSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldContainerLight,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenDark.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "Active",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = IslamicGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
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

@Composable
private fun AdminStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = HighDensitySurface,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp), color = HighDensitySlate500)
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = color
            )
        }
    }
}
