package it.ncorti.emgvisualizer

interface BaseView<T : BasePresenter> {

    fun attachPresenter(presenter: T)

}