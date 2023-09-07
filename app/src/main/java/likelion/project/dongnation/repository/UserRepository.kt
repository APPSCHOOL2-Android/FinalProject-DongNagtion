package likelion.project.dongnation.repository

import likelion.project.dongnation.db.remote.UserDataSource
import likelion.project.dongnation.model.User

class UserRepository {
    private val userDataSource = UserDataSource()

    suspend fun getAllUsers() = userDataSource.getAllUsers()

    suspend fun getUser(user: User) = userDataSource.getUser(user)

    suspend fun saveUser(user: User) = userDataSource.saveUser(user)
}