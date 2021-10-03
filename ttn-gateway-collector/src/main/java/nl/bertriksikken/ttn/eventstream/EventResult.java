package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class EventResult {

    @JsonProperty("result")
    private Event event = new Event();
    
    public Event getEvent() {
        return event;
    }
    
}
