package ui.etabulation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Maynard
 */
public class ModelBingoLetterController implements Initializable {

    @FXML
    private AnchorPane apMain;
    @FXML
    private StackPane spBingoLetter;
    @FXML
    private Label lblBingoLetter;

    /**
     * Initializes the controller class.
     */
    public void setLetter(String fsLetter) {
        lblBingoLetter.setText(fsLetter);
    }
    public void setVisible(boolean fbVisible) {
        apMain.setVisible(fbVisible);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
