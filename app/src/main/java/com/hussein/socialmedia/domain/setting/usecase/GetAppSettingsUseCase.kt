package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting app settings
 */
class GetAppSettingsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return preferencesRepository.getAppSettings()
    }
}