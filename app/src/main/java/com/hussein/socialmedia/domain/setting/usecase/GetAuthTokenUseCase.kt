package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for getting auth token
 */
class GetAuthTokenUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): String? {
        return preferencesRepository.getAuthToken()
    }
}