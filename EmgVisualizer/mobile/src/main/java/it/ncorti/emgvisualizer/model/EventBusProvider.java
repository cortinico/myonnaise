package it.ncorti.emgvisualizer.model;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Class for handling event dispatching to subscriber.
 * <p>
 * The only public method that must be invoked is postOnMainThread to asyncronously dispatch an event
 *
 * @author Nicola
 */
public final class EventBusProvider {
    /**
     * Otto library event bus provider
     */
    private static final Bus BUS = new Bus();

    /**
     * Method for dispatching events on main thread
     *
     * @param event Event to dispatch
     */
    public static void postOnMainThread(final AbstractEvent event) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                BUS.post(event);
            }
        });
    }

    /**
     * Private constructor
     */
    private EventBusProvider() {
    }

    /**
     * Method for registering a new object for event receiving
     *
     * @param obj Obj for receiving events
     */
    public static void register(Object obj){
        BUS.register(obj);
    }

    /**
     * Method for unregistering an object
     *
     * @param obj Object to unregister
     */
    public static void unregister(Object obj){
        BUS.unregister(obj);
    }
}
