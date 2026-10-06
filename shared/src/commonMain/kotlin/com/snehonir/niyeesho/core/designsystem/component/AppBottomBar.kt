package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import niyeesho.shared.generated.resources.Res
import niyeesho.shared.generated.resources.contacts
import niyeesho.shared.generated.resources.lists
import niyeesho.shared.generated.resources.person_add
import niyeesho.shared.generated.resources.profile
import niyeesho.shared.generated.resources.scan
import org.jetbrains.compose.resources.painterResource

enum class AppNavTab {
    LISTS, CONTACTS, INVITE, PROFILE
}

@Composable
fun AppBottomBar(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NiyeEshoDarkBg),
    ) {
        HorizontalDivider(color = NiyeEshoDarkSurface, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(
                label = "Lists",
                icon = painterResource(Res.drawable.lists),
                isSelected = currentTab == AppNavTab.LISTS,
                onClick = { onTabSelected(AppNavTab.LISTS) },
            )
            NavItem(
                label = "Contacts",
                icon = painterResource(Res.drawable.contacts),
                isSelected = currentTab == AppNavTab.CONTACTS,
                onClick = { onTabSelected(AppNavTab.CONTACTS) },
            )
            NavItem(
                label = "Invite",
                icon = painterResource(Res.drawable.scan),
                isSelected = currentTab == AppNavTab.INVITE,
                onClick = { onTabSelected(AppNavTab.INVITE) },
            )
            NavItem(
                label = "Profile",
                icon = painterResource(Res.drawable.profile),
                isSelected = currentTab == AppNavTab.PROFILE,
                onClick = { onTabSelected(AppNavTab.PROFILE) },
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: Painter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = NiyeEshoGreenPrimary
    val inactiveColor = NiyeEshoGreenDarkMuted

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor,
        )
    }
}
