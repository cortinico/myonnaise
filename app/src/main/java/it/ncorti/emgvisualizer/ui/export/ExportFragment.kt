package it.ncorti.emgvisualizer.ui.export

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import it.ncorti.emgvisualizer.BaseFragment
import it.ncorti.emgvisualizer.R
import kotlinx.android.synthetic.main.layout_export.*
import java.io.File
import java.io.FileOutputStream


private const val REQUEST_WRITE_EXTERNAL_CODE = 2

class ExportFragment : BaseFragment<ExportContract.Presenter>(), ExportContract.View {

    private var fileContentToSave: String? = null

    companion object {
        fun newInstance() = ExportFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_export, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_collecting.setOnClickListener { presenter?.onCollectionTogglePressed() }
        button_reset_collecting.setOnClickListener { presenter?.onResetPressed() }
        button_share.setOnClickListener { presenter?.onSharePressed() }
        button_save.setOnClickListener { presenter?.onSavePressed() }
    }

    override fun enableStartCollectingButton() {
        button_start_collecting.isEnabled = true
    }

    override fun disableStartCollectingButton() {
        button_start_collecting.isEnabled = false
    }

    override fun showNotStreamingErrorMessage() {
        Toast.makeText(activity, "You can't collect points if Myo is not streaming!", Toast.LENGTH_SHORT).show()
    }

    override fun showCollectionStarted() {
        button_start_collecting?.text = getString(R.string.stop_collecting)
    }

    override fun showCollectionStopped() {
        button_start_collecting?.text = getString(R.string.start_collecting)
    }

    override fun showCollectedPoints(totalPoints: Int) {
        points_count.text = totalPoints.toString()
    }

    override fun enableResetButton() {
        button_reset_collecting.isEnabled = true
    }

    override fun disableResetButton() {
        button_reset_collecting.isEnabled = false
    }

    override fun hideSaveArea() {
        button_save.visibility = View.INVISIBLE
        button_share.visibility = View.INVISIBLE
        save_export_title.visibility = View.INVISIBLE
        save_export_subtitle.visibility = View.INVISIBLE
    }

    override fun showSaveArea() {
        button_save.visibility = View.VISIBLE
        button_share.visibility = View.VISIBLE
        save_export_title.visibility = View.VISIBLE
        save_export_subtitle.visibility = View.VISIBLE
    }

    override fun sharePlainText(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun saveCsvFile(content: String) {
        context?.apply {
            val hasPermission = (ContextCompat.checkSelfPermission(this,
                    WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
            if (hasPermission) {
                writeToFile(content)
            } else {
                fileContentToSave = content
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_CODE)
            }
        }
    }

    private fun writeToFile(content: String) {
        val storageDir =
                File("${Environment.getExternalStorageDirectory().absolutePath}/myo_emg")
        storageDir.mkdir()
        val outfile = File(storageDir, "myo_emg_export_${System.currentTimeMillis()}.csv")
        val fileOutputStream = FileOutputStream(outfile)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
        Toast.makeText(activity, "Saved to: ${outfile.path}", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fileContentToSave?.apply { writeToFile(this) }
                } else {
                    Toast.makeText(activity, getString(R.string.write_permission_denied_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}