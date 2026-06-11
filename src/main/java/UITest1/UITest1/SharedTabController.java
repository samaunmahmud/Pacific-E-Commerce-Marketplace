package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SharedTabController {

    @FXML private TextField searchField;

    @FXML
    private void goHome(MouseEvent event) throws IOException {
        App.setRoot("Home");
    }

    @FXML
    private void handleLogout(MouseEvent event) throws IOException {
        App.currentUserId   = -1;
        App.currentUserRole = "";
        App.setRoot("Home");
    }

    @FXML
    private void handleProfile(MouseEvent event) {
        System.out.println("Profile clicked");
    }

    @FXML
    private void handleOrders(MouseEvent event) {
        System.out.println("Orders clicked");
    }

    @FXML
    private void handleCart(MouseEvent event) {
        System.out.println("Cart clicked");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField != null ? searchField.getText() : "";
        System.out.println("Searching for: " + query);
    }
}