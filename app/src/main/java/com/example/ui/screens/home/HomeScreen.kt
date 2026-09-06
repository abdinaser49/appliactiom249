package com.example.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Category
import com.example.data.model.Lesson
import com.example.data.util.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MandeqViewModel,
    onNavigateToLesson: (Lesson) -> Unit,
    onNavigateToCategory: (Category) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHifdh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val featuredLessons by viewModel.featuredLessons.collectAsState()
    val allLessons by viewModel.publishedLessons.collectAsState()
    val continueLessonState by viewModel.continueLearning.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val unreadCount = notifications.count { !it.isRead }

    val selectedCity by viewModel.selectedCity.collectAsState()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val selectedDhikr by viewModel.selectedDhikr.collectAsState()
    val tasbeehCount by viewModel.tasbeehCount.collectAsState()
    val tasbeehTotal by viewModel.tasbeehTotal.collectAsState()
    val haptic = LocalHapticFeedback.current

    var showCityDialog by remember { mutableStateOf(false) }
    var showTasbeehDialog by remember { mutableStateOf(false) }
    var showQiblaDialog by remember { mutableStateOf(false) }
    var showDuasDialog by remember { mutableStateOf(false) }

    // Dialog 1: City Selector
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text(
                    text = "Dooro Magaalada (Select City)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(PrayerTimesCalculator.SUPPORTED_CITIES) { city ->
                        Surface(
                            onClick = {
                                viewModel.selectCity(city)
                                showCityDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (city.name == selectedCity.name) EmeraldContainerLight else Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = city.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = HighDensitySlate900
                                    )
                                    Text(
                                        text = city.country,
                                        fontSize = 11.sp,
                                        color = HighDensitySlate500
                                    )
                                }
                                if (city.name == selectedCity.name) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = IslamicGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Xir")
                }
            }
        )
    }

    // Dialog 2: Interactive Digital Tasbeeh / Sibxa
    if (showTasbeehDialog) {
        AlertDialog(
            onDismissRequest = { showTasbeehDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📿 Sibxadda Dijitaalka ah (Tasbeeh)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = IslamicGreenDark
                    )
                    IconButton(onClick = { viewModel.resetTasbeeh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = HighDensitySlate500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Dhikr Selector Tabs
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PrayerTimesCalculator.TASBEEH_LIST) { dhikr ->
                            FilterChip(
                                selected = dhikr.id == selectedDhikr.id,
                                onClick = { viewModel.selectDhikr(dhikr) },
                                label = { Text(dhikr.transliteration, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Active Dhikr Display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = selectedDhikr.arabic,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = IslamicGreenDark,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = selectedDhikr.translation,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = HighDensitySlate700,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Big Circular Tap Button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(IslamicGreenPrimary, IslamicGreenDark)
                                )
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.incrementTasbeeh()
                            }
                            .testTag("tasbeeh_tap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$tasbeehCount",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "/ ${selectedDhikr.targetCount}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = SoftGold
                            )
                        }
                    }

                    Text(
                        text = "Wadarta Guud: $tasbeehTotal jeer",
                        fontSize = 11.sp,
                        color = HighDensitySlate500
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTasbeehDialog = false }) {
                    Text("Dhammee")
                }
            }
        )
    }

    // Dialog 3: Qibla Compass
    if (showQiblaDialog) {
        AlertDialog(
            onDismissRequest = { showQiblaDialog = false },
            title = {
                Text(
                    text = "🧭 Jiheeyaha Qiblada (Qibla Direction)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = IslamicGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Magaalada: ${selectedCity.name}, ${selectedCity.country}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = HighDensitySlate900
                    )

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainerLight)
                            .border(2.dp, IslamicGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = "Qibla",
                                tint = IslamicGreenDark,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${prayerTimes.qiblaDirectionDegrees}°",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = IslamicGreenDark
                            )
                            Text(
                                text = "Ka'bah, Makkah",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = SoftGoldDark
                            )
                        }
                    }

                    Text(
                        text = "Jiheynta Qiblada xisaab ahaan waa ${prayerTimes.qiblaDirectionDegrees}° Waqooyiga ka xigta. U jeedso dhanka Kacbada sharafta leh.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = HighDensitySlate700
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQiblaDialog = false }) {
                    Text("Xir")
                }
            }
        )
    }

    // Dialog 4: Ducooyinka Maalinlaha ah (Daily Adhkar)
    if (showDuasDialog) {
        AlertDialog(
            onDismissRequest = { showDuasDialog = false },
            title = {
                Text(
                    text = "🤲 Ducooyinka Maalinlaha ah (Daily Du'as)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(PrayerTimesCalculator.DAILY_DUAS) { dua ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HighDensitySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dua.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = IslamicGreenDark
                                    )
                                    Text(
                                        text = dua.reference,
                                        fontSize = 10.sp,
                                        color = HighDensitySlate500
                                    )
                                }
                                Text(
                                    text = dua.arabic,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = HighDensitySlate900,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = dua.somali,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = HighDensitySlate700
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDuasDialog = false }) {
                    Text("Xir")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityBg)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(IslamicGreenDark, IslamicGreenPrimary)
                        )
                    )
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Top Bar (Greeting & Notifications)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Asalaamu Calaykum 👋",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = SoftGold
                                )
                            }
                            Text(
                                text = "Mandeq Islamic Academy",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "“Baro Diintaada, Kobci Iimaankaaga.”",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = EmeraldLight
                            )
                        }

                        // Notification Icon with Badge
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("home_notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = SoftGold, contentColor = IslamicGreenDark) {
                                            Text(unreadCount.toString(), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Search Bar
                    Surface(
                        onClick = onNavigateToSearch,
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("home_search_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = IslamicGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Raadi cashar, macallin, tafsiir...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = HighDensitySlate500
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Banner / Quran Academy Illustration Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(130.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.islamic_hero_banner),
                        contentDescription = "Mandeq Islamic Academy Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        IslamicGreenDark.copy(alpha = 0.88f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Akadeemiyada Mandeq",
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                color = SoftGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Casharro Diini ah oo\nLa Xaqiijiyey",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Koorsooyinka Qur'aanka, Tajweedka & Fiqiga",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // 2.5 Real Interactive Prayer Times & Daily Islamic Tools Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("prayer_times_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header: City and Change City Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Mosque,
                                contentDescription = null,
                                tint = IslamicGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Waqtiyada Salaadda",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = HighDensitySlate900
                            )
                        }

                        Surface(
                            onClick = { showCityDialog = true },
                            shape = RoundedCornerShape(6.dp),
                            color = HighDensitySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(12.dp))
                                Text(selectedCity.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HighDensitySlate900)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = HighDensitySlate500, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    // Next Prayer Alert Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenPrimary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Salaadda Xigta:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = HighDensitySlate500
                                )
                                Text(
                                    text = "${prayerTimes.nextPrayerName} — ${prayerTimes.nextPrayerTime}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = IslamicGreenDark
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = IslamicGreenDark
                            ) {
                                Text(
                                    text = "${prayerTimes.minutesUntilNextPrayer} daqiiqo",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // 5 Daily Prayers Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Pair("Subax", prayerTimes.fajr),
                            Pair("Duhur", prayerTimes.dhuhr),
                            Pair("Casar", prayerTimes.asr),
                            Pair("Maghrib", prayerTimes.maghrib),
                            Pair("Cishe", prayerTimes.isha)
                        ).forEach { (name, time) ->
                            val isNext = prayerTimes.nextPrayerName.contains(name, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isNext) EmeraldContainerLight else HighDensitySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isNext) IslamicGreenPrimary else HighDensityBorder
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isNext) IslamicGreenDark else HighDensitySlate500
                                    )
                                    Text(
                                        text = time.replace(" AM", "").replace(" PM", ""),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isNext) IslamicGreenDark else HighDensitySlate900
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = HighDensityBorder)

                    // Quick Islamic Utilities Row (Tasbeeh, Qibla, Duas, Hifdh)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showTasbeehDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("📿 Sibxad", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showQiblaDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("🧭 Qibla", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showDuasDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("🤲 Ducooyin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 3. Daily Inspiration / Ayah Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = HighDensitySurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SoftGoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Aayadda Maanta",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = IslamicGreenPrimary
                            )
                        }
                        Text(
                            text = "Suuradda Duxaa: 11",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = HighDensitySlate500
                        )
                    }

                    Text(
                        text = "﴿ وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ ﴾",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        color = IslamicGreenDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "“Nicmada Rabbigaana ka sheekee (kuna mahad naq).”",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = HighDensitySlate700,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 4. Continue Learning Section
        continueLessonState?.let { state ->
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue Learning",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = HighDensitySlate900
                        )
                        Text(
                            text = "Casharkii Ugu Dambeeyay",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = HighDensitySlate500
                        )
                    }

                    ContinueLearningCard(
                        lesson = state.lesson,
                        progressPercentage = state.progress.progressPercentage,
                        onContinueClick = {
                            viewModel.setPlayingLesson(state.lesson)
                            onNavigateToLesson(state.lesson)
                        }
                    )
                }
            }
        }

        // 5. Islamic Categories (Horizontal Scroll)
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Qeybaha Casharada (Categories)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = HighDensitySlate900
                    )
                    TextButton(
                        onClick = { viewModel.selectCategory(null) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "Dhammaan",
                            color = IslamicGreenPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        IslamicCategoryCard(
                            category = category,
                            isSelected = false,
                            onClick = {
                                viewModel.selectCategory(category.id)
                                onNavigateToCategory(category)
                            }
                        )
                    }
                }
            }
        }

        // 6. Featured Lessons Section
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Featured Lessons (Koorsooyinka Xulka)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = HighDensitySlate900
                        )
                    }
                }

                featuredLessons.take(3).forEach { lesson ->
                    val isBookmarked by viewModel.isLessonBookmarked(lesson.id).collectAsState(initial = false)
                    IslamicLessonCard(
                        lesson = lesson,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmark(lesson.id, lesson.courseId, isBookmarked) },
                        onClick = {
                            viewModel.setPlayingLesson(lesson)
                            onNavigateToLesson(lesson)
                        }
                    )
                }
            }
        }

        // 7. Recently Added Lessons Section
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Recently Added (Casharradii Ugu Dambeeyay)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = HighDensitySlate900
                )

                allLessons.drop(1).take(3).forEach { lesson ->
                    val isBookmarked by viewModel.isLessonBookmarked(lesson.id).collectAsState(initial = false)
                    IslamicLessonCard(
                        lesson = lesson,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmark(lesson.id, lesson.courseId, isBookmarked) },
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
