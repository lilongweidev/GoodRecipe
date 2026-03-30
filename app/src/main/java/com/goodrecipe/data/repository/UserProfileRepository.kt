package com.goodrecipe.data.repository

import android.content.Context
import com.goodrecipe.data.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class UserProfileRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(readInternal())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun updateProfile(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_NICKNAME, profile.nickname)
            .putString(KEY_SIGNATURE, profile.signature)
            .putString(KEY_AVATAR, profile.avatarUrl)
            .apply()
        _profile.value = readInternal()
    }

    private fun readInternal(): UserProfile = UserProfile(
        nickname = prefs.getString(KEY_NICKNAME, null) ?: DEFAULT_NICKNAME,
        signature = prefs.getString(KEY_SIGNATURE, null) ?: DEFAULT_SIGNATURE,
        avatarUrl = prefs.getString(KEY_AVATAR, "") ?: ""
    )

    companion object {
        private const val PREFS_NAME = "good_recipe_user_profile"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_SIGNATURE = "signature"
        private const val KEY_AVATAR = "avatar_url"
        private const val DEFAULT_NICKNAME = "美食家"
        private const val DEFAULT_SIGNATURE = "今天也要好好吃饭～"
    }
}
