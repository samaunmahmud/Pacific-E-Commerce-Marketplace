package UITest1.UITest1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class CustomerDashboardController {

    @FXML private VBox reviewsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    @FXML
    public void initialize() {
        if (sortComboBox != null) sortComboBox.setValue("Newest");
        loadCustomerReviews();
    }

    @FXML
    private void handleSearchAndSort() {
        loadCustomerReviews();
    }

    private void loadCustomerReviews() {
        if (reviewsContainer == null) return;
        reviewsContainer.getChildren().clear();

        String sortVal = (sortComboBox != null) ? sortComboBox.getValue() : "Newest";
        String orderClause = "r.created_at DESC";
        if ("Highest Rating".equals(sortVal))      orderClause = "r.rating DESC";
        else if ("Lowest Rating".equals(sortVal))  orderClause = "r.rating ASC";
        else if ("Most Helpful".equals(sortVal))   orderClause = "r.helpful_count DESC";

        String keyword = (searchField != null) ? searchField.getText() : "";

        String sql = "SELECT r.*, c.name AS customer_name FROM review r " +
                     "LEFT JOIN customer c ON r.customer_id = c.customer_id " +
                     "WHERE r.customer_id = ? AND r.comment LIKE ? " +
                     "ORDER BY " + orderClause;

        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, App.currentUserId);
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int helpful = 0, unhelpful = 0;
                try { helpful   = rs.getInt("helpful_count");   } catch (Exception ignored) {}
                try { unhelpful = rs.getInt("unhelpful_count"); } catch (Exception ignored) {}


                Review rev = new Review(
                    rs.getInt("review_id"),
                    rs.getInt("product_id"),   
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("review_status"),
                    rs.getString("created_at"),
                    rs.getString("customer_name") != null
                        ? rs.getString("customer_name") : "Verified Buyer",
                    helpful,
                    unhelpful
                );
                addReviewToUI(rev);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addReviewToUI(Review rev) {
        try {
            URL loc = getClass().getResource("/UITest1/UITest1/ReviewCard.fxml");
            if (loc == null) loc = getClass().getResource("/ReviewCard.fxml");
            if (loc == null) { System.err.println("ReviewCard.fxml not found"); return; }

            FXMLLoader loader = new FXMLLoader(loc);
            VBox card = loader.load();
            ReviewCardController controller = loader.getController();
            if (controller != null) {
                controller.setData(rev);
                reviewsContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleWriteReview()throws IOException { 
    	App.setRoot("UnratedProductsPage"); 
    	}
    @FXML private void navigateToMoreProducts(MouseEvent event) throws IOException { 
    	App.setRoot("UnratedProductsPage"); 
    	}
    @FXML private void handleMoreOptions(ActionEvent event)     { 
    	System.out.println("Options clicked"); 
    	}
    @FXML private void handleLogout()throws IOException { 
    	App.currentUserId = -1; App.setRoot("Home"); 
    	}
}
