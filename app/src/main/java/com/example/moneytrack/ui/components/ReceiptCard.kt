package com.example.moneytrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.moneytrack.ui.theme.Dotted
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.White

/**
 * КАСТОМНЫЙ КОМПОНЕНТ №1 — форма «оторванного чека»:
 * скруглённый верх и зубчатый (рваный) нижний край.
 */
class ZigzagBottomShape(
    private val tooth: Dp = 12.dp,
    private val depth: Dp = 8.dp,
    private val corner: Dp = 18.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val t = with(density) { tooth.toPx() }
        val d = with(density) { depth.toPx() }
        val r = with(density) { corner.toPx() }
        val bottom = size.height - d
        val teeth = (size.width / t).toInt().coerceAtLeast(1)
        val step = size.width / teeth
        val path = Path().apply {
            moveTo(0f, r)
            arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
            lineTo(size.width - r, 0f)
            arcTo(Rect(size.width - 2 * r, 0f, size.width, 2 * r), 270f, 90f, false)
            lineTo(size.width, bottom)
            for (i in teeth downTo 1) {
                val x = i * step
                lineTo(x - step / 2, size.height)
                lineTo(x - step, bottom)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/** Та же идея, но зубцы сверху — для нижней панели на экране входа. */
class ZigzagTopShape(
    private val tooth: Dp = 12.dp,
    private val depth: Dp = 10.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val t = with(density) { tooth.toPx() }
        val d = with(density) { depth.toPx() }
        val teeth = (size.width / t).toInt().coerceAtLeast(1)
        val step = size.width / teeth
        val path = Path().apply {
            moveTo(0f, d)
            for (i in 0 until teeth) {
                lineTo(i * step + step / 2, 0f)
                lineTo((i + 1) * step, d)
            }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

/** Карточка-чек: белая, с рваным краем снизу. */
@Composable
fun ReceiptCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ZigzagBottomShape())
            .background(White)
            .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

/** Пунктирная линия между блоками чека */
@Composable
fun DashedDivider(color: Color = Dotted) {
    Canvas(Modifier.fillMaxWidth().height(2.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())),
        )
    }
}

/** Точки от названия до суммы: «ЕДА ........ 850,00» */
@Composable
private fun DottedLeader(modifier: Modifier = Modifier) {
    Canvas(modifier.height(2.dp)) {
        drawLine(
            color = Dotted,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 5.dp.toPx())),
        )
    }
}

/** Строка чека: категория + комментарий слева, сумма справа */
@Composable
fun ReceiptRow(title: String, subtitle: String, amount: Long, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        DottedLeader(
            Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp, bottom = 5.dp)
        )
        Text(receiptAmount(amount), style = MaterialTheme.typography.labelLarge, color = Ink)
    }
}

/** Простая строка чека: «USD ......... 92,50» */
@Composable
fun ReceiptLine(left: String, right: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Text(left, style = MaterialTheme.typography.labelLarge, color = Ink)
        DottedLeader(
            Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp, bottom = 5.dp)
        )
        Text(right, style = MaterialTheme.typography.labelLarge, color = Ink)
    }
}

private val barcodeBars = listOf(2, 1, 3, 1, 2, 1, 3, 1, 2, 1, 1, 3, 2, 1, 3, 1, 2, 1, 3, 1, 2, 1, 1, 3, 2, 1, 3, 1, 2, 1, 3, 1)

/** Декоративный штрихкод внизу чека */
@Composable
fun Barcode(modifier: Modifier = Modifier, color: Color = Ink) {
    Canvas(modifier.width(120.dp).height(22.dp)) {
        val unit = size.width / 110f
        var x = 0f
        barcodeBars.forEach { w ->
            drawRect(color, topLeft = Offset(x * unit, 0f), size = Size(w * unit, size.height))
            x += w + 1.4f
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2EDE3, widthDp = 360)
@Composable
private fun ReceiptCardPreview() {
    MoneyTrackTheme {
        Column(Modifier.background(Paper).padding(18.dp)) {
            ReceiptCard {
                Text("ЧЕК ЗА СЕГОДНЯ", style = MaterialTheme.typography.labelMedium)
                DashedDivider()
                ReceiptRow("Еда", "Пятёрочка, 12:40", 850)
                ReceiptRow("Транспорт", "Метро, 09:15", 62)
                DashedDivider()
                Barcode()
            }
        }
    }
}
