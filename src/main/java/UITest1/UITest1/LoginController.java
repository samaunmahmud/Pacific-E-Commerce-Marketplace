package UITest1.UITest1;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;

public class LoginController {

    @FXML private ToggleButton adminToggle;
    @FXML private ToggleButton customerToggle;

    @FXML
    public void initialize() {
        
        ToggleGroup group = new ToggleGroup();
        adminToggle.setToggleGroup(group);
        customerToggle.setToggleGroup(group);
        customerToggle.setSelected(true); 
    }

    @FXML
    private void handleLogin() throws IOException {
        if (adminToggle.isSelected()) {
            App.setRoot("Home");            
        } else {
            App.setRoot("CustomerLogin");   
        }
    }
}
