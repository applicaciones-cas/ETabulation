package ui.etabulation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import ui.etabulation.FrmETabulationController;

/**
 * FXML Controller class
 *
 * @author loke
 */
public class ContestChoiceController implements Initializable {

    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;
    
    private FrmETabulationController poETabulation;
    
    @FXML
    public void onCancelClicked(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onOkClicked(ActionEvent event) {
        
        String chosenTheme = themeComboBox.getValue();
        String contestID = null;

        switch (chosenTheme) {
            case "Kawasaki Campus Princess":
                contestID = "00001";
                break;
            case "Guanzon Biker Babe":
                contestID = "00002";
                break;
            case "Miss Guanzon Bulilit":
                contestID = "00003";
                break;
            case "Honda Dream Boy":
                contestID = "00004";
                break;
            default:
                contestID = "00001"; 
        }

        
        if (poETabulation != null && contestID != null) {
            poETabulation.setContestID(contestID);
        }

        
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
    
    
    public void setParentController(FrmETabulationController parent) {
        this.poETabulation = parent;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        themeComboBox.getSelectionModel().selectFirst();
    }    
    
}
