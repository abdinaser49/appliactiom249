package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. User Roles
enum class UserRole {
    SUPER_ADMIN,
    TEACHER,
    STUDENT,
    GUEST
}

// 2. Teacher Approval Status
enum class TeacherApprovalStatus {
    PENDING,
    APPROVED,
    SUSPENDED,
    REJECTED
}

// 3. Course Status
enum class CourseStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED,
    ARCHIVED
}

// 4. Difficulty Level
enum class DifficultyLevel(val label: String) {
    BEGINNER("Bilow (Beginner)"),
    INTERMEDIATE("Dhexe (Intermediate)"),
    ADVANCED("Sare (Advanced)")
}

// 5. Course Language
enum class CourseLanguage(val label: String) {
    SOMALI("Af-Soomaali"),
    ARABIC("العربية"),
    ENGLISH("English")
}

// 6. Lesson Type
enum class LessonType(val label: String) {
    VIDEO("Muuqaal (Video)"),
    AUDIO("Cod (Audio)"),
    PDF("Buug / Qoraal (PDF)"),
    TEXT("Cashar Qoraal ah (Rich Text)"),
    QUIZ("Imtixaan Kooban (Quiz)"),
    ASSIGNMENT("Layli Guriga (Assignment)")
}

// 7. Question Type
enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    MULTIPLE_ANSWER
}

// Backwards compatibility if needed
enum class LessonStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PUBLISHED,
    ARCHIVED
}

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey
    val id: String = "user_default_1",
    val fullName: String = "Cabdiraxmaan Cali",
    val email: String = "student@mandeqislamic.so",
    val phone: String = "+252 61 555 1234",
    val avatarUrl: String = "",
    val role: UserRole = UserRole.STUDENT,
    val status: String = "ACTIVE",
    val bio: String = "Arday baranaya cilmiga sharciga iyo Qur'aanka Kariimka ah.",
    val country: String = "Somalia",
    val streakDays: Int = 14,
    val coursesEnrolled: Int = 5,
    val completedCourses: Int = 2,
    val completedLessons: Int = 37,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "teachers")
data class TeacherProfile(
    @PrimaryKey
    val id: String,
    val profileId: String,
    val fullName: String,
    val email: String,
    val phone: String = "",
    val avatarUrl: String = "",
    val country: String = "Somalia",
    val bio: String,
    val teachingSubjects: String, // Comma separated: e.g. "Tajweed, Quran, Fiqhi"
    val experienceYears: Int = 5,
    val approvalStatus: TeacherApprovalStatus = TeacherApprovalStatus.APPROVED,
    val totalCourses: Int = 0,
    val totalStudents: Int = 0,
    val totalLessons: Int = 0,
    val rating: Float = 4.9f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String = "",
    val arabicName: String = "",
    val description: String,
    val iconEmoji: String,
    val subcategories: String = "", // Comma-separated
    val lessonCount: Int = 0,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String,
    val teacherId: String,
    val teacherName: String,
    val categoryId: String,
    val categoryName: String,
    val title: String,
    val slug: String = "",
    val subtitle: String = "",
    val description: String,
    val coverImageUrl: String = "",
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val language: CourseLanguage = CourseLanguage.SOMALI,
    val duration: String = "12 Saacadood",
    val requirements: String = "Aqoonta aasaasiga ah ee xarfaha Carabiga",
    val learningObjectives: String = "Barashada Tajweedka, Sixidda dhawaaqa, Xafidaadda aayadaha",
    val tags: String = "Tajweed, Quran, Fiqhi",
    val status: CourseStatus = CourseStatus.PUBLISHED,
    val isFeatured: Boolean = false,
    val studentsCount: Int = 1245,
    val rating: Float = 4.9f,
    val totalPartsCount: Int = 4,
    val totalLessonsCount: Int = 18,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Dynamic Part System: Supports unlimited parts (Part 1, Part 2, ... Part 100 ... Part N)
@Entity(tableName = "course_parts")
data class CoursePart(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val partNumber: Int,
    val title: String,
    val description: String = "",
    val thumbnailUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Dynamic Lesson System inside each Part (NO YOUTUBE DEPENDENCY!)
@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey
    val id: String,
    val courseId: String = "course_tajweed_1",
    val partId: String = "part_tajweed_01",
    val lessonNumber: Int = 1,
    val title: String,
    val arabicTitle: String = "",
    val description: String = "",
    val lessonType: LessonType = LessonType.VIDEO,
    val contentUrl: String = "", // Direct educational media URL (MP4, MP3, PDF)
    val thumbnailUrl: String = "",
    val textContent: String = "", // Rich text in Somali and Arabic
    val duration: String = "18:40",
    val durationSeconds: Int = 1120,
    val isFree: Boolean = false,
    val isPublished: Boolean = true,
    val allowDownload: Boolean = true,
    val teacherName: String = "Ustaad Maxamed Cabdullaahi",
    val categoryId: String = "cat_tajweed",
    val tags: String = "Tajweed, Quran",
    val viewsCount: Int = 3450,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey
    val id: String,
    val lessonId: String,
    val title: String,
    val passingScore: Int = 70, // percentage e.g. 70%
    val timeLimitMinutes: Int = 15,
    val attemptsAllowed: Int = 3,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey
    val id: String,
    val quizId: String,
    val question: String,
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val points: Int = 10,
    val optionsJson: String = "", // Encoded JSON list of options
    val createdAt: Long = System.currentTimeMillis()
)

data class QuizOptionItem(
    val id: String,
    val optionText: String,
    val isCorrect: Boolean
)

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey
    val id: String,
    val lessonId: String,
    val title: String,
    val description: String,
    val instructions: String,
    val dueDate: String = "7 maalmood gudahood",
    val maxScore: Int = 100,
    val attachmentRequired: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignment_submissions")
data class AssignmentSubmission(
    @PrimaryKey
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String = "Cabdiraxmaan Cali",
    val fileUrl: String = "",
    val textAnswer: String = "",
    val score: Int? = null,
    val feedback: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val gradedAt: Long? = null
)

@Entity(tableName = "enrollments")
data class Enrollment(
    @PrimaryKey
    val id: String, // "${studentId}_${courseId}"
    val studentId: String,
    val courseId: String,
    val enrolledAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val progressPercentage: Int = 0
)

@Entity(tableName = "lesson_progress")
data class LessonProgress(
    @PrimaryKey
    val id: String, // composite "${studentId}_${lessonId}"
    val userId: String, // alias for studentId
    val lessonId: String,
    val courseId: String = "",
    val progressPercentage: Int = 0,
    val completed: Boolean = false,
    val lastPositionSeconds: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey
    val id: String,
    val studentId: String,
    val quizId: String,
    val score: Int,
    val passed: Boolean,
    val attemptNumber: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey
    val id: String, // "${userId}_${lessonId}"
    val userId: String,
    val lessonId: String,
    val courseId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hifdh_progress")
data class HifdhProgress(
    @PrimaryKey
    val id: String,
    val userId: String,
    val surahNumber: Int,
    val surahName: String,
    val arabicSurahName: String,
    val juzNumber: Int,
    val totalAyahs: Int,
    val memorizedAyahs: Int,
    val percentage: Int,
    val dailyGoalAyahs: Int = 5,
    val todayCompletedAyahs: Int = 3,
    val lastReviewedDate: String = "Maanta",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String = "ANNOUNCEMENT",
    val categoryName: String = "Tajweed",
    val isRead: Boolean = false,
    val timestampFormatted: String = "Hadda",
    val createdAt: Long = System.currentTimeMillis()
)
