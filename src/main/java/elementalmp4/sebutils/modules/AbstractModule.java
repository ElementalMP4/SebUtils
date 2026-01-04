package main.java.elementalmp4.sebutils.modules;

public abstract class AbstractModule {

    private boolean running = false;

    public final synchronized void start() {
        if (running) return;
        onStart();
        running = true;
    }

    public final synchronized void stop() {
        if (!running) return;
        onStop();
        running = false;
    }

    public final boolean isRunning() {
        return running;
    }

    protected abstract void onStart();
    protected abstract void onStop();
}

