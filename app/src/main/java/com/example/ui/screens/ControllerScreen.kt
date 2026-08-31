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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun ControllerScreen(
    viewModel: VLiveViewModel,
    modifier: Modifier = Modifier
) {
    val replacementMode by viewModel.replacementMode.collectAsState()
    val videoPath by viewModel.videoPath.collectAsState()
    val rtmpUrl by viewModel.rtmpUrl.collectAsState()
    val isReplacing by viewModel.isReplacing.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoop by viewModel.isLoop.collectAsState()
    val frontPreview by viewModel.frontPreview.collectAsState()
    val aspectCorrect by viewModel.aspectCorrect.collectAsState()
    val colorInject by viewModel.colorInject.collectAsState()
    val floatingWindow by viewModel.floatingWindow.collectAsState()
    val rotationDegrees by viewModel.rotationDegrees.collectAsState()
    val isMirrored by viewModel.isMirrored.collectAsState()
    val activeAction by viewModel.currentActionActive.collectAsState()
    val actionProgressUs by viewModel.actionProgressUs.collectAsState()

    // Tri-color test parameters
    val colorIntensity by viewModel.colorIntensity.collectAsState()
    val colorDiameter by viewModel.colorDiameter.collectAsState()
    val colorX by viewModel.colorX.collectAsState()
    val colorY by viewModel.colorY.collectAsState()
    val testMode by viewModel.testMode.collectAsState()

    var showVideoDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("controller_screen_lazy_column")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Mode Selection Card (视频替换 vs 推流替换)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("controller_mode_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "替换源选择",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModeSelectButton(
                            title = "视频替换",
                            selected = replacementMode == ReplacementMode.VIDEO,
                            modifier = Modifier.weight(1f),
                            testTag = "radio_mode_video"
                        ) { viewModel.setReplacementMode(ReplacementMode.VIDEO) }

                        ModeSelectButton(
                            title = "推流替换",
                            selected = replacementMode == ReplacementMode.RTMP,
                            modifier = Modifier.weight(1f),
                            testTag = "radio_mode_rtmp"
                        ) { viewModel.setReplacementMode(ReplacementMode.RTMP) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (replacementMode == ReplacementMode.VIDEO) {
                        // Video selection row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "已选本地视频", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = videoPath.substringAfterLast('/'),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonCyan
                                )
                            }
                            ElevatedButton(
                                onClick = { showVideoDialog = true },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_select_video")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "选择视频", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // RTMP URL Input
                        Column {
                            Text(text = "RTMP 推流地址", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = rtmpUrl,
                                onValueChange = { viewModel.setRtmpUrl(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_rtmp_url"),
                                placeholder = { Text("rtmp://live.stream.example/live/room1", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = NeonAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Master Switch: 替换相机 (Replace Camera)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isReplacing) Color(0xFF142928) else SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isReplacing) NeonGreen else SurfaceCardBorder,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isReplacing) NeonGreen.copy(alpha = 0.2f) else SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (isReplacing) NeonGreen else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "替换相机",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isReplacing) "虚拟摄像头数据管道已接管全局视频流" else "未接管摄像头，点击右侧开关启动",
                                fontSize = 11.sp,
                                color = if (isReplacing) NeonGreen else TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isReplacing,
                        onCheckedChange = { viewModel.toggleReplaceCamera() },
                        modifier = Modifier.testTag("switch_replace_camera"),
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

        // Operational Options Grid (前置预览, 悬浮窗口, 画面纠正, 循环播放, 三色注入)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("controller_options_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "功能运行配置",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OptionCheckboxItem(
                            title = "前置预览",
                            checked = frontPreview,
                            onCheckedChange = { viewModel.toggleFrontPreview() },
                            modifier = Modifier.weight(1f),
                            testTag = "chk_front_preview"
                        )
                        OptionCheckboxItem(
                            title = "悬浮窗口",
                            checked = floatingWindow,
                            onCheckedChange = { viewModel.toggleFloatingWindow() },
                            modifier = Modifier.weight(1f),
                            testTag = "chk_floating_window"
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OptionCheckboxItem(
                            title = "画面纠正",
                            checked = aspectCorrect,
                            onCheckedChange = { viewModel.toggleAspectCorrect() },
                            modifier = Modifier.weight(1f),
                            testTag = "chk_aspect_correct"
                        )
                        OptionCheckboxItem(
                            title = "循环播放",
                            checked = isLoop,
                            onCheckedChange = { viewModel.toggleLoop() },
                            modifier = Modifier.weight(1f),
                            testTag = "chk_loop_playback"
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OptionCheckboxItem(
                            title = "三色注入",
                            checked = colorInject,
                            onCheckedChange = { viewModel.toggleColorInject() },
                            modifier = Modifier.weight(1f),
                            testTag = "chk_color_inject"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Live Stream Preview
        item {
            Column {
                Text(
                    text = "虚拟视频输出预览",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
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
                    colorIntensity = colorIntensity,
                    colorDiameter = colorDiameter,
                    colorX = colorX,
                    colorY = colorY,
                    activeAction = activeAction,
                    actionProgressUs = actionProgressUs,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onRotate = { viewModel.rotate90() },
                    onToggleMirror = { viewModel.toggleMirror() },
                    onToggleFrontPreview = { viewModel.toggleFrontPreview() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                )
            }
        }

        // 三色测试 (Color Injection Test Parameters)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("controller_tricolor_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "三色测试与微调",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Mode 1 vs Mode 2
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { viewModel.setTestMode(1) }
                            ) {
                                RadioButton(
                                    selected = testMode == 1,
                                    onClick = { viewModel.setTestMode(1) },
                                    colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                                )
                                Text(text = "模式1", fontSize = 11.sp, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { viewModel.setTestMode(2) }
                            ) {
                                RadioButton(
                                    selected = testMode == 2,
                                    onClick = { viewModel.setTestMode(2) },
                                    colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                                )
                                Text(text = "模式2", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slider: 照射强度
                    SliderControlRow(
                        label = "照射强度",
                        value = colorIntensity,
                        displayValue = "${colorIntensity.toInt()}%",
                        onValueChange = { viewModel.setColorIntensity(it) },
                        testTag = "slider_color_intensity"
                    )

                    // Slider: 照射直径
                    SliderControlRow(
                        label = "照射直径",
                        value = colorDiameter,
                        displayValue = "${colorDiameter.toInt()}%",
                        onValueChange = { viewModel.setColorDiameter(it) },
                        testTag = "slider_color_diameter"
                    )

                    // Slider: X坐标
                    SliderControlRow(
                        label = "X坐标",
                        value = colorX,
                        displayValue = "${colorX.toInt()}%",
                        onValueChange = { viewModel.setColorX(it) },
                        testTag = "slider_color_x"
                    )

                    // Slider: Y坐标
                    SliderControlRow(
                        label = "Y坐标",
                        value = colorY,
                        displayValue = "${colorY.toInt()}%",
                        onValueChange = { viewModel.setColorY(it) },
                        testTag = "slider_color_y"
                    )
                }
            }
        }
    }

    // Video Preset Selection Dialog
    if (showVideoDialog) {
        VideoSelectDialog(
            currentPath = videoPath,
            onSelect = {
                viewModel.setVideoPath(it)
                showVideoDialog = false
            },
            onDismiss = { showVideoDialog = false }
        )
    }
}

@Composable
private fun ModeSelectButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) ElectricBlue else Color(0xFF151D33))
            .border(
                1.dp,
                if (selected) NeonCyan else SurfaceCardBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun OptionCheckboxItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Row(
        modifier = modifier
            .testTag(testTag)
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NeonCyan,
                uncheckedColor = TextMuted,
                checkmarkColor = DeepNavy
            )
        )
        Text(
            text = title,
            fontSize = 13.sp,
            color = if (checked) TextPrimary else TextSecondary
        )
    }
}

@Composable
private fun SliderControlRow(
    label: String,
    value: Float,
    displayValue: String,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Text(
                text = displayValue,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = SurfaceDark
            )
        )
    }
}

@Composable
private fun VideoSelectDialog(
    currentPath: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val presets = listOf(
        "virtual_avatar_live_1080p.mp4",
        "demo_face_speaking_motion.mp4",
        "outdoor_vlog_stream_feed.mp4",
        "virtual_studio_interview.mp4"
    )

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
                    text = "选择预设虚拟视频源",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                presets.forEach { preset ->
                    val isSelected = currentPath.endsWith(preset)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SurfaceCard else Color.Transparent)
                            .clickable { onSelect(preset) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = if (isSelected) NeonCyan else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = preset,
                            fontSize = 13.sp,
                            color = if (isSelected) NeonCyan else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ElevatedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = ElectricBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("完成")
                    }
                }
            }
        }
    }
}
