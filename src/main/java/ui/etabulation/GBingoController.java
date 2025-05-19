package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;

public class GBingoController implements Initializable {

    public static GRider oApp;
    private final String pxeModuleName = "Guanzon E - BINGO";

    @FXML
    private AnchorPane apMain;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnReset;
    @FXML
    private Label lblDrawLetter;
    @FXML
    private GridPane gpDrawPanel;
    @FXML
    private Label lblDrawNo;
    @FXML
    private TextField txtDrawnNo;

    private final String[] bingoLetters = {"B", "I", "N", "G", "O"};
    private final ObservableList<ModelBingoNoController> CtrlBingoNo = FXCollections.observableArrayList();
    private int psScreenSize;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //to do list
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnReset.setOnAction(this::cmdButton_Click);

        txtDrawnNo.setOnKeyPressed(this::txtField_KeyPressed);
        initDrawLabel();
        initDrawGrid();
    }

    public void setScreenSize(int screenSize) {
        psScreenSize = screenSize;

    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();

        switch (lsButton) {
            case "btnBrowse":
            case "btnNew":
                if (ShowMessageFX.YesNo(null, "New Bingo", "Are you sure, do you want to New Transaction?") == true) {
                    initDrawLabel();
                    initDrawGrid();
                }
                break;
            case "btnReset":
                if (ShowMessageFX.YesNo(null, "Reset Bingo", "Are you sure, do you want to Reset?") == true) {
                    initDrawLabel();
                    initDrawGrid();
                }
                break;
        }
    }

    private void initDrawLabel() {
        lblDrawLetter.setText("");
        lblDrawNo.setText("");
        txtDrawnNo.setText("");

    }

    private void initDrawGrid() {
        gpDrawPanel.getChildren().clear();
        CtrlBingoNo.clear();
        initBingoLetter();
        initBingoNo();
    }

    private void initBingoLetter() {

        for (int lnRow = 0; lnRow < bingoLetters.length; lnRow++) {
            try {
                FXMLLoader fxLoader;
                fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoLetter" + String.valueOf(psScreenSize) + ".fxml"));

                AnchorPane modelPane = fxLoader.load();
                ModelBingoLetterController loController = fxLoader.getController();
                loController.setLetter(bingoLetters[lnRow]);
                gpDrawPanel.add(modelPane, 0, lnRow); // Add to column 0, row N
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void initBingoNo() {
        int lnBingoNumber = 15;
        for (int lnRow = 0; lnRow < bingoLetters.length; lnRow++) {
            for (int lnCol = 1; lnCol <= lnBingoNumber; lnCol++) {
                try {
                    FXMLLoader fxLoader;
                    fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoNo" + String.valueOf(psScreenSize) + ".fxml"));

                    AnchorPane modelPane = fxLoader.load();
                    ModelBingoNoController loController = fxLoader.getController();

                    int lnNumber = lnRow * 15 + lnCol;
                    loController.setNo(String.valueOf(lnNumber));
                    loController.setNoVisible(false);

                    CtrlBingoNo.add(loController);
                    gpDrawPanel.add(modelPane, lnCol, lnRow);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ModelBingoNoController getControllerByNumber(int lnCtrlNo) {
        if (lnCtrlNo < 1 || lnCtrlNo > 75) {
            return null;
        }
        return CtrlBingoNo.get(lnCtrlNo - 1);
    }

    @FXML
    private void MainAnchorPane_Keypress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?") == true) {
                Stage stage = (Stage) apMain.getScene().getWindow();

                oApp = null;
                stage.close();
            }
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lnTextNm = txtField.getId();

        if (event.getCode() == KeyCode.TAB
                || event.getCode() == KeyCode.ENTER
                || event.getCode() == KeyCode.F3) {
            switch (lnTextNm) {

                case "txtDrawnNo":
                try {
                    int lnDrawNo = Integer.parseInt(txtField.getText());

                    if (lnDrawNo >= 1 && lnDrawNo <= 75) {
                        ModelBingoNoController controller = getControllerByNumber(lnDrawNo);
                        if (controller != null) {
                            controller.setNoVisible(true);
                            setDrawRecord(lnDrawNo);
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid number entered: " + txtField.getText());
                }
                txtDrawnNo.setText("");
                txtDrawnNo.requestFocus();
                break;
            }

            event.consume();
        } else if (event.getCode() == KeyCode.UP) {
            event.consume();
            CommonUtils.SetPreviousFocus((TextField) event.getSource());
        }
    }

    private void setDrawRecord(int lnDrawNo) {
        if (lnDrawNo < 1 || lnDrawNo > 75) {
            lblDrawLetter.setText("?");
            lblDrawNo.setText("");
            return;
        }

        int index = (lnDrawNo - 1) / 15;
        String lsDrawLetter = bingoLetters[index];

        lblDrawLetter.setText(lsDrawLetter);
        lblDrawNo.setText(String.valueOf(lnDrawNo));

    }

}
