package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.repository.SrsRepository

/**
 * BroadcastReceiver nhận báo thức nhắc học hằng ngày và khôi phục lịch sau khi thiết bị khởi động lại.
 */
class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            if (NotificationHelper.isDailyReminderEnabled(context) &&
                NotificationHelper.hasNotificationPermission(context)
            ) {
                NotificationHelper.scheduleDailyReminder(context)
            }
            return
        }

        if (action == NotificationHelper.ACTION_DAILY_STUDY_REMINDER) {
            if (NotificationHelper.isDailyReminderEnabled(context)) {
                SrsRepository.initialize(context)
                val stats = SrsRepository.getStatsSummary()
                val title = if (stats.dueCount > 0) {
                    "🥋 Haku N3: Có ${stats.dueCount} thẻ cần ôn hôm nay!"
                } else {
                    "🥋 Haku N3: Đã đến giờ luyện JLPT N3!"
                }
                val message = if (stats.streakDays > 0) {
                    "Duy trì chuỗi ${stats.streakDays} ngày liên tiếp bằng một phiên ôn tập ngắn 5 phút nhé!"
                } else {
                    "Hãy dành 5 phút ôn tập từ vựng và ngữ pháp N3 theo lịch SM-2 hôm nay!"
                }

                NotificationHelper.sendStudyReminderNotification(
                    context = context,
                    title = title,
                    message = message
                )

                // Lên lịch tự động cho ngày hôm sau
                NotificationHelper.scheduleDailyReminder(context)
            }
        }
    }
}
