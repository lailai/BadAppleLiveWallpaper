/**
 * Copyright (C) 2024 lailai.
 */
package net.lailai.android.badapple.livepaper.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import net.lailai.android.badapple.livepaper.R
import net.lailai.android.badapple.livepaper.ui.settings.SettingsScreen
import net.lailai.android.badapple.livepaper.ui.theme.BadAppleLiveWallpaperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onBack: () -> Unit = {}) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Image(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SettingsScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Preview(
    device = Devices.PIXEL,
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_UNDEFINED
)
@Composable
fun AppPreview() {
    BadAppleLiveWallpaperTheme {
        App()
    }
}
