package model;

import java.sql.Date;

public class Reservation {
    private int id;
    private int userId;
    private Room room;       
    private Date startDate;
    private Date endDate;
    private String status;
    private String roomType;


    public Reservation() {}

    public Reservation(int id, int userId, Room room, Date startDate, Date endDate, String status) {
        this.id = id;
        this.userId = userId;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Room getRoom() {        // getter for Room
        return room;
    }

    public void setRoom(Room room) {  // setter for Room
        this.room = room;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getRoomType() {
        return roomType; 
    }
    
    public void setRoomType(String roomType) { 
        this.roomType = roomType; 
    }
}
