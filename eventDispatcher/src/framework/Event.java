package framework;

public interface Event {

    Class<? extends Event> getType();
}
