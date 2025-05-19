 package ui.etabulation;


import java.net.URL;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
/**
 *
 * @author user
 */
public class ETabulation extends Application{

    public final static String pxeMainFormTitle = "E - Tabulation";
    public final static String pxeMainForm = "/views/MainMenu.fxml";
    public static GRiderCAS oApp;

    @Override
    public void start(Stage primaryStage) {
        try {
            
            FXMLLoader view = new FXMLLoader();
            view.setLocation(getClass().getResource(pxeMainForm));

            MainMenuController controller = new MainMenuController();
            controller.setGRider(oApp);

            view.setController(controller);
            Parent parent = view.load();
            Scene scene = new Scene(parent);

            //get the screen size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(pxeMainFormTitle);

            // set stage as maximized but not full screen
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }
}
