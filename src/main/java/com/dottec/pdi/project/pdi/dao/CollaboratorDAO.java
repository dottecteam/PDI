package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Collaborator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CollaboratorDAO {

    public void save(Collaborator collaborator){
        Database db = null;
        try {
            db = new Database();
            Connection connection = db.getConnection();

            String sql = "INSERT INTO collaborators (" +
                    "col_name, " +
                    "col_cpf, " +
                    "col_email, " +
                    "col_status, " +
                    "col_created_at, " +
                    "department_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, collaborator.getName());
            stmt.setString(2, collaborator.getCpf());
            stmt.setString(3, collaborator.getEmail());
            stmt.setString(4, collaborator.getStatus().toString());
            stmt.setInt(5, collaborator.getDepartment());

            System.out.println("Adicionado no Banco!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}