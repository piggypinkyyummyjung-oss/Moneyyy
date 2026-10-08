package com.example.jars.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.jars.QuickActivity
import com.example.jars.R

class ExpenseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_expense)

            views.setTextViewText(R.id.w_today_left, "฿0")
            views.setTextViewText(R.id.w_today_spent, "ใช้ไปแล้ว ฿0")

            val intent = Intent(context, QuickActivity::class.java)
            val pi = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.w_root, pi)

            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
