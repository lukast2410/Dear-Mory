package edu.bluejack20_1.dearmory.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.math.roundToInt

class SettingActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var textSize: Int = ThemeManager.SMALL_TEXT_SIZE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_setting)
        iv_main_background_setting.setImageResource(ThemeManager.setUpBackground())
        initializeTextSize()
        initializeToolbar()
        initializeSelectedLanguage()
        initializeSettingLanguage()
        initializeSelectedTheme()
        initializeSettingTheme()
    }

    private fun initializeTextSize() {
        textSize = ThemeManager.TEXT_SIZE
        setSelectedRadioButton(textSize)
    }

    private fun initializeSelectedLanguage() {
        when(ThemeManager.LANGUAGE){
            ThemeManager.ENGLISH -> {
                tv_english_language.setTextColor(ContextCompat.getColor(applicationContext, getSelectedLanguageTextColor()))
                tv_english_language.setTypeface(null, Typeface.BOLD)
                iv_english_checklist.visibility = View.VISIBLE
                iv_english_checklist.setColorFilter(ContextCompat.getColor(applicationContext, getSelectedLanguageChecklistColor()))
            }
            ThemeManager.INDONESIA -> {
                tv_indonesia_language.setTextColor(ContextCompat.getColor(applicationContext, getSelectedLanguageTextColor()))
                tv_indonesia_language.setTypeface(null, Typeface.BOLD)
                iv_indonesia_checklist.visibility = View.VISIBLE
                iv_indonesia_checklist.setColorFilter(ContextCompat.getColor(applicationContext, getSelectedLanguageChecklistColor()))
            }
        }
    }

    private fun initializeSettingLanguage() {
        tv_english_language.setOnClickListener {
            if(ThemeManager.LANGUAGE != ThemeManager.ENGLISH){
                ThemeManager.setEnglishLanguage()
                saveLanguageToPref(ThemeManager.ENGLISH)
            }
        }
        tv_indonesia_language.setOnClickListener {
            if(ThemeManager.LANGUAGE != ThemeManager.INDONESIA){
                ThemeManager.setIndonesiaLanguage()
                saveLanguageToPref(ThemeManager.INDONESIA)
            }
        }
    }

    private fun initializeSelectedTheme() {
        val scale: Float = applicationContext.resources.displayMetrics.density
        val heightSelected = (320 * scale + 0.5F).roundToInt()
        val widthSelected = (153 * scale + 0.5F).roundToInt()
        when(ThemeManager.THEME_INDEX){
            ThemeManager.DARK_THEME_INDEX -> {
                iv_dark_theme_preview.layoutParams.width = widthSelected
                iv_dark_theme_preview.layoutParams.height = heightSelected
                iv_dark_theme_preview.background = ContextCompat.getDrawable(applicationContext, R.drawable.border_theme)
                iv_dark_theme_preview.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.navBackgroundColorDarkTheme))
            }
            ThemeManager.GALAXY_THEME_INDEX -> {
                iv_galaxy_theme_preview.layoutParams.width = widthSelected
                iv_galaxy_theme_preview.layoutParams.height = heightSelected
                iv_galaxy_theme_preview.background = ContextCompat.getDrawable(applicationContext, R.drawable.border_theme)
                iv_galaxy_theme_preview.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.navBackgroundColorGalaxyTheme))
            }
            ThemeManager.LIGHT_THEME_INDEX -> {
                iv_light_theme_preview.layoutParams.width = widthSelected
                iv_light_theme_preview.layoutParams.height = heightSelected
                iv_light_theme_preview.background = ContextCompat.getDrawable(applicationContext, R.drawable.border_theme)
                iv_light_theme_preview.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.backgroundForIconLightTheme))
            }
        }
    }

    private fun initializeSettingTheme() {
        ll_dark_theme_container.setOnClickListener{
            if (ThemeManager.THEME_INDEX != ThemeManager.DARK_THEME_INDEX){
                ThemeManager.setDarkTheme()
                sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
                editor = sharedPreferences.edit()
                editor.putInt(ThemeManager.THEME, ThemeManager.DARK_THEME_INDEX)
                editor.apply()
                recreate()
            }
        }
        ll_galaxy_theme_container.setOnClickListener {
            if (ThemeManager.THEME_INDEX != ThemeManager.GALAXY_THEME_INDEX){
                ThemeManager.setGalaxyTheme()
                sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
                editor = sharedPreferences.edit()
                editor.putInt(ThemeManager.THEME, ThemeManager.GALAXY_THEME_INDEX)
                editor.apply()
                recreate()
            }
        }
        ll_light_theme_container.setOnClickListener {
            if (ThemeManager.THEME_INDEX != ThemeManager.LIGHT_THEME_INDEX){
                ThemeManager.setLightTheme()
                sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
                editor = sharedPreferences.edit()
                editor.putInt(ThemeManager.THEME, ThemeManager.LIGHT_THEME_INDEX)
                editor.apply()
                recreate()
            }
        }
    }

    fun onRadioButtonClicked(view: View){
        if(view is RadioButton){
            val checked = view.isChecked
            when(view.id){
                R.id.rb_small_font_size ->
                    if(checked && textSize != ThemeManager.SMALL_TEXT_SIZE){
                        textSize = ThemeManager.SMALL_TEXT_SIZE
                        setSelectedRadioButton(textSize)
                        saveTextSize(textSize)
                    }
                R.id.rb_medium_font_size ->
                    if(checked && textSize != ThemeManager.MEDIUM_TEXT_SIZE){
                        textSize = ThemeManager.MEDIUM_TEXT_SIZE
                        setSelectedRadioButton(textSize)
                        saveTextSize(textSize)
                    }
                R.id.rb_large_font_size ->
                    if(checked && textSize != ThemeManager.LARGE_TEXT_SIZE){
                        textSize = ThemeManager.LARGE_TEXT_SIZE
                        setSelectedRadioButton(textSize)
                        saveTextSize(textSize)
                    }
            }
        }
    }

    private fun saveTextSize(textSize: Int) {
        ThemeManager.TEXT_SIZE = textSize
        sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt(ThemeManager.TEXT, textSize)
        editor.apply()
        recreate()
    }

    private fun setSelectedRadioButton(size: Int){
        rb_small_font_size.setBackgroundColor(Color.TRANSPARENT)
        rb_medium_font_size.setBackgroundColor(Color.TRANSPARENT)
        rb_large_font_size.setBackgroundColor(Color.TRANSPARENT)
        if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX){
            rb_small_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            rb_medium_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            rb_large_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            when(size){
                ThemeManager.SMALL_TEXT_SIZE -> {
                    rb_small_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
                    rb_small_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                }
                ThemeManager.MEDIUM_TEXT_SIZE -> {
                    rb_medium_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
                    rb_medium_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                }
                ThemeManager.LARGE_TEXT_SIZE -> {
                    rb_large_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
                    rb_large_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                }
            }
        } else {
            rb_small_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            rb_medium_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            rb_large_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            when(size){
                ThemeManager.SMALL_TEXT_SIZE -> {
                    rb_small_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
                    rb_small_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                }
                ThemeManager.MEDIUM_TEXT_SIZE -> {
                    rb_medium_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
                    rb_medium_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                }
                ThemeManager.LARGE_TEXT_SIZE -> {
                    rb_large_font_size.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
                    rb_large_font_size.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                    tv_text_size_preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                }
            }
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun saveLanguageToPref(lang: String) {
        sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(ThemeManager.LANG, lang)
        editor.apply()
    }

    private fun getSelectedLanguageTextColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.DARK_THEME_INDEX -> R.color.backgroundForIconDarkTheme
            ThemeManager.GALAXY_THEME_INDEX -> R.color.navBackgroundColorGalaxyTheme
            ThemeManager.LIGHT_THEME_INDEX -> R.color.backgroundForIconLightTheme
            else -> R.color.backgroundForIconDarkTheme
        }
    }

    private fun getSelectedLanguageChecklistColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.LIGHT_THEME_INDEX -> R.color.black
            else -> R.color.white
        }
    }

    override fun onBackPressed() {
        var intent = Intent(applicationContext, AppActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        super.onBackPressed()
    }
}