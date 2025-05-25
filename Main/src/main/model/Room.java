package model;

public class Room {
    private int id;
    private String roomNumber;
    private String type;
    private String typeName;
    private int roomTypeId;
    private int capacity;

    public Room() {}

   public Room(int id, String roomNumber, String type, int capacity, int roomTypeId) {
    this.id = id;
    this.roomNumber = roomNumber;
    this.type = type;
    this.capacity = capacity;
    this.roomTypeId = roomTypeId;
}
   @Override
  public String toString() {
    return "Room " + roomNumber + " - " + type;
}


   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRoomTypeId() {
    return roomTypeId;}
    public void setRoomTypeId(int roomTypeId) {
    this.roomTypeId = roomTypeId;}
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getTypeName() {return typeName;}
    public void setTypeName(String typeName) {this.typeName = typeName;}
}
