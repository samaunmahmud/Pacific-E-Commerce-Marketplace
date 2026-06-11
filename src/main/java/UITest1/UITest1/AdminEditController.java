package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.*;

public class AdminEditController {

    @FXML private TextField editTitleField;
    @FXML private TextArea editContentArea;

    @FXML
    public void initialize() {
        if (editTitleField != null)  editTitleField.getStyleClass().add("modern-input");
        if (editContentArea != null) editContentArea.getStyleClass().add("modern-input");

        Review rev = AdminFlaggedController.getSelectedReview();
        if (rev != null) {
            String fullComment = rev.getComment();
            if (fullComment != null && fullComment.contains(": ")) {
                String[] parts = fullComment.split(": ", 2);
                editTitleField.setText(parts[0]);
                editContentArea.setText(parts[1]);
            } else {
                editTitleField.setText("Moderator Update");
                editContentArea.setText(fullComment != null ? fullComment : "");
            }
        }
    }

    @FXML
    private void handleSave() throws IOException {
        Review rev = AdminFlaggedController.getSelectedReview();
        if (rev == null) {
            System.err.println("No review selected to save.");
            return;
        }

        String title   = editTitleField.getText();
        String content = editContentArea.getText();

        String sanitizedContent = content.replaceAll("[@.#\\\\/?\"'`~$]", "");
        String sanitizedTitle   = title.replaceAll("[@.#\\\\/?\"'`~$]", "");

        String updatedComment = sanitizedTitle + ": " + sanitizedContent;

    
        String sql = "UPDATE review SET comment = ?, review_status = 'visible' WHERE review_id = ?";

        try (Connection conn = DBDoctor.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, updatedComment);
            pstmt.setInt(2, rev.getReviewId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Review sanitized and set to visible.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

 
        App.setRoot("AdminDashboard");
    }

    @FXML
    private void navigateBack() throws IOException {
 
        App.setRoot("AdminFlaggedPage");
    }
}