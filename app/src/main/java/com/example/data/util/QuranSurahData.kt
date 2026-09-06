package com.example.data.util

data class QuranVerse(
    val verseNumber: Int,
    val arabic: String,
    val somaliTranslation: String,
    val englishTranslation: String = ""
)

data class SurahMeta(
    val number: Int,
    val name: String,
    val arabicName: String,
    val somaliMeaning: String,
    val englishName: String,
    val totalAyahs: Int,
    val revelationType: String, // "Makki" ama "Madani"
    val juz: Int
)

data class SurahDetail(
    val number: Int,
    val name: String,
    val arabicName: String,
    val meaning: String,
    val totalAyahs: Int,
    val revelationType: String,
    val audioUrl: String,
    val verses: List<QuranVerse>
)

data class QuranReciter(
    val id: String,
    val name: String,
    val somaliLabel: String,
    val arabicName: String,
    val serverUrlPattern: String
)

object QuranSurahData {

    val RECITERS = listOf(
        QuranReciter(
            id = "afs",
            name = "Mishary Rashid Alafasy",
            somaliLabel = "Sheekh Mishari Raashid Al-Cafaasi",
            arabicName = "مشاري راشد العفاسي",
            serverUrlPattern = "https://server8.mp3quran.net/afs/%03d.mp3"
        ),
        QuranReciter(
            id = "basit",
            name = "Abdul Basit Abdul Samad",
            somaliLabel = "Sheekh Cabdulbaasid Cabdusamad",
            arabicName = "عبد الباسط عبد الصمد",
            serverUrlPattern = "https://server7.mp3quran.net/basit/%03d.mp3"
        ),
        QuranReciter(
            id = "husr",
            name = "Mahmoud Khalil Al-Husary",
            somaliLabel = "Sheekh Maxamuud Khaliil Al-Xusari",
            arabicName = "محمود خليل الحصري",
            serverUrlPattern = "https://server13.mp3quran.net/husr/%03d.mp3"
        ),
        QuranReciter(
            id = "minsh",
            name = "Mohamed Siddiq Al-Minshawi",
            somaliLabel = "Sheekh Maxamed Sidiiq Al-Minshaawi",
            arabicName = "محمد صديق المنشاوي",
            serverUrlPattern = "https://server10.mp3quran.net/minsh/%03d.mp3"
        ),
        QuranReciter(
            id = "sds",
            name = "Abdur-Rahman Al-Sudais",
            somaliLabel = "Sheekh Cabdiraxmaan Al-Sudays",
            arabicName = "عبد الرحمن السديس",
            serverUrlPattern = "https://server11.mp3quran.net/sds/%03d.mp3"
        ),
        QuranReciter(
            id = "gmd",
            name = "Saad Al-Ghamdi",
            somaliLabel = "Sheekh Sacad Al-Ghaamidi",
            arabicName = "سعد الغامدي",
            serverUrlPattern = "https://server7.mp3quran.net/s_gmd/%03d.mp3"
        )
    )

    fun getAudioUrlForSurah(surahNumber: Int, reciterId: String = "afs"): String {
        val reciter = RECITERS.find { it.id == reciterId } ?: RECITERS.first()
        return String.format(reciter.serverUrlPattern, surahNumber)
    }

    // Complete canonical list of all 114 Surahs of the Holy Qur'an
    val ALL_SURAHS: List<SurahMeta> = listOf(
        SurahMeta(1, "Al-Faatixa", "الفَاتِحَة", "Furitaanka Kitaabka", "The Opening", 7, "Makki", 1),
        SurahMeta(2, "Al-Baqarah", "البَقَرَة", "Sacdii", "The Cow", 286, "Madani", 1),
        SurahMeta(3, "Aali Cimraan", "آل عِمْرَان", "Reer Cimraan", "Family of Imran", 200, "Madani", 3),
        SurahMeta(4, "An-Nisaa", "النِّسَاء", "Haweenka", "The Women", 176, "Madani", 4),
        SurahMeta(5, "Al-Maa'idah", "المَائِدَة", "Miiska Cuntada", "The Table Spread", 120, "Madani", 6),
        SurahMeta(6, "Al-Ancaam", "الأَنْعَام", "Xoolaha", "The Cattle", 165, "Makki", 7),
        SurahMeta(7, "Al-Acraaf", "الأَعْرَاف", "Darbiyada Sare", "The Heights", 206, "Makki", 8),
        SurahMeta(8, "Al-Anfaal", "الأَنْفَال", "Qaniimada", "The Spoils of War", 75, "Madani", 9),
        SurahMeta(9, "At-Tawbah", "التَّوْبَة", "Towbada", "The Repentance", 129, "Madani", 10),
        SurahMeta(10, "Yuunus", "يُونُس", "Nabi Yuunus (CS)", "Jonah", 109, "Makki", 11),
        SurahMeta(11, "Huud", "هُود", "Nabi Huud (CS)", "Hud", 123, "Makki", 11),
        SurahMeta(12, "Yuusuf", "يُوسُف", "Nabi Yuusuf (CS)", "Joseph", 111, "Makki", 12),
        SurahMeta(13, "Ar-Racd", "الرَّعْد", "Onkodka", "The Thunder", 43, "Madani", 13),
        SurahMeta(14, "Ibraahiim", "إِبْرَاهِيم", "Nabi Ibraahiim (CS)", "Abraham", 52, "Makki", 13),
        SurahMeta(15, "Al-Xijr", "الحِجْر", "Dooxadii Dhagaxa", "The Rocky Tract", 99, "Makki", 14),
        SurahMeta(16, "An-Naxl", "النَّحْل", "Shinida", "The Bee", 128, "Makki", 14),
        SurahMeta(17, "Al-Israa", "الإِسْرَاء", "Safarkii Habeenkii", "The Night Journey", 111, "Makki", 15),
        SurahMeta(18, "Al-Kahf", "الكَهْف", "Godka", "The Cave", 110, "Makki", 15),
        SurahMeta(19, "Maryam", "مَرْيَم", "Maryam (CS)", "Mary", 98, "Makki", 16),
        SurahMeta(20, "Daahaa", "طه", "Daahaa", "Ta-Ha", 135, "Makki", 16),
        SurahMeta(21, "Al-Anbiyaa", "الأَنْبِيَاء", "Nabiyada", "The Prophets", 112, "Makki", 17),
        SurahMeta(22, "Al-Xajj", "الحَجّ", "Xajka", "The Pilgrimage", 78, "Madani", 17),
        SurahMeta(23, "Al-Mu'minuun", "المُؤْمِنُون", "Mu'miniinta", "The Believers", 118, "Makki", 18),
        SurahMeta(24, "An-Nuur", "النُّور", "Iftiinka", "The Light", 64, "Madani", 18),
        SurahMeta(25, "Al-Furqaan", "الفُرْقَان", "Kala Bixiyaha", "The Criterion", 77, "Makki", 18),
        SurahMeta(26, "Ash-Shucaraa", "الشُّعَرَاء", "Gabayaaga", "The Poets", 227, "Makki", 19),
        SurahMeta(27, "An-Naml", "النَّمْل", "Qudhaanjada", "The Ant", 93, "Makki", 19),
        SurahMeta(28, "Al-Qasas", "القَصَص", "Sheekooyinka", "The Stories", 88, "Makki", 20),
        SurahMeta(29, "Al-Cankabuut", "العَنْكَبُوت", "Caarada", "The Spider", 69, "Makki", 20),
        SurahMeta(30, "Ar-Ruum", "الرُّوم", "Roomaanka", "The Romans", 60, "Makki", 21),
        SurahMeta(31, "Luqmaan", "لُقْمَان", "Luqmaan Xakiim", "Luqman", 34, "Makki", 21),
        SurahMeta(32, "As-Sajdah", "السَّجْدَة", "Sujuudda", "The Prostration", 30, "Makki", 21),
        SurahMeta(33, "Al-Axzaab", "الأَحْزَاب", "Xulafada", "The Combined Forces", 73, "Madani", 21),
        SurahMeta(34, "Saba", "سَبَإ", "Reer Saba", "Sheba", 54, "Makki", 22),
        SurahMeta(35, "Faadir", "فَاطِر", "Abuuraha", "The Originator", 45, "Makki", 22),
        SurahMeta(36, "Yaasiin", "يس", "Yaasiin (Qalbiga Qur'aanka)", "Ya-Sin", 83, "Makki", 22),
        SurahMeta(37, "As-Saaffaat", "الصَّافَّات", "Kuwa Safan", "Those Ranked in Rows", 182, "Makki", 23),
        SurahMeta(38, "Saad", "ص", "Xarafka Saad", "The Letter Sad", 88, "Makki", 23),
        SurahMeta(39, "Az-Zumar", "الزُّمَر", "Kooxaha", "The Crowds", 75, "Makki", 23),
        SurahMeta(40, "Ghaafir", "غَافِر", "Dambi Dhaafaha", "The Forgiver", 85, "Makki", 24),
        SurahMeta(41, "Fussilat", "فُصِّلَت", "Kala Caddeynta", "Distinguished", 54, "Makki", 24),
        SurahMeta(42, "Ash-Shuuraa", "الشُّورَى", "Wadatashiga", "The Consultation", 53, "Makki", 25),
        SurahMeta(43, "Az-Zukhruf", "الزُّخْرُف", "Qurxinta Dahabka", "The Gold Adornment", 89, "Makki", 25),
        SurahMeta(44, "Ad-Dukhaan", "الدُّخَان", "Qiiqa", "The Smoke", 59, "Makki", 25),
        SurahMeta(45, "Al-Jaathiyah", "الجَاثِيَة", "Jilba Joogsiga", "The Crouching", 37, "Makki", 25),
        SurahMeta(46, "Al-Axqaaf", "الأَحْقَاف", "Dudumada Ciidda", "The Sand Dunes", 35, "Makki", 26),
        SurahMeta(47, "Muxammad", "مُحَمَّد", "Nabi Muxammad (SCW)", "Muhammad", 38, "Madani", 26),
        SurahMeta(48, "Al-Fatx", "الفَتْح", "Guusha Cad", "The Victory", 29, "Madani", 26),
        SurahMeta(49, "Al-Xujuraat", "الحُجُرَات", "Qolalka", "The Rooms", 18, "Madani", 26),
        SurahMeta(50, "Qaaf", "ق", "Xarafka Qaaf", "The Letter Qaf", 45, "Makki", 26),
        SurahMeta(51, "Adh-Dhaariyaat", "الذَّارِيَات", "Kuwa Dabaysha Kala Firdhiya", "The Winnowing Winds", 60, "Makki", 26),
        SurahMeta(52, "At-Tuur", "الطُّور", "Buurta Tuur", "The Mount", 49, "Makki", 27),
        SurahMeta(53, "An-Najm", "النَّجْم", "Xiddigta", "The Star", 62, "Makki", 27),
        SurahMeta(54, "Al-Qamar", "القَمَر", "Dayaxa", "The Moon", 55, "Makki", 27),
        SurahMeta(55, "Ar-Raxmaan", "الرَّحْمَٰن", "Eebbaha Naxariista Guud", "The Beneficent", 78, "Madani", 27),
        SurahMeta(56, "Al-Waaqicah", "الوَاقِعَة", "Dhacdada Wayn (Qiyaamada)", "The Inevitable", 96, "Makki", 27),
        SurahMeta(57, "Al-Xadiid", "الحَدِيد", "Birta", "The Iron", 29, "Madani", 27),
        SurahMeta(58, "Al-Mujaadilah", "المُجَادِلَة", "Haweentii Doodaysay", "The Pleading Woman", 22, "Madani", 28),
        SurahMeta(59, "Al-Xashr", "الحَشْر", "Kukicinta", "The Exile", 24, "Madani", 28),
        SurahMeta(60, "Al-Mumtaxanah", "المُمْتَحَنَة", "Kala Saarka Haweenka", "She That Is to Be Examined", 13, "Madani", 28),
        SurahMeta(61, "As-Saff", "الصَّفّ", "Safafka", "The Battle Array", 14, "Madani", 28),
        SurahMeta(62, "Al-Jumucah", "الجُمُعَة", "Maalinta Jimcaha", "Friday", 11, "Madani", 28),
        SurahMeta(63, "Al-Munaafiquun", "المُنَافِقُون", "Munaafiqiinta", "The Hypocrites", 11, "Madani", 28),
        SurahMeta(64, "At-Taghaabun", "التَّغَابُن", "Khasaaraha & Faa'iidada", "Mutual Loss and Gain", 18, "Madani", 28),
        SurahMeta(65, "At-Dalaaq", "الطَّلَاق", "Furitaanka", "The Divorce", 12, "Madani", 28),
        SurahMeta(66, "At-Taxriim", "التَّحْرِيم", "Reebista", "The Prohibition", 12, "Madani", 28),
        SurahMeta(67, "Al-Mulk", "المُلْك", "Boqortooyada (Tabaaraka)", "The Sovereignty", 30, "Makki", 29),
        SurahMeta(68, "Al-Qalam", "القَلَم", "Qalinka", "The Pen", 52, "Makki", 29),
        SurahMeta(69, "Al-Xaaqqah", "الحَاقَّة", "Xaqiiqada Huban", "The Inevitable Reality", 52, "Makki", 29),
        SurahMeta(70, "Al-Macaarij", "المَعَارِج", "Waddooyinka Koritaanka", "The Ascending Stairways", 44, "Makki", 29),
        SurahMeta(71, "Nuux", "نُوح", "Nabi Nuux (CS)", "Noah", 28, "Makki", 29),
        SurahMeta(72, "Al-Jinn", "الجِنّ", "Jinniga", "The Jinn", 28, "Makki", 29),
        SurahMeta(73, "Al-Muzzammil", "المُزَّمِّل", "Kan Is-Dadaabay", "The Enshrouded One", 20, "Makki", 29),
        SurahMeta(74, "Al-Muddaththir", "المُدَّثِّر", "Kan Huwaday Dharka", "The Cloaked One", 56, "Makki", 29),
        SurahMeta(75, "Al-Qiyaamah", "القِيَامَة", "Maalinta Qiyaame", "The Resurrection", 40, "Makki", 29),
        SurahMeta(76, "Al-Insaan", "الإِنْسَان", "Dadka / Waqtiga", "The Human", 31, "Madani", 29),
        SurahMeta(77, "Al-Mursalaat", "المُرْسَلَات", "Kuwa La Soo Diray", "The Emissaries", 50, "Makki", 29),
        SurahMeta(78, "An-Naba", "النَّبَإ", "Warka Weyn (Camma)", "The Tidings", 40, "Makki", 30),
        SurahMeta(79, "An-Naazicaat", "النَّازِعَات", "Malaa'igta Nafta Siibta", "Those Who Drag Forth", 46, "Makki", 30),
        SurahMeta(80, "Cabasa", "عَبَسَ", "Wuu Weji Xumeeyay", "He Frowned", 42, "Makki", 30),
        SurahMeta(81, "At-Takwiir", "التَّكْوِير", "Isku Duubidda Qorraxda", "The Overthrowing", 29, "Makki", 30),
        SurahMeta(82, "Al-Infidaar", "الانْفِطَار", "Dillaaca Samada", "The Cleaving", 19, "Makki", 30),
        SurahMeta(83, "Al-Mudaffifiin", "المُطَفِّفِينَ", "Kuwa Miisaanka Dhima", "The Defrauders", 36, "Makki", 30),
        SurahMeta(84, "Al-Inshiqaaq", "الانْشِقَاق", "Kala Dillaaca", "The Splitting Open", 25, "Makki", 30),
        SurahMeta(85, "Al-Buruuj", "البُرُوج", "Minaaradaha Xiddigaha", "The Mansions of the Stars", 22, "Makki", 30),
        SurahMeta(86, "At-Taariq", "الطَّارِق", "Xiddigta Habeenkii Soo Baxda", "The Morning Star", 17, "Makki", 30),
        SurahMeta(87, "Al-Acla", "الأَعْلَى", "Eebbaha Sarreeya", "The Most High", 19, "Makki", 30),
        SurahMeta(88, "Al-Ghaashiyah", "الغَاشِيَة", "Qiyaamada Daboolaysa", "The Overwhelming Event", 26, "Makki", 30),
        SurahMeta(89, "Al-Fajr", "الفَجْر", "Waaberiga", "The Dawn", 30, "Makki", 30),
        SurahMeta(90, "Al-Balad", "البَلَد", "Magaalada Makkah", "The City", 20, "Makki", 30),
        SurahMeta(91, "Ash-Shams", "الشَّمْس", "Qorraxda", "The Sun", 15, "Makki", 30),
        SurahMeta(92, "Al-Layl", "اللَّيْل", "Habeenka", "The Night", 21, "Makki", 30),
        SurahMeta(93, "Ad-Duxaa", "الضُّحَى", "Barqada", "The Morning Hours", 11, "Makki", 30),
        SurahMeta(94, "Ash-Sharx", "الشَّرْح", "Fasahaadinta Laabta", "The Relief", 8, "Makki", 30),
        SurahMeta(95, "At-Tiin", "التِّين", "Geedka Tiinka", "The Fig", 8, "Makki", 30),
        SurahMeta(96, "Al-Calaq", "العَلَق", "Xinjiro Dhiig ah (Iqra)", "The Clot", 19, "Makki", 30),
        SurahMeta(97, "Al-Qadr", "القَدْر", "Habeenka Qadarka", "The Power", 5, "Makki", 30),
        SurahMeta(98, "Al-Bayyinah", "البَيِّنَة", "Caddaynta Cad", "The Clear Proof", 8, "Madani", 30),
        SurahMeta(99, "Az-Zalzalah", "الزَّلْزَلَة", "Dhulgariirka", "The Earthquake", 8, "Madani", 30),
        SurahMeta(100, "Al-Caadiyaat", "العَادِيَات", "Fardaha Dheereeya", "The Courser", 11, "Makki", 30),
        SurahMeta(101, "Al-Qaacicah", "القَارِعَة", "Garaacda Qiyaamada", "The Calamity", 11, "Makki", 30),
        SurahMeta(102, "At-Takaathur", "التَّكَاثُر", "Tartan Badanida Maalka", "The Rivalry in World Increase", 8, "Makki", 30),
        SurahMeta(103, "Al-Casr", "العَصْر", "Waqtiga / Casarka", "The Declining Day", 3, "Makki", 30),
        SurahMeta(104, "Al-Humazah", "الهُمَزَة", "Xanta & Cayda", "The Traducer", 9, "Makki", 30),
        SurahMeta(105, "Al-Fiil", "الفِيل", "Maroodiga", "The Elephant", 5, "Makki", 30),
        SurahMeta(106, "Quraysh", "قُرَيْش", "Qabiilka Quraysh", "Quraysh", 4, "Makki", 30),
        SurahMeta(107, "Al-Maacuun", "المَاعُون", "Kaalmada Yaryar", "The Small Kindnesses", 7, "Makki", 30),
        SurahMeta(108, "Al-Kawthar", "الكَوْثَر", "Khayrka Badan", "The Abundance", 3, "Makki", 30),
        SurahMeta(109, "Al-Kaafiruun", "الكَافِرُون", "Gaalada", "The Disbelievers", 6, "Makki", 30),
        SurahMeta(110, "An-Nasr", "النَّصْر", "Gargaarka Alle", "The Divine Support", 3, "Madani", 30),
        SurahMeta(111, "Al-Masad", "المَسَد", "Xadhigga Timirta (Abu Lahab)", "The Palm Fiber", 5, "Makki", 30),
        SurahMeta(112, "Al-Ikhlaas", "الإِخْلَاص", "Ikhlaaska (Midnimada Alle)", "The Sincerity", 4, "Makki", 30),
        SurahMeta(113, "Al-Falaq", "الفَلَق", "Waaga Baryey", "The Daybreak", 5, "Makki", 30),
        SurahMeta(114, "An-Naas", "النَّاس", "Aadamaha / Dadka", "Mankind", 6, "Makki", 30)
    )

    // Detailed Ayahs for prominent Surahs
    val SURAH_VERSES_MAP: Map<Int, List<QuranVerse>> = mapOf(
        1 to listOf(
            QuranVerse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Magaca Eebbaha Naxariista Guud iyo Naxariista Gaaraba Naxariista.", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            QuranVerse(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Mahad oo dhan Eebaa iska leh ee ah Barbaariyaha caalamka.", "[All] praise is [due] to Allah, Lord of the worlds."),
            QuranVerse(3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ee Naxariista Guud iyo Naxariista Gaaraba Naxariista.", "The Entirely Merciful, the Especially Merciful,"),
            QuranVerse(4, "مَالِكِ يَوْمِ الدِّينِ", "Ee ah Boqorka Maalinta Abaalgudka (Aakhiro).", "Sovereign of the Day of Recompense."),
            QuranVerse(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Adiga kaliya ayaan ku caabudeynaa, Adiga kaliyana ayaan gargaar weydiisaneynaa.", "It is You we worship and You we ask for help."),
            QuranVerse(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Nagu toosi Jidka toosan.", "Guide us to the straight path -"),
            QuranVerse(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Jidkii kuwii aad u Nicmaysay, oon ahayn kuwii loo carooday iyo kuwa baadiyoobay toona.", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.")
        ),
        2 to listOf(
            QuranVerse(1, "الم", "Alif, Laam, Miim.", "Alif, Lam, Meem."),
            QuranVerse(2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِلْمُتَّقِينَ", "Kitaabkaas shaki kuma jiro, waana hanuunka kuwa Alle ka cabsada.", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -"),
            QuranVerse(3, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنْفِقُونَ", "Kuwa rumeeyey waxa maqan, salaaddana ooga, waxaan ku arzaaqnayna wax ka bixiya.", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,"),
            QuranVerse(4, "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنْزِلَ إِلَيْكَ وَمَا أُنْزِلَ مِنْ قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ", "Iyo kuwa rumeeyey waxa laguu soo dajiyey iyo wixii la soo dajiyey hortaa, Aakhirana si dhab ah u yaqiinsan.", "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith]."),
            QuranVerse(5, "أُولَٰئِكَ عَلَىٰ هُدًى مِنْ رَبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ", "Kuwaasi waxay ku sugan yihiin hanuun Rabbigood ka yimid, waana kuwa liibaamay.", "Those are upon [right] guidance from their Lord, and it is those who are the successful."),
            QuranVerse(255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Aayadda Kursiga: Alle ma jiro Ilaah kale xaq lagu caabudo Isaga mooyee, waa Noolaha Maamula wax walba. Ma qabato luulmo iyo hurdo toona...", "Ayat al-Kursi: Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep...")
        ),
        18 to listOf(
            QuranVerse(1, "الْحَمْدُ لِلَّهِ الَّذِي أَنْزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَلْ لَهُ عِوَجًا", "Mahad oo dhan Eebaa iska leh ee ku soo dajiyey addoonkiisa Kitaabka oon ka yeelin qallooc.", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance."),
            QuranVerse(2, "قَيِّمًا لِيُنْذِرَ بَأْسًا شَدِيدًا مِنْ لَدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Waa toosane si uu ugu digo ciqaab daran oo xaggiisa ka timaada, uguna bishaareeyo mu'miniinta camalka wanaagsan fala.", "[He has made it] straight, to warn of severe punishment from Him and to give good tidings to the believers."),
            QuranVerse(10, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِنْ لَدُنْكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "Markay dhallinyaradii u carareen godka oo ay yiraahdeen: Rabbiyow naga sii xaggaaga naxariis, noona hagaaji amarkayaga hanuun.", "[Mention] when the youths retreated to the cave and said: Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.")
        ),
        36 to listOf(
            QuranVerse(1, "يس", "Yaa-Siin.", "Ya-Sin."),
            QuranVerse(2, "وَالْقُرْآنِ الْحَكِيمِ", "Waxaan ku dhaartay Qur'aanka Xigmadda badan.", "By the wise Qur'an."),
            QuranVerse(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Inaad adigu ka mid tahay kuwa la soo diray (Rusuusha).", "Indeed you, [O Muhammad], are from among the messengers,"),
            QuranVerse(4, "عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ", "Oo ku taagan Jidka Toosan.", "On a straight path."),
            QuranVerse(5, "تَنْزِيلَ الْعَزِيزِ الرَّحِيمِ", "Waa soo dajinta Eebbaha Adkaada ee Naxariista.", "[This is] a revelation of the Exalted in Might, the Merciful.")
        ),
        67 to listOf(
            QuranVerse(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Waxaa weynaaday oo barakoobay Eebaha ay Boqortooyadu gacantiisa ku jirto, ee wax walbana awooda.", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
            QuranVerse(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Ka abuuray Geerida iyo Nolosha si uu idiin tijaabiyo kiinna camal fiican, waana Adkaade Dambi dhaafe ah.", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -"),
            QuranVerse(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Ka abuuray toddobada Samo oo is dul-saaran, kuma arkeysid abuurka Eebbaha Naxariista qallooc ama daldalool.", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.")
        ),
        93 to listOf(
            QuranVerse(1, "وَالضُّحَىٰ", "Waxaan ku dhaartay Barqada.", "By the morning sunlight"),
            QuranVerse(2, "وَاللَّيْلِ إِذَا سَجَىٰ", "Iyo Habeenka marka uu dego ee gudcumoobo.", "And [by] the night when it covers with darkness,"),
            QuranVerse(3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Kuma uusan tegin Rabbigaa kumana uusan niciin.", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you]."),
            QuranVerse(4, "وَلَلْآخِرَةُ خَيْرٌ لَكَ مِنَ الْأُولَىٰ", "Aakhirana baa kaaga wanaagsan adduunkan.", "And the Hereafter is better for you than the first [life]."),
            QuranVerse(5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "Waxaana ku siin doona Rabbigaa ilaa aad ka raalli noqoto.", "And your Lord is going to give you, and you will be satisfied."),
            QuranVerse(6, "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ", "Miyaanu ku helin adoo agoon ah oo aanu ku hoynin?", "Did He not find you an orphan and give [you] refuge?"),
            QuranVerse(7, "وَوَجَدَكَ ضَالًّا فَهَدَىٰ", "Miyaanu ku helin adoo doondoonaya hanuun oo aanu ku hanuunin?", "And He found you lost and guided [you],"),
            QuranVerse(8, "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ", "Miyaanu ku helin adoo caydh ah oo aanu ku deeqsiyin?", "And He found you poor and made [you] self-sufficient."),
            QuranVerse(9, "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ", "Haddaba agoonta ha dhibin hana dulmin.", "So as for the orphan, do not oppress [him]."),
            QuranVerse(10, "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ", "Kii wax weydiistana ha canaanan.", "And as for the petitioner, do not repel [him]."),
            QuranVerse(11, "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", "Nicmada Rabbigaana ka sheekee (kuna mahad naq).", "But as for the favor of your Lord, report [it].")
        ),
        94 to listOf(
            QuranVerse(1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Miyaanaan kuu waasicin laabtaada?", "Did We not expand for you, [O Muhammad], your breast?"),
            QuranVerse(2, "وَوَضَعْنَا عَنْكَ وِزْرَكَ", "Oon kaa dejin culayskaagii?", "And We removed from you your burden"),
            QuranVerse(3, "الَّذِي أَنْقَضَ ظَهْرَكَ", "Kii dhabarkaaga cusleeyey?", "Which had weighed upon your back"),
            QuranVerse(4, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "Oon kuu koryeelin magacaaga?", "And raised high for you your repute."),
            QuranVerse(5, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "Dhibka kaddib waxaa jira fudayd.", "For indeed, with hardship [will be] ease."),
            QuranVerse(6, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Dhab ahaan dhibka kaddib waxaa jira fudayd.", "Indeed, with hardship [will be] ease."),
            QuranVerse(7, "فَإِذَا فَرَغْتَ فَانْصَبْ", "Markaad cibaado ka faraxsatana mid kale u dadaal.", "So when you have finished [your duties], then stand up [for worship]."),
            QuranVerse(8, "وَإِلَىٰ رَبِّكَ فَارْغَبْ", "Xagga Rabbigaana u jeeso.", "And to your Lord direct [your] longing.")
        ),
        97 to listOf(
            QuranVerse(1, "إِنَّا أَنْزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Anagaa soo dajinnay Qur'aanka Habeenka Qadarka.", "Indeed, We sent the Qur'an down during the Night of Decree."),
            QuranVerse(2, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "Maxaana ku ogeysiiyey waxa uu yahay Habeenka Qadarku?", "And what can make you know what is the Night of Decree?"),
            QuranVerse(3, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِنْ أَلْفِ شَهْرٍ", "Habeenka Qadarku wuxuu ka khayr badan yahay kun bilood.", "The Night of Decree is better than a thousand months."),
            QuranVerse(4, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِمْ مِنْ كُلِّ أَمْرٍ", "Waxaa soo dega Malaa'igta iyo Jibriil fasaxa Rabbigood darteed amar kasta la xiriira.", "The angels and the Spirit descend therein by permission of their Lord for every matter."),
            QuranVerse(5, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Waa nabadgalyo ilaa waaga ka baryo.", "Peace it is until the emergence of dawn.")
        ),
        103 to listOf(
            QuranVerse(1, "وَالْعَصْرِ", "Waxaan ku dhaartay Waqtiga (ama Casarka).", "By time,"),
            QuranVerse(2, "إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ", "In aadamuhu uu khasaare ku sugan yahay.", "Indeed, mankind is in loss,"),
            QuranVerse(3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Kuwii rumeeyey Eebbe, wanaaggana falay mooyee, oo isku dardaarmay xaqa iskuna dardaarmay samirka.", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.")
        ),
        108 to listOf(
            QuranVerse(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Anagaa ku siinnay Khayrka badan (ama Webiga Al-Kawthar).", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
            QuranVerse(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Haddaba Rabbigaa u tuko, gowracna u allabari.", "So pray to your Lord and sacrifice [to Him alone]."),
            QuranVerse(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Qofka ku neceb ayaa ah kan dhab ahaan go'an oo aan khayr lahayn.", "Indeed, your enemy is the one cut off.")
        ),
        112 to listOf(
            QuranVerse(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Waxaad dhahdaa: Isagu Eebbe waa Mid keliya.", "Say, He is Allah, [who is] One,"),
            QuranVerse(2, "اللَّهُ الصَّمَدُ", "Eebbe waa midka loo baahan yahay, ee waxba u baahnayn.", "Allah, the Eternal Refuge."),
            QuranVerse(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Waxba ma dhalin, Isagana lama dhalin.", "He neither begets nor is born,"),
            QuranVerse(4, "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ", "Mana jiro mid la mid ah ama la mid dhigma toona.", "Nor is there to Him any equivalent.")
        ),
        113 to listOf(
            QuranVerse(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Waxaad dhahdaa: waxaan ka magan-galayaa Rabbiga waaga baryey.", "Say, I seek refuge in the Lord of daybreak"),
            QuranVerse(2, "مِنْ شَرِّ مَا خَلَقَ", "Sharkii wixii uu abuuray oo dhan.", "From the evil of that which He created"),
            QuranVerse(3, "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Iyo sharka habeenka marka uu gudcumoobo.", "And from the evil of darkness when it settles"),
            QuranVerse(4, "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Iyo sharka kuwa candhuufta ku afuufa guntimaha (sixiroolayaasha).", "And from the evil of the blowers in knots"),
            QuranVerse(5, "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Iyo sharka qofka xaasidka ah marka uu wax xaasido.", "And from the evil of an envier when he envies.")
        ),
        114 to listOf(
            QuranVerse(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Waxaad dhahdaa: waxaan ka magan-galayaa Rabbiga dadka.", "Say, I seek refuge in the Lord of mankind,"),
            QuranVerse(2, "مَلِكِ النَّاسِ", "Boqorka dadka.", "The Sovereign of mankind."),
            QuranVerse(3, "إِلَٰهِ النَّاسِ", "Ilaaha dadka.", "The God of mankind,"),
            QuranVerse(4, "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Sharka waswaasiyaha dhuumaaleysiga badan.", "From the evil of the retreating whisperer -"),
            QuranVerse(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Ee ku waswaasiya laabaha dadka.", "Who whispers into the breasts of mankind -"),
            QuranVerse(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Kuna jira jinniga iyo dadkaba.", "From among the jinn and mankind.")
        )
    )

    fun getSurahMeta(number: Int): SurahMeta {
        return ALL_SURAHS.find { it.number == number } ?: ALL_SURAHS.first()
    }

    fun getSurahDetail(number: Int, reciterId: String = "afs"): SurahDetail {
        val meta = getSurahMeta(number)
        val verses = SURAH_VERSES_MAP[number] ?: listOf(
            QuranVerse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Magaca Eebbaha Naxariista Guud iyo Naxariista Gaaraba Naxariista.", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            QuranVerse(2, "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ", "Wax ku akhri Magaca Rabbigaa ee uumay.", "Recite in the name of your Lord who created -"),
            QuranVerse(3, "خَلَقَ الْإِنْسَانَ مِنْ عَلَقٍ", "Dadkana ka abuuray xinjir.", "Created man from a clinging substance."),
            QuranVerse(4, "اقْرَأْ وَرَبُّكَ الْأَكْرَمُ", "Akhri, Rabbigaana waa kan ugu Sharafta badan.", "Recite, and your Lord is the most Generous -"),
            QuranVerse(5, "الَّذِي عَلَّمَ بِالْقَلَمِ", "Ee wax ku baray qalinka.", "Who taught by the pen -"),
            QuranVerse(6, "عَلَّمَ الْإِنْسَانَ مَا لَمْ يَعْلَمْ", "Barayna aadamaha wax uusan aqoonin.", "Taught man that which he knew not.")
        )

        return SurahDetail(
            number = meta.number,
            name = meta.name,
            arabicName = meta.arabicName,
            meaning = meta.somaliMeaning,
            totalAyahs = meta.totalAyahs,
            revelationType = meta.revelationType,
            audioUrl = getAudioUrlForSurah(number, reciterId),
            verses = verses
        )
    }

    fun searchSurahs(query: String): List<SurahMeta> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return ALL_SURAHS
        return ALL_SURAHS.filter {
            it.name.lowercase().contains(q) ||
            it.arabicName.contains(q) ||
            it.somaliMeaning.lowercase().contains(q) ||
            it.englishName.lowercase().contains(q) ||
            it.number.toString() == q
        }
    }
}
