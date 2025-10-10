package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Goal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class GoalDAO
{
    private final Database db;
    
    public GoalDAO() throws SQLException {
        this.db = new Database();
    }



    public void create(Goal goal)
    {
        String sql = """
        INSERT INTO goals (name, description, deadline,
        tag, employeeId, collaboratorStatus) VALUES (?, ?, ?, ?, ?, ?)
        """;
       
        try (Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, goal.getName());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getDeadline());
            stmt.setString(4, goal.getCategory());
            stmt.setInt(5, goal.getEmployeeId());
            stmt.setString(6, goal.getStatus());

            stmt.executeUpdate();
            System.out.println("goal created.");
        }

        catch (SQLException e)
        {
            System.err.println("error when creating goal: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    private Goal extractGoalFromResultSet(ResultSet rs) throws SQLException
    {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String deadline = rs.getString("deadline");
        String tag = rs.getString("tag");
        int employeeId = rs.getInt("employeeId");
        String collaboratorStatus = rs.getString("collaboratorStatus");

        return new Goal(id, name, description, deadline, tag, employeeId, collaboratorStatus);
    }



    public Goal readById(int id)
    {
        String sql = "SELECT * FROM goals WHERE id = ?";

        try (Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return extractGoalFromResultSet(rs);
                }
            }
        }

        catch (SQLException e)
        {
            System.err.println("error when reading goal by id: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }



    public List<Goal> readAll()
    {
        List<Goal> goals = new ArrayList<>();
        
        String sql = "SELECT * FROM goals";

        try (Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                goals.add(extractGoalFromResultSet(rs));
            }
        }

        catch (SQLException e)
        {
            System.err.println("error when reading all goals: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return goals;
    }



    public void update(Goal goal)
    {
        String sql = """
        UPDATE goals SET name = ?, description = ?, deadline = ?,
        tag = ?, employeeId = ?, collaboratorStatus = ? WHERE id = ?
        """;

        try (Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, goal.getName());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getDeadline());
            stmt.setString(4, goal.getCategory());
            stmt.setInt(5, goal.getEmployeeId());
            stmt.setString(6, goal.getStatus());
            stmt.setInt(7, goal.getId());

            stmt.executeUpdate();
            System.out.println("goal updated.");
        }

        catch (SQLException e)
        {
            System.err.println("error when updating goal: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    public void delete(int id)
    {
        String sql = "DELETE FROM goals WHERE id = ?";

        try (Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            stmt.executeUpdate();
            System.out.println("goal deleted.");
        }

        catch (SQLException e)
        {
            System.err.println("error when deleting goal: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
