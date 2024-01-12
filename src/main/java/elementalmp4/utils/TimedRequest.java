package main.java.elementalmp4.utils;

public abstract class TimedRequest {

    long expiryTime;

    TimedRequest(long lifespan) {
        this.expiryTime = System.currentTimeMillis() + lifespan;
    }

    public boolean expired() {
        return expiryTime < System.currentTimeMillis();
    }

}
