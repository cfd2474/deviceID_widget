package com.example.devicenamewidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.provider.Settings
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
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
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
        appWidgetId: Int
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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
