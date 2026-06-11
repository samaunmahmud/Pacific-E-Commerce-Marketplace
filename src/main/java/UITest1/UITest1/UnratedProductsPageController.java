package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class UnratedProductsPageController {

    @FXML private GridPane productGrid;
    private static int selectedProductId;

    public static int getSelectedProductId() { return selectedProductId; }


    public static void setSelectedProductId(int id) { selectedProductId = id; }

    @FXML
    public void initialize() {
        if (productGrid != null) loadUnratedProducts();
    }

    private void loadUnratedProducts() {
        productGrid.getChildren().clear();
        String sql = "SELECT product_id, name, price FROM product WHERE product_id NOT IN " +
                     "(SELECT product_id FROM review WHERE customer_id = ?)";
        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, App.currentUserId);
            ResultSet rs = pstmt.executeQuery();
            int col = 0, row = 0;
            while (rs.next()) {
                int    pId   = rs.getInt("product_id");
                String name  = rs.getString("name");
                double price = rs.getDouble("price");
                VBox card = createProductCard(pId, name, String.format("%.2f", price));
                productGrid.add(card, col, row);
                if (++col == 4) { col = 0; row++; }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductCard(int id, String name, String price) {
        VBox card = new VBox(15);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(260);
        card.setPadding(new javafx.geometry.Insets(20));

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("product-title-text");
        nameLabel.setWrapText(true);

        Label priceLabel = new Label("£" + price);
        priceLabel.getStyleClass().add("product-price");

        Button btn = new Button("Write Review");
        btn.getStyleClass().add("submit-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnAction(e -> {
            selectedProductId = id;
            try { App.setRoot("ReviewPage"); } catch (IOException ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(nameLabel, priceLabel, btn);
        return card;
    }

    @FXML
    private void navigateBack() throws IOException {
        App.setRoot("CustomerDashboard");
    }
}