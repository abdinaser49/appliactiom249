package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.util.PrayerTimesCalculator
import com.example.data.util.QuranSurahData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mandeq Islamic", appName)
  }

  @Test
  fun `verify prayer times calculation`() {
    val city = PrayerTimesCalculator.SUPPORTED_CITIES.first()
    val schedule = PrayerTimesCalculator.calculatePrayerTimes(city)
    assertNotNull(schedule.fajr)
    assertNotNull(schedule.dhuhr)
    assertNotNull(schedule.asr)
    assertNotNull(schedule.maghrib)
    assertNotNull(schedule.isha)
    assertTrue(schedule.fajr.contains(":"))
  }

  @Test
  fun `verify quran surahs list has data`() {
    val surahs = QuranSurahData.ALL_SURAHS
    assertEquals(114, surahs.size)
    val fatiha = QuranSurahData.getSurahDetail(1)
    assertNotNull(fatiha)
    assertEquals(7, fatiha.verses.size)
  }
}
