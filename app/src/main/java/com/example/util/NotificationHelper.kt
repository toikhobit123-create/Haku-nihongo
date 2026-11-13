package com.example.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import java.util.Calendar
import java.util.TimeZone

object NotificationHelper {

    const val CHANNEL_ID = "haku_n3_study_reminder"
    const val CHANNEL_NAME = "Nhắc nhở học tập N3"
    const val CHANNEL_DESC = "Thông báo nhắc nhở ôn tập Spaced Repetition (SRS) hàng ngày"
    const val ACTION_DAILY_STUDY_REMINDER = "com.example.ACTION_DAILY_STUDY_REMINDER"

    private const val NOTIFICATION_ID = 1001
    private const val REMINDER_REQUEST_CODE = 2001
    private const val PREFS_REMINDER = "haku_n3_reminder_prefs"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val KEY_REMINDER_MINUTE = "reminder_minute"

    const val DEFAULT_REMINDER_HOUR = 20
    const val DEFAULT_REMINDER_MINUTE = 0

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isDailyReminderEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_REMINDER, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, true)
    }

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_REMINDER, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR).coerceIn(0, 23)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE).coerceIn(0, 59)
        return Pair(hour, minute)
    }

    /**
     * Tính thời điểm kích hoạt tiếp theo (theo giờ:phút hằng ngày).
     * Nếu giờ hẹn hôm nay đã trôi qua, tự động chuyển sang cùng giờ ngày mai.
     */
    fun computeNextTriggerTimeMillis(
        hour: Int,
        minute: Int,
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }

    /**
     * Lập lịch thông báo nhắc học tự động hằng ngày qua AlarmManager (không cần quyền báo thức chính xác đặc biệt).
     */
    fun scheduleDailyReminder(
        context: Context,
        hour: Int? = null,
        minute: Int? = null
    ) {
        val appCtx = context.applicationContext
        val (savedHour, savedMinute) = getReminderTime(appCtx)
        val targetHour = (hour ?: savedHour).coerceIn(0, 23)
        val targetMinute = (minute ?: savedMinute).coerceIn(0, 59)

        appCtx.getSharedPreferences(PREFS_REMINDER, Context.MODE_PRIVATE).edit().apply {
            putBoolean(KEY_REMINDER_ENABLED, true)
            putInt(KEY_REMINDER_HOUR, targetHour)
            putInt(KEY_REMINDER_MINUTE, targetMinute)
            apply()
        }

        createNotificationChannel(appCtx)

        val alarmManager = appCtx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = buildReminderPendingIntent(appCtx)
        alarmManager.cancel(pendingIntent)

        val triggerAtMillis = computeNextTriggerTimeMillis(targetHour, targetMinute)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    /**
     * Hủy lịch nhắc học tự động hằng ngày.
     */
    fun cancelDailyReminder(context: Context) {
        val appCtx = context.applicationContext
        appCtx.getSharedPreferences(PREFS_REMINDER, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_REMINDER_ENABLED, false)
            .apply()

        val alarmManager = appCtx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        alarmManager.cancel(buildReminderPendingIntent(appCtx))
    }

    private fun buildReminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, StudyReminderReceiver::class.java).apply {
            action = ACTION_DAILY_STUDY_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun sendStudyReminderNotification(
        context: Context,
        title: String = "🥋 Haku N3: Đến giờ ôn tập SRS!",
        message: String = "Duy trì chuỗi ngày học (Streak) và làm chủ từ vựng, ngữ pháp N3 hôm nay nhé!"
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission was revoked in settings
        }
    }
}
