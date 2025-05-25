package ui;

import dao.ReservationDAO;
import dao.RoomDAO;
import model.Reservation;
import model.Room;
import model.User;
import model.RoomComboItem;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Calendar;

import main.ui.LoginWindow;

public class UserDashboard extends JFrame {

    private User user;
    private JComboBox<RoomComboItem> roomBox;
    private JComboBox<String> departmentBox;
    private JDateChooser startDateChooser, endDateChooser;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private DefaultTableModel tableModel;

    private JTextField nameField, departmentField;

    
    public UserDashboard(User user) {
        this.user = user;
        setTitle("User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==== Header Panel with Logout Button ====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanel.getBackground());

        JLabel titleLabel = new JLabel("Welcome, " + user.getFullName(), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });
         JTextField nameField = new JTextField(user.getFullName());
departmentBox = new JComboBox<>(new String[]{
    "CCS", "CHM", "CBA", "CN", "CCRIM", "CAS"
});
departmentBox.setSelectedIndex(-1);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==== Top Booking Panel ====
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        topPanel.setBackground(mainPanel.getBackground());

        nameField = new JTextField(user.getFullName());
        departmentField = new JTextField(); // User can fill this in manually

        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setDateFormatString("yyyy-MM-dd");

        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        roomBox = new JComboBox<>();
        loadAvailableRooms();

        JButton reserveBtn = new JButton("Reserve");
        reserveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reserveBtn.setBackground(new Color(0, 123, 255));
        reserveBtn.setForeground(Color.WHITE);
        reserveBtn.setFocusPainted(false);
        reserveBtn.addActionListener(e -> makeReservation());

        topPanel.add(new JLabel("Full Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Department:"));
        topPanel.add(departmentBox);

        topPanel.add(new JLabel("Room Type:"));
        topPanel.add(roomBox);
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(new JLabel("Start Date:"));
        topPanel.add(startDateChooser);
        topPanel.add(new JLabel("Start Time:"));
        topPanel.add(startTimeSpinner);

        topPanel.add(new JLabel("End Date:"));
        topPanel.add(endDateChooser);
        topPanel.add(new JLabel("End Time:"));
        topPanel.add(endTimeSpinner);

        mainPanel.add(topPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.add(reserveBtn);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        // ==== Reservation History Table ====
        String[] columns = {"Reservation ID","Name","Department", "Room", "Room Type", "Start Date", "End Date","Start Time","End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reservation History"));
        scrollPane.setPreferredSize(new Dimension(850, 200));

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        loadUserReservations();
    }

    private void loadAvailableRooms() {
        RoomDAO roomDAO = new RoomDAO();
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room r : rooms) {
            roomBox.addItem(new RoomComboItem(r.getId(), r.getType()));
        }
    }

    private void makeReservation() {
        java.util.Date start = startDateChooser.getDate();
        java.util.Date end = endDateChooser.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.");
            return;
        }

        if (end.before(start)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
            return;
        }

        RoomComboItem selectedItem = (RoomComboItem) roomBox.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Please select a room.");
            return;
        }

        // Combine date + time
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar timeStart = Calendar.getInstance();
        timeStart.setTime((java.util.Date) startTimeSpinner.getValue());
        startCal.set(Calendar.HOUR_OF_DAY, timeStart.get(Calendar.HOUR_OF_DAY));
        startCal.set(Calendar.MINUTE, timeStart.get(Calendar.MINUTE));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        Calendar timeEnd = Calendar.getInstance();
        timeEnd.setTime((java.util.Date) endTimeSpinner.getValue());
        endCal.set(Calendar.HOUR_OF_DAY, timeEnd.get(Calendar.HOUR_OF_DAY));
        endCal.set(Calendar.MINUTE, timeEnd.get(Calendar.MINUTE));

        Date sqlStart = new Date(startCal.getTimeInMillis());
        Date sqlEnd = new Date(endCal.getTimeInMillis());

        int roomId = selectedItem.getId();

        ReservationDAO dao = new ReservationDAO();
        boolean success = dao.createReservation(user.getId(), roomId, sqlStart, sqlEnd);

        if (success) {
            JOptionPane.showMessageDialog(this, "Reservation request submitted.");
            tableModel.setRowCount(0);
            loadUserReservations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reserve. Room may be booked.");
        }
    }

    private void loadUserReservations() {
        tableModel.setRowCount(0);
        ReservationDAO dao = new ReservationDAO();
        List<Reservation> reservations = dao.getUserReservations(user.getId());
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(), r.getRoom().getRoomNumber(), r.getRoomType(),
                r.getStartDate(), r.getEndDate(),
                r.getStatus()
            });
        }
    }
}
