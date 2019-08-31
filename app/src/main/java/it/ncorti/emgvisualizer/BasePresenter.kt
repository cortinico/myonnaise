package it.ncorti.emgvisualizer

abstract class BasePresenter<V : BaseView>(open val view: V) {

    abstract fun create()

    abstract fun start()

    abstract fun stop()
}