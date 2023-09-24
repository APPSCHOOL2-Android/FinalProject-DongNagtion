package likelion.project.dongnation.repository

import kotlinx.coroutines.flow.Flow
import likelion.project.dongnation.db.remote.ReviewDataSource
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.model.User

class ReviewRepository {
    private val reviewDataSource = ReviewDataSource()

    suspend fun addReview(review: Review): Flow<Result<Boolean>> {
        return reviewDataSource.addReview(review)
    }

    suspend fun getReviews(donationIdx: String): Flow<Result<List<Review>>> {
        return reviewDataSource.getReviews(donationIdx)
    }

    suspend fun deleteReview(user: User) = reviewDataSource.deleteReview(user)
}