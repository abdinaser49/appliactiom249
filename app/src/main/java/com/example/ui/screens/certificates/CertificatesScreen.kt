package com.example.ui.screens.certificates

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shahaadooyinka (Certificates)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = IslamicGreenDark
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(HighDensityBg)
                .padding(innerPadding)
                .testTag("certificates_screen"),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Shahaadooyinka aad ku guuleysatay",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = HighDensitySlate900
                )
            }

            // Certificate 1: Tajweed Basics
            item {
                IslamicCertificateCard(
                    courseTitle = "Aasaaska Cilmiga Tajweedka",
                    studentName = userProfile?.fullName ?: "Cabdiraxmaan Cali",
                    completionDate = "15 Ramadaan 1447H / 2026",
                    grade = "Mumtaaz (Grade: A+)",
                    onShare = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Waxaan ku guuleystay Shahaadada 'Aasaaska Cilmiga Tajweedka' ee Akadeemiyada Mandeq Islamic! 🎓✨")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "La wadaag shahaadadaada"))
                    }
                )
            }

            // Certificate 2: Quran Recitation & Makhaarij
            item {
                IslamicCertificateCard(
                    courseTitle = "Makhaarijul Xuruuf & Qiraa'ada Xafs",
                    studentName = userProfile?.fullName ?: "Cabdiraxmaan Cali",
                    completionDate = "28 Shacbaan 1447H / 2026",
                    grade = "Mumtaaz (Grade: A)",
                    onShare = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Waxaan ku guuleystay Shahaadada 'Makhaarijul Xuruuf' ee Mandeq Islamic Academy! 🎓✨")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "La wadaag shahaadadaada"))
                    }
                )
            }
        }
    }
}

@Composable
private fun IslamicCertificateCard(
    courseTitle: String,
    studentName: String,
    completionDate: String,
    grade: String,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, SoftGold.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GoldContainerLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = SoftGoldDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "MANDEQ ISLAMIC ACADEMY",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = IslamicGreenDark
            )

            Text(
                text = "SHAHAADADA GUUSHA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = IslamicGreenDark
            )

            Text(
                text = "Waxaa caddaynaysa in ardayga:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = HighDensitySlate500
            )

            Text(
                text = studentName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = IslamicGreenDark
                )
            )

            Text(
                text = "Uu si guul leh u dhammaystay koorsada:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = HighDensitySlate500
            )

            Text(
                text = courseTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                ),
                color = HighDensitySlate900
            )

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldContainerLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenDark.copy(alpha = 0.3f))
            ) {
                Text(
                    text = grade,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = IslamicGreenDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }

            Text(
                text = "Taariikhda: $completionDate",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = HighDensitySlate500
            )

            Spacer(modifier = Modifier.height(2.dp))

            Button(
                onClick = onShare,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("La Wadaag Shahaadada (Share)", fontSize = 11.sp)
            }
        }
    }
}
