package com.necrosed.noesis.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.necrosed.noesis.ui.theme.NoesisViolet

data class GraphNode(val id: Long, val label: String, val position: Offset = Offset.Zero)
data class GraphEdge(val fromId: Long, val toId: Long)

@Composable
fun KnowledgeGraphCanvas(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    onNodeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.4f, 3.0f)
                    offset += pan
                }
            }
    ) {
        val nodeMap = nodes.associateBy { it.id }

        // 1. Draw Connecting Edge Lines
        edges.forEach { edge ->
            val startNode = nodeMap[edge.fromId]
            val endNode = nodeMap[edge.toId]
            if (startNode != null && endNode != null) {
                val start = (startNode.position * scale) + offset
                val end = (endNode.position * scale) + offset
                drawLine(
                    color = Color.Gray.copy(alpha = 0.4f),
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx() * scale
                )
            }
        }

        // 2. Draw Concept Circle Nodes & Labels
        nodes.forEach { node ->
            val nodePos = (node.position * scale) + offset
            val radius = 16.dp.toPx() * scale

            drawCircle(
                color = NoesisViolet,
                radius = radius,
                center = nodePos
            )

            val textLayoutResult = textMeasurer.measure(
                text = node.label,
                style = TextStyle(color = Color.White, fontSize = (11 * scale).sp)
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = nodePos.x - (textLayoutResult.size.width / 2f),
                    y = nodePos.y + radius + 4.dp.toPx()
                )
            )
        }
    }
}
