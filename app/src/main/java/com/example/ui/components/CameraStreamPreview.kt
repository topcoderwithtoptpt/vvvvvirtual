package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FacialAction
import com.example.data.ReplacementMode
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun CameraStreamPreview(
    replacementMode: ReplacementMode,
    isReplacing: Boolean,
    isPlaying: Boolean,
    isLoop: Boolean,
    frontPreview: Boolean,
    rotationDegrees: Int,
    isMirrored: Boolean,
    colorInject: Boolean,
    colorIntensity: Float,
    colorDiameter: Float,
    colorX: Float,
    colorY: Float,
    activeAction: FacialAction?,
    actionProgressUs: Long,
    onTogglePlayPause: () -> Unit,
    onRotate: () -> Unit,
    onToggleMirror: () -> Unit,
    onToggleFrontPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timerUs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isPlaying, isLoop) {
        while (isPlaying) {
            delay(50)
            timerUs += 50_000L
            if (timerUs > 12_000_000L) {
                timerUs = if (isLoop) 0L else 12_000_000L
            }
        }
    }

    val currentDisplayUs = if (activeAction != null) actionProgressUs else timerUs
    val seconds = currentDisplayUs / 1_000_000L
    val millis = (currentDisplayUs % 1_000_000L) / 1_000L
    val timecodeStr = String.format("%02d:%02d.%03d", seconds / 60, seconds % 60, millis)

    Box(
        modifier = modifier
            .testTag("camera_stream_preview")
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF070B14))
            .border(1.5.dp, if (isReplacing) NeonGreen else SurfaceCard, RoundedCornerShape(18.dp))
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            // Simulated Video Feed / Camera Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotationDegrees.toFloat()
                        scaleX = if (isMirrored) -1f else 1f
                    }
            ) {
                // Background subtle mesh grid
                val gridSpacing = 40.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0x1800E5FF),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x1800E5FF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Face Simulation Landmarks (shows virtual live face tracking)
                val cx = size.width / 2f
                val cy = size.height / 2f
                val faceRadius = (size.width.coerceAtMost(size.height) * 0.32f)

                // Face contour oval
                drawOval(
                    color = if (isReplacing) Color(0x3300E676) else Color(0x3300E5FF),
                    topLeft = Offset(cx - faceRadius * 0.75f, cy - faceRadius),
                    size = androidx.compose.ui.geometry.Size(faceRadius * 1.5f, faceRadius * 2f),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                )

                // Eye points
                val eyeOffsetY = cy - faceRadius * 0.3f
                val leftEyeX = cx - faceRadius * 0.35f
                val rightEyeX = cx + faceRadius * 0.35f
                val isBlinking = activeAction?.id == 1

                if (isBlinking) {
                    drawLine(
                        color = NeonCyan,
                        start = Offset(leftEyeX - 16f, eyeOffsetY),
                        end = Offset(leftEyeX + 16f, eyeOffsetY),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = NeonCyan,
                        start = Offset(rightEyeX - 16f, eyeOffsetY),
                        end = Offset(rightEyeX + 16f, eyeOffsetY),
                        strokeWidth = 4f
                    )
                } else {
                    drawCircle(color = NeonCyan, radius = 7f, center = Offset(leftEyeX, eyeOffsetY))
                    drawCircle(color = NeonCyan, radius = 7f, center = Offset(rightEyeX, eyeOffsetY))
                }

                // Nose bridge & mouth
                val noseY = cy + faceRadius * 0.1f
                drawCircle(color = ElectricBlue, radius = 5f, center = Offset(cx, noseY))

                val mouthY = cy + faceRadius * 0.45f
                val isMouthOpen = activeAction?.id == 3
                if (isMouthOpen) {
                    drawOval(
                        color = NeonAmber,
                        topLeft = Offset(cx - 24f, mouthY - 14f),
                        size = androidx.compose.ui.geometry.Size(48f, 28f),
                        style = Stroke(width = 3f)
                    )
                } else {
                    drawLine(
                        color = NeonCyan,
                        start = Offset(cx - 22f, mouthY),
                        end = Offset(cx + 22f, mouthY),
                        strokeWidth = 3f
                    )
                }

                // Tri-color Injection light highlight if active
                if (colorInject) {
                    val lightCenterX = size.width * (colorX / 100f)
                    val lightCenterY = size.height * (colorY / 100f)
                    val radius = (size.width.coerceAtMost(size.height) * (colorDiameter / 100f)).coerceAtLeast(20f)
                    val alpha = (colorIntensity / 100f).coerceIn(0.1f, 0.8f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF4081).copy(alpha = alpha),
                                Color(0xFF00E5FF).copy(alpha = alpha * 0.5f),
                                Color.Transparent
                            ),
                            center = Offset(lightCenterX, lightCenterY),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(lightCenterX, lightCenterY)
                    )

                    // Reticle marker
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = 8f,
                        center = Offset(lightCenterX, lightCenterY),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // Top Status Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Replacement Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isReplacing) Color(0x3300E676) else Color(0x442B3A64))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isReplacing) NeonGreen else NeonRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isReplacing) "虚拟替换生效中" else "相机直通 (未开启替换)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isReplacing) NeonGreen else Color(0xFFE2E8F0)
                    )
                }

                // Timecode
                Text(
                    text = timecodeStr,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x770A0E1A))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            // Active Action Floating HUD Banner
            AnimatedVisibility(
                visible = activeAction != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                activeAction?.let { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xCC0D1B2A))
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "正在执行: ${action.name}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${actionProgressUs} μs",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Bottom Quick Controls Floating Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC070B14))
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .testTag("preview_play_pause_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onRotate,
                        modifier = Modifier
                            .testTag("preview_rotate_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenRotation,
                            contentDescription = "旋转90度",
                            tint = Color(0xFFC5D1E8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleMirror,
                        modifier = Modifier
                            .testTag("preview_mirror_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "水平镜像",
                            tint = if (isMirrored) NeonCyan else Color(0xFFC5D1E8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleFrontPreview,
                        modifier = Modifier
                            .testTag("preview_camera_switch_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "前后镜头",
                            tint = if (frontPreview) NeonCyan else Color(0xFFC5D1E8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Mode Info Pill
                Text(
                    text = if (replacementMode == ReplacementMode.VIDEO) "本地视频模式" else "RTMP推流模式",
                    fontSize = 11.sp,
                    color = Color(0xFFA0AEC0),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
