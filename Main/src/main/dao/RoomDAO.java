package dao;

import model.Room;
import model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RoomDAO {
    
    /**
     * Retrieve a room by its ID with type information
     * @param id The room ID to search for
     * @return Room object if found, null otherwise
     */
    public Room getRoomById(int id) {
        String sql = "SELECT r.*, rt.type_name FROM room r " +
                    "JOIN room_type rt ON r.room_type_id = rt.room_type_id " +
                    "WHERE r.room_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setCapacity(rs.getInt("capacity"));
                room.setRoomTypeId(rs.getInt("room_type_id"));
                room.setType(rs.getString("type_name"));
                return room;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving room by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new room to the database
     * @param room The room object to add
     * @return true if successful, false otherwise
     */
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO room (room_number, capacity, room_type_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setInt(3, room.getRoomTypeId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing room in the database
     * @param room The room object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateRoom(Room room) {
        String sql = "UPDATE room SET room_number = ?, capacity = ?, room_type_id = ? WHERE room_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setInt(4, room.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    /**
     * Delete a room by its ID
     * @param roomId The ID of the room to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM room WHERE room_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
    }

   
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.room_number, r.capacity, rt.room_type_id, rt.type_name " +
                    "FROM room r JOIN room_type rt ON r.room_type_id = rt.room_type_id " +
                    "ORDER BY r.room_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setCapacity(rs.getInt("capacity"));
                room.setRoomTypeId(rs.getInt("room_type_id"));
                room.setType(rs.getString("type_name"));
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all rooms: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    /**
     * Initialize default room types if the room_types table is empty
     * Improved version with better error handling and debugging
     */
    public void initializeDefaultRoomTypes() {
        String checkSql = "SELECT COUNT(*) FROM room_type";
        String insertSql = "INSERT INTO room_type (room_type_id, type_name) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if room_types table is empty
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 ResultSet rs = checkStmt.executeQuery()) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Room types table is empty. Inserting default types...");
                    
                    // Prepare default room types
                    Object[][] defaultTypes = {
                        {1, "GREY AVR"},
                       {2, "MAROON AVR"},
                       {3, "BLUE AVA"},
                       {4, "YELLOW AVR"},
                    };
                    
                    // Insert default types
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        for (Object[] type : defaultTypes) {
                            insertStmt.setInt(1, (int) type[0]);
                            insertStmt.setString(2, (String) type[1]);
                            int rowsAffected = insertStmt.executeUpdate();
                            System.out.println("Inserted: " + type[1] + " (Rows affected: " + rowsAffected + ")");
                        }
                        System.out.println("All default room types inserted successfully.");
                    }
                } else {
                    System.out.println("Room types already exist. Current count: " + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing room types: " + e.getMessage());
            e.printStackTrace();
            // Optionally show user-friendly message
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize room types: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Alternative method that doesn't use manual IDs
     * Use this if your room_type_id is AUTO_INCREMENT
     */
    public void initializeDefaultRoomTypesAlternative() {
        String checkSql = "SELECT COUNT(*) FROM room_type";
        String insertSql = "INSERT INTO room_type (type_name) VALUES (?)"; // Let DB generate ID
        
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 ResultSet rs = checkStmt.executeQuery()) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Inserting default room types (auto-increment IDs)...");
                    
                    String[] defaultTypeNames = {
                        "GREY AVR",
                        "MAROON AVR",
                        "BLUE AVR",
                        "YELLOW AVR",
                       
                    };
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        for (String typeName : defaultTypeNames) {
                            insertStmt.setString(1, typeName);
                            int rowsAffected = insertStmt.executeUpdate();
                            System.out.println("Inserted: " + typeName + " (Rows affected: " + rowsAffected + ")");
                        }
                        System.out.println("Default room types inserted successfully.");
                    }
                } else {
                    System.out.println("Room types already exist. Current count: " + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing room types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to verify room types exist and display them
     * Use this for debugging to see what's in your database
     */
    public void verifyRoomTypes() {
        String sql = "SELECT room_type_id, type_name FROM room_type ORDER BY room_type_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("=== Current room types in database ===");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("ID: " + rs.getInt("room_type_id") + 
                                 ", Name: " + rs.getString("type_name"));
            }
            
            if (!hasData) {
                System.out.println("No room types found in database.");
            }
            System.out.println("======================================");
            
        } catch (SQLException e) {
            System.err.println("Error verifying room types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if a room number already exists
     * @param roomNumber The room number to check
     * @return true if exists, false otherwise
     */
    public boolean roomNumberExists(String roomNumber) {
        String sql = "SELECT COUNT(*) FROM room WHERE room_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking room number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if a room number exists for a different room (used during updates)
     * @param roomNumber The room number to check
     * @param excludeRoomId The room ID to exclude from the check
     * @return true if exists, false otherwise
     */
    public boolean roomNumberExistsForOtherRoom(String roomNumber, int excludeRoomId) {
        String sql = "SELECT COUNT(*) FROM room WHERE room_number = ? AND room_id != ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomNumber);
            stmt.setInt(2, excludeRoomId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking room number for other rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}