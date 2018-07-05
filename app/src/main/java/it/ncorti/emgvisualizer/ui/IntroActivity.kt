package it.ncorti.emgvisualizer.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import it.ncorti.emgvisualizer.R

private const val PREFS_GLOBAL = "global"
private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"
private const val REQUEST_LOCATION_CODE = 1

class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor =
                ContextCompat.getColor(this, R.color.primaryColor)

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_description_1),
                R.drawable.onboarding_0,
                backgroundColor))

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.scan),
                getString(R.string.onboarding_description_2),
                R.drawable.onboarding_1,
                backgroundColor))

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.control),
                getString(R.string.onboarding_description_3),
                R.drawable.onboarding_2,
                backgroundColor))

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.graph),
                getString(R.string.onboarding_description_4),
                R.drawable.onboarding_3,
                backgroundColor))

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.export),
                getString(R.string.onboarding_description_5),
                R.drawable.onboarding_4,
                backgroundColor))

        setBarColor(ContextCompat.getColor(this, R.color.primaryDarkColor))
        setSeparatorColor(ContextCompat.getColor(this, R.color.primaryLightColor))
        showSkipButton(false)
        isProgressButtonEnabled = true
        setVibrate(true)
        setVibrateIntensity(30)
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        saveOnBoardingCompleted()
        requestPermission()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        saveOnBoardingCompleted()
        requestPermission()
    }

    private fun saveOnBoardingCompleted() {
        val editor = getSharedPreferences(PREFS_GLOBAL, Context.MODE_PRIVATE).edit()
        editor.putBoolean(KEY_COMPLETED_ONBOARDING, true)
        editor.apply()
    }

    private fun requestPermission() {
        val hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        if (hasPermission) {
            startMainActivity()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMainActivity()
                } else {
                    Toast.makeText(this, getString(R.string.location_permission_denied_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startMainActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}