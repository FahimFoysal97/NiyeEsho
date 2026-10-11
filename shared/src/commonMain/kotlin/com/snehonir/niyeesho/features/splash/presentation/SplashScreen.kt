package com.snehonir.niyeesho.features.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

// region Color Tokens
private val BgDeep        = Color(0xFF0A1510)
private val GreenPrimary  = Color(0xFF1B8A6B)
private val GreenLight    = Color(0xFFA7F3D0)
private val GreenEmerald  = Color(0xFF6EE7B7)
private val GreenSage     = Color(0xFF6B8A7A)
private val GreenDark     = Color(0xFF0F5C47)
private val GreenMuted    = Color(0xFF2D4A3A)
private val GreenSurface  = Color(0xFF1F2D24)
// endregion

// region Particle Data
private data class Particle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val baseOpacity: Float,
    val speed: Float,
    val drift: Float,
)

private data class PulseRing(
    var radius: Float,
    var opacity: Float,
)
// endregion

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    // region Entrance animation states
    val iconAlpha    = remember { Animatable(0f) }
    val iconScale    = remember { Animatable(0.4f) }
    val iconOffsetY  = remember { Animatable(20f) }
    val titleAlpha   = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(18f) }
    val tagAlpha     = remember { Animatable(0f) }
    val tagOffsetY   = remember { Animatable(12f) }
    val bottomAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Icon — bouncy pop entrance
        launch {
            delay(120.milliseconds)
            launch {
                iconAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                )
            }
            launch {
                iconScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness    = Spring.StiffnessLow,
                    ),
                )
            }
            launch {
                iconOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(700, easing = FastOutSlowInEasing),
                )
            }
        }
        // Title — rises in
        launch {
            delay(480.milliseconds)
            launch {
                titleAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(700, easing = FastOutSlowInEasing),
                )
            }
            launch {
                titleOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(700, easing = FastOutSlowInEasing),
                )
            }
        }
        // Tagline — rises after title
        launch {
            delay(720.milliseconds)
            launch {
                tagAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                )
            }
            launch {
                tagOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                )
            }
        }
        // Bottom loading section
        launch {
            delay(1000.milliseconds)
            bottomAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutSlowInEasing),
            )
        }
    }
    // endregion

    // region Continuous animations
    val infiniteTransition = rememberInfiniteTransition(label = "SplashLoop")

    // Center glow breathing
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue  = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breatheScale",
    )
    val breatheOpacity by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue  = 0.85f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breatheOpacity",
    )

    // Scan line sweep
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue  = 1.1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scanLine",
    )

    // Icon glow pulse
    val iconGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 0.9f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "iconGlow",
    )
    // endregion

    // region Particle system (remembered, mutated in Canvas)
    val particles = remember {
        List(48) {
            Particle(
                x           = Random.nextFloat() * 1000f,
                y           = Random.nextFloat() * 2000f,
                radius      = Random.nextFloat() * 2.2f + 0.4f,
                baseOpacity = Random.nextFloat() * 0.35f + 0.05f,
                speed       = Random.nextFloat() * 0.6f + 0.15f,
                drift       = (Random.nextFloat() - 0.5f) * 0.25f,
            )
        }.toMutableList()
    }

    // Pulse rings list — new ring spawned every 2.4s
    val pulseRings = remember { mutableStateListOf<PulseRing>() }
    var pulseFrame by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2400.milliseconds)
            pulseRings.add(PulseRing(radius = 40f, opacity = 0.55f))
        }
    }
    // endregion

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep),
        contentAlignment = Alignment.Center,
    ) {

        // region Background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w        = size.width
            val h        = size.height
            val centerX  = w / 2f
            val centerY  = h * 0.40f

            drawAmbientGlows(
                centerX        = centerX,
                centerY        = centerY,
                breatheScale   = breatheScale,
                breatheOpacity = breatheOpacity,
                width          = w,
                height         = h,
            )

            drawDotGrid(centerX = centerX, centerY = centerY, width = w, height = h)

            drawFloatingParticles(particles = particles, width = w, height = h)

            drawPulseRings(
                rings   = pulseRings,
                centerX = centerX,
                centerY = centerY,
            )

            // Scan line sweep
            val scanY = h * scanProgress
            drawRect(
                brush = Brush.verticalGradient(
                    colors    = listOf(
                        Color.Transparent,
                        GreenPrimary.copy(alpha = 0.04f),
                        GreenEmerald.copy(alpha = 0.06f),
                        GreenPrimary.copy(alpha = 0.04f),
                        Color.Transparent,
                    ),
                    startY = scanY - 60f,
                    endY   = scanY + 60f,
                ),
                topLeft = Offset(0f, scanY - 60f),
                size    = Size(w, 120f),
            )
        }
        // endregion

        // region Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(horizontal = 32.dp),
        ) {

            // App icon with glow halo
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .offset(y = iconOffsetY.value.dp)
                    .graphicsLayer {
                        alpha  = iconAlpha.value
                        scaleX = iconScale.value
                        scaleY = iconScale.value
                    },
            ) {
                // Outer glow halo
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .graphicsLayer { alpha = iconGlowAlpha }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GreenPrimary.copy(alpha = 0.35f),
                                    Color.Transparent,
                                ),
                            ),
                            shape = CircleShape,
                        ),
                )
                // Icon card
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(100.dp)
                        .shadow(
                            elevation    = 32.dp,
                            shape        = RoundedCornerShape(26.dp),
                            spotColor    = GreenPrimary.copy(alpha = 0.6f),
                            ambientColor = GreenPrimary.copy(alpha = 0.3f),
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(GreenPrimary, GreenDark),
                            ),
                        ),
                ) {
                    // Shopping bag icon drawn with Canvas for KMP compatibility
                    Canvas(modifier = Modifier.size(56.dp)) {
                        val w    = size.width
                        val h    = size.height
                        val sw   = w * 0.08f

                        // Bag handle arc
                        drawArc(
                            color      = GreenLight.copy(alpha = 0.9f),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter  = false,
                            topLeft    = Offset(w * 0.28f, h * 0.06f),
                            size       = Size(w * 0.44f, h * 0.36f),
                            style      = Stroke(width = sw, cap = StrokeCap.Round),
                        )
                        // Bag body
                        val bodyLeft   = w * 0.10f
                        val bodyTop    = h * 0.38f
                        val bodyRight  = w * 0.90f
                        val bodyBottom = h * 0.94f
                        val cornerR    = w * 0.14f
                        drawRoundRect(
                            color       = GreenLight.copy(alpha = 0.15f),
                            topLeft     = Offset(bodyLeft, bodyTop),
                            size        = Size(
                                bodyRight - bodyLeft,
                                bodyBottom - bodyTop,
                            ),
                            cornerRadius = CornerRadius(cornerR),
                        )
                        drawRoundRect(
                            color       = GreenLight.copy(alpha = 0.7f),
                            topLeft     = Offset(bodyLeft, bodyTop),
                            size        = Size(
                                bodyRight - bodyLeft,
                                bodyBottom - bodyTop,
                            ),
                            cornerRadius = CornerRadius(cornerR),
                            style        = Stroke(width = sw * 0.8f),
                        )
                        // Checkmark inside bag
                        val cx = w * 0.50f
                        val cy = h * 0.68f
                        drawLine(
                            color       = GreenLight,
                            start       = Offset(cx - w * 0.20f, cy),
                            end         = Offset(cx - w * 0.04f, cy + h * 0.13f),
                            strokeWidth = sw,
                            cap         = StrokeCap.Round,
                        )
                        drawLine(
                            color       = GreenLight,
                            start       = Offset(cx - w * 0.04f, cy + h * 0.13f),
                            end         = Offset(cx + w * 0.22f, cy - h * 0.14f),
                            strokeWidth = sw,
                            cap         = StrokeCap.Round,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bengali title with gradient
            Text(
                text  = "নিয়ে এসো।",
                style = TextStyle(
                    fontSize   = 52.sp,
                    fontWeight = FontWeight.Bold,
                    brush      = Brush.linearGradient(
                        colors = listOf(
                            GreenLight,
                            GreenEmerald,
                            GreenPrimary,
                        ),
                    ),
                    lineHeight    = 58.sp,
                    letterSpacing = (-0.5).sp,
                ),
                modifier = Modifier
                    .offset(y = titleOffsetY.value.dp)
                    .graphicsLayer { alpha = titleAlpha.value },
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text  = "Share lists. Shop together.",
                style = TextStyle(
                    fontSize      = 14.sp,
                    color         = GreenSage,
                    letterSpacing = 0.3.sp,
                ),
                modifier = Modifier
                    .offset(y = tagOffsetY.value.dp)
                    .graphicsLayer { alpha = tagAlpha.value },
            )
        }
        // endregion

        // region Bottom loading
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .graphicsLayer { alpha = bottomAlpha.value },
        ) {
            BouncingDotsLoader()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text  = "NIYE ESHO",
                style = TextStyle(
                    fontSize      = 10.sp,
                    color         = GreenMuted,
                    letterSpacing = 2.5.sp,
                    fontWeight    = FontWeight.Medium,
                ),
            )
        }
        // endregion
    }
}

// region Canvas draw helpers

private fun DrawScope.drawAmbientGlows(
    centerX: Float,
    centerY: Float,
    breatheScale: Float,
    breatheOpacity: Float,
    width: Float,
    height: Float,
) {
    // Center breathing glow
    val breathRadius = 260.dp.toPx() * breatheScale
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF1B8A6B).copy(alpha = 0.42f * breatheOpacity),
                Color(0xFF1B8A6B).copy(alpha = 0.10f * breatheOpacity),
                Color.Transparent,
            ),
            center = Offset(centerX, centerY),
            radius = breathRadius,
        ),
        center = Offset(centerX, centerY),
        radius = breathRadius,
    )

    // Top-right accent glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF1B8A6B).copy(alpha = 0.28f),
                Color.Transparent,
            ),
            center = Offset(width + 80.dp.toPx(), -80.dp.toPx()),
            radius = 300.dp.toPx(),
        ),
        center = Offset(width + 80.dp.toPx(), -80.dp.toPx()),
        radius = 300.dp.toPx(),
    )

    // Bottom-left secondary glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF6EE7B7).copy(alpha = 0.10f),
                Color.Transparent,
            ),
            center = Offset(-80.dp.toPx(), height + 80.dp.toPx()),
            radius = 260.dp.toPx(),
        ),
        center = Offset(-80.dp.toPx(), height + 80.dp.toPx()),
        radius = 260.dp.toPx(),
    )
}

private fun DrawScope.drawDotGrid(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
) {
    val dotSpacing = 22.dp.toPx()
    val dotRadius  = 1.3.dp.toPx()
    val maxDist    = width * 0.72f

    var y = 0f
    while (y < height) {
        var x = 0f
        while (x < width) {
            val dx   = x - centerX
            val dy   = y - centerY
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < maxDist) {
                val factor = (1f - dist / maxDist).coerceIn(0f, 1f)
                drawCircle(
                    color  = Color(0xFF6EE7B7).copy(alpha = 0.09f * factor),
                    radius = dotRadius,
                    center = Offset(x, y),
                )
            }
            x += dotSpacing
        }
        y += dotSpacing
    }
}

private fun DrawScope.drawFloatingParticles(
    particles: List<Particle>,
    width: Float,
    height: Float,
) {
    particles.forEach { p ->
        // mutate position
        p.y  -= p.speed
        p.x  += p.drift
        if (p.y < -6f)    p.y = height + 4f
        if (p.x < -6f)    p.x = width + 4f
        if (p.x > width + 6f) p.x = -4f

        drawCircle(
            color  = Color(0xFFA7F3D0).copy(alpha = p.baseOpacity),
            radius = p.radius,
            center = Offset(p.x, p.y),
        )
    }
}

private fun DrawScope.drawPulseRings(
    rings: MutableList<PulseRing>,
    centerX: Float,
    centerY: Float,
) {
    val toRemove = mutableListOf<PulseRing>()
    rings.forEach { ring ->
        ring.radius  += 3.5f
        ring.opacity -= 0.010f
        if (ring.opacity <= 0f) {
            toRemove.add(ring)
            return@forEach
        }
        drawCircle(
            color  = Color(0xFF1B8A6B).copy(alpha = ring.opacity),
            radius = ring.radius,
            center = Offset(centerX, centerY),
            style  = Stroke(width = 1.5.dp.toPx()),
        )
    }
    rings.removeAll(toRemove)
}

// endregion

// region Bouncing dots loader

@Composable
private fun BouncingDotsLoader(
    modifier:  Modifier = Modifier,
    dotColor:  Color    = GreenEmerald,
    dotSizeDp: Dp       = 6.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BouncingDots")
    val delays = listOf(0, 160, 320)

    Row(
        modifier             = modifier,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment    = Alignment.CenterVertically,
    ) {
        delays.forEachIndexed { index, delayMs ->
            val anim by infiniteTransition.animateFloat(
                initialValue  = 0f,
                targetValue   = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1100
                        0.0f at 0
                        1.0f at 420
                        0.0f at 840
                        0.0f at 1100
                    },
                    repeatMode          = RepeatMode.Restart,
                    initialStartOffset  = androidx.compose.animation.core.StartOffset(delayMs),
                ),
                label = "Dot_$index",
            )

            val offsetY = (-5).dp * anim
            val alpha   = 0.22f + 0.78f * anim
            val scale   = 0.7f + 0.3f * anim

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .size(dotSizeDp)
                    .graphicsLayer {
                        this.alpha  = alpha
                        this.scaleX = scale
                        this.scaleY = scale
                    }
                    .background(
                        color = dotColor,
                        shape = CircleShape,
                    ),
            )
        }
    }
}
