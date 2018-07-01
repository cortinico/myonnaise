package it.ncorti.emgvisualizer.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.ui.control.ControlDeviceFragment
import it.ncorti.emgvisualizer.ui.control.ControlDevicePresenter
import it.ncorti.emgvisualizer.ui.export.ExportFragment
import it.ncorti.emgvisualizer.ui.graph.GraphFragment
import it.ncorti.emgvisualizer.ui.export.ExportPresenter
import it.ncorti.emgvisualizer.ui.graph.GraphPresenter
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceFragment
import it.ncorti.emgvisualizer.ui.scan.ScanDevicePresenter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var scanDeviceFragment: ScanDeviceFragment
    private lateinit var controlDeviceFragment: ControlDeviceFragment
    private lateinit var graphFragment: GraphFragment
    private lateinit var exportFragment: ExportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.new_toolbar))

        createPresenters()
        val fragmentList = listOf<Fragment>(
                scanDeviceFragment,
                controlDeviceFragment,
                graphFragment,
                exportFragment
        )

        view_pager.adapter = MyAdapter(supportFragmentManager, fragmentList)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prevMenuItem: MenuItem? = null
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    bottom_navigation.menu.getItem(0).isChecked = false
                }
                bottom_navigation.menu.getItem(position).isChecked = true
                prevMenuItem = bottom_navigation.menu.getItem(position)
            }

        })
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_scan -> view_pager.currentItem = 0
                R.id.item_control -> view_pager.currentItem = 1
                R.id.item_graph -> view_pager.currentItem = 2
                R.id.item_export -> view_pager.currentItem = 3
            }
            false
        }

    }

    private fun createPresenters() {
        scanDeviceFragment = ScanDeviceFragment.newInstance()
        val scanPresenter = ScanDevicePresenter(scanDeviceFragment)
        scanDeviceFragment.setPresenter(scanPresenter)

        controlDeviceFragment = ControlDeviceFragment.newInstance()
        val controlPresenter = ControlDevicePresenter(controlDeviceFragment)
        controlDeviceFragment.setPresenter(controlPresenter)

        graphFragment = GraphFragment.newInstance()
        val graphPresenter = GraphPresenter(graphFragment)
        graphFragment.setPresenter(graphPresenter)

        exportFragment = ExportFragment.newInstance()
        val exportPresenter = ExportPresenter(exportFragment)
        exportFragment.setPresenter(exportPresenter)
    }

    fun navigateToPage(pageId: Int) {
        view_pager.currentItem = pageId
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