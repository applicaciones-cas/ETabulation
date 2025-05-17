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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.base.GRider;

public class GBingoController implements Initializable {

    public static GRider oApp;
    private final String pxeModuleName = "Guanzon E - BINGO";

    @FXML
    private AnchorPane apMain;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnNext;
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
        btnBack.setOnAction(this::cmdButton_Click);
        btnNext.setOnAction(this::cmdButton_Click);
        btnReset.setOnAction(this::cmdButton_Click);
        initDrawLabel();
        initDrawGrid();
    }

    public void setScreenSize(int screenSize) {
        psScreenSize = screenSize;

    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();

        switch (lsButton) {
        }
    }

    private void initDrawLabel() {
        lblDrawLetter.setText("");
        lblDrawNo.setText("");
    }

    private void initDrawGrid() {
        gpDrawPanel.getChildren().clear();
        initBingoLetter();
        initBingoNo();
    }

    private void initBingoLetter() {

        for (int lnRow = 0; lnRow < bingoLetters.length; lnRow++) {
            try {
                FXMLLoader fxLoader;
                if (psScreenSize == 1920) {
                    fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoLetter1920.fxml"));
                } else {
                    fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoLetter1080.fxml"));
                }
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
                    if (psScreenSize == 1920) {
                        fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoNo1920.fxml"));
                    } else {
                        fxLoader = new FXMLLoader(getClass().getResource("/views/ModelBingoNo1080.fxml"));
                    }
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
    
    

}
