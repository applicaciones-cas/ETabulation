package ui.etabulation;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
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
    private final ObservableList<TableModelETabulation> paCriteria = FXCollections.observableArrayList();
    private final List<List<TextField>> textFieldGrid = new ArrayList<>();

    private static GRiderCAS oApp;
    private Scoring oTrans;
    private String psContestID = "";
    private String psJudgeName = "";
    private JSONObject poJSON;
    private int pnRow = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //for testing
        psContestID = "00001";
        psJudgeName = "Waluigi";
        try {
            oTrans = new TabulationControllers(oApp, null).Scoring();
            oTrans.setContestId(psContestID);
            oTrans.setJudgeName(psJudgeName);

            poJSON = oTrans.initTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Init failed: " + poJSON.get("message"));
            }

            poJSON = initRecord();
            if (!"success".equals(poJSON.get("result"))) {
                System.err.println("Init failed: " + poJSON.get("message"));
            }
            lblContestDetail.setText("");
            lblContestantName.setText("");
            initRecord();
            initCriteria();
            initImages();
            initListener();

            loadParticipants();

        } catch (SQLException ex) {
            Logger.getLogger(FrmETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(FrmETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FrmETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO
    }

    void setGRider(GRiderCAS foApp) {
        oApp = foApp;
    }

    private void cmdForm_Keypress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?") == true) {
                Stage stage = (Stage) apMain.getScene().getWindow();

                oApp = null;
                stage.close();
            }
        }
    }

    private void cmdTable_Keypress(KeyEvent event) {

        if (event.getCode() == KeyCode.TAB) {
            TablePosition<?, ?> pos = tblCandidate.getFocusModel().getFocusedCell();
            int row = pos.getRow();
            int col = pos.getColumn();
            int colCount = tblCandidate.getVisibleLeafColumns().size();
            int nextCol = (col + 1) % colCount;

            while (nextCol == 0 || nextCol == colCount - 1
                    || !tblCandidate.getVisibleLeafColumns().get(nextCol).isEditable()) {
                nextCol = (nextCol + 1) % colCount;
            }

//            tblCandidate.getFocusModel().focus(row,
//                    (TableColumn<TableModelETabulation.Result, ?>) tblCandidate.getVisibleLeafColumns().get(nextCol));
//            tblCandidate.edit(row,
//                    (TableColumn<TableModelETabulation.Result, ?>) tblCandidate.getVisibleLeafColumns().get(nextCol));
            event.consume();
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
        double contestantColumnRatio = 0.21; // 21% for contestant column
        double remainingRatio = 1.0 - contestantColumnRatio;
        int criteriaCount = oTrans.getCriteriaCount();

        // Contestant Column
        TableColumn index01 = new TableColumn("Contestant");
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index01.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 10;");
        tblCandidate.getColumns().add(index01);

        // Bind width
        index01.prefWidthProperty().bind(tblCandidate.widthProperty().multiply(contestantColumnRatio));

        for (int i = 0; i < criteriaCount; i++) {
            final int columnIndex = i + 2;
            String criteriaDesc = oTrans.Criteria(i).getDescription();
            BigDecimal percent = (BigDecimal) oTrans.Criteria(i).getPercentage();
            String columnTitle = criteriaDesc.toUpperCase() + "\n(" + CommonUtils.NumberFormat(percent, "#0") + "%)";

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

                    // Commit edit on Enter key press
                    textField.setOnAction(e -> {
                        commitEdit(textField.getText());
                    });

                    // Cancel edit on focus lost
                    textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (isNowFocused) {
                            getTableView().getSelectionModel().select(getIndex());
                        } else {
                            commitEdit(textField.getText());
                        }
                    });
                    textField.setOnMouseClicked(e -> {
                        getTableView().getSelectionModel().select(getIndex());
                    });

                    textField.setOnAction(e -> commitEdit(textField.getText()));
                    textField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.TAB) {
                            commitEdit(textField.getText());

                            TableView<TableModelETabulation> table = getTableView();
                            int currentRow = getIndex();
                            TableColumn<TableModelETabulation, ?> currentColumn = getTableColumn();
                            int currentColIndex = table.getVisibleLeafIndex(currentColumn);
                            int totalCols = table.getVisibleLeafColumns().size();

                            int nextRow = currentRow;
                            int nextColIndex;

                            if (event.isShiftDown()) {
                                if (currentColIndex > 1) {
                                    nextColIndex = currentColIndex - 1;
                                } else if (currentRow > 0) {
                                    nextRow = currentRow - 1;
                                    nextColIndex = totalCols - 2;
                                } else {
                                    event.consume();
                                    return;
                                }
                            } else {
                                if (currentColIndex < totalCols - 2) {
                                    nextColIndex = currentColIndex + 1;
                                } else if (currentRow < table.getItems().size() - 1) {
                                    nextRow = currentRow + 1;
                                    nextColIndex = 1;
                                } else {
                                    event.consume();
                                    return;
                                }
                            }

                            TableColumn<TableModelETabulation, ?> nextCol = table.getVisibleLeafColumn(nextColIndex);
                            int finalNextRow = nextRow;

                            Platform.runLater(() -> {
                                table.edit(finalNextRow, nextCol);

                                // Try to find the TextField in the new cell and request focus
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
                            });
                            lblContestantName.setText(paParticipants.get(finalNextRow).getIndex01().toString());
                            lblContestDetail.setText(paParticipants.get(finalNextRow).getIndex09().toString());
                            
                            // Optional: scroll the ScrollPane if needed (ensure it's roughly synced)
                            double rowHeight = apCenterPanel.getHeight();
                            double scrollY = finalNextRow * rowHeight;
                            double scrollMax = tblCandidate.getItems().size() * rowHeight;
                            double scrollValue = scrollY / scrollMax;

                            // Clamp value between 0 and 1
                            scrollValue = Math.min(1.0, Math.max(0.0, scrollValue));
                            spCenter.setVvalue(scrollValue);
                            event.consume();
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
                    TableModelETabulation model = getTableView().getItems().get(getIndex());
                    try {
                        Method method = TableModelETabulation.class.getMethod("setIndex0" + columnIndex, String.class);
                        method.invoke(model, newValue);
                    } catch (Exception ex) {
                        Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
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
        tblCandidate.setFixedCellSize(70);
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

    private void initImages() {
        ivContestant.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.setWidth(newB.getWidth());
            clip.setHeight(newB.getHeight());
            clip.setArcWidth(60);
            clip.setArcHeight(60);
            ivContestant.setClip(clip);
        });

    }

    private void initListener() {

        Platform.runLater(() -> {
            apMain.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdForm_Keypress);
        });

        spCenter.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            double scrollY = newVal.doubleValue();
            double maxV = spCenter.getContent().getBoundsInLocal().getHeight() - spCenter.getViewportBounds().getHeight();
            apContestant.setLayoutY(scrollY * maxV);
        });
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
                    Double loRate = oTrans.getDetail(lnCtrCriterea).getRate();
                    BigDecimal loRatePercencentage = oTrans.Criteria(lnCtrCriterea).getPercentage();
                    double critValue = (loRate != null) ? loRate : 0.0;
                    double critPercentage = (loRatePercencentage != null) ? Double.valueOf(String.valueOf(loRatePercencentage)) : 0.0;

                    switch (lnCtrCriterea) {
                        case 0:
                            index02Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 1:
                            index03Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 2:
                            index04Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 3:
                            index05Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 4:
                            index06Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                            break;
                        case 5:
                            index07Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
                        case 6:
                            index08Value = CommonUtils.NumberFormat(critValue, "#,#0.00");
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
                        CommonUtils.NumberFormat(lnTotal, "##0.00")
                ));

            }
            if (pnRow >= 0) {
                tblCandidate.getSelectionModel().select(pnRow);
            } else {
                //select first
                tblCandidate.getSelectionModel().select(0);
                pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
                getSelected(pnRow);
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void tblParticipantClick(MouseEvent event) {
        pnRow = tblCandidate.getSelectionModel().getSelectedIndex();
        if (pnRow < 0) {
            return;
        }
        getSelected(pnRow);
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

    private void getSelected(int pnRow) {
        lblContestantName.setText(paParticipants.get(pnRow).getIndex01().toString());
        lblContestDetail.setText(paParticipants.get(pnRow).getIndex09().toString());

        Platform.runLater(() -> {
            int firstEditableColIndex = 1;

            TableCell<TableModelETabulation, ?> cell = getCell(tblCandidate, pnRow, firstEditableColIndex);
            if (cell != null) {
                tblCandidate.edit(pnRow, cell.getTableColumn());

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
        });

    }

    public JSONObject setRating(String recordId, String terminalNo, int detailIdx, double rate) {
        JSONObject loJSON;

        try {
            loJSON = oTrans.openTransaction(recordId, terminalNo);
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                return loJSON;
            } else {
                System.out.println("Successfully set");
            }

            oTrans.getDetail(detailIdx).setRate(rate);
            System.out.println("Set score to: " + oTrans.getDetail(detailIdx).getRate());
            System.out.println("detail id: " + detailIdx);

            loJSON = oTrans.saveTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
            }
        } catch (CloneNotSupportedException | SQLException
                | ExceptionInInitializerError | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            loJSON = null;
        }
        return loJSON;
    }

}
