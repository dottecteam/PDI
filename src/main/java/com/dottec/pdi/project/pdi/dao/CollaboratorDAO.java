package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Collaborator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.dottec.pdi.project.pdi.enums.Status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollaboratorDAO {

    public void save(Collaborator collaborator) {
        Database db = null;
        try {
            db = new Database();
            Connection connection = db.getConnection();

            // Revisar os campos
            String sql = "INSERT INTO collaborators (" +
                    "col_name, " +
                    "col_cpf, " +
                    "col_email, " +
                    "col_status, " +
                    "col_created_at, " +
                    "department_id) " +
                    "VALUES (?, ?, ?, ?, NOW(), ?);";

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, collaborator.getName());
            stmt.setString(2, collaborator.getCpf());
            stmt.setString(3, collaborator.getEmail());
            stmt.setString(4, collaborator.getStatus().toString());
            stmt.setInt(5, collaborator.getDepartment());

            stmt.executeUpdate();

            System.out.println("Adicionado no Banco!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collaborator findById(int id) {
        Database db = null;
        Collaborator collaborator = null;
        try {
            db = new Database();
            Connection connection = db.getConnection();


            String sql = "SELECT * FROM collaborators WHERE col_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            // Revisar os campos
            if (rs.next()) {
                collaborator = new Collaborator();
                collaborator.setId(rs.getInt("col_id"));
                collaborator.setName(rs.getString("col_name"));
                collaborator.setCpf(rs.getString("col_cpf"));
                collaborator.setEmail(rs.getString("col_email"));
                collaborator.setStatus(Status.valueOf(rs.getString("col_status")));
//                collaborator.setCreatedAt(rs.getTimestamp("col_created_at").toLocalDateTime());
                collaborator.setDepartment(rs.getInt("department_id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collaborator;
    }

    public List<Collaborator> findAll() {
        Database db = null;
        List<Collaborator> collaborators = new ArrayList<>();
        try {
            db = new Database();
            Connection connection = db.getConnection();

            String sql = "SELECT * FROM collaborators";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Collaborator collaborator = new Collaborator();
                collaborator.setId(rs.getInt("col_id"));
                collaborator.setName(rs.getString("col_name"));
                collaborator.setCpf(rs.getString("col_cpf"));
                collaborator.setEmail(rs.getString("col_email"));
                collaborator.setStatus(Status.valueOf(rs.getString("col_status")));
//                collaborator.setCreatedAt(rs.getTimestamp("col_created_at").toLocalDateTime());
                collaborator.setDepartment(rs.getInt("department_id"));
                collaborators.add(collaborator);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collaborators;
    }

    public List<Collaborator> findByName(String name) {
        Database db = null;
        List<Collaborator> collaborators = new ArrayList<>();
        try {
            db = new Database();
            Connection connection = db.getConnection();

            String sql = "SELECT * FROM collaborators WHERE col_name LIKE ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Collaborator collaborator = new Collaborator();
                collaborator.setId(rs.getInt("col_id"));
                collaborator.setName(rs.getString("col_name"));
                collaborator.setCpf(rs.getString("col_cpf"));
                collaborator.setEmail(rs.getString("col_email"));
                collaborator.setStatus(Status.valueOf(rs.getString("col_status")));
//                collaborator.setCreatedAt(rs.getTimestamp("col_created_at").toLocalDateTime());
                collaborator.setDepartment(rs.getInt("department_id"));
                collaborators.add(collaborator);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collaborators;
    }

    public void update(Collaborator collaborator) {
        Database db = null;
        try {
            db = new Database();
            Connection connection = db.getConnection();

            // Revisar os campos
            String sql = "UPDATE collaborators SET " +
                    "col_name = ?, " +
                    "col_cpf = ?, " +
                    "col_email = ?, " +
                    "col_status = ?, " +
                    "department_id = ? " +
                    "WHERE col_id = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, collaborator.getName());
            stmt.setString(2, collaborator.getCpf());
            stmt.setString(3, collaborator.getEmail());
            stmt.setString(4, collaborator.getStatus().toString());
            stmt.setInt(5, collaborator.getDepartment());
            stmt.setInt(6, collaborator.getId());

            stmt.executeUpdate();
            System.out.println("Colaborador atualizado!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        Database db = null;
        try {
            db = new Database();
            Connection connection = db.getConnection();

            String sql = "DELETE FROM collaborators WHERE col_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
            System.out.println("Colaborador deletado!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
