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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
import static ui.etabulation.FrmETabulationController.oApp;
import static ui.etabulation.FrmETabulationController.tabcont;
import ui.etabulation.TableModelETabulation.Result;

/**
 * FXML Controller class
 *
 * @author Maynard
 */
public class ETabulationController implements Initializable {

    @FXML
    private AnchorPane apMain;
    @FXML
    private AnchorPane apMainBanner;
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

    private static GRiderCAS oApp;
    private Scoring oTrans;
    private String psContestID = "";
    private String psJudgeName = "";
    private JSONObject poJSON;

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

            initRecord();
            initCriteria();
//            initTableSetter();
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
        double contestantColumnRatio = 0.23; // 23% for contestant column
        double remainingRatio = 1.0 - contestantColumnRatio;
        int criteriaCount = oTrans.getCriteriaCount();

        // Contestant Column
        TableColumn index01 = new TableColumn("Contestant");
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index01.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 10;");
        tblCandidate.getColumns().add(index01);

        // Bind width
        index01.prefWidthProperty().bind(tblCandidate.widthProperty().multiply(contestantColumnRatio));

        // Criteria Columns
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
            column.setCellFactory(TextFieldTableCell.forTableColumn());

            // Bind width: divide remaining width among criteria columns
            column.prefWidthProperty().bind(tblCandidate.widthProperty()
                    .multiply(remainingRatio).divide(criteriaCount + 1)); // +1 for TOTAL column

            // Handle editing with reflection
            column.setOnEditCommit(event -> {
                TableModelETabulation model = event.getRowValue();
                try {
                    Method method = TableModelETabulation.class.getMethod("setIndex0" + columnIndex, String.class);
                    method.invoke(model, event.getNewValue());
                } catch (Exception ex) {
                    Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            tblCandidate.getColumns().add(column);
        }

        // TOTAL column (fixed width)
        TableColumn<TableModelETabulation, String> indexTotal = new TableColumn<>("TOTAL");
        indexTotal.setCellValueFactory(new PropertyValueFactory<>("index10"));
        indexTotal.setStyle("-fx-alignment: CENTER;");
        indexTotal.setPrefWidth(75);
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
//
//        tblCandidate.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
//            if (newSel != null) {
//
//                String fullPath = newSel.getImageUrl();
//                File imgFile = new File(fullPath);
//
//                if (imgFile.exists()) {
//                    String uri = imgFile.toURI().toString();
//                    // e.g. "file:/D:/GGC_Maven_Systems/images/alice.png"
////                    imgCandidate.setImage(new Image(uri));
//                } else {
//                    // fallback placeholder
////                    imgCandidate.setImage(new Image("D:\\GGC_Maven_Systems\\images\\alice.png"));
//                }
//                lblJudgeNm.setText(psJudgeName);
//                lblContestantName.setText(newSel.getCandidates());
//                lblContestDetail.setText(newSel.getSchool());
//
//            }
//        });

        Platform.runLater(() -> {
            apMain.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdForm_Keypress);
        });

        Platform.runLater(() -> {
            tblCandidate.addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdTable_Keypress);
        });

        Platform.runLater(() -> {
            for (Node node : tblCandidate.lookupAll(".column-header .label")) {
                if (node instanceof Labeled) {
                    ((Labeled) node).setAlignment(Pos.CENTER);
                    ((Labeled) node).setTextAlignment(TextAlignment.CENTER);
                }
            }
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

                int loCriteria = oTrans.getCriteriaCount();
                for (int lnCtrCriterea = 0; lnCtrCriterea < loCriteria; lnCtrCriterea++) {
                    Double loRate = oTrans.getDetail(lnCtrCriterea).getRate();
                    double critValue = (loRate != null) ? loRate : 0.0;

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
                    lnTotal += critValue;
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
                        String.valueOf(lnTotal)
                ));

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(ETabulationController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
