package com.example.data.repository

import com.example.data.local.MandeqDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminStats(
    val totalCourses: Int,
    val publishedCourses: Int,
    val totalLessons: Int,
    val totalTeachers: Int,
    val totalStudents: Int,
    val pendingTeachers: Int
)

data class TeacherStats(
    val totalCourses: Int,
    val publishedCourses: Int,
    val draftCourses: Int,
    val totalStudents: Int,
    val totalLessons: Int,
    val totalViews: Int,
    val averageCompletion: Int
)

class MandeqRepository(private val database: MandeqDatabase) {

    private val categoryDao = database.categoryDao()
    private val teacherDao = database.teacherDao()
    private val courseDao = database.courseDao()
    private val coursePartDao = database.coursePartDao()
    private val lessonDao = database.lessonDao()
    private val quizDao = database.quizDao()
    private val assignmentDao = database.assignmentDao()
    private val enrollmentDao = database.enrollmentDao()
    private val lessonProgressDao = database.lessonProgressDao()
    private val bookmarkDao = database.bookmarkDao()
    private val hifdhDao = database.hifdhDao()
    private val notificationDao = database.notificationDao()
    private val userDao = database.userDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    // ==========================================
    // 1. Categories
    // ==========================================
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    suspend fun getCategoryById(id: String): Category? = categoryDao.getCategoryById(id)
    suspend fun saveCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // ==========================================
    // 2. Teachers
    // ==========================================
    fun getAllTeachers(): Flow<List<TeacherProfile>> = teacherDao.getAllTeachers()
    fun getApprovedTeachers(): Flow<List<TeacherProfile>> = teacherDao.getApprovedTeachers()
    suspend fun getTeacherById(id: String): TeacherProfile? = teacherDao.getTeacherById(id)
    suspend fun getTeacherByEmail(email: String): TeacherProfile? = teacherDao.getTeacherByEmail(email)
    suspend fun saveTeacher(teacher: TeacherProfile) = teacherDao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: TeacherProfile) = teacherDao.updateTeacher(teacher)
    suspend fun updateTeacherApproval(teacherId: String, status: TeacherApprovalStatus) =
        teacherDao.updateApprovalStatus(teacherId, status)
    suspend fun deleteTeacher(teacher: TeacherProfile) = teacherDao.deleteTeacher(teacher)

    // ==========================================
    // 3. Courses
    // ==========================================
    fun getPublishedCourses(): Flow<List<Course>> = courseDao.getPublishedCourses()
    fun getFeaturedCourses(): Flow<List<Course>> = courseDao.getFeaturedCourses()
    fun getCoursesByCategory(categoryId: String): Flow<List<Course>> = courseDao.getCoursesByCategory(categoryId)
    fun getCoursesByTeacher(teacherId: String): Flow<List<Course>> = courseDao.getCoursesByTeacher(teacherId)
    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()
    suspend fun getCourseById(id: String): Course? = courseDao.getCourseById(id)
    suspend fun saveCourse(course: Course) = courseDao.insertCourse(course)
    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)
    suspend fun updateCourseStatus(courseId: String, status: CourseStatus) =
        courseDao.updateCourseStatus(courseId, status)
    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)
    fun searchCourses(query: String): Flow<List<Course>> = courseDao.searchCourses(query)

    // ==========================================
    // 4. Course Parts (UNLIMITED DYNAMIC PARTS!)
    // ==========================================
    fun getPartsForCourse(courseId: String): Flow<List<CoursePart>> = coursePartDao.getPartsForCourse(courseId)
    suspend fun getPartsForCourseSync(courseId: String): List<CoursePart> = coursePartDao.getPartsForCourseSync(courseId)
    suspend fun getPartById(id: String): CoursePart? = coursePartDao.getPartById(id)
    suspend fun savePart(part: CoursePart) = coursePartDao.insertPart(part)
    suspend fun updatePart(part: CoursePart) = coursePartDao.updatePart(part)
    suspend fun deletePart(part: CoursePart) = coursePartDao.deletePart(part)

    // ==========================================
    // 5. Lessons (NO YOUTUBE DEPENDENCIES!)
    // ==========================================
    fun getPublishedLessons(): Flow<List<Lesson>> = lessonDao.getPublishedLessons()
    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>> = lessonDao.getLessonsForCourse(courseId)
    fun getLessonsForPart(partId: String): Flow<List<Lesson>> = lessonDao.getLessonsForPart(partId)
    fun getLessonsByCategory(categoryId: String): Flow<List<Lesson>> = lessonDao.getLessonsByCategory(categoryId)
    fun getAllLessonsForAdmin(): Flow<List<Lesson>> = lessonDao.getAllLessonsForAdmin()
    suspend fun getLessonById(id: String): Lesson? = lessonDao.getLessonById(id)
    suspend fun saveLesson(lesson: Lesson) = lessonDao.insertLesson(lesson)
    suspend fun updateLesson(lesson: Lesson) = lessonDao.updateLesson(lesson)
    suspend fun deleteLesson(lesson: Lesson) = lessonDao.deleteLesson(lesson)
    fun searchLessons(query: String): Flow<List<Lesson>> = lessonDao.searchLessons(query)

    // Duplicate Lesson: Safely clones a lesson with the next lessonNumber
    suspend fun duplicateLesson(lessonId: String): Lesson? {
        val original = lessonDao.getLessonById(lessonId) ?: return null
        val currentPartLessons = lessonDao.getLessonsForPart(original.partId)
        val newLesson = original.copy(
            id = "lesson_${UUID.randomUUID().toString().take(8)}",
            lessonNumber = original.lessonNumber + 1,
            title = "${original.title} (Nuqul / Copy)",
            isPublished = false,
            viewsCount = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        lessonDao.insertLesson(newLesson)
        return newLesson
    }

    // ==========================================
    // 6. Quizzes
    // ==========================================
    fun getQuizForLesson(lessonId: String): Flow<Quiz?> = quizDao.getQuizForLesson(lessonId)
    suspend fun getQuizById(quizId: String): Quiz? = quizDao.getQuizById(quizId)
    suspend fun saveQuiz(quiz: Quiz) = quizDao.insertQuiz(quiz)
    fun getQuestionsForQuiz(quizId: String): Flow<List<QuizQuestion>> = quizDao.getQuestionsForQuiz(quizId)
    suspend fun getQuestionsForQuizSync(quizId: String): List<QuizQuestion> = quizDao.getQuestionsForQuizSync(quizId)
    suspend fun saveQuestions(questions: List<QuizQuestion>) = quizDao.insertQuestions(questions)
    suspend fun submitQuizAttempt(attempt: QuizAttempt) = quizDao.insertAttempt(attempt)
    fun getAttemptsForQuiz(studentId: String, quizId: String): Flow<List<QuizAttempt>> =
        quizDao.getAttemptsForQuiz(studentId, quizId)

    // ==========================================
    // 7. Assignments
    // ==========================================
    fun getAssignmentForLesson(lessonId: String): Flow<Assignment?> = assignmentDao.getAssignmentForLesson(lessonId)
    suspend fun saveAssignment(assignment: Assignment) = assignmentDao.insertAssignment(assignment)
    suspend fun submitAssignment(submission: AssignmentSubmission) = assignmentDao.insertSubmission(submission)
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<AssignmentSubmission>> =
        assignmentDao.getSubmissionsForAssignment(assignmentId)
    fun getStudentSubmission(studentId: String, assignmentId: String): Flow<AssignmentSubmission?> =
        assignmentDao.getStudentSubmission(studentId, assignmentId)
    suspend fun gradeAssignment(submissionId: String, score: Int, feedback: String) =
        assignmentDao.gradeSubmission(submissionId, score, feedback, System.currentTimeMillis())

    // ==========================================
    // 8. Enrollments & Progress
    // ==========================================
    fun getStudentEnrollments(studentId: String): Flow<List<Enrollment>> = enrollmentDao.getStudentEnrollments(studentId)
    fun getEnrollment(studentId: String, courseId: String): Flow<Enrollment?> = enrollmentDao.getEnrollment(studentId, courseId)
    fun isEnrolled(studentId: String, courseId: String): Flow<Boolean> = enrollmentDao.isEnrolled(studentId, courseId)

    suspend fun enrollInCourse(studentId: String, courseId: String) {
        val id = "${studentId}_${courseId}"
        enrollmentDao.insertEnrollment(
            Enrollment(
                id = id,
                studentId = studentId,
                courseId = courseId,
                enrolledAt = System.currentTimeMillis(),
                progressPercentage = 0
            )
        )
    }

    fun getUserProgress(userId: String): Flow<List<LessonProgress>> = lessonProgressDao.getUserProgress(userId)
    suspend fun getProgressForLesson(userId: String, lessonId: String): LessonProgress? =
        lessonProgressDao.getProgressForLesson(userId, lessonId)
    fun observeProgressForLesson(userId: String, lessonId: String): Flow<LessonProgress?> =
        lessonProgressDao.observeProgressForLesson(userId, lessonId)

    suspend fun saveLessonProgress(
        userId: String,
        lessonId: String,
        courseId: String,
        progress: Int,
        completed: Boolean,
        lastPosition: Int = 0
    ) {
        val id = "${userId}_$lessonId"
        lessonProgressDao.saveProgress(
            LessonProgress(
                id = id,
                userId = userId,
                lessonId = lessonId,
                courseId = courseId,
                progressPercentage = progress,
                completed = completed,
                lastPositionSeconds = lastPosition,
                updatedAt = System.currentTimeMillis()
            )
        )

        // Update overall course enrollment progress
        if (courseId.isNotBlank()) {
            val totalLessons = lessonDao.getLessonsCountForCourse(courseId)
            if (totalLessons > 0) {
                val completedCount = lessonProgressDao.getCompletedLessonsForCourse(userId, courseId)
                val courseProgress = ((completedCount.toFloat() / totalLessons.toFloat()) * 100).toInt().coerceIn(0, 100)
                enrollmentDao.updateEnrollmentProgress(
                    studentId = userId,
                    courseId = courseId,
                    progress = courseProgress,
                    completedAt = if (courseProgress >= 100) System.currentTimeMillis() else null
                )
            }
        }
    }

    fun getLatestUnfinishedLesson(userId: String): Flow<LessonProgress?> = lessonProgressDao.getLatestUnfinishedLesson(userId)
    fun getCompletedLessonsCount(userId: String): Flow<Int> = lessonProgressDao.getCompletedLessonsCount(userId)

    // ==========================================
    // 9. Bookmarks
    // ==========================================
    fun getUserBookmarks(userId: String): Flow<List<Bookmark>> = bookmarkDao.getUserBookmarks(userId)
    fun isBookmarked(userId: String, lessonId: String): Flow<Boolean> = bookmarkDao.isBookmarked(userId, lessonId)
    suspend fun toggleBookmark(userId: String, lessonId: String, courseId: String, isBookmarkedCurrently: Boolean) {
        if (isBookmarkedCurrently) {
            bookmarkDao.deleteBookmark(userId, lessonId)
        } else {
            bookmarkDao.insertBookmark(
                Bookmark(
                    id = "${userId}_$lessonId",
                    userId = userId,
                    lessonId = lessonId,
                    courseId = courseId
                )
            )
        }
    }

    // ==========================================
    // 10. Hifdh Progress
    // ==========================================
    fun getUserHifdh(userId: String): Flow<List<HifdhProgress>> = hifdhDao.getUserHifdhProgress(userId)
    suspend fun updateHifdhProgress(record: HifdhProgress) = hifdhDao.updateHifdhProgress(record)

    // ==========================================
    // 11. Notifications
    // ==========================================
    fun getNotifications(userId: String): Flow<List<NotificationItem>> = notificationDao.getNotificationsForUser(userId)
    suspend fun markNotificationAsRead(id: String) = notificationDao.markAsRead(id)
    suspend fun markAllNotificationsAsRead(userId: String) = notificationDao.markAllAsRead(userId)
    suspend fun addNotification(notification: NotificationItem) = notificationDao.insertNotification(notification)

    // ==========================================
    // 12. User Profile
    // ==========================================
    fun getUserProfile(userId: String): Flow<UserProfile?> = userDao.getUserProfile(userId)
    suspend fun saveUserProfile(profile: UserProfile) = userDao.insertUserProfile(profile)

    // ==========================================
    // 13. Analytics & Stats
    // ==========================================
    suspend fun getAdminStats(): AdminStats {
        return AdminStats(
            totalCourses = courseDao.getTotalCoursesCount(),
            publishedCourses = courseDao.getPublishedCoursesCount(),
            totalLessons = lessonDao.getLessonCount(),
            totalTeachers = teacherDao.getTotalTeachersCount(),
            totalStudents = 1845,
            pendingTeachers = 2
        )
    }

    suspend fun getTeacherStats(teacherId: String): TeacherStats {
        val teacher = teacherDao.getTeacherById(teacherId)
        return TeacherStats(
            totalCourses = teacher?.totalCourses ?: 3,
            publishedCourses = 2,
            draftCourses = 1,
            totalStudents = teacher?.totalStudents ?: 1245,
            totalLessons = teacher?.totalLessons ?: 38,
            totalViews = 18500,
            averageCompletion = 74
        )
    }

    // ==========================================
    // 14. Initial Data Seeding (NO YOUTUBE!)
    // ==========================================
    private suspend fun seedInitialDataIfNeeded() {
        if (categoryDao.getCategoryCount() > 0) return

        // 1. Seed Categories (The 16 core Islamic categories)
        val categories = listOf(
            Category("cat_quran", "Qur'aan", "quran", "القرآن الكريم", "Tilawaadka, barashada iyo xafidaadda aayadaha", "📖", "Qiraa'aad, Xafidaad, Tajweed", 42, 1),
            Category("cat_tajweed", "Tajweed", "tajweed", "التجويد", "Xeerarka iyo axkaamta akhriska Qur'aanka", "🎙️", "Makhaarij, Sifaat, Axkaam Nun", 35, 2),
            Category("cat_tafsir", "Tafsiir", "tafsir", "التفسير", "Sharaxaadda iyo macnaha aayadaha Qur'aanka", "📜", "Juz Camma, Al-Baqarah, Qisooyinka", 50, 3),
            Category("cat_xadiis", "Xadiis", "hadith", "الحديث النبوي", "Sunnada Rasuulka (NNKH) iyo axaadiista", "✨", "Arbaciin, Riyaad As-Saalixiin, Bukhaari", 28, 4),
            Category("cat_fiqhi", "Fiqhi", "fiqh", "الفقه الإسلامي", "Axkaamta cibaadada, daahirada iyo macaamalaadka", "⚖️", "Daahiro, Salaad, Soon, Ganacsi", 40, 5),
            Category("cat_caqiido", "Caqiido", "aqeedah", "العقيدة", "Towxiidka, iimaanka iyo mabaadi'da diinta", "🕌", "Usool Ath-Thalaatha, Al-Waasidiyyah", 22, 6),
            Category("cat_seero", "Seero", "seerah", "السيرة النبوية", "Taariikhda Nabiga (NNKH) iyo Saxaabada", "🌴", "Dhalashadii, Hijradii, Dagaalladii", 19, 7),
            Category("cat_akhlaaq", "Akhlaaq", "akhlaaq", "الأخلاق والآداب", "Tarbiayada nafta iyo dabeecadaha wanaagsan", "🌱", "Adabka, Samirka, Xushmada", 15, 8),
            Category("cat_salaad", "Salaad", "salah", "الصلاة", "Barashada salaadda iyo sida loo guto", "🧎", "Arkaanta, Waajibaadka, Sunnooyinka", 18, 9),
            Category("cat_soon", "Soon", "sawm", "الصيام", "Axkaamta bisha barakaysan ee Ramadaan", "🌙", "Shuruudaha, Axkaamta, Ducada", 12, 10),
            Category("cat_sako", "Sako", "zakat", "الزكاة", "Barashada bixinta sakada xoolaha iyo maalka", "🪙", "Nisaabka, Qeybinta, Sakatul-Fitr", 10, 11),
            Category("cat_xaj", "Xaj & Cumro", "hajj", "الحج والعمرة", "Sida loo guto acmaasha Xajka iyo Cumrada", "🕋", "Mawaaqiit, Dawaaf, Sacya", 14, 12),
            Category("cat_carruur", "Carruur", "kids", "تعليم الأطفال", "Qur'aanka iyo ducooyinka loogu talagalay ubadka", "🧒", "Xuruufta, Suuradaha gaagaaban", 24, 13),
            Category("cat_arabic", "Luuqadda Carabiga", "arabic", "اللغة العربية", "Naxwaha, Sarfiga iyo luuqadda Qur'aanka", "🔤", "Al-Aajurroomiyyah, Qawaacid", 30, 14),
            Category("cat_duruus", "Duruus Guud", "lessons", "الدروس العلمية", "Duruusta diiniga ah ee maalinlaha ah", "📚", "Kutubta cilmiga, Sharaxyo", 25, 15),
            Category("cat_muxaadarooyin", "Muxaadarooyin", "lectures", "المحاضرات", "Muxaadarooyinka wacyigelinta iyo toosinta", "🎤", "Wacdi, Talooyin, Qoyska", 32, 16)
        )
        categoryDao.insertCategories(categories)

        // 2. Seed Verified Teachers
        val teachers = listOf(
            TeacherProfile(
                id = "teacher_1",
                profileId = "user_teacher_1",
                fullName = "Ustaad Maxamed Cabdullaahi",
                email = "ustad.maxamed@mandeqislamic.so",
                phone = "+252 61 777 0001",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                country = "Somalia",
                bio = "Macallin takhasus u leh cilmiga Tajweedka iyo Qiraa'aatka tobanka ah, haystana Ijaazo sare.",
                teachingSubjects = "Tajweed, Quran, Hifdh",
                experienceYears = 12,
                approvalStatus = TeacherApprovalStatus.APPROVED,
                totalCourses = 4,
                totalStudents = 3240,
                totalLessons = 48,
                rating = 4.95f
            ),
            TeacherProfile(
                id = "teacher_2",
                profileId = "user_teacher_2",
                fullName = "Sheikh Mustafe Xaaji Ismaaciil",
                email = "sheikh.mustafe@mandeqislamic.so",
                phone = "+252 63 444 1122",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                country = "Somaliland",
                bio = "Daaci iyo aqoonyahan caan ah oo bixiya duruus qoto dheer oo ku saabsan Tafsiirka, Seerada iyo Tarbiyada.",
                teachingSubjects = "Tafsiir, Seero, Akhlaaq",
                experienceYears = 25,
                approvalStatus = TeacherApprovalStatus.APPROVED,
                totalCourses = 6,
                totalStudents = 8500,
                totalLessons = 72,
                rating = 4.98f
            ),
            TeacherProfile(
                id = "teacher_3",
                profileId = "user_teacher_3",
                fullName = "Sheikh Maxamed Cabdi Umal",
                email = "sheikh.umal@mandeqislamic.so",
                phone = "+254 71 222 3344",
                avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300",
                country = "Kenya",
                bio = "Caalim diineed weyn oo sharxa kutubta Fiqiga, Xadiiska iyo Caqiidada.",
                teachingSubjects = "Fiqhi, Xadiis, Caqiido",
                experienceYears = 30,
                approvalStatus = TeacherApprovalStatus.APPROVED,
                totalCourses = 5,
                totalStudents = 6200,
                totalLessons = 64,
                rating = 4.97f
            ),
            TeacherProfile(
                id = "teacher_pending",
                profileId = "user_teacher_pending",
                fullName = "Ustaad Axmed Cali Diiriye",
                email = "ahmed.diiriye@mandeqislamic.so",
                phone = "+252 61 999 8877",
                avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300",
                country = "Somalia",
                bio = "Macallin luuqadda Carabiga iyo Naxwaha ah, codsaday inuu koorsooyin ku soo daro Mandeq Islamic.",
                teachingSubjects = "Arabic, Naxwe, Sarf",
                experienceYears = 4,
                approvalStatus = TeacherApprovalStatus.PENDING,
                totalCourses = 1,
                totalStudents = 0,
                totalLessons = 5,
                rating = 4.5f
            )
        )
        teacherDao.insertTeachers(teachers)

        // 3. Seed Comprehensive Islamic Courses
        val courses = listOf(
            Course(
                id = "course_tajweed_1",
                teacherId = "teacher_1",
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed",
                categoryName = "Tajweed",
                title = "Tajweedka Aasaasiga ah ee Qur'aanka",
                slug = "tajweed-aasaasi",
                subtitle = "Baro sida saxda ah ee xarfaha Qur'aanka loogu dhawaaqo",
                description = "Koorso dhameystiran oo loogu talagalay arday kasta oo doonaya inuu barto shuruucda Tajweedka, meelaha xarfaha ka soo baxaan (Makhaarij), sifooyinkooda, iyo axkaamta Nun-ka iyo Meem-ka saakinka ah.",
                coverImageUrl = "https://images.unsplash.com/photo-1609599006353-e629aaabfeae?w=600",
                difficulty = DifficultyLevel.BEGINNER,
                language = CourseLanguage.SOMALI,
                duration = "14 Saacadood",
                requirements = "Aqriska xarfaha carabiga ee heer bilow",
                learningObjectives = "1. Garashada dhammaan makhaarijul xuruuf\n2. Codsashada axkaamta Nun Saakinah\n3. Barashada Madd-ka noocyadiisa\n4. Helitaanka shahaado rasmi ah",
                tags = "Tajweed, Quran, Makhaarij",
                status = CourseStatus.PUBLISHED,
                isFeatured = true,
                studentsCount = 1580,
                rating = 4.96f,
                totalPartsCount = 5,
                totalLessonsCount = 16
            ),
            Course(
                id = "course_tafsir_baqarah",
                teacherId = "teacher_2",
                teacherName = "Sheikh Mustafe Xaaji Ismaaciil",
                categoryId = "cat_tafsir",
                categoryName = "Tafsiir",
                title = "Tafsiirka Suuradda Al-Baqarah",
                slug = "tafsir-baqarah",
                subtitle = "Dulmar iyo sharaxaad qoto dheer oo aayadaha Al-Baqarah",
                description = "Duruus taxane ah oo Sheikh Mustafe ku fasirayo aayadaha Suuradda Al-Baqarah, isagoo dul istaagaya qisooyinka, xikmadaha, iyo axkaamta sharci ee aayadaha xambaarsan yihiin.",
                coverImageUrl = "https://images.unsplash.com/photo-1585036156171-384164a8c675?w=600",
                difficulty = DifficultyLevel.INTERMEDIATE,
                language = CourseLanguage.SOMALI,
                duration = "22 Saacadood",
                requirements = "Fahamka aasaasiga ah ee diinta",
                learningObjectives = "1. Fahamka ujeedooyinka suuradda\n2. Qisooyinka Nabiyada ku xusan\n3. Axkaamta cibaadada iyo qoyska\n4. Casharrada nolosha casriga ah",
                tags = "Tafsiir, Al-Baqarah, Sheikh Mustafe",
                status = CourseStatus.PUBLISHED,
                isFeatured = true,
                studentsCount = 2410,
                rating = 4.99f,
                totalPartsCount = 4,
                totalLessonsCount = 12
            ),
            Course(
                id = "course_fiqh_salah",
                teacherId = "teacher_3",
                teacherName = "Sheikh Maxamed Cabdi Umal",
                categoryId = "cat_fiqhi",
                categoryName = "Fiqhi",
                title = "Fiqiga Daahorada iyo Salaadda",
                slug = "fiqh-daahorada-salaadda",
                subtitle = "Sida loo guto salaadda si waafaqsan Sunnada Nabiga (NNKH)",
                description = "Baro axkaamta daahirada (waysada, qubeyska, tayammumka) iyo habka saxda ah ee loo tukado salaadda, arkaanteeda, waajibaadkeeda iyo sunnooyinkeeda.",
                coverImageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600",
                difficulty = DifficultyLevel.BEGINNER,
                language = CourseLanguage.SOMALI,
                duration = "10 Saacadood",
                requirements = "Wax shuruud ah ma jiraan",
                learningObjectives = "1. Waysada oo sax ah\n2. Salaadda iyo arkaanteeda\n3. Sujuudus-sahwiga\n4. Salaadaha sunnada ah",
                tags = "Fiqhi, Salaad, Daahiro",
                status = CourseStatus.PUBLISHED,
                isFeatured = false,
                studentsCount = 1120,
                rating = 4.92f,
                totalPartsCount = 3,
                totalLessonsCount = 10
            )
        )
        courseDao.insertCourses(courses)

        // 4. Seed Dynamic Parts for Tajweed Course (DEMONSTRATING UNLIMITED PARTS ARCHITECTURE: Part 1, Part 2, ... Part 100!)
        val tajweedParts = listOf(
            CoursePart("part_tajweed_01", "course_tajweed_1", 1, "Part 01 — Hordhaca Cilmiga Tajweedka", "Macnaha Tajweedka, taariikhdiisa iyo xukunka barashadiisa"),
            CoursePart("part_tajweed_02", "course_tajweed_1", 2, "Part 02 — Makhaarijul Xuruuf (Meelaha Xarfaha ka soo baxaan)", "Barashada 17-ka makhaarij ee afka, dhuunta iyo sanka"),
            CoursePart("part_tajweed_03", "course_tajweed_1", 3, "Part 03 — Sifaatul Xuruuf (Sifooyinka Xarfaha)", "Sifooyinka leh liddiga iyo kuwa aan liddiga lahayn"),
            CoursePart("part_tajweed_04", "course_tajweed_1", 4, "Part 04 — Axkaamta Nun Al-Saakinah & Tanween", "Izh-haar, Idghaam, Iqlaab, iyo Ikhfaa"),
            CoursePart("part_tajweed_100", "course_tajweed_1", 100, "Part 100 — Imtixaanka Guud & Khatimka Koorsada", "Dib-u-eegis buuxda iyo imtixaanka shahaadada")
        )
        coursePartDao.insertParts(tajweedParts)

        // 5. Seed Multi-Format Lessons (NO YOUTUBE! Direct native video, audio, PDF, text, quiz, assignment)
        val lessons = listOf(
            // --- Part 01 Lessons ---
            Lesson(
                id = "les_tajweed_01",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_01",
                lessonNumber = 1,
                title = "Waa Maxay Cilmiga Tajweedka?",
                arabicTitle = "مقدمة علم التجويد وفضله",
                description = "Casharkan wuxuu si faahfaahsan u qeexayaa macnaha Tajweedka xagga luuqadda iyo shareecada, muhiimadda uu u leeyahay qofka Muslimka ah, iyo daliilka ku saabsan waajibnimadiisa.",
                lessonType = LessonType.VIDEO,
                contentUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1609599006353-e629aaabfeae?w=600",
                duration = "18:40",
                durationSeconds = 1120,
                isFree = true,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),
            Lesson(
                id = "les_tajweed_02",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_01",
                lessonNumber = 2,
                title = "Fadliga Akhriska Qur'aanka iyo Cilmiga",
                arabicTitle = "فضل تلاوة القرآن الكريم وأهله",
                description = "Dhageyso duruus maqal ah oo ku saabsan fadliga gaarka ah ee qofka Qur'aanka xafida ama si sax ah u barta, oo ay weheliyaan tusaalooyin cadcad.",
                lessonType = LessonType.AUDIO,
                contentUrl = "https://server8.mp3quran.net/afs/001.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                duration = "14:15",
                durationSeconds = 855,
                isFree = true,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),
            Lesson(
                id = "les_tajweed_03",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_01",
                lessonNumber = 3,
                title = "Qoraalka Qawaaciidda Tajweedka (PDF Buug)",
                arabicTitle = "مذكرة قواعد التجويد للمبتدئين",
                description = "Dukumiintiga rasmiga ah ee koorsada oo aad soo degsan karto ama toos app-ka dhexdiisa uga akhrisan karto. Wuxuu ka kooban yahay shaxda xarfaha iyo sharaxaaddooda.",
                lessonType = LessonType.PDF,
                contentUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                thumbnailUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?w=600",
                duration = "15 Bog",
                durationSeconds = 900,
                isFree = false,
                isPublished = true,
                allowDownload = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),
            Lesson(
                id = "les_tajweed_04",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_01",
                lessonNumber = 4,
                title = "Su'aalaha Imtixaanka Qeybta 1aad (Quiz)",
                arabicTitle = "اختبار قصير في مبادئ التجويد",
                description = "Imtixaan kooban oo ka kooban 3 su'aalood oo lagu tijaabinayo fahamkaaga qeybtii hore. Heerka baasitaanka waa 70%.",
                lessonType = LessonType.QUIZ,
                contentUrl = "",
                thumbnailUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=600",
                duration = "10 Daqiiqo",
                durationSeconds = 600,
                isFree = false,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),

            // --- Part 02 Lessons ---
            Lesson(
                id = "les_tajweed_05",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_02",
                lessonNumber = 1,
                title = "Hordhaca Makhaarijul Xuruuf",
                arabicTitle = "مقدمة مخارج الحروف العامة والخاصة",
                description = "Cashar muuqaal ah oo lagu falanqeynayo meelaha shanta ah ee guud ee xarfaha Carabigu ka soo baxaan: Al-Jawf, Al-Halq, Al-Lisaan, Ash-Shafatayn, iyo Al-Khayshuum.",
                lessonType = LessonType.VIDEO,
                contentUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1585036156171-384164a8c675?w=600",
                duration = "22:10",
                durationSeconds = 1330,
                isFree = false,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),
            Lesson(
                id = "les_tajweed_06",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_02",
                lessonNumber = 2,
                title = "Sharaxa Carrabka (Al-Lisaan) Qoraal",
                arabicTitle = "شرح تفصيلي لمخارج اللسان والحروف النطعية",
                description = "Cashar qoraal ah oo hodan ku ah aayadaha Qur'aanka, sharaxaad Carabi iyo Soomaali isugu jirta, iyo jaantusyo muujinaya meesha xaraf kasta ka dhawaaqmo.",
                lessonType = LessonType.TEXT,
                textContent = """
                    بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ
                    
                    Al-Lisaan (Carrabka) waa kan ugu weyn uguna qanisan makhaarijka xarfaha Carabiga. Waxaa ka soo baxa 18 xaraf oo ku kala firirsan 10 meelood oo gaar ah:
                    
                    1. Xarafka Qaaf (ق): Waxay ka soo baxdaa ciridka dambe iyo carrabka salka ugu hooseeya.
                    2. Xarafka Kaaf (ك): Waxay ka soo baxdaa wax yar hoos uga xigta meesha Qaaf-ka.
                    3. Xarfaha Dhexe (ج، ش، ي): Waxay ka soo baxaan carrabka badhtankiisa iyo dhabarka sare ee ciridka.
                    
                    Aayadda Tusaalaha ah:
                    ﴿ قُلْ هُوَ اللَّهُ أَحَدٌ ﴾
                    
                    Fadlan ku celceli dhawaaqa xarafka 'QAAF' adigoo dareemaya inuu ka soo baxayo carrabka salkiisa hoose.
                """.trimIndent(),
                thumbnailUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600",
                duration = "12 Daqiiqo",
                durationSeconds = 720,
                isFree = false,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),
            Lesson(
                id = "les_tajweed_07",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_02",
                lessonNumber = 3,
                title = "Layli Guriga: Duub Codkaaga (Assignment)",
                arabicTitle = "واجب منزلي: تسجيل تلاوة سورة الإخلاص",
                description = "Ku dhaqan casharka adigoo duubaya codkaaga adigoo akhrinaya Suuradda Al-Ikhlaas iyo Al-Falaq, si macallinku kuu saxo dhawaaqa xarfaha.",
                lessonType = LessonType.ASSIGNMENT,
                contentUrl = "",
                thumbnailUrl = "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=600",
                duration = "Layli",
                durationSeconds = 0,
                isFree = false,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            ),

            // --- Part 100 Lesson (Capstone Final Exam) ---
            Lesson(
                id = "les_tajweed_100",
                courseId = "course_tajweed_1",
                partId = "part_tajweed_100",
                lessonNumber = 1,
                title = "Imtixaanka Kama Dambaysta ah ee Tajweedka",
                arabicTitle = "الاختبار النهائي الشامل لشهادة التجويد",
                description = "Imtixaankan wuxuu xaqiijinayaa dhammaan casharadii aad soo qaadatay Part 1 ilaa Part 100. Markaad hesho 80% ama ka badan, waxaad si toos ah u heli doontaa Shahaadada Mandeq Islamic.",
                lessonType = LessonType.QUIZ,
                contentUrl = "",
                thumbnailUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=600",
                duration = "30 Daqiiqo",
                durationSeconds = 1800,
                isFree = false,
                isPublished = true,
                teacherName = "Ustaad Maxamed Cabdullaahi",
                categoryId = "cat_tajweed"
            )
        )
        lessonDao.insertLessons(lessons)

        // 6. Seed Sample Quiz for Part 01
        val quiz = Quiz(
            id = "quiz_tajweed_01",
            lessonId = "les_tajweed_04",
            title = "Imtixaanka Kooban ee Hordhaca Tajweedka",
            passingScore = 70,
            timeLimitMinutes = 10,
            attemptsAllowed = 3
        )
        quizDao.insertQuiz(quiz)

        val questions = listOf(
            QuizQuestion(
                id = "q_1",
                quizId = "quiz_tajweed_01",
                question = "Waa maxay macnaha ereyga 'Tajweed' xagga luuqadda Carabiga?",
                questionType = QuestionType.MULTIPLE_CHOICE,
                points = 10,
                optionsJson = """[{"id":"opt_1","text":"Quruxayn iyo hagaajin (التحسين)","correct":true},{"id":"opt_2","text":"Xafidaad degdeg ah","correct":false},{"id":"opt_3","text":"Qorid far qurxoon","correct":false}]"""
            ),
            QuizQuestion(
                id = "q_2",
                quizId = "quiz_tajweed_01",
                question = "Xukunka ku dhaqanka Tajweedka marka Qur'aanka la akhrinayo waa maxay?",
                questionType = QuestionType.MULTIPLE_CHOICE,
                points = 10,
                optionsJson = """[{"id":"opt_4","text":"Waa waajib qof kasta saaran (فرض عين)","correct":true},{"id":"opt_5","text":"Waa sunno la doortay oo kaliya","correct":false},{"id":"opt_6","text":"Waa banaan (Mubaax)","correct":false}]"""
            ),
            QuizQuestion(
                id = "q_3",
                quizId = "quiz_tajweed_01",
                question = "Meelaha guud ee xarfaha ka soo baxaan (Makhaarijul Xuruuf) waa imisa meelood?",
                questionType = QuestionType.MULTIPLE_CHOICE,
                points = 10,
                optionsJson = """[{"id":"opt_7","text":"5 meelood oo guud","correct":true},{"id":"opt_8","text":"10 meelood","correct":false},{"id":"opt_9","text":"2 meelood","correct":false}]"""
            )
        )
        quizDao.insertQuestions(questions)

        // 7. Seed Sample Assignment for Part 02
        val assignment = Assignment(
            id = "assign_tajweed_01",
            lessonId = "les_tajweed_07",
            title = "Layliga Duubista Suuradda Al-Ikhlaas",
            description = "Fadlan duub codkaaga adigoo akhrinaya Suuradda Al-Ikhlaas adigoo ilaalinaya makhaarijka xarfaha gaar ahaan Qaaf iyo Xaa.",
            instructions = "1. Ka bilow Bismillah\n2. Xaraf walba si cad u dhawaaq\n3. Geli qoraal ahaan wixii su'aal ah oo aad qabto\n4. Riix badhanka 'Submit Assignment'",
            dueDate = "Axad, 10-ka bisha",
            maxScore = 100,
            attachmentRequired = false
        )
        assignmentDao.insertAssignment(assignment)

        // 8. Seed Default User Profile & Initial Enrollment
        val student = UserProfile(
            id = "user_default_1",
            fullName = "Cabdiraxmaan Cali Maxamed",
            email = "student@mandeqislamic.so",
            phone = "+252 61 555 1234",
            role = UserRole.STUDENT,
            streakDays = 18,
            coursesEnrolled = 3,
            completedCourses = 1,
            completedLessons = 14
        )
        userDao.insertUserProfile(student)

        enrollInCourse("user_default_1", "course_tajweed_1")
        saveLessonProgress("user_default_1", "les_tajweed_01", "course_tajweed_1", 100, true)
        saveLessonProgress("user_default_1", "les_tajweed_02", "course_tajweed_1", 100, true)

        // 9. Seed Initial Notifications
        val notifs = listOf(
            NotificationItem(
                id = "notif_1",
                userId = "user_default_1",
                title = "🎉 Ku soo dhowaw Mandeq Islamic LMS!",
                message = "Waxaa lagugu daray nidaamka cusub ee macallimiinta iyo koorsooyinka casriga ah.",
                type = "ANNOUNCEMENT",
                categoryName = "System",
                isRead = false,
                timestampFormatted = "Hadda"
            ),
            NotificationItem(
                id = "notif_2",
                userId = "user_default_1",
                title = "📚 Cashar cusub oo Tajweed ah",
                message = "Ustaad Maxamed wuxuu soo galiyey Part 02 ee casharka Makhaarijul Xuruuf.",
                type = "LESSON",
                categoryName = "Tajweed",
                isRead = false,
                timestampFormatted = "Saacad ka hor"
            )
        )
        notificationDao.insertNotifications(notifs)
    }
}
