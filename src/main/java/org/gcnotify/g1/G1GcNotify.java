package org.gcnotify.g1;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import org.gcnotify.EventPublisher;
import org.gcnotify.GCCause;
import org.gcnotify.NotifyEvent;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class G1GcNotify implements AutoCloseable {
    NotificationListener notify = new Notify();

    private final List<Runnable> notificationListenerCleanUpRunnableList = new CopyOnWriteArrayList<>();

    public void start() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!(gcBean instanceof NotificationEmitter)) {
                continue;
            }
            NotificationEmitter notificationEmitter = (NotificationEmitter) gcBean;
            notificationEmitter.addNotificationListener(notify, notification -> notification.getType()
                    .equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION), null);
            notificationListenerCleanUpRunnableList.add(() -> {
                try {
                    notificationEmitter.removeNotificationListener(notify);
                } catch (ListenerNotFoundException ignore) {
                }
            });
        }
    }

    public void close() {
        notificationListenerCleanUpRunnableList.forEach(Runnable::run);
    }


    class Notify implements NotificationListener {


        @Override
        public void handleNotification(Notification notification, Object handback) {
            CompositeData cd = (CompositeData) notification.getUserData();
            GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);

            String gcCause = notificationInfo.getGcCause();
            String gcAction = notificationInfo.getGcAction();
            GcInfo gcInfo = notificationInfo.getGcInfo();
            long duration = gcInfo.getDuration();
            NotifyEvent event = new NotifyEvent(gcAction, gcInfo, duration, GCCause.getByCode(gcCause));

            // publish event
            EventPublisher.publish(event);
        }
    }
}
