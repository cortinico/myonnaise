package it.ncorti.emgvisualizer.ui.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import it.ncorti.emgvisualizer.R
import kotlinx.android.synthetic.main.layout_export.*

class ExportFragment : Fragment(), ExportContract.View {

    private lateinit var presenter: ExportContract.Presenter

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

        button_start_collecting.setOnClickListener { presenter.onCollectionTogglePressed() }
        button_reset_collecting.setOnClickListener { presenter.onResetPressed() }
        button_share.setOnClickListener { presenter.onSharePressed() }
        button_save.setOnClickListener { presenter.onSavePressed() }
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun setPresenter(presenter: ExportContract.Presenter) {
        this.presenter = presenter
    }

    override fun showCollectionStarted() {
        button_start_collecting.text = "Stop Collecting"
    }

    override fun showCollectionStopped() {
        button_start_collecting.text = "Start Collecting"
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

    override fun showSavedToCsvMessage() {
        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
    }
}