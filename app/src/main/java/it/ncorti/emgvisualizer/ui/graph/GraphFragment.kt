package it.ncorti.emgvisualizer.ui.graph

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ncorti.myonnaise.MYO_CHANNELS
import com.ncorti.myonnaise.MYO_MAX_VALUE
import com.ncorti.myonnaise.MYO_MIN_VALUE
import dagger.android.support.AndroidSupportInjection
import it.ncorti.emgvisualizer.BaseFragment
import it.ncorti.emgvisualizer.R
import javax.inject.Inject
import kotlinx.android.synthetic.main.layout_graph.*

class GraphFragment : BaseFragment<GraphContract.Presenter>(), GraphContract.View {

    companion object {
        fun newInstance() = GraphFragment()
    }

    @Inject
    lateinit var graphPresenter: GraphPresenter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        attachPresenter(graphPresenter)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_graph, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensor_graph_view.channels = MYO_CHANNELS
        sensor_graph_view.maxValue = MYO_MAX_VALUE
        sensor_graph_view.minValue = MYO_MIN_VALUE
    }

    override fun showData(data: FloatArray) {
        sensor_graph_view?.addPoint(data)
    }

    override fun startGraph(running: Boolean) {
        sensor_graph_view?.apply {
            this.running = running
        }
    }

    override fun showNoStreamingMessage() {
        text_empty_graph.visibility = View.VISIBLE
    }

    override fun hideNoStreamingMessage() {
        text_empty_graph.visibility = View.INVISIBLE
    }
}
