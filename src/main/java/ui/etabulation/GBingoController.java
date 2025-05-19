package ui.etabulation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private StackPane spBingoLetter;
    @FXML
    private Label lblBingoLetter;
    @FXML
    private StackPane spBingoNo;
    @FXML
    private Label lblBingoNo;

    private final ObservableList<Label> bingo = FXCollections.observableArrayList();

    String[] bingoLetters = {"B", "I", "N", "G", "O"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //to do list
        btnBack.setOnAction(this::cmdButton_Click);
        btnNext.setOnAction(this::cmdButton_Click);
        btnReset.setOnAction(this::cmdButton_Click);
        initDrawLabel();
        initDrawGrid();
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();

        switch (lsButton) {
        }
    }

    private void initDrawLabel() {
        lblDrawLetter.setText("");
        lblDrawLetter.setText("");
    }

    private void initDrawGrid() {
        gpDrawPanel.getChildren().clear();
        
        for (int row = 0; row < bingoLetters.length; row++) {
            lblBingoLetter = new Label(bingoLetters[row]);
            gpDrawPanel.add(lblBingoLetter, 0, row); // column 0 for labels
        }


    }
}
