package com.example.ui.screens.quran

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HifdhProgress
import com.example.data.util.QuranReciter
import com.example.data.util.QuranSurahData
import com.example.data.util.SurahDetail
import com.example.data.util.SurahMeta
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

enum class QuranTabMode(val label: String) {
    SURAHS("114 Suuradood"),
    HIFDH("Hifdh Tracker")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranHifdhScreen(
    viewModel: MandeqViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(QuranTabMode.SURAHS) }

    // Quran State
    val audioState by viewModel.audioPlayerState.collectAsState()
    val selectedReciter by viewModel.selectedReciter.collectAsState()
    val favoriteSurahs by viewModel.favoriteSurahs.collectAsState()
    val lastReadSurahNum by viewModel.lastReadSurah.collectAsState()
    val lastReadAyahNum by viewModel.lastReadAyah.collectAsState()
    val bookmarkedAyahs by viewModel.bookmarkedAyahs.collectAsState()

    // Hifdh State
    val hifdhList by viewModel.hifdhList.collectAsState()
    val dailyHifdhGoal by viewModel.dailyHifdhGoal.collectAsState()
    val todayMemorizedAyahs by viewModel.todayMemorizedAyahs.collectAsState()

    // Search and filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") } // "all", "makki", "madani", "favorites"
    var showReciterDialog by remember { mutableStateOf(false) }

    // Active Reader State
    var activeSurahForReading by remember { mutableStateOf<SurahDetail?>(null) }
    var editingHifdhItem by remember { mutableStateOf<HifdhProgress?>(null) }
    var showGoalDialog by remember { mutableStateOf(false) }

    // Filtered Surahs list
    val filteredSurahs = remember(searchQuery, selectedFilter, favoriteSurahs) {
        val list = QuranSurahData.searchSurahs(searchQuery)
        when (selectedFilter) {
            "makki" -> list.filter { it.revelationType.equals("Makki", ignoreCase = true) }
            "madani" -> list.filter { it.revelationType.equals("Madani", ignoreCase = true) }
            "favorites" -> list.filter { favoriteSurahs.contains(it.number) }
            else -> list
        }
    }

    // Reciter Selection Dialog
    if (showReciterDialog) {
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Text(
                    text = "Dooro Qaariga (Select Reciter)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = IslamicGreenDark
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuranSurahData.RECITERS.forEach { reciter ->
                        val isSelected = reciter.id == selectedReciter.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldContainerLight else HighDensitySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IslamicGreenPrimary else HighDensityBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectReciter(reciter)
                                    showReciterDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reciter.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = HighDensitySlate900
                                    )
                                    Text(
                                        text = "${reciter.arabicName} • ${reciter.somaliLabel}",
                                        fontSize = 11.sp,
                                        color = HighDensitySlate700
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = IslamicGreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterDialog = false }) {
                    Text("Xir (Close)")
                }
            }
        )
    }

    // Daily Hifdh Goal Dialog
    if (showGoalDialog) {
        var goalInput by remember { mutableStateOf(dailyHifdhGoal.toString()) }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = "Deji Yoolka Maalinlaha ah ee Hifdhiga",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Geli inta aayadood ee aad doonayso inaad xafiddo maalintiiba:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Yoolka (Ayahs)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = goalInput.toIntOrNull() ?: dailyHifdhGoal
                        viewModel.setDailyHifdhGoal(num)
                        showGoalDialog = false
                    }
                ) {
                    Text("Deji")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    // Edit Hifdh Item Dialog
    if (editingHifdhItem != null) {
        val item = editingHifdhItem!!
        var currentMemorized by remember(item) { mutableStateOf(item.memorizedAyahs.toString()) }

        AlertDialog(
            onDismissRequest = { editingHifdhItem = null },
            title = {
                Text(
                    text = "Cusbooneysii Hifdhiga: ${item.surahName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Geli tirada aayadaha aad xafidday (Wadarta: ${item.totalAyahs} aayadood):", fontSize = 12.sp)
                    OutlinedTextField(
                        value = currentMemorized,
                        onValueChange = { currentMemorized = it.filter { char -> char.isDigit() } },
                        label = { Text("Aayadaha aad xafidday") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = currentMemorized.toIntOrNull() ?: item.memorizedAyahs
                        viewModel.updateHifdhProgress(item.id, num.coerceIn(0, item.totalAyahs), item.totalAyahs)
                        editingHifdhItem = null
                    }
                ) {
                    Text("Keydi")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingHifdhItem = null }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    // Full Quran Arabic Reader Dialog
    if (activeSurahForReading != null) {
        val surah = activeSurahForReading!!
        val isCurrentPlaying = audioState.isPlaying && audioState.currentSurahNumber == surah.number

        Dialog(
            onDismissRequest = { activeSurahForReading = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "${surah.number}. Suuradda ${surah.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = IslamicGreenDark
                                )
                                Text(
                                    text = "${surah.arabicName} • ${surah.revelationType} • ${surah.totalAyahs} Ayahs",
                                    fontSize = 11.sp,
                                    color = HighDensitySlate500
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { activeSurahForReading = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Reader",
                                    tint = IslamicGreenDark
                                )
                            }
                        },
                        actions = {
                            // Play audio recitation for this Surah
                            IconButton(
                                onClick = {
                                    if (isCurrentPlaying) {
                                        viewModel.toggleAudioPlayPause()
                                    } else {
                                        viewModel.playSurah(surah.number)
                                    }
                                }
                            ) {
                                if (audioState.isBuffering && audioState.currentSurahNumber == surah.number) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = if (isCurrentPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                        contentDescription = "Play/Pause Audio",
                                        tint = IslamicGreenDark,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensitySurface)
                    )
                },
                bottomBar = {
                    Surface(
                        color = HighDensitySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    val prevNum = if (surah.number > 1) surah.number - 1 else 114
                                    activeSurahForReading = QuranSurahData.getSurahDetail(prevNum, selectedReciter.id)
                                    viewModel.setLastRead(prevNum)
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Suuraddii Hore", fontSize = 12.sp)
                            }

                            Text(
                                text = "${surah.number} / 114",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensitySlate700
                            )

                            TextButton(
                                onClick = {
                                    val nextNum = if (surah.number < 114) surah.number + 1 else 1
                                    activeSurahForReading = QuranSurahData.getSurahDetail(nextNum, selectedReciter.id)
                                    viewModel.setLastRead(nextNum)
                                }
                            ) {
                                Text("Suuradda Xigta", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HighDensityBg)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Reciter Bar in Reader
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldContainerLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Headphones, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = selectedReciter.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = IslamicGreenDark
                                    )
                                }
                                TextButton(
                                    onClick = { showReciterDialog = true },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Beddel (Change)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Bismillah Banner for Surahs other than At-Tawbah (9)
                    if (surah.number != 9) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = HighDensitySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    color = IslamicGreenDark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                )
                            }
                        }
                    }

                    // Ayahs List
                    items(surah.verses) { verse ->
                        val isBookmarked = bookmarkedAyahs.contains("${surah.number}:${verse.verseNumber}")

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HighDensitySurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isBookmarked) SoftGoldDark else HighDensityBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Verse Header (Number + Actions)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isBookmarked) SoftGoldDark else IslamicGreenDark
                                    ) {
                                        Text(
                                            text = "${verse.verseNumber}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Bookmark Ayah
                                        IconButton(
                                            onClick = {
                                                viewModel.toggleAyahBookmark(surah.number, verse.verseNumber)
                                                viewModel.setLastRead(surah.number, verse.verseNumber)
                                                Toast.makeText(
                                                    context,
                                                    if (!isBookmarked) "Aayadda waa la calaamadeeyey (Bookmarked)" else "Calaamaddii waa laga saaray",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                                contentDescription = "Bookmark Ayah",
                                                tint = if (isBookmarked) SoftGoldDark else HighDensitySlate500,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Share Ayah
                                        IconButton(
                                            onClick = {
                                                val shareText = "${verse.arabic}\n\n${verse.somaliTranslation}\n\n[Suuradda ${surah.name} : Aayadda ${verse.verseNumber}] - Mandeq Islamic"
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "La wadaag Aayaddan"))
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Share,
                                                contentDescription = "Share Ayah",
                                                tint = HighDensitySlate500,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Arabic Ayah Text (Large, High Contrast)
                                Text(
                                    text = verse.arabic,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 36.sp,
                                        textAlign = TextAlign.Right
                                    ),
                                    color = HighDensitySlate900,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                HorizontalDivider(color = HighDensityBorder.copy(alpha = 0.6f))

                                // Somali Translation
                                if (verse.somaliTranslation.isNotBlank()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Macnaha Af-Soomaaliga:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IslamicGreenDark
                                        )
                                        Text(
                                            text = verse.somaliTranslation,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp
                                            ),
                                            color = HighDensitySlate900
                                        )
                                    }
                                }

                                // English Translation
                                if (verse.englishTranslation.isNotBlank()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "English Translation:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HighDensitySlate500
                                        )
                                        Text(
                                            text = verse.englishTranslation,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp
                                            ),
                                            color = HighDensitySlate700
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

    // Main Quran Screen Scaffold layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityBg)
            .testTag("quran_hifdh_screen")
    ) {
        // Tab Mode Row: 114 Surahs vs Hifdh Tracker
        Surface(
            color = HighDensitySurface,
            border = androidx.compose.foundation.BorderStroke(0.dp, HighDensityBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuranTabMode.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        label = {
                            Text(
                                text = tab.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (tab == QuranTabMode.SURAHS) Icons.Default.MenuBook else Icons.Default.Checklist,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Screen Body depending on Tab
        Box(modifier = Modifier.weight(1f)) {
            if (currentTab == QuranTabMode.SURAHS) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(IslamicGreenDark, IslamicGreenPrimary)
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📖 Qur'aanka Kariimka ah",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SoftGold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSecondary.copy(alpha = 0.35f)
                                    ) {
                                        Text(
                                            text = "114 Suuradood",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "﴿ إِنَّ هَٰذَا الْقُرْآنَ يَهْدِي لِلَّتِي هِيَ أَقْوَمُ ﴾",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = "“Qur'aankan wuxuu ku hanuuniyaa jidka ugu toosan.”",
                                    fontSize = 11.sp,
                                    color = TextLight.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Continue Reading / Last Read Position Card
                    item {
                        val lastMeta = QuranSurahData.getSurahMeta(lastReadSurahNum)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldContainerLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("continue_reading_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(SoftGoldDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                    Column {
                                        Text(
                                            text = "Sii wad akhriska (Continue Reading)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = HighDensitySlate700
                                        )
                                        Text(
                                            text = "${lastMeta.number}. ${lastMeta.name} (${lastMeta.arabicName})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = HighDensitySlate900
                                        )
                                        Text(
                                            text = "Aayadda $lastReadAyahNum • ${lastMeta.totalAyahs} Aayadood",
                                            fontSize = 10.sp,
                                            color = HighDensitySlate500
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            activeSurahForReading = QuranSurahData.getSurahDetail(lastMeta.number, selectedReciter.id)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenDark),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Akhriso", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Search & Reciter selector
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Search bar
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Raadi Suurad (magac, af-carabi ama tiro)...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("surah_search_input")
                            )

                            // Reciter Selector Pill
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HighDensitySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showReciterDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("Qaariga (Reciter):", fontSize = 9.sp, color = HighDensitySlate500)
                                            Text(selectedReciter.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HighDensitySlate900)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldContainerLight
                                    ) {
                                        Text(
                                            text = "Beddel",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IslamicGreenDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // Filter chips row
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedFilter == "all",
                                        onClick = { selectedFilter = "all" },
                                        label = { Text("Dhammaan (114)", fontSize = 11.sp) }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedFilter == "makki",
                                        onClick = { selectedFilter = "makki" },
                                        label = { Text("Makki (86)", fontSize = 11.sp) }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedFilter == "madani",
                                        onClick = { selectedFilter = "madani" },
                                        label = { Text("Madani (28)", fontSize = 11.sp) }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedFilter == "favorites",
                                        onClick = { selectedFilter = "favorites" },
                                        label = { Text("Xulashada (${favoriteSurahs.size})", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(12.dp), tint = SoftGoldDark) }
                                    )
                                }
                            }
                        }
                    }

                    // Surah Cards List (114 Surahs canonical cards)
                    items(filteredSurahs, key = { it.number }) { surah ->
                        val isFav = favoriteSurahs.contains(surah.number)
                        val isPlayingThis = audioState.isPlaying && audioState.currentSurahNumber == surah.number

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeSurahForReading = QuranSurahData.getSurahDetail(surah.number, selectedReciter.id)
                                    viewModel.setLastRead(surah.number)
                                }
                                .testTag("surah_card_${surah.number}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Surah Number badge & Names
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldContainerLight,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = String.format("%02d", surah.number),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                ),
                                                color = IslamicGreenDark
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = surah.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = HighDensitySlate900
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (surah.revelationType == "Makki") EmeraldContainerLight else GoldContainerLight
                                            ) {
                                                Text(
                                                    text = surah.revelationType,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (surah.revelationType == "Makki") IslamicGreenDark else SoftGoldDark,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${surah.totalAyahs} Ayahs • ${surah.somaliMeaning}",
                                            fontSize = 11.sp,
                                            color = HighDensitySlate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Arabic Name + Favorite & Play buttons
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = surah.arabicName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = IslamicGreenDark,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )

                                    // Favorite toggle
                                    IconButton(
                                        onClick = { viewModel.toggleFavoriteSurah(surah.number) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (isFav) Color.Red else HighDensitySlate500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Direct Play button
                                    IconButton(
                                        onClick = {
                                            if (isPlayingThis) {
                                                viewModel.toggleAudioPlayPause()
                                            } else {
                                                viewModel.playSurah(surah.number)
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        if (audioState.isBuffering && audioState.currentSurahNumber == surah.number) {
                                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(
                                                imageVector = if (isPlayingThis) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                                contentDescription = "Dhageyso Suuradda",
                                                tint = IslamicGreenPrimary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // HIFDH TRACKER MODE
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Daily Goal Widget
                    item {
                        val progressFraction = (todayMemorizedAyahs.toFloat() / dailyHifdhGoal.toFloat()).coerceIn(0f, 1f)
                        val percentInt = (progressFraction * 100).toInt()

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldContainerLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("daily_hifdh_goal_card")
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Flag, contentDescription = null, tint = SoftGoldDark, modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = "Daily Hifdh Goal",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = HighDensitySlate900
                                            )
                                            Text("Yoolka Maalinlaha ah", fontSize = 10.sp, color = HighDensitySlate500)
                                        }
                                    }

                                    Surface(
                                        onClick = { showGoalDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SoftGoldDark
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "$dailyHifdhGoal Ayahs Today",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Horumarka Maanta: $todayMemorizedAyahs / $dailyHifdhGoal Aayadood",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = HighDensitySlate900
                                    )
                                    Text(
                                        text = "$percentInt%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (percentInt >= 100) EmeraldSecondary else SoftGoldDark
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = if (percentInt >= 100) EmeraldSecondary else SoftGoldDark,
                                    trackColor = Color.White
                                )

                                Button(
                                    onClick = { viewModel.incrementDailyHifdhAyah() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                                    contentPadding = PaddingValues(vertical = 8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("increment_hifdh_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Xaqiiji Aayad Cusub Maanta (+1 Ayah)", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Hifdh Progress List Header
                    item {
                        Text(
                            text = "Kala soco heerka xafidaada suuradaha:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = IslamicGreenDark
                        )
                    }

                    // Hifdh items list
                    items(hifdhList) { item ->
                        val fraction = (item.memorizedAyahs.toFloat() / item.totalAyahs.toFloat()).coerceIn(0f, 1f)
                        val pInt = (fraction * 100).toInt()

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.surahName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = HighDensitySlate900
                                        )
                                        Text(
                                            text = "${item.arabicSurahName} • ${item.memorizedAyahs} / ${item.totalAyahs} aayadood",
                                            fontSize = 11.sp,
                                            color = HighDensitySlate500
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { editingHifdhItem = item },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Wax ka beddel", fontSize = 11.sp)
                                        }
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { fraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(CircleShape),
                                    color = if (pInt >= 100) EmeraldSecondary else IslamicGreenPrimary,
                                    trackColor = HighDensityBorder
                                )
                            }
                        }
                    }
                }
            }

            // STICKY QURAN AUDIO PLAYER CONTROLLER
            if (audioState.isPlaying || audioState.durationMs > 0 || audioState.currentUrl.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = HighDensitySurface,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Title & Reciter + Close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = audioState.title.ifEmpty { "Qur'aan Recitation" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = IslamicGreenDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = audioState.subtitle.ifEmpty { selectedReciter.somaliLabel },
                                    fontSize = 10.sp,
                                    color = HighDensitySlate500,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Speed chip
                                FilterChip(
                                    selected = audioState.speed > 1.0f,
                                    onClick = {
                                        val newSpeed = when (audioState.speed) {
                                            1.0f -> 1.25f
                                            1.25f -> 1.5f
                                            else -> 1.0f
                                        }
                                        viewModel.setAudioSpeed(newSpeed)
                                    },
                                    label = { Text("${audioState.speed}x", fontSize = 10.sp) },
                                    modifier = Modifier.height(28.dp)
                                )

                                IconButton(
                                    onClick = { viewModel.stopAudio() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Player", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Progress Slider
                        val currentMs = audioState.currentPositionMs.toFloat()
                        val totalMs = audioState.durationMs.coerceAtLeast(1).toFloat()
                        Slider(
                            value = (currentMs / totalMs).coerceIn(0f, 1f),
                            onValueChange = { frac ->
                                viewModel.seekAudioTo((frac * totalMs).toInt())
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                        )

                        // Media Controls Row: Repeat, Prev, -10s, Play/Pause, +10s, Next
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Repeat toggle
                            IconButton(
                                onClick = { viewModel.toggleAudioRepeat() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Repeat",
                                    tint = if (audioState.isRepeat) SoftGoldDark else HighDensitySlate500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Previous Surah
                            IconButton(
                                onClick = { viewModel.playPreviousSurah() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Prev Surah", tint = IslamicGreenDark)
                            }

                            // -10s
                            IconButton(
                                onClick = { viewModel.skipAudio(-10) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Replay10, contentDescription = "-10s", tint = IslamicGreenDark)
                            }

                            // Play / Pause
                            IconButton(
                                onClick = { viewModel.toggleAudioPlayPause() },
                                modifier = Modifier.size(42.dp)
                            ) {
                                if (audioState.isBuffering) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = if (audioState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                        contentDescription = "Play/Pause",
                                        tint = IslamicGreenDark,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }

                            // +10s
                            IconButton(
                                onClick = { viewModel.skipAudio(10) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Forward10, contentDescription = "+10s", tint = IslamicGreenDark)
                            }

                            // Next Surah
                            IconButton(
                                onClick = { viewModel.playNextSurah() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Surah", tint = IslamicGreenDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
