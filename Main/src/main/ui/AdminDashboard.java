package ui;

import dao.ReservationDAO;
import model.Reservation;
import main.model.ReservationDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import main.ui.LoginWindow;
import model.User;

public class AdminDashboard extends JFrame {

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(User adminUser) {
        setTitle("Admin Dashboard");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding and background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // === Header Panel with title and logout button ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanel.getBackground());

        JLabel titleLabel = new JLabel("Welcome, " + adminUser.getFullName(), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(108, 117, 125));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        
        String[] columns = {"Reservation ID", "Name","Department", "Room","Start Date", "End Date","Start Time","End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        reservationTable = new JTable(tableModel);
        reservationTable.setRowHeight(25);
        reservationTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reservationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reservationTable.getTableHeader().setBackground(new Color(220, 220, 220));
        reservationTable.getTableHeader().setForeground(Color.BLACK);
        reservationTable.setGridColor(Color.LIGHT_GRAY);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < reservationTable.getColumnCount(); i++) {
            reservationTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        loadPendingReservations();
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // === Buttons Panel ===
        JButton approveBtn = new JButton("Approve");
        JButton denyBtn = new JButton("Deny");
        JButton manageRoomsBtn = new JButton("Manage Rooms");

        // Style buttons
        Color btnColor = new Color(0, 123, 255);
        Color denyColor = new Color(220, 53, 69);
        Color greenColor = new Color(40, 167, 69);

        styleButton(approveBtn, greenColor);
        styleButton(denyBtn, denyColor);
        styleButton(manageRoomsBtn, btnColor);

        approveBtn.addActionListener(e -> handleAction("approved"));
        denyBtn.addActionListener(e -> handleAction("denied"));
        manageRoomsBtn.addActionListener(e -> new RoomManagementWindow().setVisible(true));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.add(approveBtn);
        buttonPanel.add(denyBtn);
        buttonPanel.add(manageRoomsBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(130, 35));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void loadPendingReservations() {
        tableModel.setRowCount(0);
        ReservationDAO dao = new ReservationDAO();
        List<ReservationDTO> reservations = dao.getAllReservations();
        for (ReservationDTO r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(),
               r.getFull_name(),
                r.getRoom().getRoomNumber(),
                r.getStartDate(),
                r.getEndDate(),
                r.getStatus()
            });
        }
    }

    private void handleAction(String action) {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        ReservationDAO dao = new ReservationDAO();
        boolean success = dao.updateReservationStatus(reservationId, action);
        if (success) {
            JOptionPane.showMessageDialog(this, "Reservation " + action + " successfully.");
            loadPendingReservations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.");
        }
    }
}
