package com.seras.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.seras.model.Resource;
import com.seras.util.DBConnection;

public class ResourceDAO {

    // ADD RESOURCE
    public void addResource(String name, String type) {

        try {
            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO resources (name, type) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, type);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteResource(int id) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "DELETE FROM resources WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getTotalResources() {

        int count = 0;

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT COUNT(*) FROM resources";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    
    // GET ALL RESOURCES
    public ArrayList<Resource> getAllResources() {

        ArrayList<Resource> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM resources";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Resource r = new Resource(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type")
                );
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}