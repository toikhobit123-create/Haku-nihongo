package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Haku N3", appName)
  }

  @Test
  fun `schedule and cancel daily study reminder updates AlarmManager and prefs`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.util.NotificationHelper.scheduleDailyReminder(context, hour = 20, minute = 30)

    org.junit.Assert.assertTrue(com.example.util.NotificationHelper.isDailyReminderEnabled(context))
    assertEquals(Pair(20, 30), com.example.util.NotificationHelper.getReminderTime(context))

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarm = org.robolectric.Shadows.shadowOf(alarmManager)
    org.junit.Assert.assertNotNull(shadowAlarm.nextScheduledAlarm)

    com.example.util.NotificationHelper.cancelDailyReminder(context)
    org.junit.Assert.assertFalse(com.example.util.NotificationHelper.isDailyReminderEnabled(context))
    org.junit.Assert.assertNull(shadowAlarm.nextScheduledAlarm)
  }
}
