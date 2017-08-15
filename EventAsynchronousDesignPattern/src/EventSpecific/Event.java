package EventSpecific;

import AllInterfaces.IEvent;
import AllInterfaces.ThreadCompleteListener;

//import java.util.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * Each EventSpecific.Event runs as a separate/individual thread.
 *
 */
public class Event implements IEvent, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

    private int eventId;
    private int eventTime;
    private boolean isSynchronous;
    private Thread thread;
    private boolean isComplete = false;
    private ThreadCompleteListener eventListener;

    /**
     *
     * @param eventId event ID
     * @param eventTime event time
     * @param isSynchronous is of synchronous type
     */
    public Event(final int eventId, final int eventTime, final boolean isSynchronous) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.isSynchronous = isSynchronous;
    }

    public boolean isSynchronous() {
        return isSynchronous;
    }

    @Override
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {
        if (null == thread) {
            return;
        }
        thread.interrupt();
    }

    @Override
    public void status() {
        if (!isComplete) {
            System.out.print("[{}] is not done." + eventId);
        } else {
            System.out.print("[{}] is done." + eventId);
        }
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + (eventTime * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000); // Sleep for 1 second.
            } catch (InterruptedException e) {
                return;
            }
        }
        isComplete = true;
        completed();
    }

    public final void addListener(final ThreadCompleteListener listener) {
        this.eventListener = listener;
    }

    public final void removeListener(final ThreadCompleteListener listener) {
        this.eventListener = null;
    }

    private final void completed() {
        if (eventListener != null) {
            eventListener.completedEventHandler(eventId);
        }
    }

}