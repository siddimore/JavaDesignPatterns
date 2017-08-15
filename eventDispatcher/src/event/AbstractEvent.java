package event;

import framework.Event;

public abstract class AbstractEvent implements Event {

    public Class<? extends Event> getType() {
        return getClass();
    }
}
