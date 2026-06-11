package UITest1.UITest1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class ReviewPageController {

    @FXML private HBox starContainer;
    @FXML private TextArea customerReviewArea;
    @FXML private TextField reviewTitleField;
    @FXML private TextField publicNameField;
    @FXML private Label fileNameLabel;
    @FXML private TextField helpfulRatingField;

    private File selectedMedia;
    private int starRating = 0;

   
    private static Review reviewToEdit = null;

    
    public static void setReviewToEdit(Review rev) {
        reviewToEdit = rev;
    }

    @FXML
    public void initialize() {
        customerReviewArea.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.length() > 500) customerReviewArea.setText(old);
        });

      
        if (reviewToEdit != null) {
           
            String fullComment = reviewToEdit.getComment();
            if (fullComment != null && fullComment.contains(": ")) {
                String[] parts = fullComment.split(": ", 2);
                reviewTitleField.setText(parts[0]);
                customerReviewArea.setText(parts[1]);
            } else {
                customerReviewArea.setText(fullComment != null ? fullComment : "");
            }

          
            starRating = reviewToEdit.getRating();
            for (int i = 0; i < starContainer.getChildren().size(); i++) {
                Button star = (Button) starContainer.getChildren().get(i);
                star.setText(i < starRating ? "★" : "☆");
                star.setStyle(i < starRating
                    ? "-fx-text-fill: #e5ac62; -fx-background-color: transparent; -fx-font-size: 24;"
                    : "-fx-text-fill: #444444; -fx-background-color: transparent; -fx-font-size: 24;");
            }

           
            UnratedProductsPageController.setSelectedProductId(reviewToEdit.getProductId());
        }
    }

    @FXML
    private void handleStarClick(ActionEvent event) {
        Button clickedStar = (Button) event.getSource();
        starRating = Integer.parseInt(clickedStar.getUserData().toString());

        for (int i = 0; i < starContainer.getChildren().size(); i++) {
            Button star = (Button) starContainer.getChildren().get(i);
            star.setText(i < starRating ? "★" : "☆");
            star.setStyle(i < starRating
                ? "-fx-text-fill: #e5ac62; -fx-background-color: transparent; -fx-font-size: 24;"
                : "-fx-text-fill: #444444; -fx-background-color: transparent; -fx-font-size: 24;");
        }
    }

    @FXML
    private void handleVideoUpload(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Product Media");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Media Files", "*.png", "*.jpg", "*.mp4"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        selectedMedia = fc.showOpenDialog(stage);
        if (selectedMedia != null) fileNameLabel.setText(selectedMedia.getName());
    }

    @FXML
    private void handleSubmit() throws IOException {
        String body  = customerReviewArea.getText();
        String title = reviewTitleField.getText();
        int productId = UnratedProductsPageController.getSelectedProductId();

        if (starRating == 0 || body.trim().isEmpty()) {
            showAlert("Required", "Please provide both a star rating and a comment.");
            return;
        }

        String prohibitedRegex = ".*[@.#/\\\\?\"'~$].*";
        if (body.matches(prohibitedRegex) || title.matches(prohibitedRegex)) {
            showAlert("Invalid Input", "Your review contains prohibited characters.");
            return;
        }

       
        if (reviewToEdit != null) {
            String sql = "UPDATE review SET rating = ?, comment = ?, image_url = ?, " +
                         "created_at = datetime('now') WHERE review_id = ?";
            try (Connection c = DBDoctor.connect();
                 PreparedStatement pstmt = c.prepareStatement(sql)) {
                pstmt.setInt(1, starRating);
                pstmt.setString(2, title + ": " + body);
                pstmt.setString(3, selectedMedia != null ? selectedMedia.getAbsolutePath() : "");
                pstmt.setInt(4, reviewToEdit.getReviewId());
                pstmt.executeUpdate();
                reviewToEdit = null;
                App.setRoot("CustomerDashboard");
            } catch (SQLException e) {
                showAlert("Database Error", "Error: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

      
        String sql = "INSERT INTO review (product_id, customer_id, rating, comment, " +
                     "review_status, created_at, image_url) VALUES (?, ?, ?, ?, 'visible', datetime('now'), ?)";
        try (Connection c = DBDoctor.connect();
             PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, App.currentUserId);
            pstmt.setInt(3, starRating);
            pstmt.setString(4, title + ": " + body);
            pstmt.setString(5, selectedMedia != null ? selectedMedia.getAbsolutePath() : "");
            pstmt.executeUpdate();
            App.setRoot("CustomerDashboard");
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                showAlert("Duplicate", "You have already reviewed this product.");
            } else {
                showAlert("Database Error", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void navigateBack() throws IOException {
        reviewToEdit = null; // Clear if cancelled
        App.setRoot("CustomerDashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
