package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class HomeController {

    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPassField;

    @FXML
    private void handleAdminLogin() throws IOException {
        String inputUser = adminUserField.getText();
        String inputPass = adminPassField.getText();

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            showError("Error", "Please fill in all fields.");
            return;
        }

  
        String sql = "SELECT * FROM admin WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inputUser);
            pstmt.setString(2, inputPass);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Admin login OK: " + rs.getString("username"));
             
                App.currentUserRole = "ADMIN";
                App.setRoot("AdminDashboard");
            } else {
                showError("Access Denied", "Invalid Admin ID or Password.");
            }

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            showError("System Error", "Could not connect to DataBase1.db");
        }
    }

    @FXML
    private void switchToCustomer() throws IOException {
        App.setRoot("CustomerLogin");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
