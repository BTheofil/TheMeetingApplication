package hu.tb.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserDatastoreRepository(
    private val context: Context
) {
    fun userdataFlow(): Flow<UserData> = context.userDataStore.data

    suspend fun updateUserData(
        name: String? = null,
        password: String? = null,
        profileType: String? = null,
        token: String? = null,
        tokenRefreshDate: Long? = null
    ) = context.userDataStore.updateData { userData ->
        userData.copy(
            name = name ?: userData.name,
            password = password ?: userData.password,
            profileType = profileType ?: userData.profileType,
            token = token ?: userData.token,
            tokenRefreshDate = tokenRefreshDate ?: userData.tokenRefreshDate
        )
    }

    suspend fun clearUserData() = context.userDataStore.updateData { UserData() }
}
