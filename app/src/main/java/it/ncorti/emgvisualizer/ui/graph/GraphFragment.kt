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
import it.ncorti.emgvisualizer.databinding.LayoutGraphBinding
import javax.inject.Inject

class GraphFragment : BaseFragment<GraphContract.Presenter>(), GraphContract.View {

    private lateinit var binding: LayoutGraphBinding

    companion object {
        fun newInstance() = GraphFragment()
    }

    @Inject
    lateinit var graphPresenter: GraphPresenter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        attachPresenter(graphPresenter)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutGraphBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sensorGraphView.channels = MYO_CHANNELS
        binding.sensorGraphView.maxValue = MYO_MAX_VALUE
        binding.sensorGraphView.minValue = MYO_MIN_VALUE
    }

    override fun showData(data: FloatArray) {
        binding.sensorGraphView.addPoint(data)
    }

    override fun startGraph(running: Boolean) {
        binding.sensorGraphView.apply {
            this.running = running
        }
    }

    override fun showNoStreamingMessage() {
        binding.textEmptyGraph.visibility = View.VISIBLE
    }

    override fun hideNoStreamingMessage() {
        binding.textEmptyGraph.visibility = View.INVISIBLE
    }
}
