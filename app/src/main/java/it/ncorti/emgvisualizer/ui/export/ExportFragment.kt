package it.ncorti.emgvisualizer.ui.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import it.ncorti.emgvisualizer.BaseFragment
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.databinding.LayoutExportBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject

private const val REQUEST_WRITE_EXTERNAL_CODE = 2

class ExportFragment : BaseFragment<ExportContract.Presenter>(), ExportContract.View {

    private lateinit var binding: LayoutExportBinding

    companion object {
        fun newInstance() = ExportFragment()
    }

    @Inject
    lateinit var exportPresenter: ExportPresenter

    private var fileContentToSave: String? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        attachPresenter(exportPresenter)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutExportBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartCollecting.setOnClickListener { exportPresenter.onCollectionTogglePressed() }
        binding.buttonResetCollecting.setOnClickListener { exportPresenter.onResetPressed() }
        binding.buttonShare.setOnClickListener { exportPresenter.onSharePressed() }
        binding.buttonSave.setOnClickListener { exportPresenter.onSavePressed() }
    }

    override fun enableStartCollectingButton() {
        binding.buttonStartCollecting.isEnabled = true
    }

    override fun disableStartCollectingButton() {
        binding.buttonStartCollecting.isEnabled = false
    }

    override fun showNotStreamingErrorMessage() {
        Toast.makeText(
            activity, "You can't collect points if Myo is not streaming!", Toast.LENGTH_SHORT
        ).show()
    }

    override fun showCollectionStarted() {
        binding.buttonStartCollecting.text = getString(R.string.stop_collecting)
    }

    override fun showCollectionStopped() {
        binding.buttonStartCollecting.text = getString(R.string.start_collecting)
    }

    override fun showCollectedPoints(totalPoints: Int) {
        binding.pointsCount.text = String.format(Locale.getDefault(), totalPoints.toString())
    }

    override fun enableResetButton() {
        binding.buttonResetCollecting.isEnabled = true
    }

    override fun disableResetButton() {
        binding.buttonResetCollecting.isEnabled = false
    }

    override fun hideSaveArea() {
        binding.buttonSave.visibility = View.INVISIBLE
        binding.buttonShare.visibility = View.INVISIBLE
        binding.saveExportTitle.visibility = View.INVISIBLE
        binding.saveExportSubtitle.visibility = View.INVISIBLE
    }

    override fun showSaveArea() {
        binding.buttonSave.visibility = View.VISIBLE
        binding.buttonShare.visibility = View.VISIBLE
        binding.saveExportTitle.visibility = View.VISIBLE
        binding.saveExportSubtitle.visibility = View.VISIBLE
    }

    override fun sharePlainText(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun saveCsvFile(content: String) {
        val fileName = getCsvFilename()
        // Use scoped storage for API level 29 and above
        val contentResolver = context?.contentResolver
        val contentValues = ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(
                android.provider.MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS
            )
        }
        val uri: Uri? = contentResolver?.insert(
            android.provider.MediaStore.Files.getContentUri("external"), contentValues
        )
        if (uri != null) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            val userReadablePath = "${Environment.DIRECTORY_DOCUMENTS}/$fileName"
            Toast.makeText(activity, "Export saved to: $userReadablePath", Toast.LENGTH_LONG).show()
        } else {
            // Handle failure to create the file
            Toast.makeText(activity, "ERROR: Impossible to save export to file", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun writeToFile(fileName: String, content: String) {
        val storageDir = File("${Environment.getExternalStorageDirectory().absolutePath}/myo_emg")
        storageDir.mkdir()
        val outfile = File(storageDir, fileName)
        val fileOutputStream = FileOutputStream(outfile)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
        Toast.makeText(activity, "Saved to: ${outfile.path}", Toast.LENGTH_LONG).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    fileContentToSave?.apply { writeToFile(getCsvFilename(), this) }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.write_permission_denied_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getCsvFilename() = "myo_emg_export_${System.currentTimeMillis()}.csv"
}
