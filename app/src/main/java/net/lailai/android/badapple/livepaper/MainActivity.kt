/**
 * Copyright (C) 2020-2023 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.app.Dialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

/**
 * ライブ壁紙設定画面に飛ばすだけのActivity
 * @author lailai
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openLiveWallpaperSettings()
    }

    // ライブ壁紙設定画面を開く
    private fun openLiveWallpaperSettings() {
        // 直接ライブ壁紙設定画面に遷移
        val badAppleIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, BadAppleLiveWallpaperService::class.java)
            )
        }
        try {
            startActivity(badAppleIntent)
            finish()
            return
        } catch (e: ActivityNotFoundException) {
            // nothing to do
        }

        // ライブ壁紙一覧に遷移
        val liveWallpaperIntent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
        try {
            startActivity(liveWallpaperIntent)
            finish()
        } catch (e: ActivityNotFoundException) {
            // 駄目ならエラーダイアログ表示
            showErrorDialog()
        }
    }

    // エラーダイアログを表示する
    private fun showErrorDialog() {
        ErrorDialogFragment().show(supportFragmentManager, "")
    }

    /** エラーダイアログ */
    class ErrorDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return context?.let {
                AlertDialog.Builder(it)
                    .setMessage(R.string.main_error_message)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        activity?.finish()
                    }
                    .create()
            } ?: super.onCreateDialog(savedInstanceState)
        }
    }
}
