package it.ncorti.emgvisualizer.ui.scan

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.ncorti.emgvisualizer.Device
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.ui.MainActivity
import kotlinx.android.synthetic.main.layout_scan_device.*

class ScanDeviceFragment : Fragment(), ScanDeviceContract.View {

    private lateinit var listDeviceAdapter: DeviceAdapter
    private lateinit var presenter: ScanDeviceContract.Presenter

    companion object {
        fun newInstance() = ScanDeviceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_scan_device, container, false)

        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_scan.setOnClickListener { presenter.onScanToggleClicked() }

        listDeviceAdapter = DeviceAdapter(object : DeviceSelectedListener{
            override fun onDeviceSelected(v: View, position: Int) {
                presenter.onDeviceSelected(position)
            }
        })
        list_device_found.layoutManager = LinearLayoutManager(this.context)
        list_device_found.itemAnimator = FadeInAnimator()
        list_device_found.adapter = listDeviceAdapter
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun populateDeviceList(list: List<Device>) {
        listDeviceAdapter.deviceList = list.toMutableList()
        listDeviceAdapter.notifyDataSetChanged()
    }

    override fun addDeviceToList(device: Device) {
        listDeviceAdapter.deviceList.add(device)
        listDeviceAdapter.notifyItemInserted(listDeviceAdapter.itemCount)
    }

    override fun wipeDeviceList() {
        listDeviceAdapter.deviceList = mutableListOf()
        listDeviceAdapter.notifyItemRangeRemoved(0, listDeviceAdapter.itemCount)
    }

    override fun showScanLoading() {
        progress_bar_search.animate().alpha(1.0f)
        fab_scan.setImageDrawable(context?.getDrawable(R.drawable.ic_stop))
    }

    override fun hideScanLoading() {
        progress_bar_search.animate().alpha(0.0f)
        fab_scan.setImageDrawable(context?.getDrawable(R.drawable.ic_magnify))
    }

    override fun showScanError() {
        Toast.makeText(this.context, "Scan Failed", Toast.LENGTH_SHORT).show()
    }

    override fun showScanCompleted() {
        Toast.makeText(this.context, "Scan Completed!", Toast.LENGTH_SHORT).show()
    }

    override fun setPresenter(presenter: ScanDeviceContract.Presenter) {
        this.presenter = presenter
    }

    override fun navigateToControlDevice() {
        (activity as MainActivity).navigateToPage(1)
    }

    class DeviceAdapter(
            private val deviceSelectedListener: DeviceSelectedListener
    ) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

        var deviceList = mutableListOf<Device>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false), deviceSelectedListener)

        override fun getItemCount() = deviceList.size

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.bind(deviceList[position])
        }


        class DeviceViewHolder(
                val item: View,
                private val listener: DeviceSelectedListener
        ) : RecyclerView.ViewHolder(item) {
            fun bind(device: Device) {
                item.findViewById<TextView>(R.id.text_name).text =
                        device.name ?: item.context.getString(R.string.unknown_device)
                item.findViewById<TextView>(R.id.text_address).text = device.address
                item.findViewById<MaterialButton>(R.id.button_select).setOnClickListener {
                    listener.onDeviceSelected(it, adapterPosition)
                }
            }
        }
    }

    interface DeviceSelectedListener {
        fun onDeviceSelected(v: View, position: Int)
    }


    inner class FadeInAnimator : DefaultItemAnimator() {

        override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
            if (viewHolder is DeviceAdapter.DeviceViewHolder) {
                viewHolder.item.alpha = 0.0f
                viewHolder.itemView.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                dispatchAddFinished(viewHolder)
                            }
                        })
                        .start()
            }
            return false
        }
    }

//
//    private fun requestPermission() {
//        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity!!,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    123)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            123 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission Granted
//                Toast.makeText(context!!, "Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                // Permission Denied
//                Toast.makeText(context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
}