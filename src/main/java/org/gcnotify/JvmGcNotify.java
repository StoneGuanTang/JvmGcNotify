package org.gcnotify;

import org.gcnotify.g1.G1GcNotify;
import org.gcnotify.g1.G1HumongousAllocationEventListener;

public class JvmGcNotify {

    public static void start() {
       new G1GcNotify().start();
    }

    public static void register(EventListener eventListener, Class<?> event) {
        ListenerRegister.register(eventListener, event);
    }
}
