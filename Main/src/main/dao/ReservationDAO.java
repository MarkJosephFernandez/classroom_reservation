package dao;

import model.Room;
import model.Reservation;
import main.model.ReservationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean createReservation(int userId, int roomId, Date startDate, Date endDate) {
        String checkSql = "SELECT COUNT(*) FROM reservation " +
                "WHERE room_id=? AND status='approved' AND " +
                "((start_date <= ? AND end_date >= ?) OR " +
                " (start_date <= ? AND end_date >= ?) OR " +
                " (start_date >= ? AND end_date <= ?))";

        String insertSql = "INSERT INTO reservation (user_id, room_id, start_date, end_date) VALUES (?, ?, ?, ? )";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, roomId);
            checkStmt.setDate(2, startDate);
            checkStmt.setDate(3, startDate);
            checkStmt.setDate(4, endDate);
            checkStmt.setDate(5, endDate);
            checkStmt.setDate(6, startDate);
            checkStmt.setDate(7, endDate);


            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Room is already booked
            }

            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, roomId);
            insertStmt.setDate(3, startDate);
            insertStmt.setDate(4, endDate);
            return insertStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getUserReservations(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r. user_id, r.room_id , r.start_date, r.end_date, r.status, rm.room_number, rt.type_name FROM reservation r JOIN room rm ON r.room_id = rm.room_id JOIN room_type rt ON rm.room_type_id = rt.room_type_id WHERE r.user_id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));

                r.setId(rs.getInt("reservation_id")); // ✅ fixed
                r.setUserId(rs.getInt("user_id"));
                r.setRoom(room);
                r.setRoomType(rs.getString("type_name"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setStatus(rs.getString("status"));

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ReservationDTO> getAllReservations() {
        List<ReservationDTO> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, u.full_name, r.room_id, rm.room_number, r.start_date, r.end_date, r.status, rm.room_number FROM reservation r JOIN user u ON u.user_id = r.user_id JOIN room rm ON r.room_id = rm.room_id;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReservationDTO r = new ReservationDTO();
                r.setId(rs.getInt("reservation_id")); // ✅ fixed
                r.setFull_name(rs.getString("full_name"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setStatus(rs.getString("status"));

                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                r.setRoom(room);

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean createReservation(int userId, int roomId, String roomType, Date startDate, Date endDate) {
    String query = "INSERT INTO reservation (user_id, room_id, room_type, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, roomId);
        stmt.setString(3, roomType);
        stmt.setDate(4, startDate);
        stmt.setDate(5, endDate);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}
