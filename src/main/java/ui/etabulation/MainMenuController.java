package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController implements Initializable {

    @FXML
    private MenuItem etabulation;
    @FXML private HBox BannerBoxH;
    @FXML private VBox BannerBoxV;
    @FXML
    private ImageView imgBanner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        etabulation.setOnAction(this::handleScoring);
        imgBanner.setImage(new Image(getClass().getResourceAsStream("/images/BannerSample.png")));
        imgBanner.fitWidthProperty().bind(BannerBoxV.widthProperty());
    }

    private void handleScoring(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/frmETabulation.fxml"));

            
            Stage stage = new Stage();
            stage.setTitle("Scoring Module");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
