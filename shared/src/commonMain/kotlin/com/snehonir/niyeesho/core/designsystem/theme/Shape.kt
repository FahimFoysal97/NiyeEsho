package com.snehonir.niyeesho.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NiyeEshoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

object AppShapes {
    val card = RoundedCornerShape(14.dp)
    val cardLarge = RoundedCornerShape(18.dp)
    val button = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(10.dp)
    val sheet = RoundedCornerShape(24.dp)
    val dialog = RoundedCornerShape(24.dp)
    val iconBox = RoundedCornerShape(16.dp)
    val badge = RoundedCornerShape(8.dp)
    val surfaceSmall = RoundedCornerShape(12.dp)
    val surfaceMedium = RoundedCornerShape(16.dp)
    val surfaceLarge = RoundedCornerShape(20.dp)
    val hero = RoundedCornerShape(30.dp)
}
