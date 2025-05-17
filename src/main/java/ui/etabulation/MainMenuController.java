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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;

public class MainMenuController implements Initializable {

    public static GRider oApp;

    @FXML
    private MenuItem mnuAbout, mnuScoring, mnuClose, mnuBingo;
    @FXML
    private Pane pnMainCenter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //to do list
        Platform.runLater(() -> launchConfetti(pnMainCenter));

    }

    @FXML
    private void handleMenu(ActionEvent event) {
        MenuItem mnuName = (MenuItem) event.getSource();
        String lsFormMenu = mnuName.getId();
        switch (lsFormMenu) {
            case "mnuBingo":
                openWindow("GuanzonBingo1920", "Guanzon E - Bingo", false);
//                openWindow("GuanzonBingo1920Neo", "Guanzon E - Bingo");
//                openWindow("GuanzonBingo1080", "Guanzon E - Bingo");
//                openWindow("GuanzonBingo1080Neo", "Guanzon E - Bingo");
                break;
            case "mnuScoring":
                openWindow("frmETabulation", "Scoring Module", true);
                break;
            case "mnuAbout":
                openWindow("About", "About This App", false);
                break;
            case "mnuClose":
                Platform.exit();
                break;
            default:
                System.err.println("Unhandled menu: " + lsFormMenu);
        }
    }

    private void openWindow(String fsFormName, String fsTitle, boolean isMaximized) {
        try {
            Stage newScene = new Stage();

            FXMLLoader fxLoader = new FXMLLoader();
            Object fxObj = getUIController(fsFormName);

            if (fxObj == null) {
                System.err.println("No controller found for form: " + fsFormName);
                return;
            }
            URL fxURLResource = getClass().getResource("/views/" + fsFormName + ".fxml");
            if (fxURLResource == null) {
                System.err.println("FXML file not found: /views/" + fsFormName + ".fxml");
                return;
            }
            fxLoader.setLocation(fxURLResource);
            fxLoader.setController(fxObj);

            Parent foParent = fxLoader.load();

            if (isMaximized) {
                //get the screen size
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                // set stage as maximized but not full screen
                newScene.setX(bounds.getMinX());
                newScene.setY(bounds.getMinY());
                newScene.setWidth(bounds.getWidth());
                newScene.setHeight(bounds.getHeight());
            }
            Scene childScene = new Scene(foParent);
            newScene.setTitle(fsTitle);
            newScene.setScene(childScene);
            newScene.initStyle(StageStyle.UNDECORATED);
            newScene.initModality(Modality.APPLICATION_MODAL);
            newScene.centerOnScreen();
            newScene.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getUIController(String fsMenuForm) {
        GBingoController GBingoCtrl;
        switch (fsMenuForm) {
            case "GuanzonBingo1080":
            case "GuanzonBingo1080Neo":
                GBingoCtrl = new GBingoController();
                GBingoCtrl.setScreenSize(1080);
            case "GuanzonBingo1920":
            case "GuanzonBingo1920Neo":
                GBingoCtrl = new GBingoController();
                GBingoCtrl.setScreenSize(1920);
                //todo incase
                return GBingoCtrl;
            case "frmETabulation":
                FrmETabulationController Etabulation = new FrmETabulationController();
                //todo incase
                return Etabulation;

            default:
                return null;

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
