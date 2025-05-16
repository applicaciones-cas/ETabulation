package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;

public class MainMenuController implements Initializable {

    public static GRider oApp;

    @FXML
    private MenuItem mnuAbout, mnuScoring, mnuClose;
    @FXML
    private Pane pnMainCenter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //to do list
        Platform.runLater(() -> launchConfetti(pnMainCenter));

    }

    @FXML
    private void handleMenu(ActionEvent event) {
        MenuItem src = (MenuItem) event.getSource();
        switch (src.getId()) {
            case "mnuScoring":
                openWindow("/views/frmETabulation.fxml", "Scoring Module");
                break;
            case "mnuAbout":
                openWindow("/views/About.fxml", "About This App");
                break;
            case "mnuClose":
                Platform.exit();
                break;
            default:
                System.err.println("Unhandled menu: " + src.getId());
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {

            //get the screen size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = new Stage();
            // set stage as maximized but not full screen
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            stage.setTitle(title);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setGRider(GRider poApp) {
        oApp = poApp;
    }

    public void launchConfetti(Pane root) {
        Random random = new Random();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenHeight = bounds.getHeight();
        int width = (int) (root.getWidth() > 0 ? root.getWidth() : 400);
        int height = (int) (screenHeight);

        for (int i = 0; i < 30; i++) {
            Circle confetti = new Circle(4);
            confetti.setFill(Paint.valueOf(randomColor()));
            confetti.setLayoutX(random.nextInt(width));
            confetti.setLayoutY(10);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(5 + random.nextInt(3)), confetti);
            fall.setToY(height + 30);
            fall.setCycleCount(Animation.INDEFINITE);
            fall.setDelay(Duration.seconds(random.nextDouble() * 5));
            fall.play();

            root.getChildren().add(confetti);
        }

    }

    private String randomColor() {
        String[] neonColors = {
            "#facc15", "#00ffd0", "#ff4fd8", "#00bfff", "#ffbf00"
        };
        return neonColors[new Random().nextInt(neonColors.length)];
    }
}
