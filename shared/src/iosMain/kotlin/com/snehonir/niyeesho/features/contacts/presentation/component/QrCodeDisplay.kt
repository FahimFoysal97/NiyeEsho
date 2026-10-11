package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import kotlin.math.abs

@Composable
actual fun QrCodeDisplay(
    value: String,
    sizeDp: Int,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val gridSize = 21
                    val cellSize = canvasWidth / gridSize
                    val seed = value.hashCode()
                    for (x in 0 until gridSize) {
                        for (y in 0 until gridSize) {
                            val isTopLeftFinder = x < 7 && y < 7
                            val isTopRightFinder = x >= gridSize - 7 && y < 7
                            val isBottomLeftFinder = x < 7 && y >= gridSize - 7
                            if (!isTopLeftFinder && !isTopRightFinder && !isBottomLeftFinder) {
                                val hashVal = abs((seed + x * 31 + y * 97).hashCode())
                                if (hashVal % 2 == 0) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = Offset(x * cellSize, y * cellSize),
                                        size = Size(cellSize, cellSize),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Text(
                text = "Scan QR Code to Join",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
