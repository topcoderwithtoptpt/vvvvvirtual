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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColorMode
import com.example.data.FacialAction
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
fun SettingsScreen(
    viewModel: VLiveViewModel,
    modifier: Modifier = Modifier
) {
    val actionList by viewModel.actionList.collectAsState()
    val actionBeginMap by viewModel.actionBeginMap.collectAsState()
    val actionEndMap by viewModel.actionEndMap.collectAsState()
    val monitorTargetX by viewModel.monitorTargetX.collectAsState()
    val monitorTargetY by viewModel.monitorTargetY.collectAsState()
    val colorMode by viewModel.colorMode.collectAsState()
    val injectionStatus by viewModel.injectionStatus.collectAsState()
    val residualResult by viewModel.residualResult.collectAsState()

    var showCoordDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_lazy_column")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Facial Actions Timing Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("settings_actions_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "动作设置 (微秒 μs)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "1秒 = 1,000,000μs",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    actionList.forEach { action ->
                        val startVal = actionBeginMap[action.id] ?: action.defaultStartUs
                        val endVal = actionEndMap[action.id] ?: action.defaultEndUs

                        ActionTimingRow(
                            action = action,
                            startUs = startVal,
                            endUs = endVal,
                            onUpdate = { s, e -> viewModel.updateActionRange(action.id, s, e) },
                            onTest = { viewModel.triggerFacialAction(action.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons: Save & Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetToDefaults() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_reset_actions"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonAmber)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "恢复默认", fontSize = 13.sp)
                        }

                        ElevatedButton(
                            onClick = { viewModel.saveSettings() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_save_actions"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = NeonCyan,
                                contentColor = DeepNavy
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "保存配置", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 三色设置 (Color Injection & Screen Coordinate Sampling)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("settings_color_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "三色设置",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Modes: 帧内提取, 视频替换, 实时渲染
                    ColorMode.entries.forEach { mode ->
                        val isSelected = colorMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1B2640) else Color.Transparent)
                                .clickable { viewModel.setColorMode(mode) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setColorMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                                Text(
                                    text = mode.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Coordinate Display & Select Button
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "当前选择监测屏幕坐标: ($monitorTargetX, $monitorTargetY)",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ElevatedButton(
                                onClick = { showCoordDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_select_coords"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdsClick,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "选择检测屏幕坐标", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ROOT Native Injection & Anti-Detection Safety Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("settings_root_safety_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (residualResult.isClean) NeonGreen else NeonRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ROOT 内核注入与防风控安全",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (residualResult.isClean) Color(0xFF102E20) else Color(0xFF3B1818))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (residualResult.isClean) "安全 (无残留)" else "风险: 存在残留",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (residualResult.isClean) NeonGreen else NeonRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "本模块管理底层 Hook 注入依赖库 (/data/libvc.so, /data/libvc++.so / ShadowHook) 及 cameraserver 拦截进程 (vcplax)。直播平台在启动时可能扫描常见外挂目录以识别作弊特征，请定期执行残留清理。",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail items
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "ROOT 运行环境", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = if (injectionStatus.isRootGranted) "已授权 UID 0 (su 正常)" else "未检测到 su 权限",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (injectionStatus.isRootGranted) NeonGreen else NeonAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SELinux 模式", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = injectionStatus.seLinuxMode,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (injectionStatus.seLinuxMode.equals("Permissive", ignoreCase = true)) NeonCyan else NeonAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "守护进程 vcplax", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = if (injectionStatus.isDaemonRunning) "运行中 (PID: ${injectionStatus.daemonPid})" else "未运行",
                                    fontSize = 12.sp,
                                    color = if (injectionStatus.isDaemonRunning) NeonGreen else TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "ServiceManager 注册名", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = injectionStatus.serverName.ifEmpty { "未生成" },
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonPurple
                                )
                            }

                            if (!residualResult.isClean) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "发现以下残留特征路径:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed
                                )
                                residualResult.detectedPaths.forEach { p ->
                                    Text(
                                        text = "• $p",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = NeonRed.copy(alpha = 0.9f)
                                    )
                                }
                            }
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
                                .weight(1f)
                                .testTag("settings_btn_deploy_native"),
                            shape = RoundedCornerShape(8.dp),
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
                            Text(text = "部署/启动内核", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        ElevatedButton(
                            onClick = { viewModel.cleanResidualFiles() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_btn_clean_residuals"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (residualResult.isClean) SurfaceDark else Color(0xFF521919),
                                contentColor = if (residualResult.isClean) TextSecondary else NeonRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "防风控清理", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.stopDaemon() },
                            modifier = Modifier.testTag("settings_btn_stop_daemon"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonAmber)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // About & Version Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "关于 VLive",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "VLive 18.0\n专为虚拟直播、视频替换和面部动画微调设计的控制中枢。\n支持毫秒级动作区间控制、RTMP推流替换及全向悬浮交互。",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    if (showCoordDialog) {
        CoordinateSelectDialog(
            currentX = monitorTargetX,
            currentY = monitorTargetY,
            onConfirm = { x, y ->
                viewModel.setMonitorCoordinates(x, y)
                showCoordDialog = false
            },
            onDismiss = { showCoordDialog = false }
        )
    }
}

@Composable
private fun ActionTimingRow(
    action: FacialAction,
    startUs: Long,
    endUs: Long,
    onUpdate: (Long, Long) -> Unit,
    onTest: () -> Unit
) {
    var startText by remember(startUs) { mutableStateOf(startUs.toString()) }
    var endText by remember(endUs) { mutableStateOf(endUs.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131B2E))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Action Label
        Column(modifier = Modifier.width(60.dp)) {
            Text(
                text = action.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Text(
                text = "[${action.shortLabel}]",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }

        // Start US input
        OutlinedTextField(
            value = startText,
            onValueChange = {
                startText = it
                it.toLongOrNull()?.let { s -> onUpdate(s, endUs) }
            },
            modifier = Modifier
                .weight(1f)
                .testTag("input_start_${action.id}"),
            label = { Text("起始(μs)", fontSize = 9.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = SurfaceCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        // End US input
        OutlinedTextField(
            value = endText,
            onValueChange = {
                endText = it
                it.toLongOrNull()?.let { e -> onUpdate(startUs, e) }
            },
            modifier = Modifier
                .weight(1f)
                .testTag("input_end_${action.id}"),
            label = { Text("结束(μs)", fontSize = 9.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = SurfaceCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Quick test trigger
        ElevatedButton(
            onClick = onTest,
            modifier = Modifier
                .size(36.dp)
                .testTag("btn_test_action_${action.id}"),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = ElectricBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "测试动作",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CoordinateSelectDialog(
    currentX: Int,
    currentY: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var xText by remember { mutableStateOf(currentX.toString()) }
    var yText by remember { mutableStateOf(currentY.toString()) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "设置屏幕监测取样坐标",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "输入需要提取色彩的光照检测点绝对像素坐标 (基于屏幕分辨率)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = xText,
                        onValueChange = { xText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dialog_coord_x"),
                        label = { Text("X 像素") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = yText,
                        onValueChange = { yText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dialog_coord_y"),
                        label = { Text("Y 像素") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetCoordChip("屏幕中心 (540, 960)") {
                        xText = "540"
                        yText = "960"
                    }
                    PresetCoordChip("顶部光线 (540, 200)") {
                        xText = "540"
                        yText = "200"
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ElevatedButton(
                        onClick = {
                            val x = xText.toIntOrNull() ?: currentX
                            val y = yText.toIntOrNull() ?: currentY
                            onConfirm(x, y)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = NeonCyan,
                            contentColor = DeepNavy
                        )
                    ) {
                        Text("确认设置", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetCoordChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 10.sp, color = NeonCyan)
    }
}
