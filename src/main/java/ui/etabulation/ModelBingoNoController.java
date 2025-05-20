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
public class ModelBingoNoController implements Initializable {

    @FXML
    private AnchorPane apMain;
    @FXML
    private StackPane spBingoNo;
    @FXML
    private Label lblBingoNo;

    /**
     * Initializes the controller class.
     */
    public void setNo(String fsNo) {
        lblBingoNo.setText(fsNo);
    }

    public void setVisible(boolean fbVisible) {
        apMain.setVisible(fbVisible);
    }

    public void setNoVisible(boolean fbVisible) {
        lblBingoNo.setVisible(fbVisible);
    }
    
    public boolean getNoVisible() {
        return lblBingoNo.isVisible();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
