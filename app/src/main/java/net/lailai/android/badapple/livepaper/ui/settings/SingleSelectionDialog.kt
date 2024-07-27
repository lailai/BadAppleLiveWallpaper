/**
 * Copyright (C) 2024 lailai.
 */
package net.lailai.android.badapple.livepaper.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.lailai.android.badapple.livepaper.ui.theme.BadAppleLiveWallpaperTheme

@Composable
fun SingleSelectionDialog(
    title: String,
    list: List<String>,
    initialSelectedIndex: Int,
    modifier: Modifier = Modifier,
    onOk: (index: Int) -> Unit = { _ -> },
    onCancel: () -> Unit = {}
) {
    val (selectedText, onSelect) = remember { mutableStateOf(list[initialSelectedIndex]) }
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(bottom = 4.dp),
                    fontSize = 24.sp,
                )
                list.forEach { text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = text == selectedText,
                                onClick = { onSelect(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = text == selectedText,
                            onClick = null,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Text(
                            text = text,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick = { onOk(list.indexOf(selectedText)) }) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }
            }
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
fun SingleSelectionDialogPreview() {
    BadAppleLiveWallpaperTheme {
        SingleSelectionDialog(
            title = "Dialog",
            list = listOf("hoge", "fuga", "piyo"),
            initialSelectedIndex = 1
        )
    }
}
