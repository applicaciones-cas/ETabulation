package ui.etabulation;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
    private TableView<TableModelETabulation.Result> tblCandidate;
    private TableColumn<Result, String> columnCandidates;
    private TableColumn<Result, Double> columnSportswear;
    private TableColumn<Result, Double> columnFilipiniana;
    private TableColumn<Result, Double> columnTalent;
    private TableColumn<Result, Double> columnPersonality;
    private TableColumn<Result, Double> columnBeauty;
    private TableColumn<Result, Double> columnAudience;
    private TableColumn<Result, Double> columnTotal;
    private static List<TableModelETabulation.Result> poResults = new ArrayList<>();

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
            initTable();
            initTableSetter();
            initImages();
            initListener();

            new Thread(() -> {
                loadResult();
            }).start();

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

            tblCandidate.getFocusModel().focus(row,
                    (TableColumn<TableModelETabulation.Result, ?>) tblCandidate.getVisibleLeafColumns().get(nextCol));
            tblCandidate.edit(row,
                    (TableColumn<TableModelETabulation.Result, ?>) tblCandidate.getVisibleLeafColumns().get(nextCol));

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

    private void initTable() {
        columnCandidates = new TableColumn<>("CANDIDATES");
        columnSportswear = new TableColumn<>("SPORTSWEAR\n (15%)");
        columnFilipiniana = new TableColumn<>("FILIPINIANA\n (15%)");
        columnTalent = new TableColumn<>("TALENT\nPORTION\n (20%)");
        columnPersonality = new TableColumn<>("PERSONALITY\n (25%)");
        columnBeauty = new TableColumn<>("BEAUTY\nOF FACE\n (25%)");
        columnAudience = new TableColumn<>("AUDIENCE\nIMPACT\n (10%)");
        columnTotal = new TableColumn<>("TOTAL\nSCORE");

        columnCandidates.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("candidates"));
        columnSportswear.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("sportswear"));
        columnFilipiniana.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("filipiniana"));
        columnTalent.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("talent"));
        columnPersonality.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("personality"));
        columnBeauty.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("beauty"));
        columnAudience.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("audience"));

        columnTotal.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        columnCandidates.setStyle("-fx-alignment: center-left;");
        columnSportswear.setStyle("-fx-alignment: center;");
        columnFilipiniana.setStyle("-fx-alignment: center;");
        columnTalent.setStyle("-fx-alignment: center;");
        columnPersonality.setStyle("-fx-alignment: center;");
        columnBeauty.setStyle("-fx-alignment: center;");
        columnAudience.setStyle("-fx-alignment: center;");
        columnTotal.setStyle("-fx-alignment: center;");

        columnCandidates.setEditable(false);
        columnSportswear.setEditable(true);
        columnFilipiniana.setEditable(true);
        columnTalent.setEditable(true);
        columnPersonality.setEditable(true);
        columnBeauty.setEditable(true);
        columnAudience.setEditable(true);
        columnTotal.setEditable(false);

        tblCandidate.setFixedCellSize(70);

        tblCandidate.getColumns().addAll(
                columnCandidates, columnSportswear, columnFilipiniana,
                columnTalent, columnPersonality, columnBeauty,
                columnAudience, columnTotal
        );
        TableColumn<?, ?> firstCol = tblCandidate.getColumns().get(0);
        firstCol.getStyleClass().add("header-1");
        tblCandidate.getColumns().forEach(col -> col.setSortable(false));

    }

    private void initTableSetter() {
        tblCandidate.widthProperty().addListener((obs, oldW, newW) -> {
            double width = newW.doubleValue();
            columnCandidates.setPrefWidth(width * 0.3);
            columnSportswear.setPrefWidth(width * 0.1);
            columnFilipiniana.setPrefWidth(width * 0.1);
            columnTalent.setPrefWidth(width * 0.1);
            columnPersonality.setPrefWidth(width * 0.1);
            columnBeauty.setPrefWidth(width * 0.1);
            columnAudience.setPrefWidth(width * 0.1);
            columnTotal.setPrefWidth(width * 0.09);
        });
        columnCandidates.setCellFactory(tc -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : (getIndex() + 1) + ". " + item);
            }
        });

        columnSportswear.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );
        columnFilipiniana.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );
        columnTalent.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );
        columnPersonality.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );
        columnBeauty.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );
        columnAudience.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );

        columnTotal.setCellFactory(
                ETabulationUtils.createEditableCellFactory(25, 15, Color.web("#c7c7c7"))
        );

        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnSportswear,
                TableModelETabulation.Result::setSportswear, ETabulationUtils.W_SW
        );
        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnFilipiniana,
                TableModelETabulation.Result::setFilipiniana, ETabulationUtils.W_FL
        );
        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnTalent,
                TableModelETabulation.Result::setTalent, ETabulationUtils.W_TA
        );
        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnPersonality,
                TableModelETabulation.Result::setPersonality, ETabulationUtils.W_PE
        );
        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnBeauty,
                TableModelETabulation.Result::setBeauty, ETabulationUtils.W_BE
        );
        ETabulationUtils.bindPercentageColumn(
                tblCandidate, columnAudience,
                TableModelETabulation.Result::setAudience, ETabulationUtils.W_AU
        );

        //listener for table
        tblCandidate.setRowFactory(tv -> {
            TableRow<Result> row = new TableRow<>();
            row.selectedProperty().addListener((obs, oldSel, newSel) -> {
                ETabulationUtils.updateRowStyle(row, row.getIndex());
            });
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                ETabulationUtils.updateRowStyle(row, row.getIndex());
            });
            return row;
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

        tblCandidate.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {

                String fullPath = newSel.getImageUrl();
                File imgFile = new File(fullPath);

                if (imgFile.exists()) {
                    String uri = imgFile.toURI().toString();
                    // e.g. "file:/D:/GGC_Maven_Systems/images/alice.png"
//                    imgCandidate.setImage(new Image(uri));
                } else {
                    // fallback placeholder
//                    imgCandidate.setImage(new Image("D:\\GGC_Maven_Systems\\images\\alice.png"));
                }
                lblJudgeNm.setText(psJudgeName);
                lblContestantName.setText(newSel.getCandidates());
                lblContestDetail.setText(newSel.getSchool());

            }
        });

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

    private void loadResult() {
        poResults = new ArrayList<>();
        int count = oTrans.getParticipantsCount();
        for (int i = 0; i < count; i++) {
            try {
                Model_Contest_Participants p = oTrans.Participant(i);

                //get participant school/town name
                Model_Contest_Participants_Meta loMeta = oTrans.ParticipantMeta(p.getGroupId(), "00002");
                String school = loMeta.getValue();

                //get participant name
                loMeta = oTrans.ParticipantMeta(p.getGroupId(), "00001");
                String name = loMeta.getValue();

                //get participant picture name
                loMeta = oTrans.ParticipantMeta(p.getGroupId(), "00003");
                String img = loMeta.getValue();
                img = System.getProperty("sys.default.path.images") + img.substring(45);

                oTrans.getMaster().setGroupId(p.getGroupId());
                oTrans.getMaster().setComputerId(oApp.getTerminalNo());
                oTrans.openTransaction(p.getGroupId(), oApp.getTerminalNo());
//                    oTrans.getDetail(1).getRate()
                System.out.println("Set score to: " + String.valueOf(oTrans.getDetail(1).getRate()));
                poResults.add(new TableModelETabulation.Result(
                        name,
                        oTrans.getDetail(1).getRate(),
                        oTrans.getDetail(2).getRate(),
                        oTrans.getDetail(3).getRate(),
                        oTrans.getDetail(4).getRate(),
                        oTrans.getDetail(5).getRate(),
                        oTrans.getDetail(6).getRate(),
                        0,
                        img,
                        school,
                        p.getGroupId()
                ));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            tblCandidate.getItems().setAll(poResults);

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
