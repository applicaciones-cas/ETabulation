package ui.etabulation;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.MiscUtil;

/**
 *
 * @author user
 */
public class ETabulation extends Application {

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
        String path;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
        } else {
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.images", path + "/images");
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/tabulation/");

        //to do before connection
        oApp = MiscUtil.Connect();
        launch(args);
    }

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }
}
