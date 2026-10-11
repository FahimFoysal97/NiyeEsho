package com.snehonir.niyeesho.features.splash.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoEmeraldAccent
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoSageGreen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import niyeesho.shared.generated.resources.Res
import niyeesho.shared.generated.resources.niye_esho_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // Ambient Center Glow Breathing Animation (3s loop)
    val infiniteTransition = rememberInfiniteTransition(label = "SplashGlowBreathe")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    val breatheOpacity by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheOpacity"
    )

    // Entrance Animations
    // Icon Pop (0.8s)
    val iconAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "iconAlpha"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.55f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )
    val iconOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 14.dp,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "iconOffsetY"
    )

    // Title Rise (delay 250ms)
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 250, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val titleOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = 700, delayMillis = 250, easing = FastOutSlowInEasing),
        label = "titleOffsetY"
    )

    // Tagline Rise (delay 400ms)
    val tagAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "tagAlpha"
    )
    val tagOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = 700, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "tagOffsetY"
    )

    // Loading Section Rise (delay 600ms)
    val loadAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "loadAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
        contentAlignment = Alignment.Center
    ) {
        // Background Ambient Glows & Dot Grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height * 0.42f)

            // Glow A (Top-Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NiyeEshoGreenPrimary.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(width + 110.dp.toPx(), -120.dp.toPx()),
                    radius = 340.dp.toPx()
                ),
                center = Offset(width + 110.dp.toPx(), -120.dp.toPx()),
                radius = 340.dp.toPx()
            )

            // Glow B (Bottom-Left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NiyeEshoEmeraldAccent.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(-120.dp.toPx(), height + 110.dp.toPx()),
                    radius = 300.dp.toPx()
                ),
                center = Offset(-120.dp.toPx(), height + 110.dp.toPx()),
                radius = 300.dp.toPx()
            )

            // Glow C (Center Breathing)
            val breathingRadius = 280.dp.toPx() * breatheScale
            val breathingColor = NiyeEshoGreenPrimary.copy(alpha = 0.45f * breatheOpacity)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(breathingColor, Color.Transparent),
                    center = center,
                    radius = breathingRadius
                ),
                center = center,
                radius = breathingRadius
            )

            // Dot Grid Overlay
            val dotSpacing = 22.dp.toPx()
            val dotRadius = 1.2.dp.toPx()
            val maxDistance = width * 0.75f

            var y = 0f
            while (y < height) {
                var x = 0f
                while (x < width) {
                    val dx = x - center.x
                    val dy = y - center.y
                    val dist = kotlin.math.sqrt((dx * dx) + (dy * dy))

                    if (dist < maxDistance) {
                        val factor = (1f - (dist / maxDistance)).coerceIn(0f, 1f)
                        val dotAlpha = 0.12f * factor
                        drawCircle(
                            color = NiyeEshoGreenLight.copy(alpha = dotAlpha),
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                    }
                    x += dotSpacing
                }
                y += dotSpacing
            }
        }

        // Center Content (Icon + Title + Tagline)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // App Icon with Pop Animation
            Box(
                modifier = Modifier
                    .offset(y = iconOffsetY)
                    .graphicsLayer {
                        alpha = iconAlpha
                        scaleX = iconScale
                        scaleY = iconScale
                    }
                    .shadow(
                        elevation = 28.dp,
                        shape = AppShapes.hero,
                        spotColor = NiyeEshoGreenPrimary.copy(alpha = 0.5f),
                        ambientColor = NiyeEshoGreenPrimary.copy(alpha = 0.5f)
                    )
                    .clip(AppShapes.hero)
                    .size(132.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.niye_esho_icon),
                    contentDescription = AppStrings.APP_NAME,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Heading Title ("নিয়ে এসো।")
            Text(
                text = "নিয়ে এসো।",
                style = TextStyle(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(NiyeEshoGreenLight, NiyeEshoEmeraldAccent)
                    )
                ),
                modifier = Modifier
                    .offset(y = titleOffsetY)
                    .graphicsLayer { alpha = titleAlpha }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline ("Shared lists, made easy")
            Text(
                text = AppStrings.TAGLINE_SPLASH,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = NiyeEshoSageGreen
                ),
                modifier = Modifier
                    .offset(y = tagOffsetY)
                    .graphicsLayer { alpha = tagAlpha }
            )
        }

        // Bottom Loading Section (3 Bouncing Dots + Version)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .graphicsLayer { alpha = loadAlpha }
        ) {
            // 3-Dot Bouncing Animation
            BouncingDotsLoader()

            Spacer(modifier = Modifier.height(14.dp))

            // Version Label
            Text(
                text = AppStrings.APP_NAME,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = NiyeEshoGreenDarkMuted,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

@Composable
private fun BouncingDotsLoader(
    modifier: Modifier = Modifier,
    dotColor: Color = NiyeEshoEmeraldAccent
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BouncingDots")
    val delays = listOf(0, 180, 360)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEachIndexed { index, delayMillis ->
            val animatedValue by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0
                        1.0f at 480
                        0.0f at 1200
                    },
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(delayMillis)
                ),
                label = "Dot_$index"
            )

            val offsetY = (-4).dp * animatedValue
            val opacity = 0.25f + (0.75f * animatedValue)

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .size(7.dp)
                    .graphicsLayer { alpha = opacity }
                    .background(color = dotColor, shape = CircleShape)
            )
        }
    }
}
