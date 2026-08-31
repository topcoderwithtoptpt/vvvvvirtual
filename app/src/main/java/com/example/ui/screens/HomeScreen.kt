package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReplacementMode
import com.example.ui.components.CameraStreamPreview
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VLiveViewModel

@Composable
fun HomeScreen(
    viewModel: VLiveViewModel,
    onNavigateToController: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReplacing by viewModel.isReplacing.collectAsState()
    val replacementMode by viewModel.replacementMode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoop by viewModel.isLoop.collectAsState()
    val frontPreview by viewModel.frontPreview.collectAsState()
    val rotationDegrees by viewModel.rotationDegrees.collectAsState()
    val isMirrored by viewModel.isMirrored.collectAsState()
    val colorInject by viewModel.colorInject.collectAsState()
    val floatingWindow by viewModel.floatingWindow.collectAsState()
    val activeAction by viewModel.currentActionActive.collectAsState()
    val actionProgressUs by viewModel.actionProgressUs.collectAsState()
    val actionList by viewModel.actionList.collectAsState()
    val actionBeginMap by viewModel.actionBeginMap.collectAsState()
    val actionEndMap by viewModel.actionEndMap.collectAsState()
    val videoPath by viewModel.videoPath.collectAsState()
    val injectionStatus by viewModel.injectionStatus.collectAsState()
    val deployProgress by viewModel.deployProgress.collectAsState()
    val residualResult by viewModel.residualResult.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // App Title & Status Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VLive",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF202A44))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v18.0",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple
                            )
                        }
                    }
                    Text(
                        text = "虚拟视频与推流助手",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Master Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isReplacing) "替换生效" else "已停用",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isReplacing) NeonGreen else TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isReplacing,
                        onCheckedChange = { viewModel.toggleReplaceCamera() },
                        modifier = Modifier.testTag("home_master_replace_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepNavy,
                            checkedTrackColor = NeonGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDark
                        )
                    )
                }
            }
        }

        // Live Stream / Virtual Camera Surface Preview
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "实时画面监控",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "点击前往详细控制 >",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        modifier = Modifier.clickable { onNavigateToController() }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CameraStreamPreview(
                    replacementMode = replacementMode,
                    isReplacing = isReplacing,
                    isPlaying = isPlaying,
                    isLoop = isLoop,
                    frontPreview = frontPreview,
                    rotationDegrees = rotationDegrees,
                    isMirrored = isMirrored,
                    colorInject = colorInject,
                    colorIntensity = 20f,
                    colorDiameter = 25f,
                    colorX = 50f,
                    colorY = 50f,
                    activeAction = activeAction,
                    actionProgressUs = actionProgressUs,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onRotate = { viewModel.rotate90() },
                    onToggleMirror = { viewModel.toggleMirror() },
                    onToggleFrontPreview = { viewModel.toggleFrontPreview() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
            }
        }

        // ROOT Native Injection & Hooking Engine Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (injectionStatus.isRootGranted) NeonGreen.copy(alpha = 0.5f) else SurfaceCardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("home_root_engine_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = if (injectionStatus.isRootGranted) NeonGreen else NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ROOT 相机底层注入引擎",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (injectionStatus.isRootGranted) Color(0xFF102E20) else Color(0xFF332010)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (injectionStatus.isRootGranted) "ROOT 已获取 (UID 0)" else "无 ROOT (仅预览)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (injectionStatus.isRootGranted) NeonGreen else NeonAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status details grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "守护进程 (vcplax)", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = if (injectionStatus.isDaemonRunning) "运行中 (${injectionStatus.daemonPid})" else "未启动",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (injectionStatus.isDaemonRunning) NeonGreen else TextMuted
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Binder IPC 服务", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = if (injectionStatus.isBinderConnected) "已连接 (${injectionStatus.serverName.take(8)})" else "未连接",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (injectionStatus.isBinderConnected) NeonCyan else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "SELinux 模式", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = injectionStatus.seLinuxMode,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (injectionStatus.seLinuxMode.equals("Permissive", ignoreCase = true)) NeonCyan else NeonAmber
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "内核目标架构", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = injectionStatus.architecture,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Deploy progress / status message
                    if (deployProgress != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF131E35))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = deployProgress ?: "",
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    // Residual security warning
                    if (!residualResult.isClean) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF331616))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = NeonRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "检测到 ${residualResult.detectedPaths.size} 处系统残留文件，存在风控被检风险",
                                fontSize = 11.sp,
                                color = NeonRed,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "立即清理",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber,
                                modifier = Modifier.clickable { viewModel.cleanResidualFiles() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ElevatedButton(
                            onClick = { viewModel.deployNativeEngine() },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("home_btn_deploy_native"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = NeonCyan,
                                contentColor = DeepNavy
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "一键部署内核", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.refreshRootAndDaemonStatus() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_btn_refresh_root"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "刷新状态", fontSize = 12.sp)
                        }

                        if (!residualResult.isClean) {
                            ElevatedButton(
                                onClick = { viewModel.cleanResidualFiles() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_btn_clean_residuals"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xFF4A1818),
                                    contentColor = NeonRed
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "防风控", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Quick Function Toggles Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatusPill(
                    title = "悬浮窗口",
                    status = if (floatingWindow) "运行中" else "未开启",
                    active = floatingWindow,
                    modifier = Modifier.weight(1f),
                    testTag = "home_pill_floating"
                ) { viewModel.toggleFloatingWindow() }

                QuickStatusPill(
                    title = "循环播放",
                    status = if (isLoop) "开启" else "关闭",
                    active = isLoop,
                    modifier = Modifier.weight(1f),
                    testTag = "home_pill_loop"
                ) { viewModel.toggleLoop() }

                QuickStatusPill(
                    title = "三色注入",
                    status = if (colorInject) "已激活" else "未激活",
                    active = colorInject,
                    modifier = Modifier.weight(1f),
                    testTag = "home_pill_color"
                ) { viewModel.toggleColorInject() }
            }
        }

        // Facial Action Presets Trigger Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                    .testTag("home_actions_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "快捷面部动作触发",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "时间范围配置 >",
                            fontSize = 11.sp,
                            color = NeonCyan,
                            modifier = Modifier.clickable { onNavigateToSettings() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(actionList) { action ->
                            val beginUs = actionBeginMap[action.id] ?: action.defaultStartUs
                            val endUs = actionEndMap[action.id] ?: action.defaultEndUs
                            val isCurrent = activeAction?.id == action.id

                            ElevatedButton(
                                onClick = { viewModel.triggerFacialAction(action.id) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (isCurrent) NeonCyan else Color(0xFF131D33),
                                    contentColor = if (isCurrent) DeepNavy else TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("action_btn_${action.id}")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = action.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${beginUs / 1000}ms",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isCurrent) DeepNavy else NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Mode & Source Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "替换配置概览",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "替换模式", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = if (replacementMode == ReplacementMode.VIDEO) "本地视频替换" else "RTMP推流替换",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "当前媒体源", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = videoPath.substringAfterLast('/'),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "前置镜头", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = if (frontPreview) "已启用" else "后置镜头",
                            fontSize = 12.sp,
                            color = if (frontPreview) NeonGreen else TextSecondary
                        )
                    }
                }
            }
        }

        // System Permissions Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "系统权限状态",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    PermissionItem("悬浮窗口权限 (SYSTEM_ALERT_WINDOW)", true)
                    PermissionItem("摄像头权限 (CAMERA)", true)
                    PermissionItem("屏幕录制投影 (MediaProjection)", true)
                    PermissionItem("前台服务保活 (FOREGROUND_SERVICE)", true)
                }
            }
        }

        // Usage Instructions & Guide
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1524)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF223150), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "使用说明与技巧",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. 在「控制」页面中选择本地视频或RTMP推流地址，开启「替换相机」开关。\n" +
                               "2. 开启「悬浮窗口」即可获得随时拖拽的4x4快捷按键，在第三方应用中一键触发眨眼、张嘴、转头与播放控制。\n" +
                               "3. 在「设置」中可自定义动作起止微秒(μs)，1秒=1,000,000微秒。\n" +
                               "4.「三色注入」可监测指定屏幕坐标色彩，智能渲染光影让替换画面更加逼真。",
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatusPill(
    title: String,
    status: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(
                1.dp,
                if (active) NeonCyan.copy(alpha = 0.6f) else SurfaceCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) NeonCyan else TextMuted
            )
        }
    }
}

@Composable
private fun PermissionItem(name: String, granted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 11.sp, color = TextSecondary)
        Icon(
            imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (granted) NeonGreen else NeonAmber,
            modifier = Modifier.size(14.dp)
        )
    }
}
