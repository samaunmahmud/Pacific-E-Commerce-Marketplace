package UITest1.UITest1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

import java.net.URL;
import java.io.IOException;
import java.sql.*;

public class AdminDashboardController {

    @FXML private PieChart averageReviewsChart;
    @FXML private Label statsLabel;
    @FXML private FlowPane reviewsFlowPane;


    @FXML private VBox productRatingsBox;

    @FXML
    public void initialize() {
        if (reviewsFlowPane != null) {
            reviewsFlowPane.setHgap(20);
            reviewsFlowPane.setVgap(20);
        }
        loadDashboardData();
        loadProductAverages();  
    }

    private void loadDashboardData() {
        if (reviewsFlowPane != null) reviewsFlowPane.getChildren().clear();

        String sql = "SELECT r.*, c.name AS customer_name FROM review r " +
                     "JOIN customer c ON r.customer_id = c.customer_id";

        try (Connection conn = DBDoctor.connect()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            int total = 0, flagged = 0, fiveStar = 0;

            while (rs.next()) {
                total++;
                Review rev = new Review(
                    rs.getInt("review_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("review_status"),
                    rs.getString("created_at"),
                    rs.getString("customer_name")
                );
                if (rev.getRating() == 5) fiveStar++;
                if ("flagged".equalsIgnoreCase(rev.getReviewStatus())) flagged++;
                loadCard(rev);
            }

            final int f = flagged, t = total;
            statsLabel.setText(f + " flagged out of " + t + " total reviews");
            updateChart(total, flagged, fiveStar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
    private void loadProductAverages() {
        if (productRatingsBox == null) return;
        productRatingsBox.getChildren().clear();

      
        String sql = "SELECT p.name AS product_name, " +
                     "ROUND(AVG(r.rating), 1) AS avg_rating, " +
                     "COUNT(r.review_id) AS review_count " +
                     "FROM product p " +
                     "LEFT JOIN review r ON p.product_id = r.product_id " +
                     "GROUP BY p.product_id, p.name " +
                     "ORDER BY avg_rating DESC";

        try (Connection conn = DBDoctor.connect();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                String name   = rs.getString("product_name");
                double avg    = rs.getDouble("avg_rating");
                int    count  = rs.getInt("review_count");

                productRatingsBox.getChildren().add(
                    buildProductRatingRow(name, avg, count)
                );
            }

        
            if (productRatingsBox.getChildren().isEmpty()) {
                Label empty = new Label("No product reviews yet.");
                empty.setStyle("-fx-text-fill: #aaa;");
                productRatingsBox.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox buildProductRatingRow(String productName, double avgRating, int reviewCount) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 0;");

        Label nameLabel = new Label(productName);
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        nameLabel.setWrapText(true);


        ProgressBar bar = new ProgressBar(avgRating / 5.0);
        bar.setPrefWidth(150);
        bar.setPrefHeight(12);
        bar.setStyle("-fx-accent: #860752;");

 
        String stars = avgRating > 0
            ? String.format("%.1f ★", avgRating)
            : "No ratings";
        Label avgLabel = new Label(stars);
        avgLabel.setStyle("-fx-text-fill: #860752; -fx-font-weight: bold; -fx-font-size: 13px;");


        Label countLabel = new Label("(" + reviewCount + " review" + (reviewCount != 1 ? "s" : "") + ")");
        countLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");

        if (avgRating >= 4.5 && reviewCount > 0) {
            Label badge = new Label("⭐ Top Rated");
            badge.setStyle("-fx-background-color: #f9dcd1; -fx-text-fill: #860752; " +
                           "-fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11px; " +
                           "-fx-font-weight: bold;");
            row.getChildren().addAll(nameLabel, bar, avgLabel, countLabel, badge);
        } else {
            row.getChildren().addAll(nameLabel, bar, avgLabel, countLabel);
        }

        return row;
    }

    private void loadCard(Review rev) {
        try {
            URL fxmlLoc = getClass().getResource("/UITest1/UITest1/ReviewCard.fxml");
            if (fxmlLoc == null) fxmlLoc = getClass().getResource("/ReviewCard.fxml");
            if (fxmlLoc == null) { System.err.println("ReviewCard.fxml not found!"); return; }

            FXMLLoader loader = new FXMLLoader(fxmlLoc);
            VBox card = loader.load();
            card.getStyleClass().add("review-card");
            ReviewCardController controller = loader.getController();
            controller.setData(rev);
            reviewsFlowPane.getChildren().add(card);

        } catch (Exception e) {
            System.err.println("Card Load Error: " + e.getMessage());
        }
    }

    private void updateChart(int total, int flagged, int fiveStar) {
        if (averageReviewsChart == null) return;
        if (total == 0) {
            averageReviewsChart.setTitle("No Data Available");
            averageReviewsChart.setData(FXCollections.emptyObservableList());
            return;
        }

        int other = Math.max(0, total - (flagged + fiveStar));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Flagged", flagged),
            new PieChart.Data("5 Stars", fiveStar),
            new PieChart.Data("Other",   other)
        );

        Platform.runLater(() -> {
            averageReviewsChart.setData(pieData);
            averageReviewsChart.setTitle("Review Distribution");
        });
    }

    @FXML private void handleViewMore() throws IOException {
    	App.setRoot("AdminFlaggedPage"); 
    	}
    
    
    @FXML private void handleLogout()   throws IOException { 
    	App.setRoot("Home"); 
    	
    }
}
