package model;

public class RoomComboItem {
    private int id;
    private String typeName;

    public RoomComboItem(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return typeName;  // Only show type name in JComboBox
    }
}
