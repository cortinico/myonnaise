package it.ncorti.emgvisualizer

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment<P: BasePresenter<BaseView>> : Fragment(), BaseView {

    open var presenter: P? = null

    fun attachPresenter(presenter: P) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter?.create()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter?.start()
        } else {
            presenter?.stop()
        }
    }

}