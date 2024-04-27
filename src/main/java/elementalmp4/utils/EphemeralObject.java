package main.java.elementalmp4.utils;

public abstract class EphemeralObject {

    long expiryTime;

    EphemeralObject(long lifespan) {
        this.expiryTime = System.currentTimeMillis() + lifespan;
    }

    public boolean expired() {
        boolean expired = expiryTime < System.currentTimeMillis();
        if (expired) whenExpired();
        return expired;
    }

    public abstract void whenExpired();

}
