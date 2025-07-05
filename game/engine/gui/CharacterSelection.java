package game.engine.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class CharacterSelection {

    private Stage stage;
    private String selectedCharacter = null;

    public CharacterSelection(Stage stage) {
        this.stage = stage;
    }

    public Scene createCharacterSelectionScene() {
        BorderPane mainPane = new BorderPane();

        // Load background
        Image bgImage = new Image("https://wallpapercave.com/wp/wp7996305.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true));
        mainPane.setBackground(new Background(backgroundImage));

        // Title
        Label title = new Label("SELECT YOUR SCOUT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(10, Color.BLACK));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(30, 0, 0, 0));
        mainPane.setTop(title);

        // Character selection grid
        GridPane charactersGrid = new GridPane();
        charactersGrid.setAlignment(Pos.CENTER);
        charactersGrid.setHgap(50);
        charactersGrid.setVgap(30);

        // Add character options
        addCharacterOption(charactersGrid, 0, 0, "Eren Yeager",
                "https://www.pngmart.com/files/13/Attack-On-Titan-Eren-Jaeger-PNG-Transparent-Image.png",
                "Titan Shifter\nSpecial: Titan Transformation\nStrength: High\nAgility: Medium");

        addCharacterOption(charactersGrid, 1, 0, "Mikasa Ackerman",
                "https://www.pngmart.com/files/13/Attack-On-Titan-Mikasa-PNG-Clipart.png",
                "Elite Soldier\nSpecial: Ackerman Power\nStrength: High\nAgility: Very High");

        addCharacterOption(charactersGrid, 0, 1, "Levi Ackerman",
                "https://www.pngmart.com/files/13/Captain-Levi-PNG-Picture.png",
                "Captain\nSpecial: Whirlwind Attack\nStrength: Very High\nAgility: Extreme");

        addCharacterOption(charactersGrid, 1, 1, "Armin Arlert",
                "https://www.pngmart.com/files/22/Attack-On-Titan-Armin-PNG-HD.png",
                "Tactician\nSpecial: Strategic Analysis\nStrength: Low\nAgility: Medium");

        mainPane.setCenter(charactersGrid);

        // Bottom buttons
        HBox bottomButtons = new HBox(20);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(0, 0, 30, 0));

        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> {
            // Return to main menu
            stage.setScene(new HelloApplication().createMainMenuScene());
        });

        Button selectButton = new Button("Select & Continue");
        styleButton(selectButton);
        selectButton.setDisable(true);
        selectButton.setOnAction(e -> {
            if (selectedCharacter != null) {
                // Start game with selected character
                // This would connect to your game initialization
            }
        });

        bottomButtons.getChildren().addAll(backButton, selectButton);
        mainPane.setBottom(bottomButtons);

        return new Scene(mainPane, 1280, 720);
    }

    private void addCharacterOption(GridPane grid, int col, int row, String name, String imageUrl, String description) {
        VBox characterBox = new VBox(15);
        characterBox.setAlignment(Pos.CENTER);
        characterBox.setPadding(new Insets(15));
        characterBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-border-color: #8B0000; -fx-border-width: 2px;");
        characterBox.setPrefWidth(300);
        characterBox.setPrefHeight(380);

        // Character name
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.WHITE);

        // Character image
        Image charImage = new Image(imageUrl);
        ImageView imageView = new ImageView(charImage);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Character description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.LIGHTGRAY);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setWrapText(true);

        // Select button
        Button selectBtn = new Button("Select");
        selectBtn.getStyleClass().add("aot-button");
        selectBtn.setPrefWidth(120);

        // Add click handler
        final String charName = name;
        selectBtn.setOnAction(e -> {
            selectedCharacter = charName;

            // Reset all character boxes
            for (int i = 0; i < grid.getChildren().size(); i++) {
                if (grid.getChildren().get(i) instanceof VBox) {
                    VBox box = (VBox) grid.getChildren().get(i);
                    box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-border-color: #8B0000; -fx-border-width: 2px;");
                }
            }

            // Highlight selected character
            characterBox.setStyle("-fx-background-color: rgba(139, 0, 0, 0.6); -fx-border-color: #FFD700; -fx-border-width: 3px;");

            // Enable continue button
            ((Button)((HBox)((BorderPane)grid.getParent()).getBottom()).getChildren().get(1)).setDisable(false);
        });

        characterBox.getChildren().addAll(nameLabel, imageView, descLabel, selectBtn);
        grid.add(characterBox, col, row);
    }

    private void styleButton(Button button) {
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.getStyleClass().add("aot-button");
    }
}