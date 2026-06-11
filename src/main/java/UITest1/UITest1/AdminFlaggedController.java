package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.Pos;
import java.io.IOException;
import java.sql.*;

public class AdminFlaggedController {

    @FXML private FlowPane flaggedReviewsPane;
    private static Review selectedReview;

    public static void setSelectedReview(Review rev) { selectedReview = rev; }
    public static Review getSelectedReview()         { return selectedReview; }

    @FXML
    public void initialize() {
        loadFlaggedReviews();
    }

    private void loadFlaggedReviews() {
        if (flaggedReviewsPane == null) return;
        flaggedReviewsPane.getChildren().clear();

        String sql = "SELECT r.*, c.name AS customer_name FROM review r " +
                     "JOIN customer c ON r.customer_id = c.customer_id " +
                     "WHERE r.review_status = 'flagged'";


        try (Connection conn = DBDoctor.connect()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Review rev = new Review(
                    rs.getInt("review_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("review_status"),
                    rs.getString("created_at"),
                    rs.getString("customer_name")
                );
                flaggedReviewsPane.getChildren().add(createFlaggedCard(rev));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createFlaggedCard(Review rev) {
        VBox card = new VBox(12);
        card.setPrefWidth(285);
        card.getStyleClass().add("square-review-box");


        HBox topButtons = new HBox(8);
        topButtons.setAlignment(Pos.TOP_RIGHT);

        Button editBtn = new Button("✎");
        editBtn.getStyleClass().add("mini-icon-btn");
        editBtn.setOnAction(e -> {
            setSelectedReview(rev);
            try {
               
                App.setRoot("AdminEditReview");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("mini-icon-btn", "delete-btn");
        deleteBtn.setOnAction(e -> handleDelete(rev));

        topButtons.getChildren().addAll(editBtn, deleteBtn);

        // Stars
        int rating = Math.max(0, Math.min(5, rev.getRating()));
        Label stars = new Label("★".repeat(rating) + "☆".repeat(5 - rating));
        stars.getStyleClass().add("star-label");

        Label reason = new Label("Moderation Required");
        reason.setStyle("-fx-font-weight: bold;");

        Label comment = new Label(rev.getComment());
        comment.setWrapText(true);
        comment.setStyle("-fx-text-fill: #666;");

        // Footer: avatar + name/date
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarFrame = new StackPane();
        avatarFrame.getStyleClass().add("avatar-circle");

        ImageView avatar = new ImageView();
        try {
            var imgStream = getClass().getResourceAsStream("/UITest1/UITest1/avatarCustomerLogin.png");
            if (imgStream == null)
                imgStream = getClass().getResourceAsStream("/avatarCustomerLogin.png");
            if (imgStream != null)
                avatar.setImage(new Image(imgStream));
        } catch (Exception e) {
            System.err.println("Could not load avatar image.");
        }
        avatar.setFitHeight(35);
        avatar.setFitWidth(35);
        avatarFrame.getChildren().add(avatar);

        VBox nameBox = new VBox();
        Label name = new Label(rev.getCustomerName());
        name.getStyleClass().add("reviewer-name");
        Label date = new Label(rev.getCreatedAt());
        date.getStyleClass().add("review-date");
        nameBox.getChildren().addAll(name, date);

        footer.getChildren().addAll(avatarFrame, nameBox);
        card.getChildren().addAll(topButtons, stars, reason, comment, footer);

        return card;
    }

    private void handleDelete(Review rev) {
      
        try (Connection conn = DBDoctor.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM review WHERE review_id = ?");
            pstmt.setInt(1, rev.getReviewId());
            pstmt.executeUpdate();
            loadFlaggedReviews();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateBack() throws IOException {
        App.setRoot("AdminDashboard");
    }
}
