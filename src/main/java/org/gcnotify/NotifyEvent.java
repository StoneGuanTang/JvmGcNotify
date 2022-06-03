package org.gcnotify;

import com.sun.management.GcInfo;
import org.gcnotify.GCCause;

import java.io.Serializable;

public class NotifyEvent implements Serializable {

    private String gcAction;

    private GcInfo gcInfo;

    private long duration;

    private GCCause gcCause;

    public NotifyEvent(String gcAction, GcInfo gcInfo, long duration, GCCause gcCause) {
        this.gcAction = gcAction;
        this.gcInfo = gcInfo;
        this.duration = duration;
        this.gcCause = gcCause;
    }

    public String getGcAction() {
        return gcAction;
    }

    public GcInfo getGcInfo() {
        return gcInfo;
    }

    public long getDuration() {
        return duration;
    }

    public GCCause getGcCause() {
        return gcCause;
    }

    @Override
    public String toString() {
        return "NotifyEvent{" +
                "gcAction='" + gcAction + '\'' +
                ", gcInfo=" + gcInfo +
                ", duration=" + duration +
                ", gcCause=" + gcCause.value() +
                '}';
    }
}
