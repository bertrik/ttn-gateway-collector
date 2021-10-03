package nl.bertriksikken.ttngatewaycollector;

import nl.bertriksikken.ttn.eventstream.Event;

/**
 * Callback for events as they are received over the event stream.
 */
public interface IEventStreamCallback {
    
    // notifies of a received event
    public void eventReceived(Event  event);

}
