package game.engine.gui;

import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import game.engine.Battle;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.engine.lanes.Lane;
import game.engine.titans.*;
import game.engine.weapons.Weapon;
import game.engine.weapons.WeaponRegistry;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HelloApplication extends Application implements EventHandler<ActionEvent> {

    // Main UI elements
    private Label label;
    private ToggleGroup switchMode;
    private RadioButton easy;
    private RadioButton hard;

    // Game variables
    private static Battle battle;
    private WeaponRegistry weaponRegistry;
    private Button passTurnEasy;
    private Stage stage;

    // Game status labels
    private Label Turn = new Label("Turn:");
    private Label Score = new Label("Score:");
    private Label Phase = new Label("Phase:");
    private Label Res = new Label("Resources:");

    // Game elements
    private PureTitan pureTitan;
    private static ArrayList<Lane> lanes = new ArrayList<>();
    private static int WeaponCode = 1;
    private static VBox vboxButtonLane = new VBox();

    // Grid panes for lanes
    private static GridPane gridPane1;
    private static GridPane gridPane2;
    private static GridPane gridPane3;
    private static GridPane gridPane4;
    private static GridPane gridPane5;

    // Movement speed multiplier (increased for faster movement)
    private static final double SPEED_MULTIPLIER = 2.0;

    // Default colors for titans and weapons
    private static HashMap<String, Color> titanColors = new HashMap<>();
    private static HashMap<String, Color> weaponColors = new HashMap<>();

    // Initialize color schemes for titans and weapons
    static {
        // Set titan colors
        titanColors.put("PureTitan", Color.rgb(220, 50, 50));
        titanColors.put("AbnormalTitan", Color.rgb(240, 150, 20));
        titanColors.put("ArmoredTitan", Color.rgb(50, 50, 180));
        titanColors.put("ColossalTitan", Color.rgb(20, 20, 20));

        // Set weapon colors
        weaponColors.put("PiercingCannon", Color.RED);
        weaponColors.put("WallTrap", Color.GOLD);
        weaponColors.put("VolleySpreadCannon", Color.GREEN);
        weaponColors.put("SniperCannon", Color.CYAN);
    }

    // Weapon buttons with enhanced styling
    private Button piercingCannon = createWeaponButton("Piercing Cannon", "25", "10", Color.RED);
    private Button walltrap = createWeaponButton("Wall Trap", "100", "5", Color.GOLD);
    private Button volleySpreadCannon = createWeaponButton("Volley Spread Cannon", "100", "5", Color.GREEN);
    private Button snipercannon = createWeaponButton("Sniper Cannon", "25", "35", Color.CYAN);

    // Create stylized weapon button using color instead of images for reliability
    private Button createWeaponButton(String name, String price, String damage, Color color) {
        Button button = new Button();
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);

        // Create weapon icon using a rectangle with color
        Rectangle weaponIcon = new Rectangle(30, 30);
        weaponIcon.setFill(color);
        weaponIcon.setStroke(Color.BLACK);
        weaponIcon.setStrokeWidth(1);
        weaponIcon.setArcHeight(5);
        weaponIcon.setArcWidth(5);

        // Create labels
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Impact", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE);

        Label priceLabel = new Label("Cost: " + price);
        priceLabel.setFont(Font.font("Arial", 12));
        priceLabel.setTextFill(Color.YELLOW);

        Label damageLabel = new Label("Damage: " + damage);
        damageLabel.setFont(Font.font("Arial", 12));
        damageLabel.setTextFill(Color.LIGHTGREEN);

        content.getChildren().addAll(weaponIcon, nameLabel, priceLabel, damageLabel);
        button.setGraphic(content);
        button.setPrefSize(162, 85);

        // Add styling
        button.setStyle("-fx-background-color: #3c3c3c; -fx-background-radius: 5; -fx-border-color: #8b0000; -fx-border-width: 2; -fx-border-radius: 5;");

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(10, Color.RED));
            button.setStyle("-fx-background-color: #5c5c5c; -fx-background-radius: 5; -fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-radius: 5;");
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            button.setStyle("-fx-background-color: #3c3c3c; -fx-background-radius: 5; -fx-border-color: #8b0000; -fx-border-width: 2; -fx-border-radius: 5;");
        });

        return button;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Set application title and icon
        primaryStage.setTitle("Attack on Titan: Wall Defense");

        // Create main menu scene
        Scene mainMenuScene = createMainMenuScene();
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public Scene createMainMenuScene() {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(1000, 800);

        // Set background color
        root.setStyle("-fx-background-color: #333333;");

        // Create title with glow effect
        label = new Label("Attack on Titan: Wall Defense");
        label.setLayoutX(220);
        label.setLayoutY(79);
        label.setPrefSize(600, 100);
        label.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(20, Color.RED));

        // Add logo using rectangle for reliability
        Rectangle logoRect = new Rectangle(120, 120);
        logoRect.setFill(Color.DARKRED);
        logoRect.setStroke(Color.GOLD);
        logoRect.setStrokeWidth(3);
        logoRect.setArcHeight(20);
        logoRect.setArcWidth(20);
        logoRect.setLayoutX(440);
        logoRect.setLayoutY(170);

        // Create stylized difficulty selection buttons
        switchMode = new ToggleGroup();

        easy = createStyledRadioButton("Easy Mode", 242, 286);
        easy.setToggleGroup(switchMode);
        easy.setOnAction(event -> easy());

        hard = createStyledRadioButton("Hard Mode", 609, 286);
        hard.setToggleGroup(switchMode);
        hard.setOnAction(event -> hard());

        // Create stylized buttons
        Button btnStart = createStylizedButton("Start Game", 418, 365, 200, 71);
        btnStart.setOnAction(this);

        Button btnInstructions = createStylizedButton("Instructions", 418, 460, 200, 61);
        btnInstructions.setOnAction(this);

        // Add credits at the bottom
        Label credits = new Label("© 2025 Scout Regiment");
        credits.setLayoutX(440);
        credits.setLayoutY(750);
        credits.setTextFill(Color.WHITE);
        credits.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        root.getChildren().addAll(label, logoRect, easy, hard, btnStart, btnInstructions, credits);

        return new Scene(root);
    }

    private RadioButton createStyledRadioButton(String text, int x, int y) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setLayoutX(x);
        radioButton.setLayoutY(y);
        radioButton.setFont(Font.font("Impact", 25));
        radioButton.setTextFill(Color.WHITE);

        // Add drop shadow for better visibility against background
        radioButton.setEffect(new DropShadow(5, Color.BLACK));

        return radioButton;
    }

    private Button createStylizedButton(String text, int x, int y, int width, int height) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(width, height);
        button.setFont(Font.font("Impact", 24));

        // Style the button
        button.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(10, Color.RED));
            button.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            button.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        return button;
    }

    private void Instructions(ActionEvent event) {
        // Create instruction dialog
        Stage instructionStage = new Stage();
        instructionStage.setTitle("Game Instructions");
        instructionStage.initModality(Modality.APPLICATION_MODAL);

        VBox instructionBox = new VBox(20);
        instructionBox.setPadding(new Insets(20));
        instructionBox.setAlignment(Pos.CENTER);
        instructionBox.setStyle("-fx-background-color: #333333;");

        // Add logo using rectangle for reliability
        Rectangle logoRect = new Rectangle(80, 80);
        logoRect.setFill(Color.DARKRED);
        logoRect.setStroke(Color.GOLD);
        logoRect.setStrokeWidth(3);
        logoRect.setArcHeight(20);
        logoRect.setArcWidth(20);

        // Add title
        Label title = new Label("HOW TO PLAY");
        title.setFont(Font.font("Impact", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        // Add instructions
        String instructions =
                "1. Defend your walls from advancing Titans\n\n" +
                        "2. Purchase weapons and place them in lanes\n\n" +
                        "3. Each weapon has different damage and cost\n\n" +
                        "4. Titans move closer each turn\n\n" +
                        "5. If titans reach the wall, you lose health\n\n" +
                        "6. Survive as long as possible!";

        Label instructionsLabel = new Label(instructions);
        instructionsLabel.setFont(Font.font("Arial", 16));
        instructionsLabel.setTextFill(Color.WHITE);

        // Add weapon display using colored rectangles
        HBox weaponDisplay = new HBox(20);
        weaponDisplay.setAlignment(Pos.CENTER);

        weaponDisplay.getChildren().addAll(
                createColoredWeaponBox("Piercing Cannon", Color.RED),
                createColoredWeaponBox("Wall Trap", Color.GOLD),
                createColoredWeaponBox("Thunder Spear", Color.CYAN)
        );

        // Add game mode descriptions
        Label easyModeTitle = new Label("EASY MODE");
        easyModeTitle.setFont(Font.font("Impact", 18));
        easyModeTitle.setTextFill(Color.GREEN);

        Label easyModeDesc = new Label("3 lanes, more resources, slower titans");
        easyModeDesc.setFont(Font.font("Arial", 14));
        easyModeDesc.setTextFill(Color.WHITE);

        Label hardModeTitle = new Label("HARD MODE");
        hardModeTitle.setFont(Font.font("Impact", 18));
        hardModeTitle.setTextFill(Color.RED);

        Label hardModeDesc = new Label("5 lanes, fewer resources, faster titans");
        hardModeDesc.setFont(Font.font("Arial", 14));
        hardModeDesc.setTextFill(Color.WHITE);

        // Add close button
        Button closeButton = new Button("I'm Ready to Fight!");
        closeButton.setFont(Font.font("Impact", 16));
        closeButton.setPrefSize(200, 40);
        closeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        closeButton.setOnAction(e -> instructionStage.close());

        // Add hover effect
        closeButton.setOnMouseEntered(e -> {
            closeButton.setEffect(new DropShadow(10, Color.RED));
            closeButton.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        closeButton.setOnMouseExited(e -> {
            closeButton.setEffect(null);
            closeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        // Add all elements
        instructionBox.getChildren().addAll(
                logoRect, title, instructionsLabel,
                new Label("WEAPONS:"),
                weaponDisplay,
                easyModeTitle, easyModeDesc,
                hardModeTitle, hardModeDesc,
                closeButton
        );

        Scene instructionScene = new Scene(instructionBox, 600, 700);
        instructionStage.setScene(instructionScene);
        instructionStage.showAndWait();
    }

    private VBox createColoredWeaponBox(String name, Color color) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // Create colored rectangle instead of using image
        Rectangle weaponRect = new Rectangle(40, 40);
        weaponRect.setFill(color);
        weaponRect.setStroke(Color.BLACK);
        weaponRect.setStrokeWidth(1);
        weaponRect.setArcHeight(5);
        weaponRect.setArcWidth(5);

        Label nameLabel = new Label(name);
        nameLabel.setTextFill(Color.WHITE);

        box.getChildren().addAll(weaponRect, nameLabel);
        return box;
    }

    private void Start() {
        System.out.println("Start button clicked");
    }

    private void easy() {
        System.out.println("Easy mode selected");
    }

    private void hard() {
        System.out.println("Hard mode selected");
    }

    // دالة إنشاء جدار المقر مع معلومات الدمج والصحة
    private StackPane createWallWithInfo(double x, double y, double width, double height, int laneIndex) {
        StackPane wallStack = new StackPane();
        wallStack.setLayoutX(x);
        wallStack.setLayoutY(y);

        // إنشاء المقر نفسه بتأثير جرافيكي أفضل
        Rectangle wall = new Rectangle(width, height);
        wall.setArcHeight(8);
        wall.setArcWidth(8);

        // تدرج لوني للجدار يعطي إحساس أفضل بالعمق
        Stop[] stops = new Stop[] {
                new Stop(0, Color.rgb(100, 100, 100)),
                new Stop(0.3, Color.rgb(150, 150, 150)),
                new Stop(0.7, Color.rgb(120, 120, 120)),
                new Stop(1, Color.rgb(90, 90, 90))
        };

        LinearGradient wallGradient = new LinearGradient(0, 0, 1, 1, true,
                javafx.scene.paint.CycleMethod.NO_CYCLE, stops);
        wall.setFill(wallGradient);

        // إطار سميك للجدار
        wall.setStroke(Color.rgb(60, 60, 60));
        wall.setStrokeWidth(3);

        // إضافة تأثير ظل للعمق
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(10);
        wall.setEffect(shadow);

        // إنشاء VBox لعرض معلومات المقر
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER);

        // صحة المقر
        int wallHealth = lanes.get(laneIndex).getWallHealth();
        Label healthLabel = new Label("HP: " + wallHealth);
        healthLabel.setFont(Font.font("Impact", FontWeight.BOLD, 14));
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 2 5; -fx-background-radius: 3;");

        // دمج المقر (قوة الهجوم)
        int wallDamage = lanes.get(laneIndex).getWallDamage();
        Label damageLabel = new Label("DMG: " + wallDamage);
        damageLabel.setFont(Font.font("Impact", 14));
        damageLabel.setTextFill(Color.rgb(255, 200, 50));
        damageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 2 5; -fx-background-radius: 3;");

        infoBox.getChildren().addAll(healthLabel, damageLabel);

        // إضافة الجدار والمعلومات للـ StackPane
        wallStack.getChildren().addAll(wall, infoBox);

        return wallStack;
    }

    private AnchorPane Easymode() {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(1000, 800);

        // Set background color
        root.setStyle("-fx-background-color: #222222;");

        // Add semi-transparent overlay for better visibility of game elements
        Rectangle overlay = new Rectangle(0, 0, 1000, 800);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));
        root.getChildren().add(overlay);

        HBox hBox = new HBox(10);
        hBox.setLayoutX(200);
        hBox.setLayoutY(14);
        hBox.setPrefSize(600, 85);
        try {
            battle = new Battle(3, 100, 1, 0, 80, 3, 250);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lanes = new ArrayList<>();
        lanes.addAll(battle.getLanes());

        // Reset button actions with the existing code
        piercingCannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(1); });
        walltrap.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(4); });
        volleySpreadCannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(3); });
        snipercannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(2); });

        hBox.getChildren().addAll(piercingCannon, walltrap, volleySpreadCannon, snipercannon);

        // Create stylized status labels
        Turn = createStatusLabel("Turn:", 14, 14);
        Phase = createStatusLabel("Phase:", 14, 57);
        Score = createStatusLabel("Score:", 831, 14);
        Res = createStatusLabel("Resources:", 831, 57);

        // Create styled divider lines
        Line line1 = createDividerLine(-101, 0, 963.5, 0, 101, 172);
        Line line2 = createDividerLine(-107, 37, 958.5, 37, 106, 335);
        Line line3 = createDividerLine(-107, 0, 959.5, 0, 107, 572);
        Line line4 = createDividerLine(-100, 0, 965.5, 0, 101, 772);

        // إنشاء المقرات المحسنة مع معلومات
        StackPane wall1 = createWallWithInfo(2, 222, 155, 150, 0);
        StackPane wall2 = createWallWithInfo(2, 422, 155, 150, 1);
        StackPane wall3 = createWallWithInfo(2, 622, 155, 150, 2);

        // Create lane labels
        Label l5 = createLaneLabel("Health", 10, 164);
        Label l6 = createLaneLabel("Damage", 10, 184);
        Label l7 = createLaneLabel("Health", 10, 370);
        Label l8 = createLaneLabel("Damage", 10, 390);
        Label l9 = createLaneLabel("Health", 10, 572);
        Label l10 = createLaneLabel("Damage", 10, 592);

        // Create stylized pass turn button
        passTurnEasy = createPassTurnButton(410, 126);
        passTurnEasy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                battle.passTurn();
                PassturnEasy(event);
                moveTitan();

                // Add animation effect when turn passes
                Glow glow = new Glow();
                glow.setLevel(0.8);
                passTurnEasy.setEffect(glow);

                // Remove effect after 500ms
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(500));
                pause.setOnFinished(e -> passTurnEasy.setEffect(null));
                pause.play();
            }
        });

        // Create grid panes for titan placement
        gridPane1 = createGridPane();
        gridPane1.setLayoutX(156);
        gridPane1.setLayoutY(171);

        gridPane2 = createGridPane();
        gridPane2.setLayoutX(156);
        gridPane2.setLayoutY(373);

        gridPane3 = createGridPane();
        gridPane3.setLayoutX(156);
        gridPane3.setLayoutY(573);

        // Add lane labels
        Label lane1Label = createLaneHeadingLabel("Lane 1", 500, 150);
        Label lane2Label = createLaneHeadingLabel("Lane 2", 500, 350);
        Label lane3Label = createLaneHeadingLabel("Lane 3", 500, 550);

        root.getChildren().addAll(hBox, Turn, Phase, Score, Res,
                line1, line2, line3, line4,
                wall1, wall2, wall3,
                l5, l6, l7, l8, l9, l10,
                passTurnEasy,
                gridPane1, gridPane2, gridPane3,
                lane1Label, lane2Label, lane3Label);

        // Update initial status
        updateBattleStatus(3, 250);

        // Initialize weapons display
        moveTitan();

        return root;
    }

    private Label createLaneHeadingLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(Font.font("Impact", 18));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.BLACK));
        return label;
    }

    private Label createStatusLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefSize(189, 51);
        label.setFont(Font.font("Impact", 18));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.BLACK));
        return label;
    }

    private Line createDividerLine(double startX, double startY, double endX, double endY, double layoutX, double layoutY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setLayoutX(layoutX);
        line.setLayoutY(layoutY);
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
        return line;
    }

    private Rectangle createWall(double x, double y, double width, double height) {
        Rectangle wall = new Rectangle(x, y, width, height);
        wall.setArcHeight(5);
        wall.setArcWidth(5);

        // Create a wall texture gradient
        Stop[] stops = new Stop[] {
                new Stop(0, Color.DARKGRAY),
                new Stop(0.3, Color.GRAY),
                new Stop(0.7, Color.DARKGRAY),
                new Stop(1, Color.GRAY)
        };
        LinearGradient wallGradient = new LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE, stops);

        wall.setFill(wallGradient);
        wall.setStroke(Color.BLACK);
        wall.setStrokeWidth(2);

        return wall;
    }

    private Label createLaneLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefSize(139, 17);
        label.setFont(Font.font("Impact", 14));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(3, Color.BLACK));
        return label;
    }

    private Button createPassTurnButton(int x, int y) {
        Button button = new Button("Pass Turn");
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(243, 40);
        button.setFont(Font.font("Impact", 18));

        // Style the button
        button.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(10, Color.RED));
            button.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            button.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        return button;
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(841, 200);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col1.setPrefWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col2.setPrefWidth(100);
        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row1.setPrefHeight(30);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row2.setPrefHeight(30);
        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row3.setPrefHeight(30);
        gridPane.getRowConstraints().addAll(row1, row2, row3);

        // Add a faint grid line pattern
        gridPane.setGridLinesVisible(true);
        gridPane.setStyle("-fx-grid-lines-visible: true; -fx-grid-line-color: rgba(255, 0, 0, 0.3);");

        return gridPane;
    }

    private AnchorPane Hardmode() throws IOException {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(1000, 800);

        // Set background color
        root.setStyle("-fx-background-color: #111111;");

        // Add semi-transparent overlay for better visibility of game elements
        Rectangle overlay = new Rectangle(0, 0, 1000, 800);
        overlay.setFill(Color.rgb(0, 0, 0, 0.6));
        root.getChildren().add(overlay);

        HBox hBox = new HBox(10);
        hBox.setLayoutX(200);
        hBox.setLayoutY(14);
        hBox.setPrefSize(600, 85);
        try {
            battle = new Battle(3, 100, 1, 0, 70, 5, 125);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lanes = new ArrayList<>();
        lanes.addAll(battle.getLanes());

        // Reset button actions with the existing code
        piercingCannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(1); });
        walltrap.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(4); });
        volleySpreadCannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(3); });
        snipercannon.setOnAction(event -> { if(!battle.isGameOver()) displayAlert(2); });

        hBox.getChildren().addAll(piercingCannon, walltrap, volleySpreadCannon, snipercannon);

        // Create stylized status labels
        Turn = createStatusLabel("Turn:", 14, 14);
        Phase = createStatusLabel("Phase:", 14, 57);
        Score = createStatusLabel("Score:", 831, 14);
        Res = createStatusLabel("Resources:", 831, 57);

        // Create styled divider lines
        Line line1 = createDividerLine(-101, 0, 980, 0, 101, 172);
        Line line2 = createDividerLine(-124.5, 37, 958.5, 37, 122, 260);
        Line line3 = createDividerLine(-107, 0, 976.5, 0, 105, 418);
        Line line4 = createDividerLine(-100, 0, 984.5, 0, 98, 545);
        Line line41 = createDividerLine(-100, 0, 979.5, 0, 104, 669);
        Line line411 = createDividerLine(-107, 0, 977.5, 0, 107, 789);

        // إنشاء المقرات المحسنة مع معلومات
        StackPane wall1 = createWallWithInfo(0, 217, 100, 80, 0);
        StackPane wall2 = createWallWithInfo(0, 338, 100, 80, 1);
        StackPane wall3 = createWallWithInfo(0, 465, 100, 80, 2);
        StackPane wall4 = createWallWithInfo(0, 589, 100, 80, 3);
        StackPane wall5 = createWallWithInfo(0, 709, 100, 80, 4);

        // Create lane labels
        Label l5 = createLaneLabel("Health:", 2, 167);
        Label l6 = createLaneLabel("Damage", 2, 187);
        Label l7 = createLaneLabel("Health:", 5, 293);
        Label l8 = createLaneLabel("Damage", 4, 312);
        Label l9 = createLaneLabel("Health:", 4, 418);
        Label l10 = createLaneLabel("Damage", 4, 438);
        Label l11 = createLaneLabel("Health:", 3, 540);
        Label l12 = createLaneLabel("Damage", 3, 560);
        Label l13 = createLaneLabel("Health:", 2, 664);
        Label l14 = createLaneLabel("Damage", 2, 684);

        // Create stylized pass turn button
        Button passTurnHard = createPassTurnButton(410, 126);
        passTurnHard.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                battle.passTurn();
                PassturnHard(event);
                moveTitan();

                // Add animation effect when turn passes
                Glow glow = new Glow();
                glow.setLevel(0.8);
                passTurnHard.setEffect(glow);

                // Remove effect after 500ms
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(500));
                pause.setOnFinished(e -> passTurnHard.setEffect(null));
                pause.play();
            }
        });

        // Create grid panes for titan placement
        gridPane1 = createGridPane2();
        gridPane1.setLayoutX(99);
        gridPane1.setLayoutY(172);

        gridPane2 = createGridPane2();
        gridPane2.setLayoutX(109);
        gridPane2.setLayoutY(299);

        gridPane3 = createGridPane2();
        gridPane3.setLayoutX(99);
        gridPane3.setLayoutY(418);

        gridPane4 = createGridPane2();
        gridPane4.setLayoutX(109);
        gridPane4.setLayoutY(545);

        gridPane5 = createGridPane2();
        gridPane5.setLayoutX(99);
        gridPane5.setLayoutY(669);

        // Add lane labels
        Label lane1Label = createLaneHeadingLabel("Lane 1", 500, 150);
        Label lane2Label = createLaneHeadingLabel("Lane 2", 500, 277);
        Label lane3Label = createLaneHeadingLabel("Lane 3", 500, 396);
        Label lane4Label = createLaneHeadingLabel("Lane 4", 500, 523);
        Label lane5Label = createLaneHeadingLabel("Lane 5", 500, 647);

        root.getChildren().addAll(
                hBox, Turn, Phase, Score, Res,
                line1, line2, line3, line4, line41, line411,
                wall1, wall2, wall3, wall4, wall5,
                l5, l6, l7, l8, l9, l10, l11, l12, l13, l14,
                passTurnHard,
                gridPane1, gridPane2, gridPane3, gridPane4, gridPane5,
                lane1Label, lane2Label, lane3Label, lane4Label, lane5Label
        );

        // Update initial status
        updateBattleStatus(5, 125);

        // Initialize weapons display
        moveTitan();

        return root;
    }

    public GridPane createGridPane2() {
        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(989, 124);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col1.setMinWidth(10);
        col1.setPrefWidth(100);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col2.setMinWidth(10);
        col2.setPrefWidth(100);

        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row1.setMinHeight(10);
        row1.setPrefHeight(30);

        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row2.setMinHeight(10);
        row2.setPrefHeight(30);

        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        row3.setMinHeight(10);
        row3.setPrefHeight(30);

        gridPane.getRowConstraints().addAll(row1, row2, row3);

        // Add a faint grid line pattern
        gridPane.setGridLinesVisible(true);
        gridPane.setStyle("-fx-grid-lines-visible: true; -fx-grid-line-color: rgba(255, 0, 0, 0.3);");

        return gridPane;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button clickedButton = (Button) actionEvent.getSource();
            if (clickedButton.getText().equals("Start Game")) {
                if (easy.isSelected()) {
                    Stage primaryStage = (Stage) easy.getScene().getWindow();
                    primaryStage.setScene(new Scene(Easymode()));

                } else if (hard.isSelected()) {
                    Stage primaryStage = (Stage) hard.getScene().getWindow();
                    try {
                        primaryStage.setScene(new Scene(Hardmode()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Show an alert if no mode is selected
                    showError("Please select a game mode first!");
                }
            } else if (clickedButton.getText().equals("Instructions")) {
                Instructions(actionEvent);
            }
        }
    }

    private void displayAlert(int code) {
        Stage alertStage = new Stage();
        alertStage.setTitle("Weapon Placement");
        alertStage.initModality(Modality.APPLICATION_MODAL);

        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: #333333;");

        // Create header
        Label header = new Label("Select Lane for Placement");
        header.setFont(Font.font("Impact", 24));
        header.setTextFill(Color.WHITE);
        header.setPadding(new Insets(20, 10, 20, 10));
        header.setAlignment(Pos.CENTER);
        header.setEffect(new DropShadow(5, Color.RED));
        BorderPane.setAlignment(header, Pos.CENTER);
        mainPane.setTop(header);

        // Create weapon info based on code
        VBox weaponInfo = new VBox(10);
        weaponInfo.setAlignment(Pos.CENTER);
        weaponInfo.setPadding(new Insets(20));

        String weaponName = "";
        Color weaponColor = Color.RED;
        String weaponCost = "";
        String weaponDamage = "";

        switch(code) {
            case 1:
                weaponName = "Piercing Cannon";
                weaponColor = Color.RED;
                weaponCost = "25";
                weaponDamage = "10";
                break;
            case 2:
                weaponName = "Sniper Cannon";
                weaponColor = Color.CYAN;
                weaponCost = "25";
                weaponDamage = "35";
                break;
            case 3:
                weaponName = "Volley Spread Cannon";
                weaponColor = Color.GREEN;
                weaponCost = "100";
                weaponDamage = "5";
                break;
            case 4:
                weaponName = "Wall Trap";
                weaponColor = Color.GOLD;
                weaponCost = "100";
                weaponDamage = "5";
                break;
        }

        // Create weapon display using rectangle (more reliable than images)
        Rectangle weaponRect = new Rectangle(80, 80);
        weaponRect.setFill(weaponColor);
        weaponRect.setStroke(Color.BLACK);
        weaponRect.setStrokeWidth(2);
        weaponRect.setArcHeight(10);
        weaponRect.setArcWidth(10);

        // Add simple rotate animation for visual interest
        javafx.animation.RotateTransition rotateTransition =
                new javafx.animation.RotateTransition(Duration.millis(500), weaponRect);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.play();

        Label nameLabel = new Label(weaponName);
        nameLabel.setFont(Font.font("Impact", 20));
        nameLabel.setTextFill(Color.WHITE);

        Label costLabel = new Label("Cost: " + weaponCost + " resources");
        costLabel.setFont(Font.font("Arial", 16));
        costLabel.setTextFill(Color.YELLOW);

        Label damageLabel = new Label("Damage: " + weaponDamage);
        damageLabel.setFont(Font.font("Arial", 16));
        damageLabel.setTextFill(Color.LIGHTGREEN);

        weaponInfo.getChildren().addAll(weaponRect, nameLabel, costLabel, damageLabel);
        mainPane.setLeft(weaponInfo);

        // Create lane selection buttons
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        vboxButtonLane.getChildren().clear();

        for (int i = 0; i < lanes.size(); i++) {
            Lane lane = lanes.get(i);
            Button laneButton = new Button("Lane " + (i + 1));
            laneButton.setPrefSize(150, 50);
            laneButton.setFont(Font.font("Impact", 16));
            laneButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

            // Add hover effect
            laneButton.setOnMouseEntered(e -> {
                laneButton.setEffect(new DropShadow(10, Color.RED));
                laneButton.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
            });

            laneButton.setOnMouseExited(e -> {
                laneButton.setEffect(null);
                laneButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
            });

            final int laneIndex = i;
            laneButton.setOnAction(event -> {
                if(battle.isGameOver()){
                    showError("Game Over! You cannot place more weapons.");
                } else {
                    try {
                        battle.purchaseWeapon(code, lane);

                        // Show success message with animation
                        Label successLabel = new Label("Weapon placed successfully!");
                        successLabel.setTextFill(Color.GREEN);
                        successLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                        mainPane.setBottom(successLabel);
                        BorderPane.setAlignment(successLabel, Pos.CENTER);
                        BorderPane.setMargin(successLabel, new Insets(0, 0, 20, 0));

                        // Use faster transition for quicker feedback
                        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), successLabel);
                        fadeOut.setFromValue(1.0);
                        fadeOut.setToValue(0.0);
                        fadeOut.setDelay(Duration.millis(500));
                        fadeOut.play();

                        // Update display after short delay - FASTER SPEED
                        javafx.animation.PauseTransition pause =
                                new javafx.animation.PauseTransition(Duration.millis(300));
                        pause.setOnFinished(e -> {
                            try {
                                moveTitan();
                                alertStage.close();
                                // Update the resources display
                                updateBattleStatus(lanes.size(), battle.getResourcesGathered());
                            } catch (Exception ex) {
                                // Handle any exceptions gracefully to prevent crash
                                System.err.println("Error updating display: " + ex.getMessage());
                                alertStage.close();
                            }
                        });
                        pause.play();

                    } catch (InsufficientResourcesException e) {
                        showError("Insufficient Resources! You need more resources to purchase this weapon.");
                    } catch (InvalidLaneException e) {
                        showError("Invalid Lane! You cannot place a weapon in this lane.");
                    }
                }
            });

            vbox.getChildren().add(laneButton);
        }

        // Add close button
        Button closeButton = new Button("Cancel");
        closeButton.setPrefSize(150, 40);
        closeButton.setFont(Font.font("Impact", 16));
        closeButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        // Add hover effect
        closeButton.setOnMouseEntered(e -> {
            closeButton.setEffect(new DropShadow(10, Color.RED));
            closeButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        closeButton.setOnMouseExited(e -> {
            closeButton.setEffect(null);
            closeButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        closeButton.setOnAction(event -> alertStage.close());

        vbox.getChildren().add(closeButton);
        mainPane.setCenter(vbox);

        Scene scene = new Scene(mainPane, 600, 500);
        alertStage.setScene(scene);
        alertStage.showAndWait();
    }

    public static void showError(String message) {
        // Create a new Stage for the error dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Alert");

        // Create a Label to show the error message
        Label errorMessageLabel = new Label(message);
        errorMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        errorMessageLabel.setTextFill(Color.WHITE);
        errorMessageLabel.setWrapText(true);
        errorMessageLabel.setMaxWidth(250);
        errorMessageLabel.setAlignment(Pos.CENTER);

        // Create a Button to close the dialog
        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 30);
        closeButton.setFont(Font.font("Impact", 14));
        closeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        // Add hover effect
        closeButton.setOnMouseEntered(e -> {
            closeButton.setEffect(new DropShadow(10, Color.RED));
            closeButton.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        closeButton.setOnMouseExited(e -> {
            closeButton.setEffect(null);
            closeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogStage.close();
            }
        });

        // Create a layout for the dialog with an AOT theme
        VBox dialogVBox = new VBox(20);
        dialogVBox.setStyle("-fx-background-color: #333333; -fx-border-color: #8B0000; -fx-border-width: 3;");
        dialogVBox.setPadding(new Insets(20));

        // Add an alert icon (rectangle instead of image for reliability)
        Rectangle alertIcon = new Rectangle(50, 50);
        alertIcon.setFill(Color.DARKRED);
        alertIcon.setStroke(Color.GOLD);
        alertIcon.setStrokeWidth(2);
        alertIcon.setArcHeight(10);
        alertIcon.setArcWidth(10);

        dialogVBox.getChildren().addAll(alertIcon, errorMessageLabel, closeButton);
        dialogVBox.setAlignment(Pos.CENTER);

        // Set the Scene and show the Stage
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void PassturnEasy(ActionEvent event) {
        updateBattleStatus(3, 250);
    }

    public void PassturnHard(ActionEvent event) {
        updateBattleStatus(5, 125);
    }

    private void updateBattleStatus(int lanesCount, int resources) {
        Turn.setText("Turn: " + battle.getNumberOfTurns());
        Score.setText("Score: " + battle.getScore());
        Phase.setText("Phase: " + battle.getBattlePhase());
        Res.setText("Resources: " + battle.getResourcesGathered());

        // Check for game over
        if (battle.isGameOver()) {
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        Stage gameOverStage = new Stage();
        gameOverStage.setTitle("Game Over");
        gameOverStage.initModality(Modality.APPLICATION_MODAL);

        VBox gameOverBox = new VBox(20);
        gameOverBox.setPadding(new Insets(30));
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: #333333; -fx-border-color: #8B0000; -fx-border-width: 5;");

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Impact", 36));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setEffect(new DropShadow(10, Color.BLACK));

        Label scoreLabel = new Label("Your Score: " + battle.getScore());
        scoreLabel.setFont(Font.font("Impact", 24));
        scoreLabel.setTextFill(Color.WHITE);

        Label turnsLabel = new Label("Survived " + battle.getNumberOfTurns() + " turns");
        turnsLabel.setFont(Font.font("Arial", 18));
        turnsLabel.setTextFill(Color.LIGHTGRAY);

        Button restartButton = new Button("Play Again");
        restartButton.setPrefSize(150, 50);
        restartButton.setFont(Font.font("Impact", 18));
        restartButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        restartButton.setOnAction(e -> {
            gameOverStage.close();
            Stage primaryStage = (Stage) Turn.getScene().getWindow();
            primaryStage.setScene(createMainMenuScene());
        });

        // Add hover effect
        restartButton.setOnMouseEntered(e -> {
            restartButton.setEffect(new DropShadow(10, Color.RED));
            restartButton.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        restartButton.setOnMouseExited(e -> {
            restartButton.setEffect(null);
            restartButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        });

        gameOverBox.getChildren().addAll(gameOverLabel, scoreLabel, turnsLabel, restartButton);

        Scene gameOverScene = new Scene(gameOverBox, 400, 300);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.showAndWait();
    }

    // تحديث طريقة عرض التيتان مع إظهار معلومات الهيلث
    private static StackPane createTitanWithInfo(Titan t) {
        StackPane titanStack = new StackPane();

        // إنشاء شكل التيتان
        Rectangle titanShape = new Rectangle(45, 45);
        titanShape.setArcHeight(10);
        titanShape.setArcWidth(10);

        // تحديد اللون حسب نوع التيتان
        Color titanColor = Color.RED;
        int yOffset = 0;

        if (t instanceof PureTitan) {
            titanColor = titanColors.get("PureTitan");
        } else if (t instanceof AbnormalTitan) {
            titanColor = titanColors.get("AbnormalTitan");
            yOffset = 21;
        } else if (t instanceof ArmoredTitan) {
            titanColor = titanColors.get("ArmoredTitan");
            yOffset = 42;
        } else if (t instanceof ColossalTitan) {
            titanColor = titanColors.get("ColossalTitan");
            yOffset = 20;
        }

        titanShape.setFill(titanColor);
        titanShape.setStroke(Color.BLACK);
        titanShape.setStrokeWidth(2);
        titanShape.setTranslateY(yOffset);

        // إضافة تأثيرات مرئية
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setColor(Color.BLACK);
        titanShape.setEffect(shadow);

        // إنشاء مؤشر الصحة (الهيلث)
        VBox healthBox = new VBox(2);
        healthBox.setAlignment(Pos.TOP_CENTER);
        healthBox.setTranslateY(-30);

        // نص يعرض صحة التيتان
        Label healthText = new Label(String.valueOf(t.getCurrentHealth()));
        healthText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        healthText.setTextFill(Color.WHITE);
        healthText.setStyle("-fx-background-color: rgba(200,0,0,0.8); -fx-padding: 2 5; -fx-background-radius: 3;");

        // شريط تقدم لعرض نسبة الصحة
        double healthRatio = (double) t.getCurrentHealth() / t.getBaseHealth();
        Rectangle healthBar = new Rectangle(40, 5);
        healthBar.setArcWidth(2);
        healthBar.setArcHeight(2);

        // لون الشريط يتغير حسب نسبة الصحة
        Color barColor = Color.GREEN;
        if (healthRatio < 0.3) {
            barColor = Color.RED;
        } else if (healthRatio < 0.6) {
            barColor = Color.YELLOW;
        }

        healthBar.setFill(barColor);
        healthBar.setStroke(Color.BLACK);
        healthBar.setStrokeWidth(1);

        healthBox.getChildren().addAll(healthText, healthBar);

        // تجميع الكل في StackPane
        titanStack.getChildren().addAll(titanShape, healthBox);
        titanStack.setTranslateX(t.getDistance() * 10 * SPEED_MULTIPLIER);

        return titanStack;
    }

    // تحديث طريقة عرض التيتان والأسلحة
    public static void moveTitan() {
        try {
            int count = 0;
            gridPane1.getChildren().clear();
            gridPane2.getChildren().clear();
            gridPane3.getChildren().clear();
            if (gridPane4 != null) gridPane4.getChildren().clear();
            if (gridPane5 != null) gridPane5.getChildren().clear();

            for (Lane l : lanes) {
                // عرض الأسلحة أولاً (خلف المقر)
                displayWeaponsInLane(l, count);

                // ثم عرض التيتان
                for (Titan t : l.getTitans()) {
                    StackPane titanWithInfo = createTitanWithInfo(t);

                    // إضافة التيتان للمسار المناسب
                    switch (count) {
                        case 0:
                            gridPane1.getChildren().add(titanWithInfo);
                            break;
                        case 1:
                            gridPane2.getChildren().add(titanWithInfo);
                            break;
                        case 2:
                            gridPane3.getChildren().add(titanWithInfo);
                            break;
                        case 3:
                            if (gridPane4 != null) gridPane4.getChildren().add(titanWithInfo);
                            break;
                        case 4:
                            if (gridPane5 != null) gridPane5.getChildren().add(titanWithInfo);
                            break;
                    }
                }
                count++;
            }
        } catch (Exception e) {
            // معالجة الأخطاء
            System.err.println("Error in moveTitan: " + e.getMessage());
        }
    }

    // تحديث طريقة عرض الأسلحة لتكون خلف المقر
    private static void displayWeaponsInLane(Lane lane, int laneIndex) {
        ArrayList<Weapon> weapons = lane.getWeapons();

        if (weapons != null && !weapons.isEmpty()) {
            // تحديد موقع الأسلحة لتكون خلف المقر
            int baseX = 50; // موقع أبعد من المقر
            int spacingX = 40; // مسافة بين الأسلحة

            for (int i = 0; i < weapons.size(); i++) {
                StackPane weaponView = createWeaponView(weapons.get(i));

                // موضع الأسلحة خلف المقر بترتيب
                int xPos = baseX + (i * spacingX);
                weaponView.setTranslateX(xPos);
                weaponView.setTranslateY(30 + (i % 3) * 20); // ارتفاعات متفاوتة

                // إضافة للمكان المناسب
                GridPane targetGrid = null;
                switch(laneIndex) {
                    case 0: targetGrid = gridPane1; break;
                    case 1: targetGrid = gridPane2; break;
                    case 2: targetGrid = gridPane3; break;
                    case 3: if (gridPane4 != null) targetGrid = gridPane4; break;
                    case 4: if (gridPane5 != null) targetGrid = gridPane5; break;
                }

                if (targetGrid != null) {
                    targetGrid.getChildren().add(weaponView);

                    // إضافة تأثير انتقالي
                    weaponView.setScaleX(0.5);
                    weaponView.setScaleY(0.5);
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), weaponView);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.play();
                }
            }
        }
    }

    // تحديث تصميم عرض الأسلحة ليكون أكثر إثارة
    private static StackPane createWeaponView(Weapon weapon) {
        StackPane weaponStack = new StackPane();

        // خلفية السلاح
        Rectangle background = new Rectangle(45, 45);
        background.setArcHeight(10);
        background.setArcWidth(10);
        background.setFill(Color.rgb(30, 30, 30, 0.8));
        background.setStroke(Color.rgb(200, 150, 0));
        background.setStrokeWidth(2);

        // تحديد لون السلاح حسب نوعه
        Color weaponColor;
        String weaponType = getWeaponType(weapon);

        switch(weaponType) {
            case "PiercingCannon":
                weaponColor = Color.rgb(200, 50, 50);
                break;
            case "SniperCannon":
                weaponColor = Color.rgb(50, 200, 200);
                break;
            case "VolleySpreadCannon":
                weaponColor = Color.rgb(50, 200, 50);
                break;
            case "WallTrap":
                weaponColor = Color.rgb(200, 200, 50);
                break;
            default:
                weaponColor = Color.rgb(150, 150, 150);
        }

        // إنشاء شكل السلاح حسب نوعه
        Node weaponShape;

        switch(weaponType) {
            case "PiercingCannon":
                // مدفع طويل
                Rectangle barrel = new Rectangle(10, 25);
                barrel.setFill(weaponColor);

                Circle base = new Circle(12);
                base.setFill(Color.rgb(100, 100, 100));

                // تحديد موضع البرميل فوق القاعدة
                barrel.setTranslateY(-10);

                Group cannonGroup = new Group(base, barrel);
                weaponShape = cannonGroup;
                break;

            case "SniperCannon":
                // مدفع قنص
                Rectangle sniperBarrel = new Rectangle(5, 30);
                sniperBarrel.setFill(weaponColor);

                Rectangle scope = new Rectangle(12, 8);
                scope.setFill(Color.rgb(50, 50, 50));
                scope.setTranslateY(-10);

                Group sniperGroup = new Group(sniperBarrel, scope);
                weaponShape = sniperGroup;
                break;

            case "VolleySpreadCannon":
                // مدفع متعدد
                Group volleyGroup = new Group();

                Rectangle barrel1 = new Rectangle(5, 20);
                barrel1.setFill(weaponColor);
                barrel1.setTranslateX(-8);

                Rectangle barrel2 = new Rectangle(5, 20);
                barrel2.setFill(weaponColor);

                Rectangle barrel3 = new Rectangle(5, 20);
                barrel3.setFill(weaponColor);
                barrel3.setTranslateX(8);

                Circle baseMulti = new Circle(14);
                baseMulti.setFill(Color.rgb(80, 80, 80));
                baseMulti.setTranslateY(10);

                volleyGroup.getChildren().addAll(baseMulti, barrel1, barrel2, barrel3);
                weaponShape = volleyGroup;
                break;

            case "WallTrap":
                // فخ جداري
                Polygon trap = new Polygon(
                        0, 0,
                        20, 0,
                        20, 20,
                        10, 15,
                        0, 20
                );
                trap.setFill(weaponColor);
                weaponShape = trap;
                break;

            default:
                weaponShape = new Rectangle(25, 25, weaponColor);
        }

        // مؤشر القوة (الداميدج)
        Label damageLabel = new Label(String.valueOf(weapon.getDamage()));
        damageLabel.setFont(Font.font("Impact", 14));
        damageLabel.setTextFill(Color.WHITE);
        damageLabel.setStyle("-fx-background-color: rgba(200,50,0,0.8); -fx-padding: 2 4; -fx-background-radius: 3;");
        StackPane.setAlignment(damageLabel, javafx.geometry.Pos.BOTTOM_RIGHT);

        // إضافة تأثير توهج للسلاح
        Glow glow = new Glow(0.5);
        weaponShape.setEffect(glow);

        // إضافة الكل للـ StackPane
        weaponStack.getChildren().addAll(background, weaponShape, damageLabel);

        // إضافة تلميح عند تمرير الماوس على السلاح
        Tooltip tooltip = new Tooltip(
                weaponType + "\nDamage: " + weapon.getDamage()
        );
        Tooltip.install(weaponStack, tooltip);

        return weaponStack;
    }

    // استخراج نوع السلاح من كائن السلاح
    private static String getWeaponType(Weapon weapon) {
        String className = weapon.getClass().getSimpleName();
        if (className.endsWith("Registry")) {
            className = className.replace("Registry", "");
        }

        switch(className) {
            case "Piercing": return "PiercingCannon";
            case "Wall": return "WallTrap";
            case "VolleySpread": return "VolleySpreadCannon";
            case "Sniper": return "SniperCannon";
            default: return className;
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}