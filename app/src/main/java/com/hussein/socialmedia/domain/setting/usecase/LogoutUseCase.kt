package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for logout
 */
class LogoutUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke() {
        preferencesRepository.clearUserSession()
    }
}