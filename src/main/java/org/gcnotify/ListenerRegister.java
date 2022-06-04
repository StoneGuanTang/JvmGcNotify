package org.gcnotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerRegister {

    private final static Map<EventListener, Class<?>> map = new HashMap<>();

    public static void register(EventListener eventListener, Class<?> event) {
        map.put(eventListener, event);
    }

    public static List<EventListener> findEventListener(Object event) {
        List<EventListener> result = new ArrayList<>();
        map.forEach(((eventListener, data) -> {
            if (data.equals(event.getClass())) {
                result.add(eventListener);
            }
        }));
        return result;
    }
}
