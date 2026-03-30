package com.goodrecipe.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.goodrecipe.viewmodel.MineViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    modifier: Modifier = Modifier,
    onOpenMineFavorites: () -> Unit,
    onOpenMineMyRecipes: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: MineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val p = uiState.profile
    val context = LocalContext.current
    var showAvatarDialog by remember { mutableStateOf(false) }
    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showSignatureDialog by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf("") }
    var signatureInput by remember { mutableStateOf("") }
    var nicknameError by remember { mutableStateOf(false) }
    var signatureError by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateAvatarUri(it.toString()) }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraOutputUri?.let { viewModel.updateAvatarUri(it.toString()) }
        }
        cameraOutputUri = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createAvatarCameraUri(context)
            cameraOutputUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                val uri = createAvatarCameraUri(context)
                cameraOutputUri = uri
                takePictureLauncher.launch(uri)
            }
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MineProfileHeader(
                            nickname = p.nickname,
                            signature = p.signature,
                            avatarUrl = p.avatarUrl,
                            onOpenSettings = onOpenSettings,
                            onAvatarClick = { showAvatarDialog = true },
                            onNicknameClick = {
                                nicknameInput = p.nickname.ifBlank { "" }
                                nicknameError = false
                                showNicknameDialog = true
                            },
                            onSignatureClick = {
                                signatureInput = p.signature.ifBlank { "" }
                                signatureError = false
                                showSignatureDialog = true
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(top = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MineMenuItemCard(
                                title = "我的收藏",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = onOpenMineFavorites
                            )
                            MineMenuItemCard(
                                title = "我的菜谱",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Restaurant,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = onOpenMineMyRecipes
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("更换头像") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            showAvatarDialog = false
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("从相册选择", modifier = Modifier.fillMaxWidth())
                    }
                    TextButton(
                        onClick = {
                            showAvatarDialog = false
                            launchCamera()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("拍照", modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            title = { Text("修改昵称") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = {
                            nicknameInput = it
                            if (nicknameError && it.isNotBlank()) nicknameError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("昵称") },
                        singleLine = true,
                        isError = nicknameError
                    )
                    if (nicknameError) {
                        Text(
                            text = "昵称不能为空",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = nicknameInput.trim()
                        if (value.isBlank()) {
                            nicknameError = true
                        } else {
                            viewModel.updateNickname(value)
                            showNicknameDialog = false
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicknameDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showSignatureDialog) {
        AlertDialog(
            onDismissRequest = { showSignatureDialog = false },
            title = { Text("修改个性签名") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = signatureInput,
                        onValueChange = {
                            signatureInput = it
                            if (signatureError && it.isNotBlank()) signatureError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("个性签名") },
                        minLines = 2,
                        maxLines = 4,
                        isError = signatureError
                    )
                    if (signatureError) {
                        Text(
                            text = "个性签名不能为空",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = signatureInput.trim()
                        if (value.isBlank()) {
                            signatureError = true
                        } else {
                            viewModel.updateSignature(value)
                            showSignatureDialog = false
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignatureDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

private val MineMenuCardCornerRadius = 12.dp

@Composable
private fun MineMenuItemCard(
    title: String,
    leadingIcon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val outlineSoft = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(MineMenuCardCornerRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, outlineSoft),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MineProfileHeader(
    nickname: String,
    signature: String,
    avatarUrl: String,
    onOpenSettings: () -> Unit,
    onAvatarClick: () -> Unit,
    onNicknameClick: () -> Unit,
    onSignatureClick: () -> Unit
) {
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerBackdropLuminance =
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f).luminance()
    val useDarkStatusBarIcons = headerBackdropLuminance > 0.4f
    val view = LocalView.current

    DisposableEffect(useDarkStatusBarIcons, view) {
        val window = view.context.findActivity()?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            val previous = controller.isAppearanceLightStatusBars
            controller.isAppearanceLightStatusBars = useDarkStatusBarIcons
            onDispose { controller.isAppearanceLightStatusBars = previous }
        } else {
            onDispose { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusTop + 8.dp, end = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "设置",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick, onClickLabel = "更换头像")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "头像",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = nickname.take(1).ifBlank { "食" },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = nickname.ifBlank { "美食家" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onNicknameClick)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clickable(onClick = onSignatureClick)
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = signature.ifBlank { "今天也要好好吃饭～" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "编辑签名",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun createAvatarCameraUri(context: Context): Uri {
    val file = File.createTempFile("avatar_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
