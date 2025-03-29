package com.newolab.activitymonitor.ui.components.TopAppBar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
//    SmallTopAppBar(
//        title = title,
//        modifier = modifier,
//        navigationIcon = if (navigationIcon != null && onNavigationClick != null) {
//            {
//                IconButton(onClick = onNavigationClick) {
//                    Icon(
//                        imageVector = navigationIcon,
//                        contentDescription = null
//                    )
//                }
//            }
//        } else null,
//        actions = actions
//    )
}