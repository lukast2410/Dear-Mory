package edu.bluejack20_1.dearmory

import edu.bluejack20_1.dearmory.R

class ThemeManager {
    companion object{
        const val DARK_THEME_INDEX = 1
        const val LIGHT_THEME_INDEX = 2
        const val GALAXY_THEME_INDEX = 3
        var THEME_INDEX = DARK_THEME_INDEX

        fun setDarkTheme(){ THEME_INDEX = DARK_THEME_INDEX }
        fun setLightTheme(){ THEME_INDEX = LIGHT_THEME_INDEX }
        fun setGalaxyTheme(){ THEME_INDEX = GALAXY_THEME_INDEX }

        fun setUpTheme(): Int{
            if(THEME_INDEX == LIGHT_THEME_INDEX)
                return R.style.LightTheme
            else if(THEME_INDEX == DARK_THEME_INDEX)
                return R.style.DarkTheme
            else if(THEME_INDEX == GALAXY_THEME_INDEX)
                return R.style.GalaxyTheme
            else
                return R.style.LightTheme
        }

        fun setUpBackground(): Int{
            if(THEME_INDEX == LIGHT_THEME_INDEX)
                return R.drawable.light
            else if(THEME_INDEX == DARK_THEME_INDEX)
                return R.drawable.dark
            else if(THEME_INDEX == GALAXY_THEME_INDEX)
                return R.drawable.galaxy
            else
                return R.drawable.light
        }
    }
}