package com.example.moneytrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Линейные иконки в сетке 24×24 (как в макете). Без сторонних библиотек. */
object LineIcons {
    val Home = listOf("M4 10.5L12 4l8 6.5V19a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1z")
    val Stats = listOf("M12 3v9h9", "M20.5 15A9 9 0 1 1 9 3.5")
    val Calendar = listOf("M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2z", "M3 10h18", "M8 3v4", "M16 3v4")
    val User = listOf("M12 4a4 4 0 1 0 0 8a4 4 0 1 0 0-8z", "M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6")
    val Plus = listOf("M12 5v14", "M5 12h14")
    val Bell = listOf("M6 16V11a6 6 0 0 1 12 0v5l1.5 2h-15z", "M10 20.5a2 2 0 0 0 4 0")
    val ArrowRight = listOf("M5 12h14", "M13 6l6 6-6 6")
    val ChevronLeft = listOf("M15 5l-7 7 7 7")
    val ChevronRight = listOf("M9 5l7 7-7 7")
    val Cart = listOf("M3 4h2l2.2 10.2a1 1 0 0 0 1 .8h8.6a1 1 0 0 0 1-.8L19.5 8H6.2", "M7.6 19a1.4 1.4 0 1 0 2.8 0a1.4 1.4 0 1 0-2.8 0z", "M15.6 19a1.4 1.4 0 1 0 2.8 0a1.4 1.4 0 1 0-2.8 0z")
    val Bus = listOf("M8 3h8a3 3 0 0 1 3 3v11H5V6a3 3 0 0 1 3-3z", "M5 10h14", "M8 17l-1.5 3", "M16 17l1.5 3")
    val Film = listOf("M5 6h14a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2z", "M3 10h18", "M7 6l2 4", "M12 6l2 4", "M17 6l2 4")
    val Heart = listOf("M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z")
    val Phone = listOf("M9.5 2.5h5a2.5 2.5 0 0 1 2.5 2.5v14a2.5 2.5 0 0 1-2.5 2.5h-5A2.5 2.5 0 0 1 7 19V5a2.5 2.5 0 0 1 2.5-2.5z", "M11 18.5h2")
    val Dots = listOf("M6 12h.01", "M12 12h.01", "M18 12h.01")
    val Close = listOf("M6 6l12 12", "M18 6L6 18")
    val Trash = listOf("M4 7h16", "M10 11v6", "M14 11v6", "M6 7l1 12a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2l1-12", "M9 7V4h6v3")
    val Check = listOf("M5 12.5l4.5 4.5L19 7")
    val Wallet = listOf("M4 7a2 2 0 0 1 2-2h11v2", "M6 7h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2z", "M16 13h2")
}

@Composable
fun LineIcon(
    paths: List<String>,
    color: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 22.dp,
    strokeWidth: Float = 2f,
) {
    val parsed = remember(paths) { paths.map { PathParser().parsePathString(it).toPath() } }
    Canvas(modifier.size(iconSize)) {
        scale(scale = this.size.width / 24f, pivot = Offset.Zero) {
            parsed.forEach {
                drawPath(it, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

/** Круглая кнопка 44 dp с иконкой (стрелки месяца и т.п.) */
@Composable
fun CircleIconButton(
    icon: List<String>,
    description: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        LineIcon(icon, color, strokeWidth = 2.2f)
    }
}
