package ui;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import model.Room;
import model.RoomType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomManagementWindow extends JFrame {

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDAO roomDAO = new RoomDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    public RoomManagementWindow() {
        setTitle("Room Management - Admin");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize room types first with debugging
        initializeRoomTypesWithDebug();

        String[] columns = {"ID", "Room Number", "Room Type", "Capacity"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        loadRoomData();

        JButton addBtn = new JButton("Add Room");
        JButton editBtn = new JButton("Edit Room");
        JButton deleteBtn = new JButton("Delete Room");
        JButton debugBtn = new JButton("Debug Room Types"); // Add debug button

        // Fixed the method calls - now passing correct parameters
        addBtn.addActionListener(e -> openRoomForm(false, null));
        editBtn.addActionListener(e -> {
            int selected = roomTable.getSelectedRow();
            if (selected >= 0) {
                Room room = getRoomFromRow(selected);
                openRoomForm(true, room);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room to edit.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int selected = roomTable.getSelectedRow();
            if (selected >= 0) {
                int id = (int) tableModel.getValueAt(selected, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (roomDAO.deleteRoom(id)) {
                        JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                        loadRoomData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete room.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            }
        });

        // Debug button to check room types
        debugBtn.addActionListener(e -> debugRoomTypes());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(debugBtn); // Add debug button
        
        // Add a refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadRoomData();
            JOptionPane.showMessageDialog(this, "Data refreshed!");
        });
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Initialize room types with detailed debugging information
     */
    private void initializeRoomTypesWithDebug() {
        System.out.println("=== INITIALIZING ROOM TYPES ===");
        
        // First, verify current room types using RoomDAO
        System.out.println("Checking room types via RoomDAO:");
        roomDAO.verifyRoomTypes();
        
        // Try to initialize default room types
        System.out.println("\nAttempting to initialize default room types...");
        roomDAO.initializeDefaultRoomTypes();
        
        // Verify again
        System.out.println("\nAfter initialization - RoomDAO verification:");
        roomDAO.verifyRoomTypes();
        
        // Now check via RoomTypeDAO
        System.out.println("\nChecking room types via RoomTypeDAO:");
        try {
            List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
            if (roomTypes.isEmpty()) {
                System.out.println("No room types found via RoomTypeDAO!");
                
                // Try alternative initialization method
                System.out.println("Trying alternative initialization method...");
                roomDAO.initializeDefaultRoomTypesAlternative();
                
                // Check again
                roomTypes = roomTypeDAO.getAllRoomTypes();
                System.out.println("After alternative method, found " + roomTypes.size() + " room types");
            } else {
                System.out.println("Found " + roomTypes.size() + " room types via RoomTypeDAO:");
                for (RoomType type : roomTypes) {
                    System.out.println("  - ID: " + type.getRoomTypeId() + ", Name: " + type.getTypeName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking room types via RoomTypeDAO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== ROOM TYPE INITIALIZATION COMPLETE ===\n");
    }

    /**
     * Debug method to check room types - callable from UI
     */
    private void debugRoomTypes() {
        StringBuilder info = new StringBuilder();
        info.append("=== ROOM TYPES DEBUG INFO ===\n\n");
        
        // Check via RoomDAO
        info.append("Via RoomDAO:\n");
        try {
            // This will print to console, but we'll also try to capture info
            roomDAO.verifyRoomTypes();
            info.append("Check console for RoomDAO verification output\n\n");
        } catch (Exception e) {
            info.append("Error with RoomDAO: ").append(e.getMessage()).append("\n\n");
        }
        
        // Check via RoomTypeDAO
        info.append("Via RoomTypeDAO:\n");
        try {
            List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
            if (roomTypes.isEmpty()) {
                info.append("No room types found!\n");
            } else {
                info.append("Found ").append(roomTypes.size()).append(" room types:\n");
                for (RoomType type : roomTypes) {
                    info.append("  - ID: ").append(type.getRoomTypeId())
                        .append(", Name: ").append(type.getTypeName()).append("\n");
                }
            }
        } catch (Exception e) {
            info.append("Error: ").append(e.getMessage()).append("\n");
        }
        
        // Show in dialog
        JTextArea textArea = new JTextArea(info.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Room Types Debug Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private Room getRoomFromRow(int row) {
        Room room = new Room();
        room.setId((int) tableModel.getValueAt(row, 0));
        room.setRoomNumber((String) tableModel.getValueAt(row, 1));
        room.setType((String) tableModel.getValueAt(row, 2));
        room.setCapacity((int) tableModel.getValueAt(row, 3));
        
        // We need to find the room type ID from the database
        try {
            List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
            for (RoomType type : roomTypes) {
                if (type.getTypeName().equals(room.getType())) {
                    room.setRoomTypeId(type.getRoomTypeId());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting room type ID: " + e.getMessage());
        }
        
        return room;
    }

    private void loadRoomData() {
        tableModel.setRowCount(0);
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{r.getId(), r.getRoomNumber(), r.getType(), r.getCapacity()});
            }
            System.out.println("Loaded " + rooms.size() + " rooms into table");
        } catch (Exception e) {
            System.err.println("Error loading room data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRoomForm(boolean isEdit, Room room) {
        // Create form components
        JTextField roomNumberField = new JTextField(15);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JComboBox<RoomType> roomTypeCombo = new JComboBox<>();
        
        // Load room types into combo box
        if (!loadRoomTypesIntoCombo(roomTypeCombo)) {
            JOptionPane.showMessageDialog(this, 
                "No room types available. Please check your database setup.", 
                "Configuration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // If editing, populate fields with existing data
        if (isEdit && room != null) {
            roomNumberField.setText(room.getRoomNumber());
            capacitySpinner.setValue(room.getCapacity());
            
            // Find and select the matching room type
            for (int i = 0; i < roomTypeCombo.getItemCount(); i++) {
                RoomType type = roomTypeCombo.getItemAt(i);
                if (type != null && type.getRoomTypeId() == room.getRoomTypeId()) {
                    roomTypeCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Create form panel with better layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(roomNumberField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(capacitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(roomTypeCombo, gbc);

        String title = isEdit ? "Edit Room" : "Add New Room";
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Validate input
            String number = roomNumberField.getText().trim();
            if (number.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room number cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int capacity = (int) capacitySpinner.getValue();
            RoomType selectedType = (RoomType) roomTypeCombo.getSelectedItem();

            // Check if room type is selected
            if (selectedType == null) {
                JOptionPane.showMessageDialog(this, "Please select a room type.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate room numbers
            if (isEdit) {
                if (roomDAO.roomNumberExistsForOtherRoom(number, room.getId())) {
                    JOptionPane.showMessageDialog(this, "Room number already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                if (roomDAO.roomNumberExists(number)) {
                    JOptionPane.showMessageDialog(this, "Room number already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (isEdit) {
                // Update existing room
                room.setRoomNumber(number);
                room.setCapacity(capacity);
                room.setRoomTypeId(selectedType.getRoomTypeId());
                room.setType(selectedType.getTypeName());

                if (roomDAO.updateRoom(room)) {
                    JOptionPane.showMessageDialog(this, "Room updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update room.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Add new room
                Room newRoom = new Room();
                newRoom.setRoomNumber(number);
                newRoom.setCapacity(capacity);
                newRoom.setRoomTypeId(selectedType.getRoomTypeId());
                newRoom.setType(selectedType.getTypeName());

                if (roomDAO.addRoom(newRoom)) {
                    JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add room.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            loadRoomData(); // Refresh the table
        }
    }

    // Helper method to load room types into combo box
    private boolean loadRoomTypesIntoCombo(JComboBox<RoomType> combo) {
        try {
            // Clear existing items
            combo.removeAllItems();
            
            // Load room types from database
            List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
            
            if (roomTypes.isEmpty()) {
                System.out.println("No room types found. Attempting to initialize...");
                
                // Try both initialization methods
                roomDAO.initializeDefaultRoomTypes();
                roomTypes = roomTypeDAO.getAllRoomTypes();
                
                if (roomTypes.isEmpty()) {
                    System.out.println("First method failed, trying alternative...");
                    roomDAO.initializeDefaultRoomTypesAlternative();
                    roomTypes = roomTypeDAO.getAllRoomTypes();
                }
                
                if (roomTypes.isEmpty()) {
                    System.err.println("Failed to initialize room types!");
                    return false;
                }
            }
            
            // Add room types to combo box
            for (RoomType type : roomTypes) {
                combo.addItem(type);
            }
            
            // Select first item by default if available
            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
            
            System.out.println("Successfully loaded " + roomTypes.size() + " room types into combo box");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading room types: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room types: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}