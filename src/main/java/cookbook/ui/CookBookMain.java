package cookbook.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CookBookMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader;
        Parent root;
        try{
            loader = new FXMLLoader(getClass().getResource("mainBook.fxml"));
            root = loader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Controller controller = loader.getController();
        controller.listInfo();

        primaryStage.setTitle("Recipes R' Us!");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        if (!Datasource.returnInstance().open()) {
            System.out.println("Could not open database!");
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Datasource.returnInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}