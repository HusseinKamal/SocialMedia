package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.model.UserSession
import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import javax.inject.Inject


/**
 * Use case for saving user session
 */
class SaveUserSessionUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(session: UserSession) {
        preferencesRepository.saveUserSession(session)
    }
}