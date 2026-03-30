package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.model.UserProfile
import com.goodrecipe.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MineUiState(
    val profile: UserProfile = UserProfile(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MineViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MineUiState())
    val uiState: StateFlow<MineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileRepository.profile.collect { profile ->
                _uiState.update { it.copy(profile = profile, isLoading = false) }
            }
        }
    }

    fun updateAvatarUri(uriString: String) {
        val current = userProfileRepository.profile.value
        userProfileRepository.updateProfile(current.copy(avatarUrl = uriString))
    }

    fun updateNickname(nickname: String) {
        val current = userProfileRepository.profile.value
        userProfileRepository.updateProfile(current.copy(nickname = nickname))
    }

    fun updateSignature(signature: String) {
        val current = userProfileRepository.profile.value
        userProfileRepository.updateProfile(current.copy(signature = signature))
    }
}
