package it.ncorti.emgvisualizer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.databinding.ActivityMainBinding
import it.ncorti.emgvisualizer.ui.control.ControlDeviceFragment
import it.ncorti.emgvisualizer.ui.export.ExportFragment
import it.ncorti.emgvisualizer.ui.graph.GraphFragment
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceFragment
import javax.inject.Inject

private const val PREFS_GLOBAL = "global"
private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    @Suppress("MagicNumber")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Checking if we should on-board the user the first time.
        val prefs = getSharedPreferences(PREFS_GLOBAL, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_COMPLETED_ONBOARDING, false)) {
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }

        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.new_toolbar))

        val fragmentList = listOf<Fragment>(
            ScanDeviceFragment.newInstance(),
            ControlDeviceFragment.newInstance(),
            GraphFragment.newInstance(),
            ExportFragment.newInstance()
        )

        binding.viewPager.adapter = MyAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prevMenuItem: MenuItem? = null
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    binding.bottomNavigation.menu.getItem(0).isChecked = false
                }
                binding.bottomNavigation.menu.getItem(position).isChecked = true
                prevMenuItem = binding.bottomNavigation.menu.getItem(position)
            }
        })
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_scan -> binding.viewPager.currentItem = 0
                R.id.item_control -> binding.viewPager.currentItem = 1
                R.id.item_graph -> binding.viewPager.currentItem = 2
                R.id.item_export -> binding.viewPager.currentItem = 3
            }
            false
        }
    }

    fun navigateToPage(pageId: Int) {
        binding.viewPager.currentItem = pageId
    }

    class MyAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}
