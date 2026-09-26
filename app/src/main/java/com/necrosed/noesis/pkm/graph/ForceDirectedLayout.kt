package com.necrosed.noesis.pkm.graph

import androidx.compose.ui.geometry.Offset
import com.necrosed.noesis.ui.components.GraphEdge
import com.necrosed.noesis.ui.components.GraphNode
import kotlin.math.max
import kotlin.math.sqrt

class ForceDirectedLayout(
    private val repulsionStrength: Float = 50000f,
    private val attractionStrength: Float = 0.08f,
    private val damping: Float = 0.85f
) {
    fun computeLayout(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        iterations: Int = 100,
        boundsWidth: Float = 1000f,
        boundsHeight: Float = 1000f
    ): List<GraphNode> {
        if (nodes.isEmpty()) return emptyList()

        val positionMap = nodes.associate { node ->
            node.id to if (node.position == Offset.Zero) {
                Offset(
                    (0..boundsWidth.toInt().coerceAtLeast(1)).random().toFloat(),
                    (0..boundsHeight.toInt().coerceAtLeast(1)).random().toFloat()
                )
            } else node.position
        }.toMutableMap()

        val velocityMap = nodes.associate { it.id to Offset.Zero }.toMutableMap()

        repeat(iterations) {
            val forceMap = nodes.associate { it.id to Offset.Zero }.toMutableMap()

            // 1. Repulsion between all node pairs
            for (i in nodes.indices) {
                for (j in i + 1 until nodes.size) {
                    val idA = nodes[i].id
                    val idB = nodes[j].id
                    val posA = positionMap[idA]!!
                    val posB = positionMap[idB]!!

                    val delta = posA - posB
                    val distance = max(sqrt((delta.x * delta.x + delta.y * delta.y).toDouble()).toFloat(), 1f)
                    val force = (repulsionStrength / (distance * distance))

                    val normal = delta / distance
                    forceMap[idA] = forceMap[idA]!! + (normal * force)
                    forceMap[idB] = forceMap[idB]!! - (normal * force)
                }
            }

            // 2. Attraction along connected edges
            val nodeMap = nodes.associateBy { it.id }
            edges.forEach { edge ->
                if (nodeMap.containsKey(edge.fromId) && nodeMap.containsKey(edge.toId)) {
                    val posA = positionMap[edge.fromId]!!
                    val posB = positionMap[edge.toId]!!
                    val delta = posA - posB
                    val distance = max(sqrt((delta.x * delta.x + delta.y * delta.y).toDouble()).toFloat(), 1f)
                    val force = attractionStrength * distance

                    val normal = delta / distance
                    forceMap[edge.fromId] = forceMap[edge.fromId]!! - (normal * force)
                    forceMap[edge.toId] = forceMap[edge.toId]!! + (normal * force)
                }
            }

            // 3. Apply forces to velocities and position
            nodes.forEach { node ->
                val id = node.id
                val currentVel = (velocityMap[id]!! + forceMap[id]!!) * damping
                velocityMap[id] = currentVel
                positionMap[id] = positionMap[id]!! + currentVel
            }
        }

        return nodes.map { node ->
            node.copy(position = positionMap[node.id] ?: Offset.Zero)
        }
    }
}
