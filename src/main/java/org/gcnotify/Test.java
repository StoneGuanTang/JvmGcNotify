package org.gcnotify;

import org.gcnotify.g1.G1HumongousAllocationEventListener;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws InterruptedException, IOException {
        JvmGcNotify.start();
        JvmGcNotify.register(new G1HumongousAllocationEventListener(5, "e:/"), NotifyEvent.class);

        System.out.println("hello world");

        while (true) {
            init();
        }

    }



    private static String[] init() {
        int size = 10000000;
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = "test" + i;
        }
        return result;
    }
}
