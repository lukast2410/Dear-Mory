package edu.bluejack20_1.dearmory

import android.app.Activity
import android.content.res.Configuration
import edu.bluejack20_1.dearmory.R

class ThemeManager {
    companion object{
        const val SHARED_PREFS = "themePrefsForDearMory"
        const val THEME = "theme"
        const val DARK_THEME_INDEX = 1
        const val LIGHT_THEME_INDEX = 2
        const val GALAXY_THEME_INDEX = 3
        var THEME_INDEX = DARK_THEME_INDEX

        const val TEXT = "text"
        const val SMALL_TEXT_SIZE = 14
        const val MEDIUM_TEXT_SIZE = 17
        const val LARGE_TEXT_SIZE = 20
        var TEXT_SIZE = SMALL_TEXT_SIZE

        const val LANG = "language"
        const val ENGLISH = "en"
        const val INDONESIA = "in-rID"
        var LANGUAGE = ENGLISH

        const val GUIDE_PAGE = "Guide"

        fun setDarkTheme(){ THEME_INDEX = DARK_THEME_INDEX }
        fun setLightTheme(){ THEME_INDEX = LIGHT_THEME_INDEX }
        fun setGalaxyTheme(){ THEME_INDEX = GALAXY_THEME_INDEX }
        fun setEnglishLanguage(){ LANGUAGE = ENGLISH }
        fun setIndonesiaLanguage(){ LANGUAGE = INDONESIA }

        fun setUpTheme(): Int{
            return when(THEME_INDEX){
                DARK_THEME_INDEX -> getDarkThemeBasedOnTextSize()
                LIGHT_THEME_INDEX -> getLightThemeBasedOnTextSize()
                GALAXY_THEME_INDEX -> getGalaxyThemeBasedOnTextSize()
                else -> getDarkThemeBasedOnTextSize()
            }
        }

        fun setUpBackground(): Int{
            return when(THEME_INDEX){
                DARK_THEME_INDEX -> R.drawable.dark
                LIGHT_THEME_INDEX -> R.drawable.light
                GALAXY_THEME_INDEX -> R.drawable.galaxy
                else -> R.drawable.dark
            }
        }

        fun isDarkTheme(activity: Activity): Boolean {
            return activity.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        private fun getLightThemeBasedOnTextSize(): Int {
            return when(TEXT_SIZE){
                SMALL_TEXT_SIZE -> R.style.SmallLightTheme
                MEDIUM_TEXT_SIZE -> R.style.MediumLightTheme
                LARGE_TEXT_SIZE -> R.style.LargeLightTheme
                else -> R.style.SmallLightTheme
            }
        }

        private fun getDarkThemeBasedOnTextSize(): Int{
            return when(TEXT_SIZE){
                SMALL_TEXT_SIZE -> R.style.SmallDarkTheme
                MEDIUM_TEXT_SIZE -> R.style.MediumDarkTheme
                LARGE_TEXT_SIZE -> R.style.LargeDarkTheme
                else -> R.style.SmallDarkTheme
            }
        }

        private fun getGalaxyThemeBasedOnTextSize(): Int {
            return when(TEXT_SIZE){
                SMALL_TEXT_SIZE -> R.style.SmallGalaxyTheme
                MEDIUM_TEXT_SIZE -> R.style.MediumGalaxyTheme
                LARGE_TEXT_SIZE -> R.style.LargeGalaxyTheme
                else -> R.style.SmallGalaxyTheme
            }
        }
    }
}