package it.ncorti.emgvisualizer.ui.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.ncorti.emgvisualizer.R

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
}