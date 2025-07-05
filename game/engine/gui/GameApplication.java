package game.engine.gui;

import game.engine.Battle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GameApplication extends Application {
    private BattleView battleView;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize battle with 3 lanes
            Battle battle = new Battle(3, 100, 50, 10, 5, 20, 10);

            // Create battle view
            battleView = new BattleView(battle);

            // Create scene
            Scene scene = new Scene(battleView, 800, 600);

            // Set up stage
            primaryStage.setTitle("Attack on Titan - Defense Game");
            primaryStage.getIcons().add(new Image("https://i.imgur.com/t8ZRVBl.png"));
            primaryStage.setScene(scene);
            primaryStage.show();

            // Handle close request
            primaryStage.setOnCloseRequest(e -> {
                if (battleView != null) {
                    battleView.stopUpdates();
                }
            });
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Game Initialization Failed");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (battleView != null) {
            battleView.stopUpdates();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}