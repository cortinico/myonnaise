package it.ncorti.emgvisualizer.ui.graph

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView

interface GraphContract {

    interface View : BaseView {

        fun showData(data: FloatArray)

        fun startGraph(running: Boolean)

        fun hideNoStreamingMessage()

        fun showNoStreamingMessage()
    }

    abstract class Presenter(override val view: BaseView) : BasePresenter<BaseView>(view)
}
