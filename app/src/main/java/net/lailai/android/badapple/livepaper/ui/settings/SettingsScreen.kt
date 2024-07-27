/**
 * Copyright (C) 2024 lailai.
 */
package net.lailai.android.badapple.livepaper.ui.settings

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import net.lailai.android.badapple.livepaper.Constants
import net.lailai.android.badapple.livepaper.R
import net.lailai.android.badapple.livepaper.ui.theme.BadAppleLiveWallpaperTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val context = LocalContext.current
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        var openSizeSelectDialog by remember { mutableStateOf(false) }
        var openDirectionSelectDialog by remember { mutableStateOf(false) }
        var openNumberPickerDialog by remember { mutableStateOf(false) }
        Column {
            ListItem(
                modifier = Modifier.clickable { openSizeSelectDialog = true },
                headlineContent = { Text(text = stringResource(id = R.string.aspect_title)) },
                supportingContent = { Text(text = stringResource(id = R.string.aspect_summary)) }
            )
            HorizontalDivider()
            ListItem(
                modifier = Modifier.clickable { openDirectionSelectDialog = true },
                headlineContent = { Text(text = stringResource(id = R.string.direction_title)) },
                supportingContent = { Text(text = stringResource(id = R.string.direction_sumary)) }
            )
            HorizontalDivider()
            ListItem(
                modifier = Modifier.clickable { openNumberPickerDialog = true },
                headlineContent = { Text(text = stringResource(id = R.string.fps_title)) },
                supportingContent = { Text(text = stringResource(id = R.string.fps_summary)) }
            )
            HorizontalDivider()
        }
        if (openSizeSelectDialog) {
            val aspects = stringArrayResource(id = R.array.aspect)
            val aspectValues = stringArrayResource(id = R.array.aspect_value)
            SingleSelectionDialog(
                title = stringResource(id = R.string.aspect_title),
                list = aspects.asList(),
                initialSelectedIndex = aspectValues.indexOf(
                    sharedPreferences.getString(Constants.PREF_KEY_ASPECT, aspectValues[2])
                        ?: aspectValues[2]
                ),
                onOk = { index ->
                    sharedPreferences.edit {
                        putString(Constants.PREF_KEY_ASPECT, aspectValues[index])
                    }
                    openSizeSelectDialog = false
                },
                onCancel = { openSizeSelectDialog = false }
            )
        }
        if (openDirectionSelectDialog) {
            val directions = stringArrayResource(id = R.array.direction)
            val directionValues = stringArrayResource(id = R.array.direction_value)
            SingleSelectionDialog(
                title = stringResource(id = R.string.direction_title),
                list = directions.asList(),
                initialSelectedIndex = directionValues.indexOf(
                    sharedPreferences.getString(Constants.PREF_KEY_DIRECTION, directionValues[0])
                        ?: directionValues[0]
                ),
                onOk = { index ->
                    sharedPreferences.edit {
                        putString(Constants.PREF_KEY_DIRECTION, directionValues[index])
                    }
                    openDirectionSelectDialog = false
                },
                onCancel = { openDirectionSelectDialog = false }
            )
        }
        if (openNumberPickerDialog) {
            NumberPickerDialog(
                title = stringResource(id = R.string.fps_title),
                range = 1..30,
                initialNumber = sharedPreferences
                    .getString(Constants.PREF_KEY_FPS, "30")?.toInt() ?: 30,
                onOk = { number ->
                    sharedPreferences.edit { putString(Constants.PREF_KEY_FPS, number.toString()) }
                    openNumberPickerDialog = false
                },
                onCancel = { openNumberPickerDialog = false }
            )
        }
    }
}

@Preview(
    device = Devices.PIXEL,
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_UNDEFINED
)
@Composable
fun SettingsScreenPreview() {
    BadAppleLiveWallpaperTheme {
        SettingsScreen()
    }
}
