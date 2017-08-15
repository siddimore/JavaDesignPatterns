package Manager;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import AllInterfaces.ThreadCompleteListener;
import EventSpecific.Event;
import EventSpecific.Exceptions.EventDoesNotExistException;
import EventSpecific.Exceptions.InvalidOperationException;
import EventSpecific.Exceptions.LongRunningEventException;
import EventSpecific.Exceptions.MaxNumOfEventsAllowedException;

public class EventManager implements ThreadCompleteListener {

    public static final int MAX_RUNNING_EVENTS = 1000; // Just don't wanna have too many running events. :)
    public static final int MIN_ID = 1;
    public static final int MAX_ID = MAX_RUNNING_EVENTS;
    public static final int MAX_EVENT_TIME = 1800; // in seconds / 30 minutes.
    private int currentlyRunningSyncEvent = -1;
    private Random rand;
    private Map<Integer, Event> eventPool;

    /**
     * EventManager constructor.
     */
    public EventManager() {
        rand = new Random(1);
        eventPool = new ConcurrentHashMap<Integer, Event>(MAX_RUNNING_EVENTS);

    }

    /**
     * Create a Synchronous event.
     *
     * @param eventTime Time an event should run for.
     * @return eventId
     * @throws MaxNumOfEventsAllowedException When too many events are running at a time.
     * @throws InvalidOperationException      No new synchronous events can be created when one is already running.
     * @throws LongRunningEventException      Long running events are not allowed in the app.
     */
    public int create(int eventTime)
            throws MaxNumOfEventsAllowedException, InvalidOperationException, LongRunningEventException {
        if (currentlyRunningSyncEvent != -1) {
            throw new InvalidOperationException(
                    "Event [" + currentlyRunningSyncEvent + "] is still running. Please wait until it finishes and try again.");
        }

        int eventId = createEvent(eventTime, true);
        currentlyRunningSyncEvent = eventId;

        return eventId;
    }

    /**
     * Create an Asynchronous event.
     *
     * @param eventTime Time an event should run for.
     * @return eventId
     * @throws MaxNumOfEventsAllowedException When too many events are running at a time.
     * @throws LongRunningEventException      Long running events are not allowed in the app.
     */
    public int createAsync(int eventTime) throws MaxNumOfEventsAllowedException, LongRunningEventException {
        return createEvent(eventTime, false);
    }

    private int createEvent(int eventTime, boolean isSynchronous)
            throws MaxNumOfEventsAllowedException, LongRunningEventException {
        if (eventPool.size() == MAX_RUNNING_EVENTS) {
            throw new MaxNumOfEventsAllowedException("Too many events are running at the moment. Please try again later.");
        }

        if (eventTime >= MAX_EVENT_TIME) {
            throw new LongRunningEventException(
                    "Maximum event time allowed is " + MAX_EVENT_TIME + " seconds. Please try again.");
        }

        int newEventId = generateId();

        Event newEvent = new Event(newEventId, eventTime, isSynchronous);
        newEvent.addListener(this);
        eventPool.put(newEventId, newEvent);

        return newEventId;
    }

    /**
     * Starts event.
     *
     * @param eventId The event that needs to be started.
     * @throws EventDoesNotExistException If event does not exist in our eventPool.
     */
    public void start(int eventId) throws EventDoesNotExistException {
        if (!eventPool.containsKey(eventId)) {
            throw new EventDoesNotExistException(eventId + " does not exist.");
        }

        eventPool.get(eventId).start();
    }

    /**
     * Stops event.
     *
     * @param eventId The event that needs to be stopped.
     * @throws EventDoesNotExistException If event does not exist in our eventPool.
     */
    public void cancel(int eventId) throws EventDoesNotExistException {
        if (!eventPool.containsKey(eventId)) {
            throw new EventDoesNotExistException(eventId + " does not exist.");
        }

        if (eventId == currentlyRunningSyncEvent) {
            currentlyRunningSyncEvent = -1;
        }

        eventPool.get(eventId).stop();
        eventPool.remove(eventId);
    }

    /**
     * Get status of a running event.
     *
     * @param eventId The event to inquire status of.
     * @throws EventDoesNotExistException If event does not exist in our eventPool.
     */
    public void status(int eventId) throws EventDoesNotExistException {
        if (!eventPool.containsKey(eventId)) {
            throw new EventDoesNotExistException(eventId + " does not exist.");
        }

        eventPool.get(eventId).status();
    }

    /**
     * Gets status of all running events.
     */
    @SuppressWarnings("rawtypes")
    public void statusOfAllEvents() {
        Iterator it = eventPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ((Event) pair.getValue()).status();
        }
    }

    /**
     * Stop all running events.
     */
    @SuppressWarnings("rawtypes")
    public void shutdown() {
        Iterator it = eventPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ((Event) pair.getValue()).stop();
        }
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive. The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     */
    private int generateId() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((MAX_ID - MIN_ID) + 1) + MIN_ID;
        while (eventPool.containsKey(randomNum)) {
            randomNum = rand.nextInt((MAX_ID - MIN_ID) + 1) + MIN_ID;
        }

        return randomNum;
    }

    /**
     * Callback from an {@link Event} (once it is complete). The Event is then removed from the pool.
     */
    @Override
    public void completedEventHandler(int eventId) {
        eventPool.get(eventId).status();
        if (eventPool.get(eventId).isSynchronous()) {
            currentlyRunningSyncEvent = -1;
        }
        eventPool.remove(eventId);
    }

    /**
     * Getter method for event pool.
     */
    public Map<Integer, Event> getEventPool() {
        return eventPool;
    }

    /**
     * Get number of currently running Synchronous events.
     */
    public int numOfCurrentlyRunningSyncEvent() {
        return currentlyRunningSyncEvent;
    }
}
