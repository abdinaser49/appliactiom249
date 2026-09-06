package com.example.data.util

import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.*

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneHours: Double
)

data class PrayerTimesResult(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val minutesUntilNextPrayer: Int,
    val qiblaDirectionDegrees: Int
)

data class DhikrItem(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val targetCount: Int = 33,
    val reward: String = ""
)

data class DuaItem(
    val id: String,
    val title: String,
    val category: String,
    val arabic: String,
    val somali: String,
    val reference: String
)

object PrayerTimesCalculator {

    val SUPPORTED_CITIES = listOf(
        CityLocation("Muqdisho", "Soomaaliya", 2.0469, 45.3182, 3.0),
        CityLocation("Hargeysa", "Somaliland", 9.5600, 44.0650, 3.0),
        CityLocation("Garoowe", "Puntland", 8.4064, 48.4845, 3.0),
        CityLocation("Kismaayo", "Jubbaland", -0.3582, 42.5454, 3.0),
        CityLocation("Boosaaso", "Puntland", 11.2842, 49.1816, 3.0),
        CityLocation("Baydhabo", "Koofur Galbeed", 3.1138, 43.6498, 3.0),
        CityLocation("Jabuuti", "Djibouti", 11.8251, 42.5903, 3.0),
        CityLocation("Nairobi", "Kenya", -1.2921, 36.8219, 3.0),
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        CityLocation("Minneapolis", "United States", 44.9778, -93.2650, -5.0)
    )

    fun calculatePrayerTimes(
        city: CityLocation = SUPPORTED_CITIES[0],
        date: Date = Date()
    ): PrayerTimesResult {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        // Approximate Solar Equation calculations (Egyptian General Authority / MWL standard)
        val b = 2.0 * PI * (dayOfYear - 81) / 365.0
        val eot = 9.87 * sin(2.0 * b) - 7.53 * cos(b) - 1.5 * sin(b) // Equation of Time in minutes
        val declination = 23.45 * sin(2.0 * PI * (284.0 + dayOfYear) / 365.0) // Solar Declination in degrees
        val declinationRad = Math.toRadians(declination)
        val latRad = Math.toRadians(city.latitude)

        // Solar Noon in local time
        val timeOffsetMinutes = (city.timezoneHours * 60.0) - (city.longitude * 4.0) - eot
        val solarNoonMinutes = 12.0 * 60.0 + timeOffsetMinutes

        // Hour angle calculation helper
        fun hourAngle(angleDegrees: Double): Double? {
            val zenithRad = Math.toRadians(angleDegrees)
            val cosH = (cos(zenithRad) - sin(latRad) * sin(declinationRad)) / (cos(latRad) * cos(declinationRad))
            if (cosH < -1.0 || cosH > 1.0) return null
            return Math.toDegrees(acos(cosH))
        }

        // 1. Sunrise & Sunset (Zenith 90.833°)
        val hSun = hourAngle(90.833) ?: 90.0
        val sunriseMinutes = solarNoonMinutes - (hSun * 4.0)
        val sunsetMinutes = solarNoonMinutes + (hSun * 4.0)

        // 2. Fajr (Dawn - 18.0° or 19.5° below horizon -> Zenith 108.0°)
        val hFajr = hourAngle(108.0) ?: (hSun + 18.0)
        val fajrMinutes = solarNoonMinutes - (hFajr * 4.0)

        // 3. Dhuhr (Solar noon + 2 min buffer)
        val dhuhrMinutes = solarNoonMinutes + 2.0

        // 4. Asr (Shafi'i shadow factor = 1)
        val asrAltitude = Math.toDegrees(atan(1.0 / (1.0 + tan(abs(latRad - declinationRad)))))
        val hAsr = hourAngle(90.0 - asrAltitude) ?: 45.0
        val asrMinutes = solarNoonMinutes + (hAsr * 4.0)

        // 5. Maghrib (Sunset + 3 min buffer)
        val maghribMinutes = sunsetMinutes + 3.0

        // 6. Isha (Zenith 107.5° -> 17.5° below horizon)
        val hIsha = hourAngle(107.5) ?: (hSun + 17.5)
        val ishaMinutes = solarNoonMinutes + (hIsha * 4.0)

        fun formatTime(totalMinutes: Double): String {
            val normalized = ((totalMinutes.toInt() % 1440) + 1440) % 1440
            val hours = normalized / 60
            val mins = normalized % 60
            val ampm = if (hours >= 12) "PM" else "AM"
            val displayHour = if (hours % 12 == 0) 12 else hours % 12
            return String.format("%02d:%02d %s", displayHour, mins, ampm)
        }

        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val prayersList = listOf(
            Triple("Fajr (Subax)", fajrMinutes.toInt(), formatTime(fajrMinutes)),
            Triple("Dhuhr (Duhur)", dhuhrMinutes.toInt(), formatTime(dhuhrMinutes)),
            Triple("Asr (Casar)", asrMinutes.toInt(), formatTime(asrMinutes)),
            Triple("Maghrib (Qorrax-dhac)", maghribMinutes.toInt(), formatTime(maghribMinutes)),
            Triple("Isha (Cishe)", ishaMinutes.toInt(), formatTime(ishaMinutes))
        )

        var nextName = prayersList[0].first
        var nextTime = prayersList[0].third
        var diffMinutes = 0

        val next = prayersList.firstOrNull { it.second > currentMinutes }
        if (next != null) {
            nextName = next.first
            nextTime = next.third
            diffMinutes = next.second - currentMinutes
        } else {
            // After Isha, next is tomorrow's Fajr
            nextName = prayersList[0].first
            nextTime = prayersList[0].third
            diffMinutes = (1440 - currentMinutes) + prayersList[0].second
        }

        // Qibla Direction towards Ka'abah in Makkah (21.4225° N, 39.8262° E)
        val makkahLat = Math.toRadians(21.4225)
        val makkahLng = Math.toRadians(39.8262)
        val userLat = Math.toRadians(city.latitude)
        val userLng = Math.toRadians(city.longitude)
        val deltaLng = makkahLng - userLng

        val y = sin(deltaLng)
        val x = cos(userLat) * tan(makkahLat) - sin(userLat) * cos(deltaLng)
        var qiblaDegrees = Math.toDegrees(atan2(y, x)).toInt()
        qiblaDegrees = (qiblaDegrees + 360) % 360

        return PrayerTimesResult(
            fajr = formatTime(fajrMinutes),
            sunrise = formatTime(sunriseMinutes),
            dhuhr = formatTime(dhuhrMinutes),
            asr = formatTime(asrMinutes),
            maghrib = formatTime(maghribMinutes),
            isha = formatTime(ishaMinutes),
            nextPrayerName = nextName,
            nextPrayerTime = nextTime,
            minutesUntilNextPrayer = diffMinutes,
            qiblaDirectionDegrees = qiblaDegrees
        )
    }

    val TASBEEH_LIST = listOf(
        DhikrItem(
            id = "dhikr_1",
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "Subhaanallah",
            translation = "Ilaahay baa ceeb kasta ka nasahan",
            targetCount = 33,
            reward = "Waxaa loo qoraa 1000 xasano ama 1000 dambi baa laga tiraa."
        ),
        DhikrItem(
            id = "dhikr_2",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alxamdulillah",
            translation = "Mahad oo dhan Ilaahay bay u sugnaatay",
            targetCount = 33,
            reward = "Meezaanka aakhiro ayey buuxisaa."
        ),
        DhikrItem(
            id = "dhikr_3",
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translation = "Ilaahay baa wax kasta ka weyn",
            targetCount = 34,
            reward = "Samada iyo dhulka dhexdooda ayey iftiin ka buuxisaa."
        ),
        DhikrItem(
            id = "dhikr_4",
            arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            transliteration = "Astaghfirullah wa atoobu ilayh",
            translation = "Ilaahay baan dambi dhaaf weydiisanayaa waana u toobad keenayaa",
            targetCount = 100,
            reward = "Risiqa ayey furtaa, dhibka iyo walbahaarkana way kaxeysaa."
        ),
        DhikrItem(
            id = "dhikr_5",
            arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            transliteration = "Laa ilaaha illallaah waxdahu laa shariika lah",
            translation = "Ilaah xaq lagu caabudo ma jiro Ilaahay kaligiis mooyee, lamaane ma laha",
            targetCount = 100,
            reward = "Difaac iyo gaashaan bay ka noqotaa shaydaanka maintaas oo dhan."
        )
    )

    val DAILY_DUAS = listOf(
        DuaItem(
            id = "dua_morning",
            title = "Ducada Subaxda",
            category = "Subaxda & Galabta",
            arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            somali = "Waxaan waabariisannay iyadoo boqortooyada oo dhan Ilaahay leeyahay, mahadna Ilaahay baa iska leh, Ilaah xaq lagu caabudona ma jiro Ilaahay kaligiis mooyee.",
            reference = "Saxiixul Muslim"
        ),
        DuaItem(
            id = "dua_evening",
            title = "Ducada Galabta",
            category = "Subaxda & Galabta",
            arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            somali = "Waxaan galabaysannay iyadoo mulkiga oo dhan Ilaahay u sugnaaday, mahadna Eebbe ayaa iska leh.",
            reference = "Saxiixul Muslim"
        ),
        DuaItem(
            id = "dua_sleep",
            title = "Ducada Hurdda Ka Hor",
            category = "Hurdada",
            arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا",
            somali = "Magacaaga Rabbiyow ayaan dhinaca dhulka u dhigay, magacaagana waan ku qaadayaa, haddaad naftayda qaaddana u naxariiso.",
            reference = "Bukhaari & Muslim"
        ),
        DuaItem(
            id = "dua_parents",
            title = "Ducada Waalidiinta",
            category = "Qur'aan",
            arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            somali = "Rabbiyow labadayda waalid u naxariiso sidii ay iigu soo barbaariyeen yaraantaydii.",
            reference = "Suuradda Al-Israa: 24"
        ),
        DuaItem(
            id = "dua_knowledge",
            title = "Ducada Cilmiga & Fahamka",
            category = "Cilmiga",
            arabic = "رَبِّ زِدْنِي عِلْمًا وَارْزُقْنِي فَهْمًا",
            somali = "Rabbiyow cilmiga ii kordhi, fahamka iyo garashadana igu arzaaq.",
            reference = "Suuradda Daahaa: 114"
        )
    )
}
