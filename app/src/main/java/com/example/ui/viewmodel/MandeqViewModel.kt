package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MandeqDatabase
import com.example.data.model.*
import com.example.data.repository.AdminStats
import com.example.data.repository.MandeqRepository
import com.example.data.repository.TeacherStats
import com.example.data.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppLanguage(val code: String, val title: String, val isRtl: Boolean) {
    SOMALI("so", "Soomaali", false),
    ARABIC("ar", "العربية", true),
    ENGLISH("en", "English", false)
}

data class ContinueLessonState(
    val lesson: Lesson,
    val progress: LessonProgress
)

class MandeqViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MandeqRepository(MandeqDatabase.getDatabase(application))

    val currentUserId = "user_default_1"
    val defaultTeacherId = "teacher_1"

    // ==========================================
    // 1. Language & User Role Management
    // ==========================================
    private val _appLanguage = MutableStateFlow(AppLanguage.SOMALI)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    // Active User Profile
    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Active Role (STUDENT, TEACHER, SUPER_ADMIN, GUEST)
    private val _activeRole = MutableStateFlow(UserRole.STUDENT)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    fun switchRole(role: UserRole) {
        _activeRole.value = role
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveUserProfile(current.copy(role = role))
        }
    }

    // ==========================================
    // 2. Categories
    // ==========================================
    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    // ==========================================
    // 3. Courses (Published & Catalog)
    // ==========================================
    val publishedCourses: StateFlow<List<Course>> = repository.getPublishedCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredCourses: StateFlow<List<Course>> = repository.getFeaturedCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses: StateFlow<List<Course>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered courses by category
    val filteredCourses: StateFlow<List<Course>> = combine(
        publishedCourses,
        _selectedCategoryId
    ) { courses, catId ->
        if (catId == null) courses else courses.filter { it.categoryId == catId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Course for Detail Screen
    private val _selectedCourseId = MutableStateFlow<String?>("course_tajweed_1")
    val selectedCourseId: StateFlow<String?> = _selectedCourseId.asStateFlow()

    fun selectCourse(courseId: String) {
        _selectedCourseId.value = courseId
    }

    val selectedCourse: StateFlow<Course?> = combine(
        allCourses,
        _selectedCourseId
    ) { courses, id ->
        courses.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Parts for selected course (DYNAMIC UNLIMITED PARTS!)
    val selectedCourseParts: StateFlow<List<CoursePart>> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getPartsForCourse(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lessons for selected course
    val selectedCourseLessons: StateFlow<List<Lesson>> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getLessonsForCourse(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check if current user is enrolled in selected course
    val isEnrolledInSelectedCourse: StateFlow<Boolean> = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.isEnrolled(currentUserId, id) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun enrollInCourse(courseId: String) {
        viewModelScope.launch {
            repository.enrollInCourse(currentUserId, courseId)
        }
    }

    // ==========================================
    // 4. Lessons & Playback (NO YOUTUBE!)
    // ==========================================
    val publishedLessons: StateFlow<List<Lesson>> = repository.getPublishedLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredLessons: StateFlow<List<Lesson>> = publishedLessons.map { lessons ->
        lessons.take(4)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAdminLessons: StateFlow<List<Lesson>> = repository.getAllLessonsForAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentPlayingLesson = MutableStateFlow<Lesson?>(null)
    val currentPlayingLesson: StateFlow<Lesson?> = _currentPlayingLesson.asStateFlow()

    fun setPlayingLesson(lesson: Lesson) {
        _currentPlayingLesson.value = lesson
        _selectedCourseId.value = lesson.courseId
    }

    // Quiz for current lesson
    val currentLessonQuiz: StateFlow<Quiz?> = _currentPlayingLesson.flatMapLatest { lesson ->
        if (lesson != null) repository.getQuizForLesson(lesson.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentLessonQuestions: StateFlow<List<QuizQuestion>> = currentLessonQuiz.flatMapLatest { quiz ->
        if (quiz != null) repository.getQuestionsForQuiz(quiz.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Assignment for current lesson
    val currentLessonAssignment: StateFlow<Assignment?> = _currentPlayingLesson.flatMapLatest { lesson ->
        if (lesson != null) repository.getAssignmentForLesson(lesson.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // User Progress
    val userProgressList: StateFlow<List<LessonProgress>> = repository.getUserProgress(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedLessonIds: StateFlow<Set<String>> = userProgressList.map { list ->
        list.filter { it.completed }.map { it.lessonId }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val continueLearning: StateFlow<ContinueLessonState?> = combine(
        repository.getLatestUnfinishedLesson(currentUserId),
        publishedLessons
    ) { progress, lessons ->
        if (progress != null) {
            val lesson = lessons.find { it.id == progress.lessonId }
                ?: repository.getLessonById(progress.lessonId)
            if (lesson != null) ContinueLessonState(lesson, progress) else null
        } else {
            // Fallback to first published lesson
            lessons.firstOrNull()?.let {
                ContinueLessonState(
                    it,
                    LessonProgress(
                        id = "${currentUserId}_${it.id}",
                        userId = currentUserId,
                        lessonId = it.id,
                        courseId = it.courseId,
                        progressPercentage = 30
                    )
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun markLessonCompleted(lessonId: String, courseId: String) {
        viewModelScope.launch {
            repository.saveLessonProgress(currentUserId, lessonId, courseId, 100, true)
        }
    }

    fun submitQuizScore(quizId: String, score: Int, passed: Boolean) {
        viewModelScope.launch {
            repository.submitQuizAttempt(
                QuizAttempt(
                    id = "attempt_${UUID.randomUUID().toString().take(8)}",
                    studentId = currentUserId,
                    quizId = quizId,
                    score = score,
                    passed = passed
                )
            )
            _currentPlayingLesson.value?.let { lesson ->
                repository.saveLessonProgress(currentUserId, lesson.id, lesson.courseId, 100, true)
            }
        }
    }

    fun submitAssignmentAnswer(assignmentId: String, textAnswer: String) {
        viewModelScope.launch {
            repository.submitAssignment(
                AssignmentSubmission(
                    id = "sub_${UUID.randomUUID().toString().take(8)}",
                    assignmentId = assignmentId,
                    studentId = currentUserId,
                    textAnswer = textAnswer
                )
            )
            _currentPlayingLesson.value?.let { lesson ->
                repository.saveLessonProgress(currentUserId, lesson.id, lesson.courseId, 100, true)
            }
        }
    }

    // ==========================================
    // 5. Teacher Portal & Course Management (LMS)
    // ==========================================
    val currentTeacher: StateFlow<TeacherProfile?> = repository.getAllTeachers().map { teachers ->
        teachers.find { it.id == defaultTeacherId } ?: teachers.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val teacherCourses: StateFlow<List<Course>> = repository.getCoursesByTeacher(defaultTeacherId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _teacherStats = MutableStateFlow(
        TeacherStats(
            totalCourses = 4,
            publishedCourses = 3,
            draftCourses = 1,
            totalStudents = 3240,
            totalLessons = 36,
            totalViews = 24800,
            averageCompletion = 78
        )
    )
    val teacherStats: StateFlow<TeacherStats> = _teacherStats.asStateFlow()

    fun loadTeacherStats() {
        viewModelScope.launch {
            _teacherStats.value = repository.getTeacherStats(defaultTeacherId)
        }
    }

    fun createCourse(
        title: String,
        subtitle: String,
        categoryId: String,
        description: String,
        difficulty: DifficultyLevel,
        language: CourseLanguage,
        duration: String,
        requirements: String,
        learningObjectives: String,
        tags: String,
        isDraft: Boolean
    ) {
        viewModelScope.launch {
            val cat = categories.value.find { it.id == categoryId }
            val courseId = "course_${UUID.randomUUID().toString().take(8)}"
            val course = Course(
                id = courseId,
                teacherId = defaultTeacherId,
                teacherName = currentTeacher.value?.fullName ?: "Ustaad Maxamed Cabdullaahi",
                categoryId = categoryId,
                categoryName = cat?.name ?: "Diini",
                title = title,
                subtitle = subtitle,
                description = description,
                coverImageUrl = "https://images.unsplash.com/photo-1609599006353-e629aaabfeae?w=600",
                difficulty = difficulty,
                language = language,
                duration = duration,
                requirements = requirements,
                learningObjectives = learningObjectives,
                tags = tags,
                status = if (isDraft) CourseStatus.DRAFT else CourseStatus.PUBLISHED,
                isFeatured = false,
                totalPartsCount = 1,
                totalLessonsCount = 0
            )
            repository.saveCourse(course)

            // Automatically create initial Part 01
            val initialPart = CoursePart(
                id = "part_${UUID.randomUUID().toString().take(8)}",
                courseId = courseId,
                partNumber = 1,
                title = "Part 01 — Hordhac & Bilow",
                description = "Qeybta kowaad ee koorsada"
            )
            repository.savePart(initialPart)
            _selectedCourseId.value = courseId
        }
    }

    // Dynamic Part Creation (Can create Part 1, Part 2, ... Part 100!)
    fun addCoursePart(courseId: String, partNumber: Int, title: String, description: String) {
        viewModelScope.launch {
            val part = CoursePart(
                id = "part_${UUID.randomUUID().toString().take(8)}",
                courseId = courseId,
                partNumber = partNumber,
                title = title,
                description = description
            )
            repository.savePart(part)
        }
    }

    fun deletePart(part: CoursePart) {
        viewModelScope.launch {
            repository.deletePart(part)
        }
    }

    fun addLesson(
        courseId: String,
        partId: String,
        lessonNumber: Int,
        title: String,
        description: String,
        lessonType: LessonType,
        contentUrl: String,
        textContent: String,
        duration: String,
        isFree: Boolean,
        isPublished: Boolean
    ) {
        viewModelScope.launch {
            val lesson = Lesson(
                id = "les_${UUID.randomUUID().toString().take(8)}",
                courseId = courseId,
                partId = partId,
                lessonNumber = lessonNumber,
                title = title,
                description = description,
                lessonType = lessonType,
                contentUrl = contentUrl,
                textContent = textContent,
                duration = duration,
                isFree = isFree,
                isPublished = isPublished,
                teacherName = currentTeacher.value?.fullName ?: "Ustaad Maxamed Cabdullaahi",
                categoryId = selectedCourse.value?.categoryId ?: "cat_tajweed"
            )
            repository.saveLesson(lesson)
        }
    }

    fun duplicateLesson(lessonId: String) {
        viewModelScope.launch {
            repository.duplicateLesson(lessonId)
        }
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
        }
    }

    // ==========================================
    // 6. Super Admin Management
    // ==========================================
    val allTeachers: StateFlow<List<TeacherProfile>> = repository.getAllTeachers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _adminStats = MutableStateFlow(
        AdminStats(
            totalCourses = 6,
            publishedCourses = 5,
            totalLessons = 48,
            totalTeachers = 4,
            totalStudents = 1845,
            pendingTeachers = 1
        )
    )
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    fun approveTeacher(teacherId: String) {
        viewModelScope.launch {
            repository.updateTeacherApproval(teacherId, TeacherApprovalStatus.APPROVED)
        }
    }

    fun suspendTeacher(teacherId: String) {
        viewModelScope.launch {
            repository.updateTeacherApproval(teacherId, TeacherApprovalStatus.SUSPENDED)
        }
    }

    // ==========================================
    // 7. Bookmarks, Hifdh & Tools
    // ==========================================
    fun isLessonBookmarked(lessonId: String): Flow<Boolean> = repository.isBookmarked(currentUserId, lessonId)

    fun toggleBookmark(lessonId: String, courseId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(currentUserId, lessonId, courseId, currentStatus)
        }
    }

    val bookmarkedLessons: StateFlow<List<Lesson>> = combine(
        repository.getUserBookmarks(currentUserId),
        publishedLessons
    ) { bookmarks, lessons ->
        val ids = bookmarks.map { it.lessonId }.toSet()
        lessons.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userHifdh: StateFlow<List<HifdhProgress>> = repository.getUserHifdh(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.getNotifications(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(currentUserId)
        }
    }

    // Islamic Utility features (Prayer Times, Tasbeeh)
    val selectedCity = MutableStateFlow(com.example.data.util.PrayerTimesCalculator.SUPPORTED_CITIES[0])
    val prayerTimes = selectedCity.map { com.example.data.util.PrayerTimesCalculator.calculatePrayerTimes(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.util.PrayerTimesCalculator.calculatePrayerTimes(com.example.data.util.PrayerTimesCalculator.SUPPORTED_CITIES[0]))

    val tasbeehCount = MutableStateFlow(0)
    val tasbeehTotal = MutableStateFlow(100)
    val selectedDhikr = MutableStateFlow(com.example.data.util.PrayerTimesCalculator.TASBEEH_LIST[0])

    fun incrementTasbeeh() {
        tasbeehCount.value += 1
    }

    fun resetTasbeeh() {
        tasbeehCount.value = 0
    }

    fun selectDhikr(dhikr: com.example.data.util.DhikrItem) {
        selectedDhikr.value = dhikr
        tasbeehTotal.value = dhikr.targetCount
        tasbeehCount.value = 0
    }

    fun selectCity(city: com.example.data.util.CityLocation) {
        selectedCity.value = city
    }

    // Audio player state
    private val _audioPlayerState = MutableStateFlow(AudioState())
    val audioPlayerState: StateFlow<AudioState> = _audioPlayerState.asStateFlow()

    fun playAudioStream(url: String, title: String) {
        _audioPlayerState.value = AudioState(isPlaying = true, title = title, duration = "14:15")
    }

    fun stopAudio() {
        _audioPlayerState.value = AudioState(isPlaying = false)
    }

    // Lesson Notes (In-memory cache)
    private val lessonNotes = mutableMapOf<String, String>()

    fun getLessonNote(lessonId: String): String = lessonNotes[lessonId] ?: ""

    fun saveLessonNote(lessonId: String, note: String) {
        lessonNotes[lessonId] = note
    }
}
