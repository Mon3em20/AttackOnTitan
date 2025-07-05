package game.engine.titans;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

public class ArmoredTitan extends Titan
{
	public static final int TITAN_CODE = 3;

	// Visual representation properties
	private String normalImageUrl = "https://www.pngmart.com/files/13/Attack-On-Titan-Armored-Titan-PNG-Transparent-Image.png";
	private String attackImageUrl = "https://www.pngkey.com/png/full/315-3151013_armored-titan-attack-on-titan-armored-titan-png.png";
	private String damagedImageUrl = "https://www.pngkit.com/png/full/218-2185370_png-attack-on-titan-armored-titan.png";

	// Animation states
	private boolean isAttacking = false;
	private boolean isDamaged = false;
	private int animationFrame = 0;
	private double armorShieldOpacity = 1.0;

	public ArmoredTitan(int baseHealth, int baseDamage, int heightInMeters, int distanceFromBase, int speed,
						int resourcesValue, int dangerLevel)
	{
		super(baseHealth, baseDamage, heightInMeters, distanceFromBase, speed, resourcesValue, dangerLevel);
	}

	@Override
	public int takeDamage(int damage)
	{
		// Trigger damaged visual state
		isDamaged = true;
		animationFrame = 10; // Animation will last for 10 frames

		// Flash armor shield effect
		armorShieldOpacity = 0.7;

		// Play armor impact sound (to be handled by the UI)
		playArmorImpactSound();

		return super.takeDamage(damage / 4); // Armor reduces damage to 1/4
	}

	/**
	 * Gets the current image URL based on titan state
	 * @return URL to the appropriate image
	 */
	public String getCurrentImageUrl() {
		if (isDamaged && animationFrame > 5) {
			return damagedImageUrl;
		} else if (isAttacking) {
			return attackImageUrl;
		} else {
			return normalImageUrl;
		}
	}

	/**
	 * Updates animation states
	 */
	public void updateAnimation() {
		if (animationFrame > 0) {
			animationFrame--;
			if (animationFrame == 0) {
				isDamaged = false;
				isAttacking = false;
				armorShieldOpacity = 1.0;
			}
		}
	}

	/**
	 * Triggers attack animation
	 */
	public void startAttackAnimation() {
		isAttacking = true;
		animationFrame = 15; // Animation will last for 15 frames
	}

	/**
	 * Creates a glow effect representing the armor shield
	 * @return The glow effect with appropriate settings
	 */
	public DropShadow getArmorEffect() {
		DropShadow armorGlow = new DropShadow();
		armorGlow.setColor(Color.BLUE);
		armorGlow.setRadius(15);
		armorGlow.setSpread(0.5);
		armorGlow.setOffsetY(armorShieldOpacity);
		return armorGlow;
	}

	/**
	 * Triggers the armor impact sound effect
	 * This will be called by the UI layer to play the appropriate sound
	 */
	private void playArmorImpactSound() {
		// This is a placeholder for sound effect that would be implemented in the UI layer
		// The actual sound playing will be handled by the JavaFX code
	}

	/**
	 * Creates an attack animation that can be applied to the titan's visual representation
	 * @return A JavaFX animation that can be applied to the titan's node
	 */
	public ParallelTransition createAttackAnimation(javafx.scene.Node titanNode) {
		// First start the attack animation state
		startAttackAnimation();

		// Create a translation to move forward
		TranslateTransition moveForward = new TranslateTransition(Duration.millis(500), titanNode);
		moveForward.setByX(-50); // Move toward the wall (left)

		// Create a scale to make it appear like it's striking
		ScaleTransition scaleUp = new ScaleTransition(Duration.millis(250), titanNode);
		scaleUp.setByX(0.2);
		scaleUp.setByY(0.2);

		ScaleTransition scaleDown = new ScaleTransition(Duration.millis(250), titanNode);
		scaleDown.setByX(-0.2);
		scaleDown.setByY(-0.2);
		scaleDown.setDelay(Duration.millis(250));

		// Combine the animations
		ParallelTransition attackAnimation = new ParallelTransition(titanNode, moveForward, scaleUp, scaleDown);

		// After the attack, move back
		attackAnimation.setOnFinished(event -> {
			TranslateTransition moveBack = new TranslateTransition(Duration.millis(300), titanNode);
			moveBack.setByX(50); // Move back to original position
			moveBack.play();
		});

		return attackAnimation;
	}

	/**
	 * @return Description of the titan
	 */
	@Override
	public String getDescription() {
		return "Armored Titan: Protected by hardened plates that reduce damage to 1/4 of normal.";
	}

	/**
	 * @return Special color associated with this titan type
	 */
	public Color getTitanColor() {
		return Color.DARKBLUE;
	}

	/**
	 * @return The titan's recommended display height in UI
	 */
	public double getDisplayHeight() {
		return 400; // Pixels
	}
}