package ui.etabulation;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
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
import org.guanzon.appdriver.base.GuanzonException;

public class FrmETabulationController extends Transaction implements Initializable{
    
    @FXML private AnchorPane Background;
    @FXML private ScrollPane scrollPaneTable;
    @FXML private GridPane GridBox;
    @FXML private GridPane JudgePane;
    @FXML private HBox BannerBox;
    @FXML private VBox CandidateBox;
    @FXML private ImageView imgBanner;
    @FXML private ImageView imgCandidate;
    @FXML private TableView<TableModelETabulation.Result> ResultTable;
    @FXML private VBox tableBox;
    @FXML private VBox imageBox;
    @FXML private Text txtAdInfo;
    @FXML private Text txtCandidName;
    @FXML private Text txtJudgeName;
    
    static GRiderCAS gRider;
    public static FrmETabulationController tabcont;

    
    private Scoring scoring;
    private final String contestId = "00001";      
    private final String judgeName = "Waluigi";  
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        tabcont = this;
        
        gRider = MiscUtil.Connect();
        
        String path;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
        } else {
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.images", path + "/images");
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/tabulation/");
        
        try {
            scoring = new TabulationControllers(gRider, null).Scoring();
            scoring.setVerifyEntryNo(false);
            scoring.setContestId(contestId);
            scoring.setJudgeName(judgeName);

            JSONObject init = scoring.initTransaction();
            if (!"success".equals(init.get("result"))) {
                System.err.println("Init failed: " + init.get("message"));
            }
            JSONObject crit = scoring.loadCriteriaForJudging();
            if (!"success".equals(crit.get("result"))) {
                System.err.println("Criteria load failed: " + crit.get("message"));
            }
            JSONObject part = scoring.loadParticipants();
            if (!"success".equals(part.get("result"))) {
                System.err.println("Participants load failed: " + part.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        TableColumn<Result, String> columnCandidates = new TableColumn<>("CANDIDATES");
        columnCandidates.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("candidates"));

        columnCandidates.setCellFactory(tc -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : (getIndex() + 1) + ". " + item);
            }
        });
        
        ResultTable.setEditable(true);
        

        TableColumn<Result, Double> columnSportswear = new TableColumn<>("SPORTSWEAR\n (15%)");
        columnSportswear.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("sportswear"));
        TableColumn<Result, Double> columnFilipiniana = new TableColumn<>("FILIPINIANA\n (15%)");
        columnFilipiniana.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("filipiniana"));
        TableColumn<Result, Double> columnTalent = new TableColumn<>("TALENT\nPORTION\n (20%)");
        columnTalent.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("talent"));
        TableColumn<Result, Double> columnPersonality = new TableColumn<>("PERSONALITY\n (25%)");
        columnPersonality.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("personality"));
        TableColumn<Result, Double> columnBeauty = new TableColumn<>("BEAUTY\nOF FACE\n (25%)");
        columnBeauty.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("beauty"));
        TableColumn<Result, Double> columnAudience = new TableColumn<>("AUDIENCE\nIMPACT\n (10%)");
        columnAudience.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("audience"));
        TableColumn<Result, Double> columnTotal = new TableColumn<>("TOTAL\nSCORE");

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
        
  
        new Thread(() -> {
            List<TableModelETabulation.Result> results = new ArrayList<>();
            int count = scoring.getParticipantsCount();
            for (int i = 0; i < count; i++) {
                try {
                    Model_Contest_Participants p = scoring.Participant(i);
                    
                    //get participant school/town name
                    Model_Contest_Participants_Meta loMeta = scoring.ParticipantMeta(p.getGroupId(), "00002");
                    String school = loMeta.getValue();
                    
                    
                    //get participant name
                    loMeta = scoring.ParticipantMeta(p.getGroupId(), "00001");
                    String name = loMeta.getValue();
                    
                    //get participant picture name
                    loMeta = scoring.ParticipantMeta(p.getGroupId(), "00003");
                    String img    = loMeta.getValue();
                    img = System.getProperty("sys.default.path.images") + img.substring(45);
                    
                    scoring.getMaster().setGroupId(p.getGroupId());
                    scoring.getMaster().setComputerId(gRider.getTerminalNo());
                    scoring.openTransaction(p.getGroupId(), gRider.getTerminalNo());
//                    scoring.getDetail(1).getRate()
                     System.out.println("Set score to: " + String.valueOf(scoring.getDetail(1).getRate()));
                    results.add(new TableModelETabulation.Result(
                        name, 
                            scoring.getDetail(1).getRate(),
                            scoring.getDetail(2).getRate(),
                            scoring.getDetail(3).getRate(),
                            scoring.getDetail(4).getRate(),
                            scoring.getDetail(5).getRate(),
                            scoring.getDetail(6).getRate(), 
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
                ResultTable.getItems().setAll(results);
                
            });
        }).start();
        

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
        

        imgBanner.fitWidthProperty().bind(Background.widthProperty());
        tableBox.prefWidthProperty().bind(Background.widthProperty().multiply(0.5));
        imageBox.prefWidthProperty().bind(Background.widthProperty().multiply(0.3));
        imageBox.setAlignment(Pos.CENTER);
        imgBanner.fitHeightProperty().bind(BannerBox.heightProperty());
        imgBanner.setPreserveRatio(false);
        VBox.setVgrow(GridBox, Priority.ALWAYS);
        VBox.setVgrow(JudgePane, Priority.ALWAYS);
        

        Node viewport = scrollPaneTable.lookup(".viewport");
        if (viewport instanceof Region) {
            ((Region) viewport).setClip(null);
        }

        imgCandidate.fitWidthProperty().bind(imageBox.widthProperty().multiply(0.90));
        imgCandidate.fitHeightProperty().bind(imageBox.heightProperty().multiply(0.90));
        imgCandidate.setPreserveRatio(false);
        imgCandidate.setSmooth(true);
        imgBanner.setImage(new Image("images/BannerSample.png"));
        imgCandidate.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.setWidth(newB.getWidth());
            clip.setHeight(newB.getHeight());
            clip.setArcWidth(60);
            clip.setArcHeight(60);
            imgCandidate.setClip(clip);
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
            TableModelETabulation.Result::setFilipiniana,ETabulationUtils.W_FL
        );
        ETabulationUtils.bindPercentageColumn(
            ResultTable, columnTalent,
            TableModelETabulation.Result::setTalent,ETabulationUtils.W_TA
        );
        ETabulationUtils.bindPercentageColumn(
            ResultTable, columnPersonality,
            TableModelETabulation.Result::setPersonality,ETabulationUtils.W_PE
        );
        ETabulationUtils.bindPercentageColumn(
            ResultTable, columnBeauty,
            TableModelETabulation.Result::setBeauty,ETabulationUtils.W_BE
        );
        ETabulationUtils.bindPercentageColumn(
            ResultTable, columnAudience,
            TableModelETabulation.Result::setAudience,ETabulationUtils.W_AU
        );

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
                
                
                txtJudgeName.setText(judgeName);
                txtCandidName.setText(newSel.getCandidates());
                txtAdInfo.setText(newSel.getSchool());
                
            }
        });

        
        
        Platform.runLater(() -> {
            Scene scene = Background.getScene();
            scene.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (evt.getCode() == KeyCode.ESCAPE) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to exit?", 
                        ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Exit");
                    confirm.setHeaderText(null);

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        Stage stage = (Stage) scene.getWindow();
                        stage.close();
                    }
                    evt.consume();
                }
            });
        });


        Platform.runLater(() -> {
            for (Node node : ResultTable.lookupAll(".column-header .label")) {
                if (node instanceof Labeled) {
                    ((Labeled) node).setAlignment(Pos.CENTER);
                    ((Labeled) node).setTextAlignment(TextAlignment.CENTER);
                }
            }
        });
        
        ResultTable.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode() == KeyCode.TAB) {
                TablePosition<?,?> pos = ResultTable.getFocusModel().getFocusedCell();
                int row = pos.getRow();
                int col = pos.getColumn();
                int colCount = ResultTable.getVisibleLeafColumns().size();
                int nextCol = (col + 1) % colCount;

                while (nextCol == 0 || nextCol == colCount - 1 || 
                       !ResultTable.getVisibleLeafColumns().get(nextCol).isEditable()) {
                    nextCol = (nextCol + 1) % colCount;
                }

                ResultTable.getFocusModel().focus(row, 
                    (TableColumn<TableModelETabulation.Result, ?>)
                    ResultTable.getVisibleLeafColumns().get(nextCol));
                ResultTable.edit(row, 
                    (TableColumn<TableModelETabulation.Result, ?>)
                    ResultTable.getVisibleLeafColumns().get(nextCol));

                evt.consume();
            }
        });
        
        ResultTable.getColumns().forEach(col -> col.setSortable(false));

    }
    
    
    public JSONObject setRating(String recordId, String terminalNo, int detailIdx, double rate) {
        JSONObject loJSON;
        
        
        try {
            loJSON = scoring.openTransaction(recordId, terminalNo);
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                return loJSON;
            } else {
                System.err.println("Successfully set");
            }

            scoring.getDetail(detailIdx).setRate(rate);
            System.out.println("Set score to: " + scoring.getDetail(detailIdx).getRate());
            System.out.println("detail id: " + detailIdx);

            loJSON = scoring.saveTransaction();
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
        return gRider.getTerminalNo();
    }
    
    
}
