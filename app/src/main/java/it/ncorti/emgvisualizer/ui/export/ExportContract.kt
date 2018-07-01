package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView


interface ExportContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

    }
}