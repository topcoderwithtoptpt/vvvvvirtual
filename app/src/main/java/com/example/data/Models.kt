package com.example.data

enum class ReplacementMode {
    VIDEO,
    RTMP
}

enum class ColorMode(val id: Int, val title: String, val description: String) {
    INTRA_FRAME(0, "帧内提取", "从视频帧内容中分析并自适应调节色彩映射"),
    VIDEO_REPLACE(1, "视频替换", "直接使用预设的色彩通道替换实时画面"),
    REALTIME_RENDER(2, "实时渲染", "自动根据屏幕监测点颜色给视频注入高动态光照")
}

data class FacialAction(
    val id: Int,
    val name: String,
    val shortLabel: String,
    val defaultStartUs: Long,
    val defaultEndUs: Long,
    val description: String
)

object ActionPresets {
    val DEFAULT_ACTIONS = listOf(
        FacialAction(
            id = 1,
            name = "眨眼",
            shortLabel = "眼",
            defaultStartUs = 0L,
            defaultEndUs = 1_170_000L,
            description = "模拟双眼快速眨动动作"
        ),
        FacialAction(
            id = 2,
            name = "抬头",
            shortLabel = "↑",
            defaultStartUs = 5_000_000L,
            defaultEndUs = 5_900_000L,
            description = "头部向上仰起仰视动作"
        ),
        FacialAction(
            id = 3,
            name = "张嘴",
            shortLabel = "嘴",
            defaultStartUs = 2_000_000L,
            defaultEndUs = 3_200_000L,
            description = "下颌张开嘴唇微启说话动作"
        ),
        FacialAction(
            id = 4,
            name = "左转头",
            shortLabel = "←",
            defaultStartUs = 3_200_000L,
            defaultEndUs = 4_000_000L,
            description = "头部向左侧旋转视角"
        ),
        FacialAction(
            id = 5,
            name = "回正",
            shortLabel = "正",
            defaultStartUs = 4_000_000L,
            defaultEndUs = 4_000_000L,
            description = "面部复位回正面中心位置"
        ),
        FacialAction(
            id = 6,
            name = "右转头",
            shortLabel = "→",
            defaultStartUs = 4_000_000L,
            defaultEndUs = 5_000_000L,
            description = "头部向右侧旋转视角"
        ),
        FacialAction(
            id = 8,
            name = "点头",
            shortLabel = "↓",
            defaultStartUs = 5_600_000L,
            defaultEndUs = 6_800_000L,
            description = "头部上下点头微动动作"
        )
    )
}
