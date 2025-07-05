package game.engine.gui;

import game.engine.lanes.Lane;
import game.engine.weapons.Weapon;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LaneView extends VBox {
    private final Lane lane;
    private final Pane weaponsContainer;
    private final HashMap<Weapon, WeaponView> weaponViewMap;
    private final ProgressBar wallHealthBar;
    private final Label laneLabel;

    public LaneView(Lane lane, int laneIndex) {
        this.lane = lane;
        this.weaponViewMap = new HashMap<>();

        // Create lane container
        setPrefWidth(150);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: #A0522D; -fx-border-width: 2; -fx-background-color: rgba(139,69,19,0.3);");

        // Lane label
        laneLabel = new Label("Lane " + (laneIndex + 1));
        laneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        // Wall health bar
        wallHealthBar = new ProgressBar(1.0);
        wallHealthBar.setPrefWidth(130);
        wallHealthBar.setStyle("-fx-accent: green;");

        // Container for weapons
        weaponsContainer = new Pane();
        weaponsContainer.setPrefHeight(200);
        weaponsContainer.setStyle("-fx-border-color: #8B4513; -fx-border-width: 1; -fx-background-color: rgba(255,228,196,0.3);");

        // Add wall representation
        Rectangle wall = new Rectangle(140, 30);
        wall.setFill(Color.BROWN);

        // Weapon slots area (HBox)
        HBox weaponSlots = new HBox();
        weaponSlots.setSpacing(5);
        weaponSlots.setAlignment(Pos.CENTER);
        weaponSlots.setPrefHeight(50);

        // Add components to lane view
        getChildren().addAll(laneLabel, wallHealthBar, weaponsContainer, wall, weaponSlots);

        // Initial update with weapons
        updateWeapons();
    }

    public void updateHealthBar() {
        double healthPercentage = lane.getLaneWall().getCurrentHealth() /
                (double) lane.getLaneWall().getBaseHealth();
        wallHealthBar.setProgress(healthPercentage);

        // Change color based on health
        if (healthPercentage < 0.3) {
            wallHealthBar.setStyle("-fx-accent: red;");
        } else if (healthPercentage < 0.6) {
            wallHealthBar.setStyle("-fx-accent: orange;");
        } else {
            wallHealthBar.setStyle("-fx-accent: green;");
        }
    }

    public void updateWeapons() {
        List<Weapon> weapons = new ArrayList<>(lane.getWeapons());

        // Remove weapon views that are no longer in the lane
        List<Weapon> weaponsToRemove = new ArrayList<>();
        for (Weapon weapon : weaponViewMap.keySet()) {
            if (!weapons.contains(weapon)) {
                weaponsToRemove.add(weapon);
            }
        }

        for (Weapon weapon : weaponsToRemove) {
            WeaponView weaponView = weaponViewMap.remove(weapon);
            weaponsContainer.getChildren().remove(weaponView);
        }

        // Add new weapon views
        for (int i = 0; i < weapons.size(); i++) {
            Weapon weapon = weapons.get(i);
            if (!weaponViewMap.containsKey(weapon)) {
                WeaponView weaponView = new WeaponView(weapon);
                weaponViewMap.put(weapon, weaponView);
                weaponsContainer.getChildren().add(weaponView);

                // Position weapons in a grid-like pattern
                int row = i / 3;
                int col = i % 3;
                weaponView.updatePosition(10 + col * 45, 10 + row * 45);
            }
        }
    }

    public void highlightLane(boolean highlight) {
        if (highlight) {
            setStyle("-fx-border-color: gold; -fx-border-width: 3; -fx-background-color: rgba(139,69,19,0.5);");
        } else {
            setStyle("-fx-border-color: #A0522D; -fx-border-width: 2; -fx-background-color: rgba(139,69,19,0.3);");
        }
    }

    public Lane getLane() {
        return lane;
    }
}