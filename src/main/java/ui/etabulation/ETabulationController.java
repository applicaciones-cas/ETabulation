package ui.etabulation;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.gtabulate.Scoring;
import ph.com.guanzongroup.gtabulate.model.Model_Contest_Participants;
import ph.com.guanzongroup.gtabulate.model.Model_Contest_Participants_Meta;
import ph.com.guanzongroup.gtabulate.model.services.TabulationControllers;

/**
 * FXML Controller class
 *
 * @author Maynard
 */
public class ETabulationController implements Initializable {

    @FXML
    private AnchorPane apMain;
    @FXML
    private AnchorPane apMainBanner, apContestant;
    @FXML
    private ImageView imgBanner;
    @FXML
    private Label lblContestTitle;
    @FXML
    private AnchorPane apBottomPanel;
    @FXML
    private Label lblPerforming;
    @FXML
    private Label lblContestantName;
    @FXML
    private Label lblContestDetail;
    @FXML
    private Label lblJudgeNm;
    @FXML
    private AnchorPane apCenterPanel;
    @FXML
    private ScrollPane spCenter;
    @FXML
    private ImageView ivContestant;
    @FXML
    private TableView<TableModelETabulation> tblCandidate;
    private final ObservableList<TableModelETabulation> paParticipants = FXCollections.observableArrayList();

    private static GRiderCAS oApp;
    private Scoring oTrans;
    private String psContestDescript = "";
    private String psContestID = "";
    private String psJudgeName = "";
    private JSONObject poJSON;
    private int pnRow = -1;
    private boolean pbLoaded = true;
    private String pxeModuleName = "Tabulation Controller";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            clearForm();
            oTrans = new TabulationControllers(oApp, null).Scoring();
            poJSON = oTrans.initTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Init failed: " + poJSON.get("message"));
            }

            poJSON = oTrans.ContestMaster().searchRecord("", false);
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Init failed: " + poJSON.get("message"));
            } else {
                psContestID = oTrans.ContestMaster().getModel().getContestId();
                psContestDescript = oTrans.ContestMaster().getModel().getDescription();

                oTrans.setContestId(psContestID);

            }
            if (psContestID.isEmpty()) {
                ShowMessageFX.Error("Unable to load Record, Constest ID is Required.", "Warning", "");
                pbLoaded = false;

            }
            initController();

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(FrmETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setGRider(GRiderCAS foApp) {
        oApp = foApp;
    }

    private void cmdForm_Keypress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?") == true) {
                Stage stage = (Stage) apMain.getScene().getWindow();

                oApp = null;
                stage.close();
                event.consume();
            }
        }
    }

    private JSONObject initRecord() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;

        loJSON = oTrans.loadCriteriaForJudging();
        if (!"success".equals(loJSON.get("result"))) {
            System.err.println("Criteria load failed: " + loJSON.get("message"));
        }
        loJSON = oTrans.loadParticipants();
        if (!"success".equals(loJSON.get("result"))) {
            System.err.println("Participants load failed: " + loJSON.get("message"));
        }
        return loJSON;
    }

    private void initCriteria() {

        int criteriaCount = oTrans.getCriteriaCount();
        double contestantColumnRatio;
        if (criteriaCount > 4) {
            contestantColumnRatio = 0.18;
        } else {
            contestantColumnRatio = 0.21;
        }
        double remainingRatio = 1.0 - contestantColumnRatio;

        // Contestant Column
        TableColumn index01 = new TableColumn("Contestant");
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index01.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 10;");
        tblCandidate.getColumns().add(index01);

        // Bind width
        index01.prefWidthProperty().bind(tblCandidate.widthProperty().multiply(contestantColumnRatio));

        for (int i = 0; i < criteriaCount; i++) {
            final int criteriaIndex = i;
            final int columnIndex = i + 2;
            String criteriaDesc = oTrans.Criteria(i).getDescription();
            BigDecimal percent = (BigDecimal) oTrans.Criteria(i).getPercentage();
            String columnTitle;
            if (criteriaCount > 4 && criteriaDesc.length() > 12) {
                columnTitle = criteriaDesc.replace(" ", "\n").toUpperCase()
                        + "(" + CommonUtils.NumberFormat(percent, "#0") + "%)";
            } else {
                columnTitle = criteriaDesc.toUpperCase()
                        + "\n(" + CommonUtils.NumberFormat(percent, "#0") + "%)";
            }
            TableColumn<TableModelETabulation, String> column = new TableColumn<>(columnTitle);
            column.setStyle("-fx-alignment: CENTER;");
            column.setSortable(false);
            column.setEditable(true);

            column.setCellValueFactory(new PropertyValueFactory<>("index0" + columnIndex));

            column.setCellFactory(col -> new TableCell<TableModelETabulation, String>() {
                private final TextField textField = new TextField();
                private final StackPane pane = new StackPane(textField);

                {
                    textField.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(textField, Pos.CENTER);
                    pane.getStyleClass().add("stack-pane-editable");

                    textField.setTextFormatter(new TextFormatter<String>(change -> {
                        String newText = change.getControlNewText();

                        if (newText.isEmpty()) {
                            return change;
                        }

                        try {
                            double value = Double.parseDouble(newText);

                            if (value >= 0 && value <= 100) {
                                if (newText.matches("^\\d{0,3}(\\.\\d{0,2})?$")) {
                                    return change;
                                }
                            }
                        } catch (NumberFormatException e) {
                            textField.setText("0.00");
                        }
                        return null; // reject change
                    }));

                    textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (isNowFocused) {
                            getTableView().getSelectionModel().select(getIndex());

                        } else {
                            try {
                                double lnRate = Double.parseDouble(textField.getText());
                                if (lnRate < 0 || lnRate > 100) {
                                    lnRate = 100.00;
                                }
                                commitEdit(CommonUtils.NumberFormat(lnRate, "#0.00"));
                            } catch (NumberFormatException ex) {
                                commitEdit("0.00");
                            }
                        }
                    });
                    textField.setOnMouseClicked(e -> {
                        getTableView().getSelectionModel().select(getIndex());
                        int currentRow = getIndex();
                        if (currentRow > pnRow || currentRow < pnRow) {

                            loadParticipants();

                            TableView<TableModelETabulation> table = getTableView();
                            TableColumn<TableModelETabulation, ?> currentColumn = getTableColumn();
                            int currentColIndex = table.getVisibleLeafIndex(currentColumn);
                            TableColumn<TableModelETabulation, ?> nextCol = table.getVisibleLeafColumn(currentColIndex);

                            table.edit(currentRow, nextCol);

                            TableCell<TableModelETabulation, ?> cell = getCell(tblCandidate, currentRow, currentColIndex);
                            if (cell != null && cell.isEditing()) {
                                Node graphic = cell.getGraphic();
                                if (graphic instanceof StackPane) {
                                    StackPane pane = (StackPane) graphic;
                                    for (Node child : pane.getChildren()) {
                                        if (child instanceof TextField) {
                                            TextField tf = (TextField) child;
                                            tf.requestFocus();
                                            tf.selectAll();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        pnRow = currentRow;
                        getSelected(pnRow);
                    });

                    textField.setOnAction(e -> commitEdit(textField.getText()));
                    textField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.TAB
                                || event.getCode() == KeyCode.ENTER) {
                            commitEdit(textField.getText());

                            TableView<TableModelETabulation> table = getTableView();
                            int currentRow = pnRow;
                            TableColumn<TableModelETabulation, ?> currentColumn = getTableColumn();
                            int currentColIndex = table.getVisibleLeafIndex(currentColumn);
                            int totalCols = table.getVisibleLeafColumns().size();

                            int nextRow = currentRow;
                            int nextColIndex;

                            if (currentColIndex < totalCols - 2) {
                                nextColIndex = currentColIndex + 1;
                            } else if (currentRow < table.getItems().size() - 1) {
                                nextRow = currentRow + 1;
                                nextColIndex = 1;
                                loadParticipants();
                                pnRow = nextRow;
                            } else {
                                event.consume();
                                return;
                            }

                            TableColumn<TableModelETabulation, ?> nextCol = table.getVisibleLeafColumn(nextColIndex);
                            int finalNextRow = nextRow;

                            table.edit(finalNextRow, nextCol);

                            TableCell<TableModelETabulation, ?> cell = getCell(tblCandidate, finalNextRow, nextColIndex);
                            if (cell != null && cell.isEditing()) {
                                Node graphic = cell.getGraphic();
                                if (graphic instanceof StackPane) {
                                    StackPane pane = (StackPane) graphic;
                                    for (Node child : pane.getChildren()) {
                                        if (child instanceof TextField) {
                                            TextField tf = (TextField) child;
                                            tf.requestFocus();
                                            tf.selectAll();
                                            break;
                                        }
                                    }
                                }
                            }
                            lblContestantName.setText(paParticipants.get(finalNextRow).getIndex01().toString());
                            lblContestDetail.setText(paParticipants.get(finalNextRow).getIndex09().toString());
                            setContestantImage(finalNextRow + 1);
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        textField.setText(item);
                        setGraphic(pane);
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    textField.requestFocus();
                    textField.selectAll();
                }

                @Override
                public void commitEdit(String newValue) {
                    super.commitEdit(newValue);
                    ObservableList<TableModelETabulation> items = getTableView().getItems();
                    int rowIndex = getIndex();
                    if (rowIndex >= 0 && rowIndex < items.size()) {
                        TableModelETabulation loContestant = items.get(rowIndex);
                        try {
                            Method method = TableModelETabulation.class.getMethod("setIndex0" + columnIndex, String.class);
                            method.invoke(loContestant, newValue);
                            if (newValue != null && loContestant.getIndex11() != null) {
                                setTabulationDetail(loContestant.getIndex11(), criteriaIndex, newValue);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            });

            // Width binding
            column.prefWidthProperty().bind(tblCandidate.widthProperty()
                    .multiply(remainingRatio).divide(criteriaCount + 1));

            tblCandidate.getColumns().add(column);
        }

        // TOTAL column (fixed width)
        TableColumn<TableModelETabulation, String> indexTotal = new TableColumn<>("TOTAL");
        indexTotal.setCellValueFactory(new PropertyValueFactory<>("index10"));
        indexTotal.setStyle("-fx-alignment: CENTER;");
        indexTotal.setPrefWidth(95);
        indexTotal.setSortable(false);
        indexTotal.setResizable(false);
        tblCandidate.getColumns().add(indexTotal);

        // Prevent column header reordering
        tblCandidate.widthProperty().addListener((obs, oldW, newW) -> {
            TableHeaderRow header = (TableHeaderRow) tblCandidate.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((o, ov, nv) -> header.setReordering(false));
            }
        });

        // Other setup
        TableColumn<?, ?> firstCol = tblCandidate.getColumns().get(0);
        firstCol.getStyleClass().add("header-1");
        tblCandidate.getColumns().forEach(col -> col.setSortable(false));
        tblCandidate.setFixedCellSize(65);
        tblCandidate.setEditable(true);

        tblCandidate.setItems(paParticipants);

        //animete    
        tblCandidate.setRowFactory(tv -> {
            TableRow<TableModelETabulation> tblRow = new TableRow<>();
            tblRow.selectedProperty().addListener((obs, oldSel, newSel) -> {
                ETabulationUtils.updateRowStyle(tblRow, tblRow.getIndex());
            });
            tblRow.itemProperty().addListener((obs, oldItem, newItem) -> {
                ETabulationUtils.updateRowStyle(tblRow, tblRow.getIndex());
            });
            return tblRow;
        });

    }

    @SuppressWarnings("unchecked")
    private TableCell<TableModelETabulation, ?> getCell(
            TableView<TableModelETabulation> table, int rowIndex, int colIndex) {

        for (Node rowNode : table.lookupAll(".table-row-cell")) {
            if (rowNode instanceof TableRow<?>) {
                TableRow<?> rawRow = (TableRow<?>) rowNode;

                if (rawRow.getIndex() == rowIndex) {
                    TableRow<TableModelETabulation> row = (TableRow<TableModelETabulation>) rawRow;

                    for (Node cellNode : row.lookupAll(".table-cell")) {
                        if (cellNode instanceof TableCell<?, ?>) {
                            TableCell<?, ?> rawCell = (TableCell<?, ?>) cellNode;

                            TableCell<TableModelETabulation, ?> cell = (TableCell<TableModelETabulation, ?>) rawCell;

                            TableColumn<TableModelETabulation, ?> tableColumn
                                    = (TableColumn<TableModelETabulation, ?>) cell.getTableColumn();

                            if (tableColumn != null && table.getVisibleLeafIndex(tableColumn) == colIndex) {
                                return cell;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void initController() {
        apMain.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::cmdForm_Keypress);
                if (!pbLoaded) {
                    stageclose();
                    return;
                }
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.showingProperty().addListener((obsShowing, wasShowing, isNowShowing) -> {
                            if (isNowShowing) {
                                Platform.runLater(() -> initLoadForm());
                            }
                        });
                    }
                });
            }
        });

        Platform.runLater(() -> {
            spCenter.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                double scrollY = newVal.doubleValue();
                double maxV = spCenter.getContent().getBoundsInLocal().getHeight() - spCenter.getViewportBounds().getHeight();
                apContestant.setLayoutY(scrollY * maxV);
            });
        });
    }

    private void initLoadForm() {
        try {
            psJudgeName = loadJudge();
            if (psJudgeName.isEmpty()) {
//                ShowMessageFX.Warning("Unable to load Record, Judge is Required.", "Warning", null);
                pbLoaded = false;
                stageclose();
                return;
            }
            oTrans.setJudgeName(psJudgeName);
            lblContestTitle.setText(psContestDescript);
            lblJudgeNm.setText(psJudgeName.toUpperCase());

            poJSON = initRecord();
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Init failed: " + poJSON.get("message"));
                pbLoaded = false;
                return;
            }
            initCriteria();
            loadParticipants();
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadParticipants() {
        try {
            paParticipants.clear();

            double lnTotal = 0.00;
            String index02Value = "";
            String index03Value = "";
            String index04Value = "";
            String index05Value = "";
            String index06Value = "";
            String index07Value = "";
            String index08Value = "";
            int loParticipantCount = oTrans.getParticipantsCount();
            for (int lnCtr = 0; lnCtr < loParticipantCount; lnCtr++) {
                Model_Contest_Participants poParticipants = oTrans.Participant(lnCtr);
                oTrans.getMaster().setGroupId(poParticipants.getGroupId());
                oTrans.getMaster().setComputerId(oApp.getTerminalNo());
                oTrans.openTransaction(poParticipants.getGroupId(), oApp.getTerminalNo());
                Model_Contest_Participants_Meta poParticipantMetaNm = oTrans.ParticipantMeta(poParticipants.getGroupId(), "00001");
                String lsContestantName = poParticipants.getEntryNo() + ". " + poParticipantMetaNm.getValue();

                Model_Contest_Participants_Meta poParticipantMetaSchool = oTrans.ParticipantMeta(poParticipants.getGroupId(), "00002");
                String lsDetailSchool = poParticipantMetaSchool.getValue();

                Model_Contest_Participants_Meta poParticipantMetaImg = oTrans.ParticipantMeta(poParticipants.getGroupId(), "00003");
                String lsCandidateImg = poParticipantMetaImg.getValue();
                lsCandidateImg = System.getProperty("sys.default.path.images") + lsCandidateImg.substring(45);
                lnTotal = 0.0;
                int loCriteria = oTrans.getCriteriaCount();
                for (int lnCtrCriterea = 0; lnCtrCriterea < loCriteria; lnCtrCriterea++) {
                    Double loRate = oTrans.getDetail(lnCtrCriterea + 1).getRate();
                    BigDecimal loRatePercencentage = oTrans.Criteria(lnCtrCriterea).getPercentage();
                    double critValue = (loRate != null) ? loRate : 0.0;
                    double critPercentage = (loRatePercencentage != null) ? Double.valueOf(String.valueOf(loRatePercencentage)) : 0.0;

                    switch (lnCtrCriterea) {
                        case 0:
                            index02Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 1:
                            index03Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        case 2:
                            index04Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        case 3:
                            index05Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        case 4:
                            index06Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        case 5:
                            index07Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        case 6:
                            index08Value = CommonUtils.NumberFormat(critValue, "##0.00");
                            break;
                        default:
                            // Handle additional criteria values if needed
                            break;
                    }
                    lnTotal += critValue * (critPercentage / 100.0);
                }
                paParticipants.add(new TableModelETabulation(
                        lsContestantName,
                        index02Value,
                        index03Value,
                        index04Value,
                        index05Value,
                        index06Value,
                        index07Value,
                        index08Value,
                        lsDetailSchool,
                        CommonUtils.NumberFormat(lnTotal, "##0.00"),
                        poParticipants.getGroupId()
                ));

            }
            if (pnRow < 0) {
                //select first
                tblCandidate.getSelectionModel().select(0);
                pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
                getSelected(pnRow);
            }
//            else {
//                tblCandidate.getSelectionModel().select(pnRow);
//                pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
//                getSelected(pnRow);
//            }

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void tblParticipantClick(MouseEvent event) {
        int oldRow = pnRow;
        pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
        if (pnRow <= 0) {
            return;
        }

        if (oldRow > pnRow || oldRow < pnRow) {

            loadParticipants();
            getSelected(pnRow);

            TableView<TableModelETabulation> table = tblCandidate;
            TableColumn<TableModelETabulation, ?> firstCol = table.getVisibleLeafColumn(1);

            table.edit(pnRow, firstCol);

            TableCell<TableModelETabulation, ?> cell = getCell(tblCandidate, pnRow, 1);
            if (cell != null && cell.isEditing()) {
                Node graphic = cell.getGraphic();
                if (graphic instanceof StackPane) {
                    StackPane pane = (StackPane) graphic;
                    for (Node child : pane.getChildren()) {
                        if (child instanceof TextField) {
                            TextField tf = (TextField) child;
                            tf.requestFocus();
                            tf.selectAll();
                            break;
                        }
                    }
                }
            }
        }
        // Key navigation (Up/Down) handling as you had before
        tblCandidate.setOnKeyReleased((KeyEvent t) -> {
            KeyCode key = t.getCode();
            switch (key) {
                case DOWN:
                    pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
                    if (pnRow >= tblCandidate.getItems().size() - 1) {
                        pnRow = tblCandidate.getItems().size() - 1;
                    } else {
                        pnRow = pnRow + 1;
                    }
                    break;

                case UP:
                    pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
                    if (pnRow < 1) {
                        pnRow = 0;
                    } else {
                        pnRow = pnRow - 1;
                    }
                    break;

                default:
                    return;
            }

            getSelected(pnRow + 1);

        });
    }

    private void getSelected(int foRow) {
        lblContestantName.setText(paParticipants.get(pnRow).getIndex01().toString());
        lblContestDetail.setText(paParticipants.get(pnRow).getIndex09().toString());

        setContestantImage(foRow + 1);
    }

    private void setTabulationDetail(String foGroup, int foCriteria, String foValue) {
        try {
            if (oApp == null) {
                return;
            }
            if (foValue.isEmpty()) {
                return;
            }
            poJSON = oTrans.openTransaction(foGroup, oApp.getTerminalNo());

            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                return;
            } else {
                System.out.println("Tabulation = " + foGroup + " is Loaded");
            }
            oTrans.getDetail(foCriteria + 1).setRate(Double.valueOf(foValue));

            poJSON = oTrans.saveTransaction();
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setContestantImage(int participantNumber) {
        String folderPath = "D:\\GGC_Maven_Systems\\images\\" + contestFolder(psContestID) + "\\" + participantNumber + "\\1.png";
        File imgFile = new File(folderPath);

        if (imgFile.exists()) {
            ivContestant.setImage(new Image(imgFile.toURI().toString()));
            apContestant.setVisible(true);
        } else {
            ivContestant.setImage(null);
            apContestant.setVisible(false);
        }
    }

    public String contestFolder(String ContestID) {

        switch (ContestID) {
            case "00001":
                return "campus";
            case "00002":
                return "babe";
            case "00003":
                return "bulilit";
            case "00004":
                return "dream";
            default:
                return "campus";
        }
    }

    private void stageclose() {
        Platform.runLater(() -> {
            Stage stage = (Stage) apMain.getScene().getWindow();
            oApp = null;
            stage.close();
        });
    }

    private String loadJudge() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/childJudge.fxml"));

            ChildJudgeController loControl = new ChildJudgeController();

            fxmlLoader.setController(loControl);

            Stage stage = new Stage();
            //load the main interface
            Parent parent = fxmlLoader.load();

            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.showAndWait();

            String loJudge = loControl.getJudgeNo().trim();

            stage.close();
            return loJudge;

        } catch (IOException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(e.getMessage(), "Warning", null);
            System.exit(1);
        }
        return "";
    }

    private void clearForm() {
        ivContestant.setImage(null);
        lblContestTitle.setText("");
        lblContestDetail.setText("");
        lblContestantName.setText("");
        lblJudgeNm.setText("");
        psJudgeName = "";
        psContestID = "";
        psContestDescript = "";
        paParticipants.clear();

    }

}
