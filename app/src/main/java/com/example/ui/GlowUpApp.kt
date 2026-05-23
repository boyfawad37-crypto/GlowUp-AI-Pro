package com.example.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.db.GlowUpHistoryEntity
import com.example.api.GlowUpAnalysis
import com.example.ui.theme.*
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GlowUpApp(viewModel: GlowUpViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.historyList.collectAsState()

    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val replicateToken by viewModel.replicateToken.collectAsState()
    val replicateModelVersion by viewModel.replicateModelVersion.collectAsState()
    val customPromptOverride by viewModel.customPromptOverride.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val selectedImageBitmap by viewModel.selectedImageBitmap.collectAsState()

    val currentAnalysisResult by viewModel.currentAnalysisResult.collectAsState()
    val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
    val generationStatusText by viewModel.generationStatusText.collectAsState()

    val lastErrorStatus by viewModel.lastErrorStatus.collectAsState()
    val lastErrorMessage by viewModel.lastErrorMessage.collectAsState()

    // Activity launcher for picking media
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        }
    }

    // Current interactive footer tab selection
    var activeTab by remember { mutableStateOf("HOME") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("glowup_root_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MidnightBlack,
        bottomBar = {
            if (uiState == GlowUpUIState.HOME || uiState == GlowUpUIState.SUCCESS) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, SophisticatedBorder), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .navigationBarsPadding(),
                    color = DarkGreySurface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TAB 1: HOME
                        val isHomeActive = activeTab == "HOME"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { 
                                    activeTab = "HOME"
                                    if (uiState != GlowUpUIState.HOME) {
                                        viewModel.navigateBackToHome()
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home Workspace Tab",
                                tint = if (isHomeActive) GlowAmber else SoftGreyText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "HOME",
                                color = if (isHomeActive) GlowAmber else SoftGreyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // TAB 2: PORTFOLIO
                        val isGalleryActive = activeTab == "PORTFOLIO"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { 
                                    activeTab = "PORTFOLIO"
                                    if (uiState != GlowUpUIState.HOME) {
                                        viewModel.navigateBackToHome()
                                    }
                                    Toast.makeText(context, "Browsing historical transformation portfolio below!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Portfolio Tab",
                                tint = if (isGalleryActive) CyberViolet else SoftGreyText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "PORTFOLIO",
                                color = if (isGalleryActive) CyberViolet else SoftGreyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // TAB 3: DEVELOPER KEYS
                        val isKeysActive = activeTab == "KEYS"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { 
                                    activeTab = "KEYS"
                                    if (uiState != GlowUpUIState.HOME) {
                                        viewModel.navigateBackToHome()
                                    }
                                    Toast.makeText(context, "API Key Config parameters opened below!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Keys Tab",
                                tint = if (isKeysActive) NeonCyan else SoftGreyText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "KEYS",
                                color = if (isKeysActive) NeonCyan else SoftGreyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MidnightBlack, MidnightBlack, DarkGreySurface)
                    )
                )
        ) {
            // Elegant background aura glows
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(CyberViolet.copy(alpha = 0.08f), Color.Transparent),
                        center = center
                    ),
                    radius = size.width * 0.8f
                )
            }

            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) with fadeOut(animationSpec = tween(300))
                },
                label = "GlowUpScreenTransition"
            ) { state ->
                when (state) {
                    GlowUpUIState.SPLASH -> {
                        SplashScreen()
                    }
                    GlowUpUIState.LOGIN -> {
                        LoginScreen(onBegin = { viewModel.onLoginSuccess() })
                    }
                    GlowUpUIState.HOME -> {
                        HomeScreen(
                            history = history,
                            selectedStyle = selectedStyle,
                            replicateToken = replicateToken,
                            replicateModelVersion = replicateModelVersion,
                            customPromptOverride = customPromptOverride,
                            selectedImageUri = selectedImageUri,
                            activeTab = activeTab,
                            onStyleSelected = { viewModel.selectStyle(it) },
                            onTokenChanged = { viewModel.replicateToken.value = it },
                            onVersionChanged = { viewModel.replicateModelVersion.value = it },
                            onPromptChanged = { viewModel.customPromptOverride.value = it },
                            onSelectImageTrigger = {
                                pickMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onSelectPreset = { viewModel.usePresetModel(it) },
                            onGenerateAITrigger = {
                                if (selectedImageUri == null && selectedImageBitmap == null) {
                                    Toast.makeText(context, "Please upload a photo or tap a preset face first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.triggerGlowUpAI()
                                }
                            },
                            onDeleteHistory = { viewModel.deleteHistoryItem(it) },
                            onClearAllHistory = { viewModel.clearHistory() }
                        )
                    }
                    GlowUpUIState.ANALYZING, GlowUpUIState.GENERATING -> {
                        ProgressScreen(status = viewModel.generationStatusText, isGenerating = state == GlowUpUIState.GENERATING)
                    }
                    GlowUpUIState.SUCCESS -> {
                        SuccessScreen(
                            originalUri = selectedImageUri,
                            resultUrl = generatedImageUrl,
                            style = selectedStyle,
                            analysis = currentAnalysisResult,
                            onBackToHome = { viewModel.navigateBackToHome() }
                        )
                    }
                    GlowUpUIState.ERROR -> {
                        ExceptionScreen(
                            statusCode = lastErrorStatus,
                            errorMessage = lastErrorMessage,
                            onBackToHome = { viewModel.navigateBackToHome() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * SCREEN 1: Splash Welcome Screen
 */
@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashTransition")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SplashAlphaPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Golden AI Core logo
        Box(
            modifier = Modifier
                .size(110.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowAmber.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        radius = size.width * 0.9f
                    )
                }
                .border(2.dp, Brush.linearGradient(listOf(GlowAmber, CyberViolet)), CircleShape)
                .clip(CircleShape)
                .background(MidnightBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = "GlowUp Logo",
                tint = GlowAmber,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "GLOWUP AI",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
            modifier = Modifier.testTag("splash_title_label")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Cinematic Facial Remap & Stylization Network",
            color = GlowAmber.copy(alpha = alphaAnim),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

/**
 * SCREEN 2: Login Interface (Deep Digital Access)
 */
@Composable
fun LoginScreen(onBegin: () -> Unit) {
    var deviceCode by remember { mutableStateOf("CLOUD-NODE-882") }
    var containerLocation by remember { mutableStateOf("US-EAST-SERVER (MAIN)") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Cloud Node Access",
            tint = CyberViolet,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACCESS GATEWAY",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Synchronize local android state with cloud inference nodes",
            color = SoftGreyText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Device static node details
        OutlinedTextField(
            value = deviceCode,
            onValueChange = { deviceCode = it },
            label = { Text("Local Device ID Check") },
            textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = GlowAmber,
                unfocusedBorderColor = CardBackground,
                focusedLabelColor = GlowAmber,
                unfocusedLabelColor = SoftGreyText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_device_id"),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = containerLocation,
            onValueChange = { containerLocation = it },
            label = { Text("AI Compute Location Host") },
            textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = GlowAmber,
                unfocusedBorderColor = CardBackground,
                focusedLabelColor = GlowAmber,
                unfocusedLabelColor = SoftGreyText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_container_host"),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBegin,
            colors = ButtonDefaults.buttonColors(containerColor = GlowAmber),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("login_button")
        ) {
            Text(
                text = "ENTER SECURE CONTAINER",
                color = MidnightBlack,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * SCREEN 3: Principal Home Screen
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    history: List<GlowUpHistoryEntity>,
    selectedStyle: GlowUpStyle,
    replicateToken: String,
    replicateModelVersion: String,
    customPromptOverride: String,
    selectedImageUri: Uri?,
    activeTab: String,
    onStyleSelected: (GlowUpStyle) -> Unit,
    onTokenChanged: (String) -> Unit,
    onVersionChanged: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onSelectImageTrigger: () -> Unit,
    onSelectPreset: (GlowUpStyle) -> Unit,
    onGenerateAITrigger: () -> Unit,
    onDeleteHistory: (Int) -> Unit,
    onClearAllHistory: () -> Unit
) {
    var showExpertSettings by remember { mutableStateOf(false) }

    // Reactively expand developer drawer if "KEYS" tab is clicked
    LaunchedEffect(activeTab) {
        if (activeTab == "KEYS") {
            showExpertSettings = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_scroll_container"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GlowUp",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI",
                            color = GlowAmber, // GlowAmber is indigo-400 equivalent
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "VISUAL ENHANCEMENT",
                        color = SoftGreyText, // #938F99
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CardBackground, CircleShape)
                        .border(1.dp, SophisticatedBorderAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GlowAmber, CyberViolet)
                                ),
                                CircleShape
                            )
                    )
                }
            }
        }

        // Section 1: Picture Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("action_upload_placeholder"),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Accent gradient glow at top of container
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(GlowAmber.copy(alpha = 0.08f), Color.Transparent)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(1.dp, GlowAmber, RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected Portrait",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Scan box effect overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(2.dp, Brush.linearGradient(listOf(GlowAmber, Color.Transparent)), RoundedCornerShape(24.dp))
                                )
                            }
                        } else {
                            // High end minimalist upload slot matching the HTML design precisely
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable { onSelectImageTrigger() },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(CardBackground, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share, // Upload shape
                                            contentDescription = "Upload Photo Icon",
                                            tint = GlowAmber,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Upload Portrait",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "JPG, PNG up to 10MB",
                                        color = SoftGreyText,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gallery Selector triggers pickMediaLauncher
                        Button(
                            onClick = onSelectImageTrigger,
                            colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, GlowAmber.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("select_photo_btn")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add image", tint = GlowAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SELECT GALLERY PHOTO", color = GlowAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fast Presets for local testing environment
                        Text(
                            text = "No local faces? Instant test preset:",
                            color = SoftGreyText,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mapOf(
                                "Male Pres." to GlowUpStyle.BUSINESSMAN,
                                "Fem Pres." to GlowUpStyle.ANIME,
                                "Sports Pres." to GlowUpStyle.FITNESS
                            ).forEach { (caption, style) ->
                                Button(
                                    onClick = { onSelectPreset(style) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, SophisticatedBorderAccent.copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(caption, color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Style Selection Panels
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT PERSONA",
                        color = SophisticatedTextTertiary, // #CAC4D0
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Browse All",
                        color = GlowAmber, // #818CF8
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlowUpStyle.values().forEach { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(
                                    width = 1.6.dp,
                                    color = if (isSelected) GlowAmber else SophisticatedBorderAccent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onStyleSelected(style) }
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Decorative icon box background container
                                val styleColor = when (style) {
                                    GlowUpStyle.BUSINESSMAN -> GlowAmber
                                    GlowUpStyle.ANIME -> CyberViolet
                                    GlowUpStyle.FITNESS -> NeonCyan
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(styleColor.copy(alpha = if (isSelected) 0.25f else 0.1f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (style) {
                                            GlowUpStyle.BUSINESSMAN -> Icons.Filled.Share
                                            GlowUpStyle.ANIME -> Icons.Filled.Edit
                                            GlowUpStyle.FITNESS -> Icons.Filled.Favorite
                                        },
                                        contentDescription = style.name,
                                        tint = styleColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = style.displayName,
                                    color = if (isSelected) Color.White else SoftGreyText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Developer Keys Drawer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showExpertSettings = !showExpertSettings },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (showExpertSettings) Icons.Default.Settings else Icons.Default.Build,
                        contentDescription = "Keys Config Toggle",
                        tint = GlowAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEVELOPER / API KEY SETTINGS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                if (showExpertSettings) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .testTag("expert_settings_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Security Warning strictly mirroring secret-management skill guidelines!
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.08f)),
                                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Security Alert",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "APK Key Extraction Risk: Any token saved in an APK is not secure on production releases and should use proxy systems.",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                    )
                                }
                            }

                            // Replicate token entry
                            OutlinedTextField(
                                value = replicateToken,
                                onValueChange = onTokenChanged,
                                label = { Text("Replicate API Token (YOUR_API_KEY)") },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                placeholder = { Text("r8_...", color = SoftGreyText, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GlowAmber,
                                    unfocusedBorderColor = MidnightBlack,
                                    focusedLabelColor = GlowAmber,
                                    unfocusedLabelColor = SoftGreyText
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("replicate_token_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = replicateModelVersion,
                                onValueChange = onVersionChanged,
                                label = { Text("Replicate Model Version Code") },
                                textStyle = TextStyle(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GlowAmber,
                                    unfocusedBorderColor = MidnightBlack,
                                    focusedLabelColor = GlowAmber,
                                    unfocusedLabelColor = SoftGreyText
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = customPromptOverride,
                                onValueChange = onPromptChanged,
                                label = { Text("Fine-Tuned Prompt Override") },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GlowAmber,
                                    unfocusedBorderColor = MidnightBlack,
                                    focusedLabelColor = GlowAmber,
                                    unfocusedLabelColor = SoftGreyText
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Section 4: History log title & Clear actions
        if (history.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR TRANSFORMATION PORTFOLIO",
                        color = GlowAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "CLEAR ALL",
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onClearAllHistory() }
                    )
                }
            }

            // Lazy Horizontal Row for history entries
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history) { record ->
                        HistoryCard(record = record, onDelete = { onDeleteHistory(record.id) })
                    }
                }
            }
        }

        // Generate Master Action
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = onGenerateAITrigger,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_glowup_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "GENERATE TRANSFORMATION",
                            color = MidnightBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow Forward Spark",
                            tint = MidnightBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Historical Record Mini Card
 */
@Composable
fun HistoryCard(record: GlowUpHistoryEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = record.resultImageUrl ?: record.originalImagePath,
                    contentDescription = "History Snapshot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overall score bubble overlay
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(GlowAmber, RoundedCornerShape(8.dp))
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        "Glow: ${record.glowScore}",
                        color = MidnightBlack,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Delete tiny button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = record.styleSelected.uppercase(),
                    color = NeonCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = record.adviceTitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * SCREEN 4: Processing State (Aesthetic terminal loading feed)
 */
@Composable
fun ProgressScreen(status: StateFlow<String>, isGenerating: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val currentStatus by status.collectAsState()

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing)
        ),
        label = "RadarRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rotating AI Facial Radar ring
        Box(
            modifier = Modifier
                .size(130.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(GlowAmber, CyberViolet, NeonCyan, GlowAmber)
                        ),
                        alpha = 0.45f
                    )
                }
                .border(2.dp, Brush.linearGradient(listOf(GlowAmber, CyberViolet)), CircleShape)
                .clip(CircleShape)
                .background(MidnightBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isGenerating) Icons.Filled.Warning else Icons.Filled.Face,
                contentDescription = "Radar Symbol",
                tint = GlowAmber,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "AI PIPELINE RUNNING",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentStatus,
            color = SoftGreyText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.testTag("generation_progress_text")
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            color = GlowAmber,
            trackColor = CardBackground,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}

/**
 * SCREEN 5: GlowUp Generation Analytics success panel
 */
@Composable
fun SuccessScreen(
    originalUri: Uri?,
    resultUrl: String?,
    style: GlowUpStyle,
    analysis: GlowUpAnalysis?,
    onBackToHome: () -> Unit
) {
    var viewBeforeMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("success_result_container"),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        // Upper Title block
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GLOWUP COMPLETED",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Aesthetic Generation Matrix Successful",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Before & After Switchable Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MidnightBlack)
                    ) {
                        // Display correct dynamic before/after asset
                        if (viewBeforeMode) {
                            if (originalUri != null) {
                                AsyncImage(
                                    model = originalUri,
                                    contentDescription = "Before Avatar Original",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Original uncompressed image.", color = SoftGreyText)
                                }
                            }
                        } else {
                            if (resultUrl != null) {
                                AsyncImage(
                                    model = resultUrl,
                                    contentDescription = "Glowed Up Rendered Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Generating result...", color = SoftGreyText)
                                }
                            }
                        }

                        // Style description capsule bottom overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .background(MidnightBlack.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (viewBeforeMode) "BEFORE ORIGINAL FACE" else "AFTER GLOWUP RENDERING (${style.displayName})",
                                color = GlowAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Selector Button to Toggle Before/After
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewBeforeMode = !viewBeforeMode },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewBeforeMode) GlowAmber else DarkGreySurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (viewBeforeMode) "VIEW GLOW RENDER" else "COMPARE ORIGINAL",
                                color = if (viewBeforeMode) MidnightBlack else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Circular scores widgets panel
        item {
            if (analysis != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "AESTHETIC ANALYSIS METRICS",
                        color = GlowAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val metrics = listOf(
                            Triple("Jawline", analysis.jawlineScore, CyberViolet),
                            Triple("Symmetry", analysis.symmetryScore, NeonCyan),
                            Triple("Skin Glow", analysis.glowScore, GlowAmber),
                            Triple("Style Index", analysis.styleIndex, Color.White)
                        )
                        metrics.forEach { item ->
                            val caption = item.first
                            val score = item.second
                            val color = item.third
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(caption, color = SoftGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "$score%",
                                        color = color,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dynamic advices block
        item {
            if (analysis != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = "Active Advice", tint = GlowAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = analysis.adviceTitle.uppercase(),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grooming section
                        Text("1. PERSONALIZED GROOMING DIAGNOSTICS", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text(analysis.adviceGrooming, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

                        Divider(color = MidnightBlack.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Skin posture section
                        Text("2. SKIN THERAPY & EXERCISES", color = CyberViolet, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text(analysis.adviceskincare, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

                        Divider(color = MidnightBlack.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Outfits section
                        Text("3. HIGH-FASHION DRESSING COORDINATION", color = GlowAmber, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text(analysis.adviceFashion, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                    }
                }
            }
        }

        // Action Return button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = GlowAmber),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("success_new_conversion_btn")
                ) {
                    Text("CREATE NEW TRANSFORMATION", color = MidnightBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * SCREEN 6: Error / Crisis State Handling Page
 */
@Composable
fun ExceptionScreen(statusCode: Int?, errorMessage: String, onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                .border(2.dp, Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Warning, contentDescription = "Error status", tint = Color.Red, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "AI PIPELINE REJECTED",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HTTP Code Check: ${statusCode ?: "Local Scope Protocol Breakdown"}",
                    color = GlowAmber,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "To resolve this:\n• Double check Replicate.com container funds\n• Ensure keys are entered fully in Settings\n• Run with fallback mode by using our default preset structures.",
            color = SoftGreyText,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = GlowAmber),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("error_retry_btn")
        ) {
            Text("RETURN TO WORKSPACE", color = MidnightBlack, fontWeight = FontWeight.Bold)
        }
    }
}
