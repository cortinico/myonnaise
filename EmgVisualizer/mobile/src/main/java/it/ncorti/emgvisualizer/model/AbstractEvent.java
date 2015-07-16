package it.ncorti.emgvisualizer.model;

/**
 * Abstract class representing a generic event
 *
 * @author Nicola
 */
public abstract class AbstractEvent {

    /**
     * Time stamp of event generatior
     */
    private long timeStamp;

    /**
     * Generic public constructor. Timestamp is recorded at method invocation
     *
     */
    public AbstractEvent() {
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * Generic public constructor with a specific timestamp
     *
     * @param timeStamp Event generation timestamp
     */
    public AbstractEvent(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter for timestamp
     *
     * @return Timestamp reference
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Abstract method for event firing
     */
    public abstract void fireEvent();
}
