package handler;

import event.UserCreatedEvent;
import framework.Handler;

public class UserCreatedEventHandler  implements Handler<UserCreatedEvent>{
    @Override
    public void onEvent(UserCreatedEvent event) {

        System.out.print("User '{}' has been Created!" + event.getUser().getUsername());
    }
}
