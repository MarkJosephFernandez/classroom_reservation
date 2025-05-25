package model;

public class RoomType {
    private int roomTypeId;
    private String typeName;
    
    // Default constructor
    public RoomType() {
    }
    
    // Constructor with parameters
    public RoomType(int roomTypeId, String typeName) {
        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
    }
    
    // Getters and setters
    public int getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    // This is crucial for JComboBox display
    @Override
    public String toString() {
        return typeName;
    }
    
    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoomType roomType = (RoomType) obj;
        return roomTypeId == roomType.roomTypeId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(roomTypeId);
    }
}