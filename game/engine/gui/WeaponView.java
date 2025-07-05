package game.engine.gui;

import game.engine.weapons.Weapon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class WeaponView extends StackPane {
    private static final HashMap<String, String> WEAPON_IMAGES = new HashMap<>();
    private final Weapon weapon;
    private final ImageView weaponImageView;

    static {
        // Initialize weapon image URLs
        WEAPON_IMAGES.put("THUNDER_SPEAR", "https://i.imgur.com/LuVoZG5.png");
        WEAPON_IMAGES.put("ODM_GEAR", "https://i.imgur.com/PUmMG9l.png");
        WEAPON_IMAGES.put("BLADE", "https://i.imgur.com/QsYQZx5.png");
        WEAPON_IMAGES.put("CANNON", "https://i.imgur.com/Z5jveC9.png");
        // Add more weapons as needed
    }

    public WeaponView(Weapon weapon) {
        this.weapon = weapon;

        // Create weapon image view
        String weaponType = weapon.getClass().getSimpleName();
        String imageUrl = WEAPON_IMAGES.getOrDefault(weaponType.toUpperCase(),
                "https://i.imgur.com/QsYQZx5.png"); // Default image

        Image weaponImage = new Image(imageUrl, 40, 40, true, true);
        weaponImageView = new ImageView(weaponImage);

        // Add damage indicator
        String damageText = String.valueOf(weapon.getDamage());
        javafx.scene.control.Label damageLabel = new javafx.scene.control.Label(damageText);
        damageLabel.setStyle("-fx-background-color: black; -fx-text-fill: red; -fx-font-weight: bold;");

        // Add components to this StackPane
        getChildren().addAll(weaponImageView, damageLabel);

        // Style
        setMaxSize(50, 50);
        setPrefSize(40, 40);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void updatePosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }
}