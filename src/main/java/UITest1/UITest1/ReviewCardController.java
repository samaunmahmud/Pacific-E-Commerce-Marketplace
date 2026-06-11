package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ReviewCardController {

    @FXML private VBox cardContainer;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label starsLabel;
    @FXML private Label commentLabel;
    @FXML private Label nameLabel;
    @FXML private Label dateLabel;
    @FXML private Button helpfulButton;
    @FXML private Button unhelpfulButton;
    @FXML private Label helpfulCountLabel;
    @FXML private Label unhelpfulCountLabel;

    private Review currentReview;

    @FXML
    public void initialize() {
        setupHoverEffect();
    }

    public void setData(Review rev) {
        this.currentReview = rev;

        nameLabel.setText(rev.getCustomerName());
        commentLabel.setText(rev.getComment());
        dateLabel.setText(rev.getCreatedAt());

        int rating = Math.max(0, Math.min(5, rev.getRating()));
        starsLabel.setText("★".repeat(rating) + "☆".repeat(5 - rating));

        if (helpfulCountLabel   != null) helpfulCountLabel.setText(String.valueOf(rev.getHelpfulCount()));
        if (unhelpfulCountLabel != null) unhelpfulCountLabel.setText(String.valueOf(rev.getUnhelpfulCount()));

        applyPermissions(rev.getCreatedAt());
    }

    private void applyPermissions(String createdAtStr) {
        if ("ADMIN".equalsIgnoreCase(App.currentUserRole)) {
            setButtonsVisible(true);
            return;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime reviewTime    = LocalDateTime.parse(createdAtStr, formatter);
            long minutesElapsed         = ChronoUnit.MINUTES.between(reviewTime, LocalDateTime.now());
            setButtonsVisible(minutesElapsed < App.REVIEW_EDIT_WINDOW_MINUTES);
        } catch (Exception e) {
            setButtonsVisible(false);
            System.err.println("Time check failed: " + e.getMessage());
        }
    }

    private void setButtonsVisible(boolean visible) {
        if (editButton != null) {
            editButton.setVisible(visible);
            editButton.setManaged(visible);
        }
        if (deleteButton != null) {
            deleteButton.setVisible(visible);
            deleteButton.setManaged(visible);
        }
    }

   
    @FXML
    private void onEdit(ActionEvent event) {
        if (currentReview == null) return;
        try {
            ReviewPageController.setReviewToEdit(currentReview);
            App.setRoot("ReviewPage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    @FXML
    private void onDelete(ActionEvent event) {
        if (currentReview == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Review");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this review?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "DELETE FROM review WHERE review_id = ?";
                try (Connection conn = DBDoctor.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, currentReview.getReviewId());
                    pstmt.executeUpdate();
                    App.setRoot("CustomerDashboard");
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void onHelpful(ActionEvent event) {
        if (currentReview == null) return;
        String sql = "UPDATE review SET helpful_count = COALESCE(helpful_count, 0) + 1 WHERE review_id = ?";
        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentReview.getReviewId());
            pstmt.executeUpdate();
            int newCount = currentReview.getHelpfulCount() + 1;
            if (helpfulCountLabel != null) helpfulCountLabel.setText(String.valueOf(newCount));
            if (helpfulButton   != null) helpfulButton.setDisable(true);
            if (unhelpfulButton != null) unhelpfulButton.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUnhelpful(ActionEvent event) {
        if (currentReview == null) return;
        String sql = "UPDATE review SET unhelpful_count = COALESCE(unhelpful_count, 0) + 1 WHERE review_id = ?";
        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentReview.getReviewId());
            pstmt.executeUpdate();
            int newCount = currentReview.getUnhelpfulCount() + 1;
            if (unhelpfulCountLabel != null) unhelpfulCountLabel.setText(String.valueOf(newCount));
            if (helpfulButton   != null) helpfulButton.setDisable(true);
            if (unhelpfulButton != null) unhelpfulButton.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupHoverEffect() {
        if (cardContainer == null) return;
        cardContainer.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.02); st.setToY(1.02); st.play();
            cardContainer.setStyle(
                "-fx-background-color: #fdfdfd; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 8);");
        });
        cardContainer.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.0); st.setToY(1.0); st.play();
            cardContainer.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        });
    }
}