package dao;

import model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {
    
    /**
     * Get all room types from the database
     * @return List of all room types
     */
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> roomTypes = new ArrayList<>();
        String sql = "SELECT room_type_id, type_name FROM room_type ORDER BY room_type_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeId(rs.getInt("room_type_id"));
                roomType.setTypeName(rs.getString("type_name"));
                roomTypes.add(roomType);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving room types: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roomTypes;
    }
    
    /**
     * Get a specific room type by ID
     * @param id The room type ID
     * @return RoomType object if found, null otherwise
     */
    public RoomType getRoomTypeById(int id) {
        String sql = "SELECT room_type_id, type_name FROM room_type WHERE room_type_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeId(rs.getInt("room_type_id"));
                roomType.setTypeName(rs.getString("type_name"));
                return roomType;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving room type by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Add a new room type
     * @param roomType The room type to add
     * @return true if successful, false otherwise
     */
    public boolean addRoomType(RoomType roomType) {
        String sql = "INSERT INTO room_type (type_name) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomType.getTypeName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing room type
     * @param roomType The room type with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateRoomType(RoomType roomType) {
        String sql = "UPDATE room_type SET type_name = ? WHERE room_type_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomType.getTypeName());
            stmt.setInt(2, roomType.getRoomTypeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a room type by ID
     * @param roomTypeId The ID of the room type to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRoomType(int roomTypeId) {
        String sql = "DELETE FROM room_type WHERE room_type_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomTypeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}