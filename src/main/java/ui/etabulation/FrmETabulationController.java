package ui.etabulation;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.guanzon.appdriver.agent.services.Transaction;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.MiscUtil;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.gtabulate.Scoring;
import ph.com.guanzongroup.gtabulate.model.Model_Contest_Participants;
import ph.com.guanzongroup.gtabulate.model.Model_Contest_Participants_Meta;
import ph.com.guanzongroup.gtabulate.model.services.TabulationControllers;
import ui.etabulation.TableModelETabulation.Result;
import ui.etabulation.ETabulationUtils;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GuanzonException;

public class FrmETabulationController extends Transaction implements Initializable {

    @FXML
    private AnchorPane apMain;
    @FXML
    private ScrollPane scrollPaneTable;
    @FXML
    private GridPane GridBox;
    @FXML
    private GridPane JudgePane;
    @FXML
    private HBox BannerBox;
    @FXML
    private VBox CandidateBox;
    @FXML
    private ImageView imgBanner;
    @FXML
    private ImageView imgCandidate;
    @FXML
    private TableView<TableModelETabulation.Result> ResultTable;
    @FXML
    private VBox tableBox;
    @FXML
    private VBox imageBox;
    @FXML
    private Text txtAdInfo;
    @FXML
    private Text txtCandidName;
    @FXML
    private Text txtJudgeName;

    static GRiderCAS oApp;
    public static FrmETabulationController tabcont;

    private Scoring oTrans;
    private String psContestID = "";
    private String psJudgeName = "";
    private JSONObject poJSON;
    private TableColumn<Result, String> columnCandidates;
    private TableColumn<Result, Double> columnSportswear;
    private TableColumn<Result, Double> columnFilipiniana;
    private TableColumn<Result, Double> columnTalent;
    private TableColumn<Result, Double> columnPersonality;
    private TableColumn<Result, Double> columnBeauty;
    private TableColumn<Result, Double> columnAudience;
    private TableColumn<Result, Double> columnTotal;

    private static List<TableModelETabulation.Result> poResults = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //for testing
        psContestID = "00001";
        psJudgeName = "Waluigi";
        try {
            tabcont = this;
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

    }

    public void setJudgeName(String fsJudgeName) {
        psJudgeName = fsJudgeName;
    }

    public void setContestID(String fsContestID) {
        psContestID = fsContestID;
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
            TablePosition<?, ?> pos = ResultTable.getFocusModel().getFocusedCell();
            int row = pos.getRow();
            int col = pos.getColumn();
            int colCount = ResultTable.getVisibleLeafColumns().size();
            int nextCol = (col + 1) % colCount;

            while (nextCol == 0 || nextCol == colCount - 1
                    || !ResultTable.getVisibleLeafColumns().get(nextCol).isEditable()) {
                nextCol = (nextCol + 1) % colCount;
            }

            ResultTable.getFocusModel().focus(row,
                    (TableColumn<TableModelETabulation.Result, ?>) ResultTable.getVisibleLeafColumns().get(nextCol));
            ResultTable.edit(row,
                    (TableColumn<TableModelETabulation.Result, ?>) ResultTable.getVisibleLeafColumns().get(nextCol));

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

        columnSportswear.setEditable(true);
        columnFilipiniana.setEditable(true);
        columnTalent.setEditable(true);
        columnPersonality.setEditable(true);
        columnBeauty.setEditable(true);
        columnAudience.setEditable(true);
        columnCandidates.setEditable(false);
        columnTotal.setEditable(false);

        ResultTable.getColumns().addAll(
                columnCandidates, columnSportswear, columnFilipiniana,
                columnTalent, columnPersonality, columnBeauty,
                columnAudience, columnTotal
        );
        ResultTable.setEditable(true);

        ResultTable.getColumns().forEach(col -> col.setSortable(false));

    }

    private void initTableSetter() {
        ResultTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        ResultTable.widthProperty().addListener((obs, oldW, newW) -> {
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
                ResultTable, columnSportswear,
                TableModelETabulation.Result::setSportswear, ETabulationUtils.W_SW
        );
        ETabulationUtils.bindPercentageColumn(
                ResultTable, columnFilipiniana,
                TableModelETabulation.Result::setFilipiniana, ETabulationUtils.W_FL
        );
        ETabulationUtils.bindPercentageColumn(
                ResultTable, columnTalent,
                TableModelETabulation.Result::setTalent, ETabulationUtils.W_TA
        );
        ETabulationUtils.bindPercentageColumn(
                ResultTable, columnPersonality,
                TableModelETabulation.Result::setPersonality, ETabulationUtils.W_PE
        );
        ETabulationUtils.bindPercentageColumn(
                ResultTable, columnBeauty,
                TableModelETabulation.Result::setBeauty, ETabulationUtils.W_BE
        );
        ETabulationUtils.bindPercentageColumn(
                ResultTable, columnAudience,
                TableModelETabulation.Result::setAudience, ETabulationUtils.W_AU
        );

        //listener for table
        ResultTable.setRowFactory(tv -> {
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
//        tableBox.prefWidthProperty().bind(apMain.widthProperty().multiply(0.5));
//        imageBox.prefWidthProperty().bind(apMain.widthProperty().multiply(0.3));
        imageBox.setAlignment(Pos.CENTER);
//        imgBanner.fitWidthProperty().bind(apMain.widthProperty());
//        imgBanner.fitHeightProperty().bind(BannerBox.heightProperty());
        imgBanner.setPreserveRatio(false);
        CandidateBox.setVgrow(GridBox, Priority.ALWAYS);
        CandidateBox.setVgrow(JudgePane, Priority.ALWAYS);

        Node viewport = scrollPaneTable.lookup(".viewport");
        if (viewport instanceof Region) {
            ((Region) viewport).setClip(null);
        }

//        imgCandidate.fitWidthProperty().bind(imageBox.widthProperty().multiply(0.90));
//        imgCandidate.fitHeightProperty().bind(imageBox.heightProperty().multiply(0.90));
        imgCandidate.setPreserveRatio(false);
        imgCandidate.setSmooth(true);
        imgBanner.setImage(new Image("images/BannerSample.png"));
        imgCandidate.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.setWidth(newB.getWidth());
            clip.setHeight(newB.getHeight());
            clip.setArcWidth(60);
            clip.setArcHeight(60);
            imgCandidate.setClip(clip);
        });

    }

    private void initListener() {

        ResultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {

                String fullPath = newSel.getImageUrl();
                File imgFile = new File(fullPath);

                if (imgFile.exists()) {
                    String uri = imgFile.toURI().toString();
                    // e.g. "file:/D:/GGC_Maven_Systems/images/alice.png"
                    imgCandidate.setImage(new Image(uri));
                } else {
                    // fallback placeholder
                    imgCandidate.setImage(new Image("D:\\GGC_Maven_Systems\\images\\alice.png"));
                }
                txtJudgeName.setText(psJudgeName);
                txtCandidName.setText(newSel.getCandidates());
                txtAdInfo.setText(newSel.getSchool());

            }
        });

        Platform.runLater(() -> {
            apMain.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdForm_Keypress);
        });

        Platform.runLater(() -> {
            ResultTable.addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdTable_Keypress);
        });

        Platform.runLater(() -> {
            for (Node node : ResultTable.lookupAll(".column-header .label")) {
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
            ResultTable.getItems().setAll(poResults);

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

    public static FrmETabulationController getController() {
        return tabcont;
    }

    public String getTerminalNumber() {
        return oApp.getTerminalNo();
    }

    public void setGRider(GRiderCAS poApp) {
        oApp = poApp;
    }

}
