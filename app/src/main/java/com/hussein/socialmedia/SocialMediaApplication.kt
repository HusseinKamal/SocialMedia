package com.hussein.socialmedia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Social Media App.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class SocialMediaApplication : Application()