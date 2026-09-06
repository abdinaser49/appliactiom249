package com.example.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeacherApprovalStatus
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MandeqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLoginScreen(
    viewModel: MandeqViewModel,
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("ustad.maxamed@mandeqislamic.so") }
    var password by remember { mutableStateOf("password123") }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Register Teacher Form State
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regCountry by remember { mutableStateOf("Somalia") }
    var regSubjects by remember { mutableStateOf("Tajweed, Quran, Fiqhi") }
    var regBio by remember { mutableStateOf("") }

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Text(
                    text = "Codsiga Akoonka Macallinka",
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenDark,
                    fontSize = 16.sp
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
                        text = "Fadlan buuxi macluumaadkaaga si maamulka Mandeq Islamic u eego codsigaaga (Status: PENDING):",
                        fontSize = 12.sp,
                        color = HighDensitySlate700
                    )

                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = { Text("Magaca oo Buuxa") },
                        placeholder = { Text("Tusaale: Ustaad Axmed Cali") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email-ka") },
                        placeholder = { Text("macallin@email.com") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Taleefanka / WhatsApp") },
                        placeholder = { Text("+252 61 ...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regSubjects,
                        onValueChange = { regSubjects = it },
                        label = { Text("Maadooyinka aad dhigto") },
                        placeholder = { Text("Tajweed, Tafsiir, Xadiis...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regBio,
                        onValueChange = { regBio = it },
                        label = { Text("Taariikhdaada Waxbarasho (Bio)") },
                        placeholder = { Text("Jaamacadda, Ijaazada aad haysato...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (regFullName.isNotBlank() && regEmail.isNotBlank()) {
                            showRegisterDialog = false
                            Toast.makeText(
                                context,
                                "Masha Allah! Codsigaagii waa la diiwaangeliyey (PENDING). Maamulka ayaa kula soo xiriiri doona.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "Fadlan buuxi magacaaga iyo email-kaaga.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                ) {
                    Text("Gudbi Codsiga")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Ka Noqo")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portal Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(IslamicGreenPrimary, IslamicGreenDark)
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Text(
                        text = "MANDEQ ISLAMIC",
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Al-Bawaabada Macallimiinta (Teacher Portal)",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Ku soo dhowaw nidaamka maaraynta koorsooyinka iyo duruusta diiniga ah.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Quick Demo Switchers for Easy Review
            Surface(
                color = SoftBeige,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "⚡ Imtixaan Degdeg ah (Quick Demo Roles):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = IslamicGreenDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = true,
                            onClick = {
                                viewModel.switchRole(UserRole.TEACHER)
                                onLoginSuccess()
                            },
                            label = { Text("Teacher Portal", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(12.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = false,
                            onClick = {
                                viewModel.switchRole(UserRole.SUPER_ADMIN)
                                onLoginSuccess()
                            },
                            label = { Text("Admin CMS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(12.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Login Form Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HighDensitySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Gal Akoonkaaga (Teacher Login)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = HighDensitySlate900
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email-ka Macallinka") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = IslamicGreenDark) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_email_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Furaha Sirta ah (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = IslamicGreenDark) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_password_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                Toast.makeText(context, "Xiriir la samee maamulka: support@mandeqislamic.so", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            Text("Ma ilowday Furaha? (Forgot Password)", fontSize = 11.sp, color = IslamicGreenPrimary)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.switchRole(UserRole.TEACHER)
                            Toast.makeText(context, "Masha Allah! Ku soo dhowaw Teacher Portal.", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("teacher_login_submit_button")
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gal Bawaabada (Login)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    HorizontalDivider(color = HighDensityBorder)

                    // Request Account Button
                    OutlinedButton(
                        onClick = { showRegisterDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("request_teacher_account_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Codso Akoon Macallin (Request Teacher Account)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
