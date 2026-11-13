package com.example.data.srs

import com.example.data.model.SrsProgress
import com.example.data.model.SrsRating
import com.example.data.model.SrsStage
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.ceil
import kotlin.math.max

/**
 * Triển khai thuật toán Spaced Repetition SM-2 (SuperMemo-2)
 * - Nút AGAIN ("Quên"): đưa thẻ vào giai đoạn học lại ngắn hạn (10 phút trong ngày, intervalDays = 0).
 * - Các nút HARD / GOOD / EASY: lên lịch theo đầu ngày dương lịch (00:00:00) của ngày đến hạn.
 */
object Sm2Algorithm {

    const val RELEARNING_STEP_MINUTES = 10
    private const val ONE_MINUTE_MS = 60 * 1000L

    /**
     * Tính mốc 00:00:00.000 của ngày (hôm nay + intervalDays) theo múi giờ cục bộ.
     */
    fun startOfDayAfterDays(
        currentTime: Long,
        intervalDays: Int,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = currentTime
            add(Calendar.DAY_OF_YEAR, intervalDays)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    /**
     * Tính số ngày interval mới cho từng mức đánh giá.
     * Lưu ý: AGAIN trả về 0 (học lại ngắn hạn 10 phút trong cùng ngày).
     */
    fun computeIntervalDays(current: SrsProgress, rating: SrsRating): Int {
        return when (rating) {
            SrsRating.AGAIN -> 0
            SrsRating.HARD -> {
                if (current.repetitions == 0 || current.intervalDays <= 0) {
                    1
                } else {
                    // Dùng ceil và đảm bảo tăng tối thiểu +1 ngày để không bị kẹt mãi ở interval = 1
                    max(current.intervalDays + 1, ceil(current.intervalDays * 1.2).toInt())
                }
            }
            SrsRating.GOOD -> {
                when {
                    current.repetitions == 0 -> 1
                    current.repetitions == 1 -> 6
                    else -> max(current.intervalDays + 1, (current.intervalDays * current.easeFactor).toInt())
                }
            }
            SrsRating.EASY -> {
                when {
                    current.repetitions == 0 -> 4
                    current.repetitions == 1 -> 8
                    else -> max(current.intervalDays + 1, (current.intervalDays * current.easeFactor * 1.3).toInt())
                }
            }
        }
    }

    /**
     * Tính toán lịch ôn tiếp theo khi người dùng đánh giá thẻ
     */
    fun calculateNextReview(
        current: SrsProgress,
        rating: SrsRating,
        currentTime: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): SrsProgress {
        val q = rating.quality
        var reps = current.repetitions
        val interval = computeIntervalDays(current, rating)
        var ef = current.easeFactor
        var lapses = current.lapses

        when (rating) {
            SrsRating.AGAIN -> {
                // Quên: reset reps về 0, đưa vào học lại ngắn hạn (10 phút), giảm EF
                reps = 0
                ef = max(1.3, ef - 0.20)
                lapses += 1
            }
            SrsRating.HARD -> {
                reps += 1
                ef = max(1.3, ef - 0.15)
            }
            SrsRating.GOOD -> {
                reps += 1
                val delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)
                ef = max(1.3, ef + delta)
            }
            SrsRating.EASY -> {
                reps += 1
                ef = max(1.3, ef + 0.15)
            }
        }

        val stage = when {
            rating == SrsRating.AGAIN -> SrsStage.LEARNING
            interval >= 21 -> SrsStage.MASTERED
            reps >= 2 -> SrsStage.REVIEW
            else -> SrsStage.LEARNING
        }

        val nextReview = if (rating == SrsRating.AGAIN) {
            currentTime + RELEARNING_STEP_MINUTES * ONE_MINUTE_MS
        } else {
            startOfDayAfterDays(currentTime, interval, timeZone)
        }

        val roundedEf = (ef * 100).toInt() / 100.0

        return current.copy(
            repetitions = reps,
            intervalDays = interval,
            easeFactor = roundedEf,
            lastReviewedTimestamp = currentTime,
            nextReviewTimestamp = nextReview,
            lapses = lapses,
            totalReviews = current.totalReviews + 1,
            stage = stage
        )
    }

    /**
     * Dự đoán khoảng thời gian hiển thị trên các nút đánh giá trong UI
     */
    fun previewIntervalText(current: SrsProgress, rating: SrsRating): String {
        if (rating == SrsRating.AGAIN) {
            return "$RELEARNING_STEP_MINUTES phút"
        }
        val days = computeIntervalDays(current, rating)
        return "$days ngày"
    }
}
