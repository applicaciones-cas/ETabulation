package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.agent.ShowMessageFX;

/**
 * FXML Controller class
 *
 * @author Maynard
 */
public class ChildJudgeController implements Initializable {

    private boolean pbLoaded = false;
    private String p_sJudge = "";

    @FXML
    private AnchorPane apMain;
    @FXML
    private TextField txtField01;

    @FXML
    private void FieldKeyPressed(KeyEvent event) {

    }

    public String getJudgeNo() {
        p_sJudge = txtField01.getText();
        return p_sJudge;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pbLoaded = true;
        txtField01.requestFocus();
        apMain.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::cmdForm_Keypress);
            }
        });
    }

    private void stageclose() {
        Platform.runLater(() -> {
            Stage stage = (Stage) apMain.getScene().getWindow();
            stage.close();
        });
    }

    private void cmdForm_Keypress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?") == true) {
                Stage stage = (Stage) apMain.getScene().getWindow();
                stage.hide();
                event.consume();
            }
        }
        if (event.getCode() == KeyCode.ENTER) {
            p_sJudge = txtField01.getText();

            if (p_sJudge.isEmpty()) {
                ShowMessageFX.Information("Please Enter your Name ! Thank you", "Warning", null);
                txtField01.requestFocus();
                return;
            }

            Stage stage = (Stage) apMain.getScene().getWindow();
            stage.hide();
            event.consume();

        }
    }

}
