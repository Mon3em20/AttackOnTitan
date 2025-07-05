package game.engine.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameHUD {

    private BorderPane hudPane;
    private ProgressBar playerHealth;
    private ProgressBar playerSpecial;
    private ProgressBar enemyHealth;
    private Label statusLabel;

    public GameHUD() {
        hudPane = new BorderPane();
        setupHUD();
    }

    private void setupHUD() {
        // Top HUD - Enemy info
        HBox enemyInfo = new HBox(15);
        enemyInfo.setAlignment(Pos.CENTER_RIGHT);
        enemyInfo.setPadding(new Insets(15, 30, 0, 0));

        // Enemy health bar
        VBox enemyHealthBox = new VBox(5);
        Label enemyNameLabel = new Label("Armored Titan");
        enemyNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        enemyNameLabel.setTextFill(Color.WHITE);
        enemyNameLabel.setEffect(new DropShadow(5, Color.BLACK));

        enemyHealth = new ProgressBar(1.0);
        enemyHealth.getStyleClass().add("health-bar");
        enemyHealth.setPrefWidth(300);
        enemyHealth.setPrefHeight(20);

        enemyHealthBox.getChildren().addAll(enemyNameLabel, enemyHealth);

        // Enemy icon
        Image enemyIcon = new Image("https://www.pngmart.com/files/13/Attack-On-Titan-Armored-Titan-PNG-Transparent-Image.png");
        ImageView enemyIconView = new ImageView(enemyIcon);
        enemyIconView.setFitHeight(60);
        enemyIconView.setPreserveRatio(true);

        enemyInfo.getChildren().addAll(enemyHealthBox, enemyIconView);
        hudPane.setTop(enemyInfo);

        // Bottom HUD - Player info and actions
        BorderPane bottomHUD = new BorderPane();
        bottomHUD.setPadding(new Insets(10));
        bottomHUD.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Player info (left side)
        HBox playerInfo = new HBox(15);
        playerInfo.setAlignment(Pos.CENTER_LEFT);
        playerInfo.setPadding(new Insets(0, 0, 0, 20));

        // Player icon
        Image playerIcon = new Image("https://www.pngmart.com/files/13/Attack-On-Titan-Eren-Jaeger-PNG-Transparent-Image.png");
        ImageView playerIconView = new ImageView(playerIcon);
        playerIconView.setFitHeight(60);
        playerIconView.setPreserveRatio(true);

        // Player health and special meters
        VBox playerMeters = new VBox(8);

        HBox nameBox = new HBox();
        Label playerNameLabel = new Label("Eren Yeager");
        playerNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerNameLabel.setTextFill(Color.WHITE);
        playerNameLabel.setEffect(new DropShadow(5, Color.BLACK));
        nameBox.getChildren().add(playerNameLabel);

        HBox healthBox = new HBox(10);
        Label healthLabel = new Label("HP");
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        playerHealth = new ProgressBar(1.0);
        playerHealth.getStyleClass().add("health-bar");
        playerHealth.setPrefWidth(200);
        playerHealth.setPrefHeight(15);
        healthBox.getChildren().addAll(healthLabel, playerHealth);

        HBox specialBox = new HBox(10);
        Label specialLabel = new Label("SP");
        specialLabel.setTextFill(Color.WHITE);
        specialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        playerSpecial = new ProgressBar(0.3);
        playerSpecial.getStyleClass().add("special-meter");
        playerSpecial.setPrefWidth(200);
        playerSpecial.setPrefHeight(15);
        specialBox.getChildren().addAll(specialLabel, playerSpecial);

        playerMeters.getChildren().addAll(nameBox, healthBox, specialBox);
        playerInfo.getChildren().addAll(playerIconView, playerMeters);
        bottomHUD.setLeft(playerInfo);

        // Status display (center)
        statusLabel = new Label("Battle in progress!");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setEffect(new DropShadow(10, Color.BLACK));
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        bottomHUD.setCenter(statusLabel);

        hudPane.setBottom(bottomHUD);
    }

    public BorderPane getHUDPane() {
        return hudPane;
    }

    public void updatePlayerHealth(double value) {
        playerHealth.setProgress(value / 100);

        // Change color based on health
        if (value < 30) {
            playerHealth.setStyle("-fx-accent: #FF0000;"); // Red for low health
        } else if (value < 60) {
            playerHealth.setStyle("-fx-accent: #FFA500;"); // Orange for medium health
        }
    }

    public void updateEnemyHealth(double value) {
        enemyHealth.setProgress(value / 100);
    }

    public void updateSpecialMeter(double value) {
        playerSpecial.setProgress(value / 100);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}