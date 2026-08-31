package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceCard
import kotlin.math.roundToInt

@Composable
fun FloatingOverlayController(
    isVisible: Boolean,
    isExpanded: Boolean,
    isPlaying: Boolean,
    isLoop: Boolean,
    onToggleExpanded: () -> Unit,
    onClose: () -> Unit,
    onTriggerAction: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleLoop: () -> Unit,
    onRotate: () -> Unit,
    onToggleMirror: () -> Unit,
    onSwitchVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var offsetX by remember { mutableFloatStateOf(20f) }
    var offsetY by remember { mutableFloatStateOf(160f) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, 600f)
                    offsetY = (offsetY + dragAmount.y).coerceIn(50f, 1500f)
                }
            }
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xE60D1424))
            .border(1.5.dp, Color(0xFF2E3E6E), RoundedCornerShape(16.dp))
            .padding(8.dp)
            .testTag("floating_overlay_controller")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Drag Header Bar
            Row(
                modifier = Modifier
                    .width(if (isExpanded) 220.dp else 120.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VLive 控制",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleExpanded,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("floating_minimize_button")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "折叠" else "展开",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("floating_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭悬浮窗",
                            tint = NeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1: [眼] [↑] [嘴] [1]
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GridPadButton(text = "眼", color = NeonCyan, testTag = "float_btn_eye") { onTriggerAction(1) }
                        GridPadButton(text = "↑", color = ElectricBlue, testTag = "float_btn_up") { onTriggerAction(2) }
                        GridPadButton(text = "嘴", color = NeonAmber, testTag = "float_btn_mouth") { onTriggerAction(3) }
                        GridPadButton(text = "1", color = NeonPurple, testTag = "float_btn_1") { onTriggerAction(1) }
                    }

                    // Row 2: [←] [正] [→] [2]
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GridPadButton(text = "←", color = ElectricBlue, testTag = "float_btn_left") { onTriggerAction(4) }
                        GridPadButton(text = "正", color = NeonGreen, testTag = "float_btn_center") { onTriggerAction(5) }
                        GridPadButton(text = "→", color = ElectricBlue, testTag = "float_btn_right") { onTriggerAction(6) }
                        GridPadButton(text = "2", color = NeonPurple, testTag = "float_btn_2") { onTriggerAction(2) }
                    }

                    // Row 3: [播] [↓] [循] [3]
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GridPadButton(
                            text = if (isPlaying) "停" else "播",
                            color = if (isPlaying) NeonAmber else NeonGreen,
                            testTag = "float_btn_play"
                        ) { onTogglePlayPause() }

                        GridPadButton(text = "↓", color = ElectricBlue, testTag = "float_btn_down") { onTriggerAction(8) }

                        GridPadButton(
                            text = "循",
                            color = if (isLoop) NeonCyan else Color.Gray,
                            testTag = "float_btn_loop"
                        ) { onToggleLoop() }

                        GridPadButton(text = "3", color = NeonPurple, testTag = "float_btn_3") { onTriggerAction(3) }
                    }

                    // Row 4: [转] [翻] [关] [替]
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GridPadButton(text = "转", color = ElectricBlue, testTag = "float_btn_rotate") { onRotate() }
                        GridPadButton(text = "翻", color = NeonCyan, testTag = "float_btn_flip") { onToggleMirror() }
                        GridPadButton(text = "关", color = NeonRed, testTag = "float_btn_close_action") { onClose() }
                        GridPadButton(text = "替", color = NeonGreen, testTag = "float_btn_switch") { onSwitchVideo() }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridPadButton(
    text: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color(0xFF16213A),
            contentColor = color
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}
