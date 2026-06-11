package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class CustomerLoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passField;

    @FXML
    private void handleCustomerLogin() throws IOException {
        String inputEmail = userField.getText();
        String inputPass  = passField.getText();

        if (inputEmail.isEmpty() || inputPass.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        String sql = "SELECT customer_id FROM customer WHERE email = ? AND password_hash = ?";

      
        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inputEmail);
            pstmt.setString(2, inputPass);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                App.currentUserId   = rs.getInt("customer_id");
                App.currentUserRole = "CUSTOMER";
                System.out.println("Customer Login OK. ID: " + App.currentUserId);
                App.setRoot("CustomerDashboard");
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Check your database connection.");
        }
    }

    @FXML
    private void switchToAdmin() throws IOException {
        App.setRoot("Home");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
