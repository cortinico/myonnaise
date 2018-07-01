package it.ncorti.emgvisualizer.ui.graph

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView


interface GraphContract {

    interface View : BaseView<Presenter> {

        fun showData(data: FloatArray)

    }

    interface Presenter : BasePresenter {

    }
}