package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.sql.*;

public class ReviewEditorController {

    @FXML private TextArea commentArea;
    private static Review reviewToEdit;

    public static void setReviewToEdit(Review review) {
        reviewToEdit = review;
    }

    @FXML
    public void initialize() {
        if (reviewToEdit != null && commentArea != null) {
            commentArea.setText(reviewToEdit.getComment());
        }
    }

    @FXML
    private void saveChanges() throws IOException {
        if (reviewToEdit == null) return;

       
        try (Connection conn = DBDoctor.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE review SET comment = ? WHERE review_id = ?");
            pstmt.setString(1, commentArea.getText());
            pstmt.setInt(2, reviewToEdit.getReviewId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

      
        App.setRoot("AdminFlaggedPage");
    }

    @FXML
    private void navigateBack() throws IOException {
       
        App.setRoot("AdminFlaggedPage");
    }
}
