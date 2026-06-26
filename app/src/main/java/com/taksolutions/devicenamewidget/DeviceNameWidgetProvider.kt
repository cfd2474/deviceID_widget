package com.taksolutions.devicenamewidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.widget.RemoteViews

class DeviceNameWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Ensure the worker is scheduled (wrapped in try-catch for boot safety)
        try {
            DeviceNameUpdateWorker.schedule(context.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Iterate through all widgets currently added to the homescreen
        for (appWidgetId in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, options)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        try {
            DeviceNameUpdateWorker.schedule(context.applicationContext)
        } catch (e: Exception) {}
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        try {
            DeviceNameUpdateWorker.cancel(context.applicationContext)
        } catch (e: Exception) {}
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        options: Bundle? = null
    ) {
        // Retrieve the device name
        var deviceName = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
        if (deviceName.isNullOrEmpty()) {
            deviceName = Build.MODEL // Fallback to model if the device name is not available
        }

        // Retrieve settings
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val textColor = prefs.getInt("textColor", android.graphics.Color.WHITE)
        val bgColor = prefs.getInt("bgColor", android.graphics.Color.TRANSPARENT)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_device_name)
        views.setTextViewText(R.id.widget_text, deviceName)
        views.setTextColor(R.id.widget_text, textColor)
        views.setInt(R.id.widget_layout, "setBackgroundColor", bgColor)

        // Calculate text size based on widget dimensions
        val opts = options ?: appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
        val maxHeight = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 40)
        
        // Very basic calculation: fit text width-wise or height-wise
        // 1 char is roughly 0.6x width of height
        val charCount = deviceName.length.coerceAtLeast(1)
        val maxFontSizeByHeight = maxHeight * 0.7f
        val maxFontSizeByWidth = (minWidth / charCount.toFloat()) * 1.5f
        
        val targetTextSize = maxFontSizeByHeight.coerceAtMost(maxFontSizeByWidth).coerceIn(12f, 60f)
        views.setTextViewTextSize(R.id.widget_text, TypedValue.COMPLEX_UNIT_DIP, targetTextSize)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
