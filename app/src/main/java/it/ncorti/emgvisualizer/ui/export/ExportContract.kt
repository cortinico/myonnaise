package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView

interface ExportContract {

    interface View : BaseView {
        fun enableStartCollectingButton()

        fun disableStartCollectingButton()

        fun showNotStreamingErrorMessage()

        fun showCollectionStarted()

        fun showCollectionStopped()

        fun showCollectedPoints(totalPoints: Int)

        fun enableResetButton()

        fun disableResetButton()

        fun hideSaveArea()

        fun showSaveArea()

        fun saveCsvFile(content: String)

        fun sharePlainText(content: String)
    }

    abstract class Presenter(override val view: BaseView) : BasePresenter<BaseView>(view) {

        abstract fun onCollectionTogglePressed()

        abstract fun onResetPressed()

        abstract fun onSavePressed()

        abstract fun onSharePressed()
    }
}
