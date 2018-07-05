package it.ncorti.emgvisualizer

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment<T : BasePresenter> : Fragment(), BaseView<T> {

    var presenter: T? = null

    override fun attachPresenter(presenter: T) {
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