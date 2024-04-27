package main.java.elementalmp4.utils;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private int idx;
    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
        this.idx = 0;
    }

    private String getNameFormatted() {
        idx++;
        return "sebutils-%s-%d".formatted(name, idx);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(getNameFormatted());
        return t;
    }
}