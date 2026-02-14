package com.hussein.socialmedia.domain.setting.usecase

import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for updating app settings
 */
class UpdateAppSettingsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(settings: AppSettings) {
        preferencesRepository.updateAppSettings(settings)
    }
}
