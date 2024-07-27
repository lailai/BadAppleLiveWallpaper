/**
 * Copyright (C) 2024 lailai.
 */
package net.lailai.android.badapple.livepaper.ui.settings

import android.content.res.Configuration
import android.widget.NumberPicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import net.lailai.android.badapple.livepaper.ui.theme.BadAppleLiveWallpaperTheme

@Composable
fun NumberPickerDialog(
    title: String,
    range: IntRange,
    initialNumber: Int,
    modifier: Modifier = Modifier,
    onOk: (number: Int) -> Unit = { _ -> },
    onCancel: () -> Unit = {}
) {
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
                        .height(48.dp)
                        .padding(bottom = 16.dp),
                    fontSize = 24.sp,
                )
                var selectedNumber = remember { initialNumber }
                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = range.first
                            maxValue = range.last
                            value = initialNumber
                            setOnValueChangedListener { _, _, newVal ->
                                selectedNumber = newVal
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick = { onOk(selectedNumber) }) {
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
fun NumberPickerDialogPreview() {
    BadAppleLiveWallpaperTheme {
        NumberPickerDialog(
            title = "Dialog",
            range = 1..30,
            initialNumber = 30
        )
    }
}
