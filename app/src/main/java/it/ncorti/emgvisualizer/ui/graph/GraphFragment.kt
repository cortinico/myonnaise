package it.ncorti.emgvisualizer.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.ncorti.emgvisualizer.R
import kotlinx.android.synthetic.main.layout_graph.*

class GraphFragment : Fragment(), GraphContract.View {

    private lateinit var presenter: GraphContract.Presenter

    companion object {
        fun newInstance() = GraphFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_graph, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO Export me
        sensor_graph_view.channels = 8
        sensor_graph_view.maxValue = 128.0f
        sensor_graph_view.minValue = -128.0f
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            println("GraphFramgnet: Visible")
            presenter.start()
            sensor_graph_view?.apply {
                this.running = true
            }
        } else {
            println("GraphFramgnet: Not Visible")
            presenter.stop()
            sensor_graph_view?.apply {
                this.running = false
            }
        }
    }

    override fun setPresenter(presenter: GraphContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(data: FloatArray) {
        sensor_graph_view?.addPoint(data)
    }

}