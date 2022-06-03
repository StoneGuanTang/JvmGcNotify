package org.gcnotify;

public class EventPublisher {

    public static void publish(Object event) {
        ListenerRegister.findEventListener(event).forEach(eventListener -> eventListener.onEvent(event));
    }
}
