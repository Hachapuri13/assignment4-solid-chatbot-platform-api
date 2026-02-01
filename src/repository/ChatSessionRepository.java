package repository;

import data.interfaces.IDB;
import model.Bot;
import model.User;
import model.ChatSession;
import repository.interfaces.CrudRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatSessionRepository implements CrudRepository<ChatSession> {
    private final IDB db;

    public ChatSessionRepository(IDB db) {
        this.db = db;
    }

    @Override
    public void create(ChatSession session) throws SQLException {
        String sql = "INSERT INTO chat_sessions (bot_id, user_id, started_at, total_tokens_used) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getBot().getId());
            pstmt.setInt(2, session.getUser().getId());

            pstmt.setTimestamp(3, new Timestamp(session.getStartedAt().getTime()));

            pstmt.setInt(4, session.getTotalTokensUsed());

            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ChatSession> getAll() throws SQLException {
        String sql = "SELECT * FROM chat_sessions";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<ChatSession> sessions = new ArrayList<>();
            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
            return sessions;
        }
    }

    @Override
    public ChatSession getById(int id) throws SQLException {
        String sql = "SELECT * FROM chat_sessions WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSession(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean update(ChatSession session) throws SQLException {
        String sql = "UPDATE chat_sessions SET total_tokens_used = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getTotalTokensUsed());
            pstmt.setInt(2, session.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM chat_sessions WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private ChatSession mapRowToSession(ResultSet rs) throws SQLException {
        Bot botStub = new Bot(rs.getInt("bot_id"), "Unknown", "", "", 0);
        User userStub = new User(rs.getInt("user_id"), "Unknown", "", false);

        return new ChatSession(
                rs.getInt("id"),
                botStub,
                userStub,
                rs.getTimestamp("started_at"),
                rs.getInt("total_tokens_used")
        );
    }
}