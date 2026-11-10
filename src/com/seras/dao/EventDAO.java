package com.seras.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.seras.util.DBConnection;

public class EventDAO {

    // 🔥 ADD EVENT WITH CONFLICT CHECK
    public void addEvent(String name, int resourceId, String date, String start, String end, String priority, String status)
            throws com.seras.exception.ScheduleConflictException {

        try {
            Connection conn = DBConnection.getConnection();

            String checkQuery = "SELECT * FROM events WHERE resource_id=? AND date=?";
            PreparedStatement checkPs = conn.prepareStatement(checkQuery);

            checkPs.setInt(1, resourceId);
            checkPs.setString(2, date);

            ResultSet rs = checkPs.executeQuery();

            while (rs.next()) {

                String existingStart = rs.getString("start_time");
                String existingEnd = rs.getString("end_time");
                String existingPriority = rs.getString("priority");

                boolean overlap = !(end.compareTo(existingStart) <= 0 || start.compareTo(existingEnd) >= 0);

                if (overlap) {
                    if (priority.equals("HIGH") && !existingPriority.equals("HIGH")) {
                        continue;
                    } else {
                        throw new com.seras.exception.ScheduleConflictException(
                                "Conflict with existing " + existingPriority + " priority event");
                    }
                }
            }

            String query = "INSERT INTO events (event_name, resource_id, date, start_time, end_time, priority, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, name);
            ps.setInt(2, resourceId);
            ps.setString(3, date);
            ps.setString(4, start);
            ps.setString(5, end);
            ps.setString(6, priority);
            ps.setString(7, status);

            ps.executeUpdate();

        } catch (com.seras.exception.ScheduleConflictException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 TOTAL EVENTS
    public int getTotalEvents() {
        int count = 0;
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM events";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // 🔥 CONFLICT COUNT
    public int getConflictCount() {
        int count = 0;
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM events e1 JOIN events e2 " +
                    "ON e1.resource_id = e2.resource_id AND e1.id != e2.id " +
                    "AND e1.date = e2.date " +
                    "AND e1.start_time < e2.end_time AND e1.end_time > e2.start_time";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) count = rs.getInt(1) / 2;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // 🔥 GET ALL EVENTS
    public ArrayList<String[]> getAllEvents() {

        ArrayList<String[]> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM events";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String[] row = new String[7];

                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("event_name");
                row[2] = String.valueOf(rs.getInt("resource_id"));
                row[3] = rs.getString("date");
                row[4] = rs.getString("start_time") + " - " + rs.getString("end_time");
                row[5] = rs.getString("priority");
                row[6] = rs.getString("status");

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 ONLY APPROVED EVENTS
    public ArrayList<String[]> getApprovedEvents() {

        ArrayList<String[]> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM events WHERE status='APPROVED'";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String[] row = new String[7];

                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("event_name");
                row[2] = String.valueOf(rs.getInt("resource_id"));
                row[3] = rs.getString("date");
                row[4] = rs.getString("start_time") + " - " + rs.getString("end_time");
                row[5] = rs.getString("priority");
                row[6] = rs.getString("status");

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 TODAY EVENTS
    public int getTodayEvents() {

        int count = 0;

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM events WHERE date = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) count = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // 🔥 RESOURCE AVAILABILITY
    public boolean isResourceAvailable(int resourceId, String date, String start, String end) {

        boolean available = true;

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM events WHERE resource_id=? AND date=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, resourceId);
            ps.setString(2, date);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String existingStart = rs.getString("start_time");
                String existingEnd = rs.getString("end_time");

                if (!(end.compareTo(existingStart) <= 0 || start.compareTo(existingEnd) >= 0)) {
                    available = false;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return available;
    }

    // 🔥 DELETE EVENT
    public void deleteEvent(int id) {

        try {
            Connection conn = DBConnection.getConnection();
            String query = "DELETE FROM events WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 FIXED: NON APPROVED EVENTS (REQUEST TAB)
    public ArrayList<String[]> getPendingEvents() {

        ArrayList<String[]> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM events WHERE status!='APPROVED'";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String[] row = new String[7];

                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("event_name");
                row[2] = String.valueOf(rs.getInt("resource_id"));
                row[3] = rs.getString("date");
                row[4] = rs.getString("start_time") + " - " + rs.getString("end_time");
                row[5] = rs.getString("priority");
                row[6] = rs.getString("status");

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 UPDATE STATUS
    public void updateStatus(int id, String status) {

        try {
            Connection conn = DBConnection.getConnection();
            String query = "UPDATE events SET status=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 CREATE NOTIFICATION
    public void createNotification(int eventId, String message, String start, String end) {

        try {
            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO notifications (event_id, message, suggested_start, suggested_end, status) VALUES (?, ?, ?, ?, 'PENDING')";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, eventId); // 🔥 MUST NOT BE NULL
            ps.setString(2, message);
            ps.setString(3, start);
            ps.setString(4, end);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getEventsByResource(int resourceId) {

        ArrayList<String[]> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM events WHERE resource_id=? AND status='APPROVED'";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, resourceId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String[] row = new String[5];

                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("event_name");
                row[2] = String.valueOf(rs.getInt("resource_id"));
                row[3] = rs.getString("date");
                row[4] = rs.getString("start_time") + " - " + rs.getString("end_time");

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public ArrayList<String[]> getResourceUsage() {

        ArrayList<String[]> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT resource_id, COUNT(*) as count FROM events GROUP BY resource_id";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String[] row = new String[2];

                row[0] = String.valueOf(rs.getInt("resource_id"));
                row[1] = String.valueOf(rs.getInt("count"));

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    // 🔥 CORE: ADMIN CONTROLLED RESCHEDULE SYSTEM
    public void sendRescheduleSuggestions(int eventId, int resourceId, String date,
            String start, String end,
            String newStart, String newEnd) {

    	try {
    		Connection conn = DBConnection.getConnection();

    		// 🔥 ALWAYS CREATE NOTIFICATION FOR THIS EVENT
	String msg = "Suggested new time: " + newStart + " - " + newEnd;

	String insert = "INSERT INTO notifications (event_id, message, suggested_start, suggested_end, status) VALUES (?, ?, ?, ?, 'PENDING')";
	PreparedStatement ps = conn.prepareStatement(insert);

	ps.setInt(1, eventId);
	ps.setString(2, msg);
	ps.setString(3, newStart);
	ps.setString(4, newEnd);

	ps.executeUpdate();

	} catch (Exception e) {
		e.printStackTrace();
		}
    }
}