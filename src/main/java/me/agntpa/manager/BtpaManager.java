package me.agntpa.manager;

import me.agntpa.AgnTpa;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BtpaManager {

    private final Connection connection;

    public BtpaManager() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + AgnTpa.getInstance().getDataFolder() + "/btpa.db");
        createTable();
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS btpa (player_uuid TEXT, buddy_uuid TEXT, PRIMARY KEY(player_uuid, buddy_uuid))");
        }
    }

    public void addBuddy(UUID player, UUID buddy) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO btpa(player_uuid, buddy_uuid) VALUES(?, ?)")) {
            ps.setString(1, player.toString());
            ps.setString(2, buddy.toString());
            ps.executeUpdate();
        }
    }

    public void removeBuddy(UUID player, UUID buddy) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM btpa WHERE player_uuid=? AND buddy_uuid=?")) {
            ps.setString(1, player.toString());
            ps.setString(2, buddy.toString());
            ps.executeUpdate();
        }
    }

    public Set<UUID> getBuddies(UUID player) throws SQLException {
        Set<UUID> buddies = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT buddy_uuid FROM btpa WHERE player_uuid=?")) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    buddies.add(UUID.fromString(rs.getString("buddy_uuid")));
                }
            }
        }
        return buddies;
    }

    public boolean isBuddy(UUID player, UUID buddy) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM btpa WHERE player_uuid=? AND buddy_uuid=?")) {
            ps.setString(1, player.toString());
            ps.setString(2, buddy.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
