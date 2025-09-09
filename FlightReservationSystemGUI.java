import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

class Booking {
    String name, source, destination, date, seatClass;

    public Booking(String name, String source, String destination, String date, String seatClass) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.seatClass = seatClass;
    }

    @Override
    public String toString() {
        return "Passenger: " + name + "\nFrom: " + source + "\nTo: " + destination +
                "\nDate: " + date + "\nClass: " + seatClass;
    }
}

public class FlightReservationSystemGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Reservation components
    private JTextField nameField;
    private JComboBox<String> sourceBox;
    private JComboBox<String> destinationBox;
    private JTextField dateField;
    private JComboBox<String> seatClassBox;

    private ArrayList<Booking> bookings;  // store all bookings

    public FlightReservationSystemGUI() {
        setTitle("Flight Reservation System");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        bookings = new ArrayList<>();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "Login");
        mainPanel.add(reservationPanel(), "Reservation");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            if (user.equals("admin") && pass.equals("123")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                cardLayout.show(mainPanel, "Reservation");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        });

        return panel;
    }

    private JPanel reservationPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField();
        sourceBox = new JComboBox<>(new String[]{"Bangalore", "Delhi", "Mumbai", "Chennai"});
        destinationBox = new JComboBox<>(new String[]{"New York", "London", "Dubai", "Singapore"});
        dateField = new JTextField();
        seatClassBox = new JComboBox<>(new String[]{"Economy", "Business", "First Class"});

        JButton bookBtn = new JButton("Book Ticket");
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton viewBtn = new JButton("View All Bookings");
        JButton exitBtn = new JButton("Exit");

        bookBtn.addActionListener(e -> bookTicket());
        cancelBtn.addActionListener(e -> cancelBooking());
        viewBtn.addActionListener(e -> displayBookings());
        exitBtn.addActionListener(e -> exitSystem());

        panel.add(new JLabel("Passenger Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceBox);
        panel.add(new JLabel("Destination:"));
        panel.add(destinationBox);
        panel.add(new JLabel("Date (dd/mm/yyyy):"));
        panel.add(dateField);
        panel.add(new JLabel("Seat Class:"));
        panel.add(seatClassBox);
        panel.add(bookBtn);
        panel.add(cancelBtn);
        panel.add(viewBtn);
        panel.add(exitBtn);

        return panel;
    }

    private void bookTicket() {
        String name = nameField.getText();
        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();
        String date = dateField.getText();
        String seatClass = (String) seatClassBox.getSelectedItem();

        if (name.trim().isEmpty() || date.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all details!");
            return;
        }

        Booking booking = new Booking(name, source, destination, date, seatClass);
        bookings.add(booking);

        JOptionPane.showMessageDialog(this, "Booking Confirmed!\n\n" + booking);
    }

    private void cancelBooking() {
        String name = JOptionPane.showInputDialog(this, "Enter Passenger Name to Cancel:");
        if (name == null || name.trim().isEmpty()) return;

        Booking toRemove = null;
        for (Booking b : bookings) {
            if (b.name.equalsIgnoreCase(name.trim())) {
                toRemove = b;
                break;
            }
        }

        if (toRemove != null) {
            bookings.remove(toRemove);
            JOptionPane.showMessageDialog(this, "Booking Cancelled!\n\n" + toRemove);
        } else {
            JOptionPane.showMessageDialog(this, "No booking found for passenger: " + name);
        }
    }

    private void displayBookings() {
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings available!");
            return;
        }

        // Create table data
        String[] columnNames = {"Name", "Source", "Destination", "Date", "Class"};
        String[][] data = new String[bookings.size()][5];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.name;
            data[i][1] = b.source;
            data[i][2] = b.destination;
            data[i][3] = b.date;
            data[i][4] = b.seatClass;
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scrollPane, "All Bookings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitSystem() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightReservationSystemGUI().setVisible(true));
    }
}
