package com.spotai.overlay

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class FloatingAssistantService : Service() {

    private lateinit var windowManager: WindowManager
    private var panel: LinearLayout? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (panel == null) {
            showOverlay(intent?.getStringExtra("mode") ?: "Trading")
        }
        return START_NOT_STICKY
    }

    private fun showOverlay(mode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !android.provider.Settings.canDrawOverlays(this)
        ) {
            stopSelf()
            return
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 16, 24, 16)
            setBackgroundColor(Color.rgb(27, 35, 48))
        }

        val title = TextView(this).apply {
            text = "SpotAI • $mode"
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        val result = TextView(this).apply {
            text = "Ready to analyse"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 12, 0, 12)
        }

        val analyse = Button(this).apply {
            text = "Analyse"
            setOnClickListener {
                result.text = "Analysing live screen view..."
            }
        }

        val close = Button(this).apply {
            text = "Close"
            setOnClickListener { stopSelf() }
        }

        root.addView(title)
        root.addView(result)
        root.addView(analyse)
        root.addView(close)

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                LayoutParams.TYPE_PHONE,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 160
        }

        panel = root
        windowManager.addView(root, params)
    }

    override fun onDestroy() {
        panel?.let {
            if (::windowManager.isInitialized) {
                windowManager.removeView(it)
            }
        }
        panel = null
        super.onDestroy()
    }
}
