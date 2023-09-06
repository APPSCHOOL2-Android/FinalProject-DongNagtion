package likelion.project.dongnation.repository

import likelion.project.dongnation.db.remote.UserDataSource

class UserRepository {
    private val userDataSource = UserDataSource()

    suspend fun getAllUsers() = userDataSource.getAllUsers()
}