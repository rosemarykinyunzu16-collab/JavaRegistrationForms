import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Registrationforms extends JFrame {
    JTextField nameField, contactField;
    JTextArea addressArea;
    JRadioButton maleButton, femaleButton;
    ButtonGroup genderGroup; // ✅ Declared here
    JTable table;
    DefaultTableModel model;

    public Registrationforms() {
        setTitle("Registration Forms");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registration Form"));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        genderGroup = new ButtonGroup(); // ✅ Assigned here
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Address:"));
        addressArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(addressArea));

        formPanel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");
        formPanel.add(registerButton);
        formPanel.add(exitButton);

        add(formPanel, BorderLayout.WEST);

        // Table Panel
        String[] columns = {"ID", "Name", "Gender", "Address", "Contact"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        loadUserData(); // load from DB
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Register Button Action
        registerButton.addActionListener(e -> {
            saveData();
            loadUserData();
            clearForm();
        });

        // Exit Button Action
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void saveData() {
        String name = nameField.getText();
        String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : "");
        String address = addressArea.getText();
        String contact = contactField.getText();

        if (name.isEmpty() || gender.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "");
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, gender, address, contact) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, gender);
            ps.setString(3, address);
            ps.setString(4, contact);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void loadUserData() {
        model.setRowCount(0); // Clear table
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("contact")
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        addressArea.setText("");
        genderGroup.clearSelection(); // ✅ No more error
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Registrationforms().setVisible(true));
    }
}