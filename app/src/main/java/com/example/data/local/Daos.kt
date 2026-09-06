package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY createdAt DESC")
    fun getAllTeachers(): Flow<List<TeacherProfile>>

    @Query("SELECT * FROM teachers WHERE approvalStatus = 'APPROVED' ORDER BY rating DESC")
    fun getApprovedTeachers(): Flow<List<TeacherProfile>>

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: String): TeacherProfile?

    @Query("SELECT * FROM teachers WHERE email = :email LIMIT 1")
    suspend fun getTeacherByEmail(email: String): TeacherProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherProfile>)

    @Update
    suspend fun updateTeacher(teacher: TeacherProfile)

    @Query("UPDATE teachers SET approvalStatus = :status WHERE id = :teacherId")
    suspend fun updateApprovalStatus(teacherId: String, status: TeacherApprovalStatus)

    @Delete
    suspend fun deleteTeacher(teacher: TeacherProfile)

    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun getTotalTeachersCount(): Int
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE status = 'PUBLISHED' ORDER BY createdAt DESC")
    fun getPublishedCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE status = 'PUBLISHED' AND isFeatured = 1 ORDER BY createdAt DESC")
    fun getFeaturedCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE categoryId = :categoryId AND status = 'PUBLISHED' ORDER BY createdAt DESC")
    fun getCoursesByCategory(categoryId: String): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE teacherId = :teacherId ORDER BY createdAt DESC")
    fun getCoursesByTeacher(teacherId: String): Flow<List<Course>>

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: String): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("UPDATE courses SET status = :status WHERE id = :courseId")
    suspend fun updateCourseStatus(courseId: String, status: CourseStatus)

    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR teacherName LIKE '%' || :query || '%'")
    fun searchCourses(query: String): Flow<List<Course>>

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getTotalCoursesCount(): Int

    @Query("SELECT COUNT(*) FROM courses WHERE status = 'PUBLISHED'")
    suspend fun getPublishedCoursesCount(): Int

    @Query("SELECT COUNT(*) FROM courses WHERE status = 'DRAFT'")
    suspend fun getDraftCoursesCount(): Int
}

// Dynamic Unlimited Parts DAO (supports Part 1, Part 2, ... Part 100...)
@Dao
interface CoursePartDao {
    @Query("SELECT * FROM course_parts WHERE courseId = :courseId ORDER BY partNumber ASC")
    fun getPartsForCourse(courseId: String): Flow<List<CoursePart>>

    @Query("SELECT * FROM course_parts WHERE courseId = :courseId ORDER BY partNumber ASC")
    suspend fun getPartsForCourseSync(courseId: String): List<CoursePart>

    @Query("SELECT * FROM course_parts WHERE id = :id")
    suspend fun getPartById(id: String): CoursePart?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: CoursePart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParts(parts: List<CoursePart>)

    @Update
    suspend fun updatePart(part: CoursePart)

    @Delete
    suspend fun deletePart(part: CoursePart)

    @Query("SELECT COUNT(*) FROM course_parts WHERE courseId = :courseId")
    suspend fun getPartsCountForCourse(courseId: String): Int
}

// Lessons DAO
@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getPublishedLessons(): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY lessonNumber ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE partId = :partId ORDER BY lessonNumber ASC")
    fun getLessonsForPart(partId: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE categoryId = :categoryId AND isPublished = 1 ORDER BY lessonNumber ASC")
    fun getLessonsByCategory(categoryId: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons ORDER BY createdAt DESC")
    fun getAllLessonsForAdmin(): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons")
    suspend fun getAllLessonsSync(): List<Lesson>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: String): Lesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Update
    suspend fun updateLesson(lesson: Lesson)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR teacherName LIKE '%' || :query || '%'")
    fun searchLessons(query: String): Flow<List<Lesson>>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int

    @Query("SELECT COUNT(*) FROM lessons WHERE isPublished = 1")
    suspend fun getPublishedCount(): Int

    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :courseId")
    suspend fun getLessonsCountForCourse(courseId: String): Int
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE lessonId = :lessonId LIMIT 1")
    fun getQuizForLesson(lessonId: String): Flow<Quiz?>

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: String): Quiz?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz)

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    fun getQuestionsForQuiz(quizId: String): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuizSync(quizId: String): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttempt)

    @Query("SELECT * FROM quiz_attempts WHERE studentId = :studentId AND quizId = :quizId ORDER BY createdAt DESC")
    fun getAttemptsForQuiz(studentId: String, quizId: String): Flow<List<QuizAttempt>>
}

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments WHERE lessonId = :lessonId LIMIT 1")
    fun getAssignmentForLesson(lessonId: String): Flow<Assignment?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: AssignmentSubmission)

    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId")
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<AssignmentSubmission>>

    @Query("SELECT * FROM assignment_submissions WHERE studentId = :studentId AND assignmentId = :assignmentId LIMIT 1")
    fun getStudentSubmission(studentId: String, assignmentId: String): Flow<AssignmentSubmission?>

    @Query("UPDATE assignment_submissions SET score = :score, feedback = :feedback, gradedAt = :gradedAt WHERE id = :submissionId")
    suspend fun gradeSubmission(submissionId: String, score: Int, feedback: String, gradedAt: Long)
}

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM enrollments WHERE studentId = :studentId")
    fun getStudentEnrollments(studentId: String): Flow<List<Enrollment>>

    @Query("SELECT * FROM enrollments WHERE studentId = :studentId AND courseId = :courseId LIMIT 1")
    fun getEnrollment(studentId: String, courseId: String): Flow<Enrollment?>

    @Query("SELECT EXISTS(SELECT 1 FROM enrollments WHERE studentId = :studentId AND courseId = :courseId)")
    fun isEnrolled(studentId: String, courseId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: Enrollment)

    @Query("UPDATE enrollments SET progressPercentage = :progress, completedAt = :completedAt WHERE studentId = :studentId AND courseId = :courseId")
    suspend fun updateEnrollmentProgress(studentId: String, courseId: String, progress: Int, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM enrollments WHERE courseId = :courseId")
    suspend fun getEnrollmentCountForCourse(courseId: String): Int
}

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE userId = :userId")
    fun getUserProgress(userId: String): Flow<List<LessonProgress>>

    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun getProgressForLesson(userId: String, lessonId: String): LessonProgress?

    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND lessonId = :lessonId")
    fun observeProgressForLesson(userId: String, lessonId: String): Flow<LessonProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LessonProgress)

    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND completed = 0 ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestUnfinishedLesson(userId: String): Flow<LessonProgress?>

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE userId = :userId AND completed = 1")
    fun getCompletedLessonsCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE userId = :userId AND courseId = :courseId AND completed = 1")
    suspend fun getCompletedLessonsForCourse(userId: String, courseId: String): Int
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE userId = :userId")
    fun getUserBookmarks(userId: String): Flow<List<Bookmark>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND lessonId = :lessonId)")
    fun isBookmarked(userId: String, lessonId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun deleteBookmark(userId: String, lessonId: String)
}

@Dao
interface HifdhDao {
    @Query("SELECT * FROM hifdh_progress WHERE userId = :userId ORDER BY surahNumber ASC")
    fun getUserHifdhProgress(userId: String): Flow<List<HifdhProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<HifdhProgress>)

    @Update
    suspend fun updateHifdhProgress(progress: HifdhProgress)

    @Query("SELECT * FROM hifdh_progress WHERE userId = :userId AND surahNumber = :surahNumber")
    suspend fun getProgressForSurah(userId: String, surahNumber: Int): HifdhProgress?
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getUserProfile(id: String): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfile)

    @Update
    suspend fun updateUserProfile(user: UserProfile)
}
