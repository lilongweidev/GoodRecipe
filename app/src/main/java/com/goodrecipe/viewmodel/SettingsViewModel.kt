package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import com.goodrecipe.data.model.UserProfile
import com.goodrecipe.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val profile = userProfileRepository.profile

    fun saveProfile(profile: UserProfile) {
        userProfileRepository.updateProfile(profile)
    }
}
