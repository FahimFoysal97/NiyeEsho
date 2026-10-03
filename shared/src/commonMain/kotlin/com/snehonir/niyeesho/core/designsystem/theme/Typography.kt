package com.snehonir.niyeesho.core.designsystem.theme

import androidx.compose.material3.Typography

val NiyeEshoTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
        titleLarge = titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
    )
}
