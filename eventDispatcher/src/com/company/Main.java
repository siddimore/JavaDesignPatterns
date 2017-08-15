package com.company;

import event.UserCreatedEvent;
import event.UserUpdatedEvent;
import framework.EventDispatcher;
import handler.UserCreatedEventHandler;
import handler.UserUpdatedEventHandler;
import model.User;

public class Main {

    public static void main(String[] args) {
	// write your code here

        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.registerHandler(UserCreatedEvent.class, new UserCreatedEventHandler());
        dispatcher.registerHandler(UserUpdatedEvent.class, new UserUpdatedEventHandler());

        User user = new User("Siddharth More");
        dispatcher.dispatch(new UserCreatedEvent(user));
        dispatcher.dispatch(new UserUpdatedEvent(user));
    }
}
