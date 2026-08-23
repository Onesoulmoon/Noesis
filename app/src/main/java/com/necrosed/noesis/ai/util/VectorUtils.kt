package com.necrosed.noesis.ai.util

import kotlin.math.sqrt

object VectorUtils {

    fun cosineSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        require(vectorA.size == vectorB.size) { "Vector dimensions must match." }

        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f

        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i] * vectorA[i]
            normB += vectorB[i] * vectorB[i]
        }

        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator > 0.0f) dotProduct / denominator else 0.0f
    }
}
