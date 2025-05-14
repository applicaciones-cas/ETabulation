package ui.etabulation;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import ui.etabulation.TableModelETabulation.Result;
import ui.etabulation.ETabulationUtils; 

public class FrmETabulationController implements Initializable {

    @FXML private AnchorPane Background;
    @FXML private ScrollPane scrollPaneTable;
    @FXML private GridPane GridBox;
    @FXML private GridPane JudgePane;
    @FXML private HBox BannerBox;
    @FXML private VBox CandidateBox;
    @FXML private ImageView imgBanner;
    @FXML private ImageView imgCandidate;
    @FXML private TableView<Result> ResultTable;
    @FXML private VBox tableBox;
    @FXML private VBox imageBox;
    @FXML private Text txtAdInfo;
    @FXML private Text txtCandidName;
    

    private ModelETabulation service = new ModelETabulation("D:\\GGC_Maven_Systems\\testing.ini");

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
        

        TableColumn<Result, Integer> columnSportswear = new TableColumn<>("SPORTSWEAR\n (15%)");
        columnSportswear.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("sportswear"));
        TableColumn<Result, Integer> columnFilipiniana = new TableColumn<>("FILIPINIANA\n (15%)");
        columnFilipiniana.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("filipiniana"));
        TableColumn<Result, Integer> columnTalent = new TableColumn<>("TALENT\nPORTION\n (20%)");
        columnTalent.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("talent"));
        TableColumn<Result, Integer> columnPersonality = new TableColumn<>("PERSONALITY\n (25%)");
        columnPersonality.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("personality"));
        TableColumn<Result, Integer> columnBeauty = new TableColumn<>("BEAUTY\nOF FACE\n (25%)");
        columnBeauty.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("beauty"));
        TableColumn<Result, Integer> columnAudience = new TableColumn<>("AUDIENCE\nIMPACT\n (10%)");
        columnAudience.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("audience"));
        TableColumn<Result, Integer> columnTotal = new TableColumn<>("TOTAL\nSCORE");

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
            List<Result> results = service.fetchCandidateResults();
            Platform.runLater(() -> {
                ResultTable.getItems().clear();
                ResultTable.getItems().addAll(results);
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

        
//        ResultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
//            if (newS != null) {
//                imgCandidate.setImage(new Image(newS.getImageUrl()));
//                txtAdInfo.setText(newS.getSchool());
//                txtCandidName.setText(newS.getCandidates());
//            }
//        });

        ResultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // absolute path from your model, e.g. "D:\\GGC_Maven_Systems\\images\\alice.png"
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

                txtCandidName.setText(newSel.getCandidates());
                txtAdInfo.setText(newSel.getSchool());
            }
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
}
