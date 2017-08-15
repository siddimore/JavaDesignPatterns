package handler;

import event.UserCreatedEvent;
import event.UserUpdatedEvent;
import framework.Handler;

public class UserUpdatedEventHandler implements Handler<UserUpdatedEvent> {


    @Override
    public void onEvent(UserUpdatedEvent event) {
        System.out.print("User '{}' has been Updated!" + event.getUser().getUsername());
    }
}
