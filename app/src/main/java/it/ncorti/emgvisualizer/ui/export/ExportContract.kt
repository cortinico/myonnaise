package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView


interface ExportContract {

    interface View : BaseView<Presenter> {

        fun showCollectionStarted()

        fun showCollectionStopped()

        fun showCollectedPoints(totalPoints: Int)

        fun enableResetButton()

        fun disableResetButton()

        fun hideSaveArea()

        fun showSaveArea()

        fun showSavedToCsvMessage()

    }

    interface Presenter : BasePresenter {

        fun onCollectionTogglePressed()

        fun onResetPressed()

        fun onSavePressed()

        fun onSharePressed()

    }
}