package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
    @FXML private MenuItem etabulation, about, closeApp;
    @FXML private ImageView imgBanner;
    @FXML private VBox BannerBoxV;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgBanner.setImage(new Image(getClass().getResourceAsStream("/images/BannerSample.png")));
        imgBanner.fitWidthProperty().bind(BannerBoxV.widthProperty());
    }

    @FXML
    private void handleMenu(ActionEvent event) {
        MenuItem src = (MenuItem) event.getSource();
        switch (src.getId()) {
            case "etabulation":
                openWindow("/views/frmETabulation.fxml", "Scoring Module");
                break;
            case "about":
                openWindow("/views/About.fxml", "About This App");
                break;
            case "closeApp":
                Platform.exit();
                break;
            default:
                System.err.println("Unhandled menu: " + src.getId());
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
