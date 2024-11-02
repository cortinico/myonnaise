package it.ncorti.emgvisualizer.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage
import it.ncorti.emgvisualizer.R

private const val PREFS_GLOBAL = "global"
private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"
private const val REQUEST_LOCATION_CODE = 1
private const val VIBRATE_INTENSITY = 30L

class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroFragment.createInstance(SliderPage().apply {
            title = getString(R.string.onboarding_title_0)
            description = getString(R.string.onboarding_description_0)
            imageDrawable = R.drawable.onboarding_0
            backgroundColorRes = R.color.primaryColor
        }))

        addSlide(AppIntroFragment.createInstance(SliderPage().apply {
            title = getString(R.string.scan)
            description = getString(R.string.onboarding_description_1)
            imageDrawable = R.drawable.onboarding_1
            backgroundColorRes = R.color.primaryColor
        }))

        addSlide(AppIntroFragment.createInstance(SliderPage().apply {
            title = getString(R.string.control)
            description = getString(R.string.onboarding_description_2)
            imageDrawable = R.drawable.onboarding_2
            backgroundColorRes = R.color.primaryColor
        }))

        addSlide(AppIntroFragment.createInstance(SliderPage().apply {
            title = getString(R.string.graph)
            description = getString(R.string.onboarding_description_3)
            imageDrawable = R.drawable.onboarding_3
            backgroundColorRes = R.color.primaryColor
        }))

        addSlide(AppIntroFragment.createInstance(SliderPage().apply {
            title = getString(R.string.export)
            description = getString(R.string.onboarding_description_4)
            imageDrawable = R.drawable.onboarding_4
            backgroundColorRes = R.color.primaryColor
        }))

        setBarColor(ContextCompat.getColor(this, R.color.primaryDarkColor))
        setSeparatorColor(ContextCompat.getColor(this, R.color.primaryLightColor))
        isSkipButtonEnabled = false
        isVibrate = true
        vibrateDuration = VIBRATE_INTENSITY
        isButtonsEnabled = true
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        saveOnBoardingCompleted()
        requestPermission()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
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
        val hasPermission =
            (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        this,
                        BLUETOOTH_SCAN
                    ) == PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        this,
                        BLUETOOTH_ADMIN
                    ) == PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        this,
                        BLUETOOTH_CONNECT
                    ) == PERMISSION_GRANTED)
        if (hasPermission) {
            startMainActivity()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, BLUETOOTH_SCAN, BLUETOOTH_ADMIN, BLUETOOTH_CONNECT),
                REQUEST_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    startMainActivity()
                } else {
                    Toast.makeText(
                        this, getString(R.string.location_permission_denied_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startMainActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
