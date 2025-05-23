package ui.etabulation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;

public class MainMenuController implements Initializable {
    public static GRiderCAS oApp;
    private final List<TranslateTransition> confettiAnimations = new ArrayList<>();
    private boolean isConfettiRunning = false;

    @FXML
    AnchorPane apMain;
    @FXML
    private MenuItem mnuAbout, mnuScoring, mnuClose, mnuBingo, mnuMinimize;
    @FXML
    private Pane pnMainCenter;
    
    double xOffset = 0; 
    double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //to do list
        Platform.runLater(() -> restartConfetti(pnMainCenter));
        Platform.runLater(() -> apMain.requestFocus());
        Platform.runLater(() -> {
            apMain.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                    this::cmdForm_Keypress);
        });

    }

    @FXML
    private void handleMenu(ActionEvent event) {
        MenuItem mnuName = (MenuItem) event.getSource();
        String lsFormMenu = mnuName.getId();
        switch (lsFormMenu) {
            case "mnuBingo1920":
                getStage().setIconified(true);
                openWindow("GuanzonBingo1920", "Guanzon Bingo", false);
                break;
            case "mnuBingo1080":
                getStage().setIconified(true);
                openWindow("GuanzonBingo1080", "Guanzon Bingo", false);
                break;
            case "mnuScoring":
//                openWindow("frmETabulation", "Tabulation", true);
                openWindow("ETabulation", "Tabulation", true);
                break;
            case "mnuAbout":
                openWindow("About", "About This App", false);
                break;
            case "mnuMinimize":
                getStage().setIconified(true);
                break;
            case "mnuClose":
                Platform.exit();
                break;
            default:
                System.err.println("Unhandled menu: " + lsFormMenu);
        }
    }
    
    private Stage getStage(){
        return (Stage) apMain.getScene().getWindow();
    }

    private void openWindow(String fsFormName, String fsTitle, boolean isMaximized) {
        try {
            Stage newScene = new Stage();
            stopConfetti();

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
            
            
            foParent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            
            foParent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    newScene.setX(event.getScreenX() - xOffset);
                    newScene.setY(event.getScreenY() - yOffset);
                }
            });

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

            Platform.runLater(() -> restartConfetti(pnMainCenter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getUIController(String fsMenuForm) {
        GBingoController GBingoCtrl;
        switch (fsMenuForm) {
            case "GuanzonBingo1080":
                GBingoCtrl = new GBingoController();
                GBingoCtrl.setScreenSize(1080);
                GBingoCtrl.setGRider(oApp);

                return GBingoCtrl;
            case "GuanzonBingo1920":
                GBingoCtrl = new GBingoController();
                GBingoCtrl.setScreenSize(1920);
                GBingoCtrl.setGRider(oApp);
                //todo incase
                return GBingoCtrl;
            case "frmETabulation":
                FrmETabulationController Etabulation = new FrmETabulationController();
                Etabulation.setGRider(oApp);
                //todo incase
                return Etabulation;
            case "ETabulation":
                ETabulationController ETabulation = new ETabulationController();
                ETabulation.setGRider(oApp);
                //todo incase
                return ETabulation;

            default:
                return null;

        }

    }

    public void setGRider(GRiderCAS poApp) {
        oApp = poApp;
    }

    public void launchConfetti(Pane foPane) {
        if (isConfettiRunning) {
            return; // 
        }
        isConfettiRunning = true;

        Random random = new Random();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenHeight = bounds.getHeight();
        int width = (int) (foPane.getWidth() > 0 ? foPane.getWidth() : 400);
        int height = (int) screenHeight;

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

            confettiAnimations.add(fall);
            foPane.getChildren().add(confetti);
        }
    }

    private String randomColor() {
        String[] neonColors = {
            "#facc15", "#00ffd0", "#ff4fd8", "#00bfff", "#ffbf00"
        };
        return neonColors[new Random().nextInt(neonColors.length)];
    }

    public void stopConfetti() {
        for (TranslateTransition ttAnimate : confettiAnimations) {
            ttAnimate.stop();
        }
        confettiAnimations.clear();
        isConfettiRunning = false;
    }

    public void restartConfetti(Pane root) {
        stopConfetti(); // cleanup old
        launchConfetti(root); // restart
    }

    private void cmdForm_Keypress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            Platform.runLater(() -> {
                stopConfetti();
                if (ShowMessageFX.YesNo(null, "Exit", "Are you sure, do you want to close?")) {
                    System.exit(0);
                } else {
                    restartConfetti(pnMainCenter);
                }
            });
        }
    }
}
