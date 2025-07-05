package game.engine.gui;

import game.engine.Battle;
import game.engine.lanes.Lane;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class BattleView extends BorderPane {
    private final Battle battle;
    private final List<LaneView> laneViews;
    private final Label scoreLabel;
    private final Label resourcesLabel;
    private final Label phaseLabel;
    private final Label messageLabel;
    private final Timeline updateTimeline;

    public BattleView(Battle battle) {
        this.battle = battle;
        this.laneViews = new ArrayList<>();

        // Enable visual feedback in the battle engine
        battle.setVisualFeedbackEnabled(true);

        // Top info panel
        HBox topPanel = new HBox(20);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));
        topPanel.setStyle("-fx-background-color: #2C3E50;");

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        resourcesLabel = new Label("Resources: 0");
        resourcesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        phaseLabel = new Label("Phase: EARLY");
        phaseLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        topPanel.getChildren().addAll(scoreLabel, resourcesLabel, phaseLabel);

        // Center lanes container
        HBox lanesContainer = new HBox(10);
        lanesContainer.setAlignment(Pos.CENTER);
        lanesContainer.setPadding(new Insets(20));
        lanesContainer.setStyle("-fx-background-color: #34495E;");

        // Create lane views
        ArrayList<Lane> lanes = battle.getOriginalLanes();
        for (int i = 0; i < lanes.size(); i++) {
            Lane lane = lanes.get(i);
            LaneView laneView = new LaneView(lane, i);
            laneViews.add(laneView);
            lanesContainer.getChildren().add(laneView);
        }

        // Bottom controls
        VBox bottomPanel = new VBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #2C3E50;");

        // Message label for game events
        messageLabel = new Label("Welcome to Attack on Titan!");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        // Control buttons
        HBox controlsBox = new HBox(20);
        controlsBox.setAlignment(Pos.CENTER);

        Button nextTurnBtn = new Button("Next Turn");
        nextTurnBtn.setOnAction(e -> performNextTurn());

        Button buyWeaponBtn = new Button("Buy Weapon");
        buyWeaponBtn.setOnAction(e -> openWeaponShop());

        controlsBox.getChildren().addAll(nextTurnBtn, buyWeaponBtn);
        bottomPanel.getChildren().addAll(messageLabel, controlsBox);

        // Set layout
        setTop(topPanel);
        setCenter(lanesContainer);
        setBottom(bottomPanel);

        // Setup auto-update timeline
        updateTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> updateDisplay())
        );
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    private void updateDisplay() {
        // Update score and resources
        scoreLabel.setText("Score: " + battle.getScore());
        resourcesLabel.setText("Resources: " + battle.getResourcesGathered());
        phaseLabel.setText("Phase: " + battle.getBattlePhase());

        // Update message if there's a new one
        String lastMessage = battle.getLastActionMessage();
        if (lastMessage != null && !lastMessage.isEmpty()) {
            messageLabel.setText(lastMessage);
        }

        // Update lane views
        for (LaneView laneView : laneViews) {
            laneView.updateHealthBar();
            laneView.updateWeapons();
        }

        // Check for game over
        if (battle.isGameOver()) {
            updateTimeline.stop();
            messageLabel.setText("GAME OVER! Final score: " + battle.getScore());
        }
    }

    private void performNextTurn() {
        battle.performTurn();
        updateDisplay();
    }

    private void openWeaponShop() {
        WeaponShopDialog shopDialog = new WeaponShopDialog(battle, laneViews);
        shopDialog.show();
    }

    public void stopUpdates() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}