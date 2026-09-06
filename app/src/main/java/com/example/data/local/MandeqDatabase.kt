package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.STUDENT)

    @TypeConverter
    fun fromTeacherApprovalStatus(status: TeacherApprovalStatus): String = status.name

    @TypeConverter
    fun toTeacherApprovalStatus(value: String): TeacherApprovalStatus = runCatching { TeacherApprovalStatus.valueOf(value) }.getOrDefault(TeacherApprovalStatus.APPROVED)

    @TypeConverter
    fun fromCourseStatus(status: CourseStatus): String = status.name

    @TypeConverter
    fun toCourseStatus(value: String): CourseStatus = runCatching { CourseStatus.valueOf(value) }.getOrDefault(CourseStatus.PUBLISHED)

    @TypeConverter
    fun fromDifficultyLevel(level: DifficultyLevel): String = level.name

    @TypeConverter
    fun toDifficultyLevel(value: String): DifficultyLevel = runCatching { DifficultyLevel.valueOf(value) }.getOrDefault(DifficultyLevel.BEGINNER)

    @TypeConverter
    fun fromCourseLanguage(language: CourseLanguage): String = language.name

    @TypeConverter
    fun toCourseLanguage(value: String): CourseLanguage = runCatching { CourseLanguage.valueOf(value) }.getOrDefault(CourseLanguage.SOMALI)

    @TypeConverter
    fun fromLessonType(type: LessonType): String = type.name

    @TypeConverter
    fun toLessonType(value: String): LessonType = runCatching { LessonType.valueOf(value) }.getOrDefault(LessonType.VIDEO)

    @TypeConverter
    fun fromQuestionType(type: QuestionType): String = type.name

    @TypeConverter
    fun toQuestionType(value: String): QuestionType = runCatching { QuestionType.valueOf(value) }.getOrDefault(QuestionType.MULTIPLE_CHOICE)
}

@Database(
    entities = [
        UserProfile::class,
        TeacherProfile::class,
        Category::class,
        Course::class,
        CoursePart::class,
        Lesson::class,
        Quiz::class,
        QuizQuestion::class,
        Assignment::class,
        AssignmentSubmission::class,
        Enrollment::class,
        LessonProgress::class,
        QuizAttempt::class,
        Bookmark::class,
        HifdhProgress::class,
        NotificationItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MandeqDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun teacherDao(): TeacherDao
    abstract fun courseDao(): CourseDao
    abstract fun coursePartDao(): CoursePartDao
    abstract fun lessonDao(): LessonDao
    abstract fun quizDao(): QuizDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun hifdhDao(): HifdhDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MandeqDatabase? = null

        fun getDatabase(context: Context): MandeqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MandeqDatabase::class.java,
                    "mandeq_islamic_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
