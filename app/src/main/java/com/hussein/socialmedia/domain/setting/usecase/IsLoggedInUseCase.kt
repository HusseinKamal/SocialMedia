package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for checking login state
 */
class IsLoggedInUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Boolean {
        return preferencesRepository.isLoggedIn()
    }
}
