package main.java.elementalmp4.sebutils.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class Session {
    private final UUID id;
    private final Instant expiresAt;

    public Session(ResultSet rs) throws SQLException {
        this.id = (UUID) rs.getObject("id");
        this.expiresAt = rs.getTimestamp("expires_at").toInstant();
    }

    public UUID getId() {
        return id;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}