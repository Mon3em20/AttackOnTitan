package game.engine.gui;

import game.engine.Battle;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

public class WeaponShopDialog extends Stage {
    private final Battle battle;
    private final List<LaneView> laneViews;
    private final HashMap<Integer, String> weaponTypes;
    private final HashMap<Integer, Integer> weaponCosts;
    private final HashMap<Integer, String> weaponImages;

    public WeaponShopDialog(Battle battle, List<LaneView> laneViews) {
        this.battle = battle;
        this.laneViews = laneViews;

        // Initialize weapon types and costs
        weaponTypes = new HashMap<>();
        weaponTypes.put(1, "Blade");
        weaponTypes.put(2, "ODM Gear");
        weaponTypes.put(3, "Thunder Spear");
        weaponTypes.put(4, "Cannon");

        weaponCosts = new HashMap<>();
        weaponCosts.put(1, 20);
        weaponCosts.put(2, 50);
        weaponCosts.put(3, 100);
        weaponCosts.put(4, 150);

        weaponImages = new HashMap<>();
        weaponImages.put(1, "https://i.imgur.com/QsYQZx5.png");
        weaponImages.put(2, "https://i.imgur.com/PUmMG9l.png");
        weaponImages.put(3, "https://i.imgur.com/LuVoZG5.png");
        weaponImages.put(4, "https://i.imgur.com/Z5jveC9.png");

        // Configure dialog
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Weapon Shop");
        setMinWidth(400);
        setMinHeight(300);

        // Create layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Resources display
        Label resourcesLabel = new Label("Available Resources: " + battle.getResourcesGathered());
        resourcesLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Weapon selection grid
        GridPane weaponsGrid = new GridPane();
        weaponsGrid.setHgap(10);
        weaponsGrid.setVgap(15);
        weaponsGrid.setAlignment(Pos.CENTER);

        // Selected weapon and lane
        int[] selectedWeapon = {0};
        ComboBox<String> laneSelector = new ComboBox<>();

        for (int i = 0; i < laneViews.size(); i++) {
            laneSelector.getItems().add("Lane " + (i + 1));
        }
        laneSelector.getSelectionModel().selectFirst();

        // Create weapon cards
        for (int i = 1; i <= weaponTypes.size(); i++) {
            final int weaponId = i;
            VBox weaponCard = createWeaponCard(
                    weaponId,
                    weaponTypes.get(weaponId),
                    weaponCosts.get(weaponId),
                    weaponImages.get(weaponId),
                    selectedWeapon
            );
            weaponsGrid.add(weaponCard, (i-1) % 2, (i-1) / 2);
        }

        // Purchase controls
        HBox purchaseControls = new HBox(15);
        purchaseControls.setAlignment(Pos.CENTER);

        Label selectedLabel = new Label("Select weapon and lane");

        Button buyButton = new Button("Purchase & Deploy");
        buyButton.setDisable(true);

        // Update selected label when a weapon is chosen
        buyButton.setOnAction(e -> {
            try {
                int laneIndex = laneSelector.getSelectionModel().getSelectedIndex();
                battle.purchaseWeapon(selectedWeapon[0], laneViews.get(laneIndex).getLane());
                resourcesLabel.setText("Available Resources: " + battle.getResourcesGathered());

                // Show success message
                selectedLabel.setText("Weapon deployed successfully!");
                selectedLabel.setStyle("-fx-text-fill: green;");

                // Update the lane view
                laneViews.get(laneIndex).updateWeapons();

            } catch (InsufficientResourcesException ex) {
                selectedLabel.setText("Not enough resources!");
                selectedLabel.setStyle("-fx-text-fill: red;");
            } catch (InvalidLaneException ex) {
                selectedLabel.setText("Invalid lane selected!");
                selectedLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Enable buy button when a weapon is selected
        purchaseControls.getChildren().addAll(laneSelector, buyButton);

        // Add all components to layout
        layout.getChildren().addAll(
                resourcesLabel,
                weaponsGrid,
                selectedLabel,
                purchaseControls,
                new Button("Close") {{ setOnAction(e -> close()); }}
        );

        // Create scene and set to stage
        Scene scene = new Scene(layout);
        setScene(scene);
    }

    private VBox createWeaponCard(int id, String name, int cost, String imageUrl, int[] selectedWeapon) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #8B4513; -fx-border-width: 1; -fx-background-color: #F5F5DC;");

        // Weapon image
        ImageView imageView = new ImageView(new Image(imageUrl, 60, 60, true, true));

        // Weapon info
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label costLabel = new Label("Cost: " + cost);

        Button selectBtn = new Button("Select");
        selectBtn.setOnAction(e -> {
            selectedWeapon[0] = id;

            // Highlight selected weapon card
            for (int i = 0; i < card.getParent().getChildrenUnmodifiable().size(); i++) {
                if (card.getParent().getChildrenUnmodifiable().get(i) instanceof VBox) {
                    ((VBox) card.getParent().getChildrenUnmodifiable().get(i)).setStyle(
                            "-fx-border-color: #8B4513; -fx-border-width: 1; -fx-background-color: #F5F5DC;"
                    );
                }
            }

            card.setStyle("-fx-border-color: gold; -fx-border-width: 2; -fx-background-color: #F5F5DC;");

            // Enable buy button
            if (card.getParent().getParent().getParent() instanceof VBox) {
                VBox mainLayout = (VBox) card.getParent().getParent().getParent();
                for (javafx.scene.Node node : mainLayout.getChildren()) {
                    if (node instanceof HBox) {
                        HBox hbox = (HBox) node;
                        for (javafx.scene.Node hboxChild : hbox.getChildren()) {
                            if (hboxChild instanceof Button && ((Button) hboxChild).getText().contains("Purchase")) {
                                ((Button) hboxChild).setDisable(false);
                            }
                        }
                    }
                }
            }
        });

        card.getChildren().addAll(imageView, nameLabel, costLabel, selectBtn);
        return card;
    }
}