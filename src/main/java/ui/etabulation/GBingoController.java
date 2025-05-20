package ui.etabulation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.gtabulate.Bingo;
import ph.com.guanzongroup.gtabulate.model.services.TabulationControllers;

public class GBingoController implements Initializable {

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
    @FXML
    private ImageView ivPattern;

    public static GRiderCAS oApp;
    private final String pxeModuleName = "Guanzon E - BINGO";
    private final String[] bingoLetters = {"B", "I", "N", "G", "O"};
    private final ObservableList<ModelBingoNoController> CtrlBingoNo = FXCollections.observableArrayList();
    private int psScreenSize;
    private LogWrapper poLogWrapper;
    private Bingo oTrans;
    private JSONObject poJSON;
    private String poTransaction = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (oApp == null) {
            poLogWrapper.severe(pxeModuleName + ": Application driver is not set.");
            Stage stage = (Stage) apMain.getScene().getWindow();
            stage.close();
        }

        try {
            //to do list
            oTrans = new TabulationControllers(oApp, poLogWrapper).Bingo();
            poJSON = oTrans.initTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Unable to Initialize Class " + poJSON.get("message"));
                Stage stage = (Stage) apMain.getScene().getWindow();
                stage.close();
            }

            btnBrowse.setOnAction(this::cmdButton_Click);
            btnNew.setOnAction(this::cmdButton_Click);
            btnReset.setOnAction(this::cmdButton_Click);

            Platform.runLater(() -> {
                apMain.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                        this::cmdForm_Keypress);
            });
            txtDrawnNo.setOnKeyPressed(this::txtField_KeyPressed);
            initDrawLabel();
            initDrawGrid();
        } catch (SQLException ex) {
            Logger.getLogger(GBingoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(GBingoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setScreenSize(int screenSize) {
        psScreenSize = screenSize;
    }

    public void setGRider(GRiderCAS poApp) {
        oApp = poApp;
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();

        switch (lsButton) {
            case "btnBrowse":
                if (ShowMessageFX.YesNo(null, "Load Bingo", "Are you sure, do you want to Load Transaction?") == true) {
                    initDrawLabel();
                    initDrawGrid();
                    poTransaction = "";
                    poJSON = oTrans.searchTransaction("", true);
                    if (!"success".equals(poJSON.get("result"))) {
                        System.err.println("Unable to load Pattern: " + poJSON.get("message"));
                        poTransaction = "";
                    }
                    poTransaction = oTrans.getMaster().getTransactionNo();
                    loadTransaction();
                    txtDrawnNo.requestFocus();
                }
                break;
            case "btnNew":
                if (ShowMessageFX.YesNo(null, "New Bingo", "Are you sure, do you want to New Transaction?") == true) {
                    initDrawLabel();
                    initDrawGrid();
                    poTransaction = "";
                }
                break;
            case "btnReset":
                if (ShowMessageFX.YesNo(null, "Reset Bingo", "Are you sure, do you want to Reset?") == true) {
                    initDrawLabel();
                    initDrawGrid();
                    poTransaction = "";
                }
                break;
        }
    }

    private void initDrawLabel() {
        lblDrawLetter.setText("");
        lblDrawNo.setText("");
        txtDrawnNo.setText("");
        txtDrawnNo.setTextFormatter(new TextFormatter<String>(foUpdate -> {
            String newUpdate = foUpdate.getControlNewText();
            if (newUpdate.matches("\\d{0,3}")) {
                return foUpdate;
            }
            return null;
        }));

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

    private void cmdForm_Keypress(KeyEvent event) {
        try {
            JSONObject loJSON;
            switch (event.getCode()) {
                case ESCAPE:
                    if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?") == true) {
                        Stage stage = (Stage) apMain.getScene().getWindow();

                        oApp = null;
                        stage.close();
                        event.consume();
                    }
                    return;
                case F3://search Pattern
                    loJSON = oTrans.BingoPattern().searchRecord("", false);
                    if (!"success".equals(loJSON.get("result"))) {
                        System.err.println("Unable to load Pattern: " + loJSON.get("message"));
                    }
                    setPatternBingo();

                    event.consume();
                    return;

            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(GBingoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lnTextNm = txtField.getId();
        switch (event.getCode()) {
            case TAB:
            case ENTER:
                    try {
                int lnDrawNo = Integer.parseInt(txtField.getText());

                if (lnDrawNo >= 1 && lnDrawNo <= 75) {
                    ModelBingoNoController getBingoCtrl = getControllerByNumber(lnDrawNo);
                    if (getBingoCtrl != null) {
                        if (!getBingoCtrl.getNoVisible()) {
                            getBingoCtrl.setNoVisible(true);
                            JSONObject loJSON;
                            loJSON = oTrans.openTransaction(poTransaction);

                            if (!"success".equals((String) loJSON.get("result"))) {
                                System.err.println((String) loJSON.get("message"));
                            }
                            poTransaction = oTrans.getMaster().getTransactionNo();

                            oTrans.getDetail(oTrans.getDetailCount()).setBingoNo(lnDrawNo);
                            setDrawRecord(oTrans.getDetail(oTrans.getDetailCount() - 1).getBingoNo());
                            //save record
                            loJSON = oTrans.saveTransaction();
                            if (!"success".equals((String) loJSON.get("result"))) {
                                System.err.println((String) loJSON.get("message"));
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid number entered: " + txtField.getText());

            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                Logger.getLogger(GBingoController.class.getName()).log(Level.SEVERE, null, ex);
            }
            txtDrawnNo.setText("");
            txtDrawnNo.requestFocus();

            event.consume();
            break;

            case UP:
                event.consume();
                CommonUtils.SetPreviousFocus((TextField) event.getSource());
                return;

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

    private void setPatternBingo() {
        String patternID = Optional.ofNullable(oTrans.BingoPattern().getModel().getPatternId()).orElse("");
        String fsBingo = Optional.ofNullable(oTrans.BingoPattern().getModel().getImagePath()).orElse("");

        if (!patternID.isEmpty() && !fsBingo.isEmpty()) {
            InputStream imageStream = getClass().getResourceAsStream("/images/" + fsBingo);

            if (imageStream != null) {
                ivPattern.setImage(new Image(imageStream));
                oTrans.getMaster().setPatternId(patternID);
            } else {
                System.err.println("Image not found: /images/" + fsBingo);
                ivPattern.setImage(null);
            }
        } else {
            ivPattern.setImage(null);
        }
    }

    private void loadTransaction() {
        setPatternBingo();
        for (int lnCtr = 0; lnCtr < oTrans.getDetailCount(); lnCtr++) {
            int DetailDraw = oTrans.getDetail(lnCtr).getBingoNo();
            setDrawRecord(DetailDraw);
            if (DetailDraw >= 1 && DetailDraw <= 75) {
                ModelBingoNoController controller = getControllerByNumber(DetailDraw);
                if (controller != null) {
                    controller.setNoVisible(true);
                }
            }
        }

    }
}
