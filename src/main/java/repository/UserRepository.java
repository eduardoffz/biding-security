package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dto.UserDto;

public class UserRepository {

    private Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
    }

    // Método para registrar usuário
    public void register(UserDto user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha na atualização: nenhum registro inserido.");
            }
        }
    }

    //   login
    public UserDto logar(String email, String senha) throws SQLException {
        String sql = "SELECT name, email, password, role FROM users WHERE email = ? AND password = ?";
        UserDto user = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    user = new UserDto();
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                }
            }
        }
        return user; // retorna null se não encontrar
    }
}
