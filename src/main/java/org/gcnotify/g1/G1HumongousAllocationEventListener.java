package org.gcnotify.g1;


import org.gcnotify.EventListener;
import org.gcnotify.GCCause;
import org.gcnotify.NotifyEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class G1HumongousAllocationEventListener implements EventListener {

    private AtomicInteger maxDumpCounter = new AtomicInteger();

    private Integer maxDumpCount;

    private String dumpFileDir;

    public G1HumongousAllocationEventListener(Integer maxDumpCount, String dumpFileDir) {
        this.maxDumpCount = maxDumpCount;
        this.dumpFileDir = dumpFileDir;
    }

    private String getDumpFileName(String dir) {
        String dumpNameSuffix = "dump.hprof";
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("目录不存在");
        }
        File[] files = file.listFiles((e, name) -> isDumpName(name));
        if (Objects.isNull(files) || 0 == files.length) {
            return dumpNameSuffix;
        }

        File first = new File(dir + dumpNameSuffix);
        if (1 == files.length && first.exists()) {
            return 1 + dumpNameSuffix;
        }

        List<String> fileNames = Arrays.stream(files).map(File::getName).collect(Collectors.toList()).stream().sorted().collect(Collectors.toList());

        String lastFileName = fileNames.get(fileNames.size() - 2);
        Integer seq = Integer.valueOf(lastFileName.substring(0, 1));
        return (seq + 1) + dumpNameSuffix;
    }

    private boolean isDumpName(String name) {
        String dumpNameSuffix = "dump.hprof";
        if (!name.endsWith(dumpNameSuffix)) {
            return false;
        }
        if (name.equals(dumpNameSuffix)) {
            return true;
        }

        try {
            Integer.parseInt(name.replace(dumpNameSuffix, ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void dump() {
        Runtime rt = Runtime.getRuntime();
        var pid = ProcessHandle.current().pid();
        String dumpFileName = getDumpFileName(dumpFileDir);
        try {
            rt.exec(String.format("jmap -dump:live,format=b,file=%s%s %s", dumpFileDir, dumpFileName, pid));
        } catch (IOException e) {
            System.err.println("dump error" + dumpFileDir + dumpFileName);
        }
    }

    public void onEvent(NotifyEvent event) {
        if (GCCause._g1_humongous_allocation == event.getGcCause()) {
            System.out.println(event);
            if (maxDumpCounter.getAndIncrement() <= maxDumpCount) {
                System.out.println("start dump");
                dump();
            }
        }

    }


    @Override
    public void onEvent(Object event) {
        onEvent((NotifyEvent) event);
    }


}
