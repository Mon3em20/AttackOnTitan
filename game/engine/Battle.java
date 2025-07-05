package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import game.engine.base.Wall;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.titans.TitanRegistry;
import game.engine.weapons.Weapon;
import game.engine.weapons.factory.FactoryResponse;
import game.engine.weapons.factory.WeaponFactory;

import javafx.scene.media.AudioClip;

public class Battle
{
	private static final int[][] PHASES_APPROACHING_TITANS =
			{
					{ 1, 1, 1, 2, 1, 3, 4 },
					{ 2, 2, 2, 1, 3, 3, 4 },
					{ 4, 4, 4, 4, 4, 4, 4 }
			}; // order of the types of titans (codes) during each phase
	private static final int WALL_BASE_HEALTH = 10000;

	private int numberOfTurns;
	private int resourcesGathered;
	private BattlePhase battlePhase;
	private int numberOfTitansPerTurn; // initially equals to 1
	private int score; // Number of Enemies Killed
	private int titanSpawnDistance;
	private final WeaponFactory weaponFactory;
	private final HashMap<Integer, TitanRegistry> titansArchives;
	private final ArrayList<Titan> approachingTitans; // treated as a Queue
	private final PriorityQueue<Lane> lanes;
	private final ArrayList<Lane> originalLanes;

	// Sound effects for enhanced user experience
	private AudioClip titanSpawnSound;
	private AudioClip weaponFireSound;
	private AudioClip wallHitSound;
	private AudioClip titanDeathSound;

	// Visual feedback elements
	private boolean visualFeedbackEnabled = true;
	private String lastActionMessage = "";

	public Battle(int ofTurns, int i, int numberOfTurns, int score, int titanSpawnDistance, int initialNumOfLanes,
				  int initialResourcesPerLane) throws IOException
	{
		super();
		this.numberOfTurns = numberOfTurns;
		this.battlePhase = BattlePhase.EARLY;
		this.numberOfTitansPerTurn = 1;
		this.score = score;
		this.titanSpawnDistance = titanSpawnDistance;
		this.resourcesGathered = initialResourcesPerLane * initialNumOfLanes;
		this.weaponFactory = new WeaponFactory();
		this.titansArchives = DataLoader.readTitanRegistry();
		this.approachingTitans = new ArrayList<Titan>();
		this.lanes = new PriorityQueue<>();
		this.originalLanes = new ArrayList<>();
		this.initializeLanes(initialNumOfLanes);

		// Initialize sound effects
		try {
			titanSpawnSound = new AudioClip(getClass().getResource("/sounds/titan_spawn.mp3").toString());
			weaponFireSound = new AudioClip(getClass().getResource("/sounds/weapon_fire.mp3").toString());
			wallHitSound = new AudioClip(getClass().getResource("/sounds/wall_hit.mp3").toString());
			titanDeathSound = new AudioClip(getClass().getResource("/sounds/titan_death.mp3").toString());
		} catch (Exception e) {
			// If sounds can't be loaded, continue without them
			visualFeedbackEnabled = false;
		}
	}

	public int getNumberOfTurns()
	{
		return numberOfTurns;
	}

	public void setNumberOfTurns(int numberOfTurns)
	{
		this.numberOfTurns = numberOfTurns;
	}

	public int getResourcesGathered()
	{
		return resourcesGathered;
	}

	public void setResourcesGathered(int resourcesGathered)
	{
		this.resourcesGathered = resourcesGathered;
	}

	public BattlePhase getBattlePhase()
	{
		return battlePhase;
	}

	public void setBattlePhase(BattlePhase battlePhase)
	{
		this.battlePhase = battlePhase;

		// Update visual feedback when phase changes
		if (visualFeedbackEnabled) {
			switch(battlePhase) {
				case INTENSE:
					lastActionMessage = "Battle phase intensifies! More dangerous titans will appea!";
					break;
				case GRUMBLING:
					lastActionMessage = "DANGER! Colossal Titans are approaching!";
					break;
				default:
					break;
			}
		}
	}

	public int getNumberOfTitansPerTurn()
	{
		return numberOfTitansPerTurn;
	}

	public void setNumberOfTitansPerTurn(int numberOfTitansPerTurn)
	{
		this.numberOfTitansPerTurn = numberOfTitansPerTurn;

		// Update visual feedback when titan count changes
		if (visualFeedbackEnabled && numberOfTitansPerTurn > 1) {
			lastActionMessage = "WARNING: " + numberOfTitansPerTurn + " titans per turn now!";
		}
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getTitanSpawnDistance()
	{
		return titanSpawnDistance;
	}

	public void setTitanSpawnDistance(int titanSpawnDistance)
	{
		this.titanSpawnDistance = titanSpawnDistance;
	}

	public WeaponFactory getWeaponFactory()
	{
		return weaponFactory;
	}

	public HashMap<Integer, TitanRegistry> getTitansArchives()
	{
		return titansArchives;
	}

	public ArrayList<Titan> getApproachingTitans()
	{
		return approachingTitans;
	}

	public PriorityQueue<Lane> getLanes()
	{
		return lanes;
	}

	public ArrayList<Lane> getOriginalLanes()
	{
		return originalLanes;
	}

	public String getLastActionMessage() {
		return lastActionMessage;
	}

	public void setLastActionMessage(String message) {
		this.lastActionMessage = message;
	}

	private void initializeLanes(int numOfLanes)
	{
		for (int i = 0; i < numOfLanes; i++)
		{
			Wall w = new Wall(WALL_BASE_HEALTH);
			Lane l = new Lane(w);

			this.getOriginalLanes().add(l);
			this.getLanes().add(l);
		}
	}

	public void refillApproachingTitans() // spawns titans of the specified code to lanes based on the current phase
	{
		int[] phaseApproachingTitans;

		switch (this.getBattlePhase())
		{
			case EARLY:
				phaseApproachingTitans = PHASES_APPROACHING_TITANS[0];
				break;
			case INTENSE:
				phaseApproachingTitans = PHASES_APPROACHING_TITANS[1];
				break;
			case GRUMBLING:
				phaseApproachingTitans = PHASES_APPROACHING_TITANS[2];
				break;
			default:
				phaseApproachingTitans = new int[0];
		}

		for (int code : phaseApproachingTitans)
		{
			Titan spawnedTitan = this.getTitansArchives().get(code).spawnTitan(this.getTitanSpawnDistance());
			this.getApproachingTitans().add(spawnedTitan);

			// Play spawn sound effect
			if (visualFeedbackEnabled && titanSpawnSound != null) {
				titanSpawnSound.play(0.3);  // Play at 30% volume
			}
		}
	}

	public void purchaseWeapon(int weaponCode, Lane lane) throws InsufficientResourcesException, InvalidLaneException
	{
		if (!this.getLanes().contains(lane))
		{
			throw new InvalidLaneException("Weapon purchase failed");
		}

		FactoryResponse factoryResponse = this.getWeaponFactory().buyWeapon(getResourcesGathered(), weaponCode);
		Weapon purchasedWeapon = factoryResponse.getWeapon();

		if (purchasedWeapon != null)
		{
			lane.addWeapon(purchasedWeapon);
			this.setResourcesGathered(factoryResponse.getRemainingResources());

			// Update visual feedback
			if (visualFeedbackEnabled) {
				lastActionMessage = purchasedWeapon.getClass() + " deployed in lane " + (originalLanes.indexOf(lane) + 1);
			}

			performTurn();
		}
	}

	public void passTurn()
	{
		performTurn();

		// Update visual feedback
		if (visualFeedbackEnabled) {
			lastActionMessage = "Turn passed. Titans are moving closer!";
		}
	}

	private void addTurnTitansToLane()
	{
		Lane leastDangerLane = this.getLanes().poll();

		for (int i = 0; i < this.getNumberOfTitansPerTurn(); i++)
		{
			if (this.getApproachingTitans().isEmpty())
			{
				this.refillApproachingTitans();
			}

			if (!this.getApproachingTitans().isEmpty()) {
				Titan titan = this.getApproachingTitans().remove(0);
				leastDangerLane.addTitan(titan);

				// Visual feedback for new titan
				if (visualFeedbackEnabled) {
					int laneIndex = originalLanes.indexOf(leastDangerLane) + 1;
					lastActionMessage = titan.getClass().getSimpleName() + " approaching in lane " + laneIndex + "!";
				}
			}
		}

		this.getLanes().add(leastDangerLane);
	}

	private void moveTitans()
	{
		for (Lane l : this.getLanes())
		{
			l.moveLaneTitans();
		}
	}

	private int performWeaponsAttacks()
	{
		int resourcesGathered = 0;

		for (Lane l : this.getLanes())
		{
			int laneResources = l.performLaneWeaponsAttacks();
			resourcesGathered += laneResources;

			// Play weapon fire sound if titans were hit
			if (visualFeedbackEnabled && laneResources > 0 && weaponFireSound != null) {
				weaponFireSound.play(0.4);
			}
		}

		this.setResourcesGathered(this.getResourcesGathered() + resourcesGathered);
		this.setScore(this.getScore() + resourcesGathered);

		// Visual feedback for kills
		if (visualFeedbackEnabled && resourcesGathered > 0) {
			lastActionMessage = "Weapons destroyed titans! +" + resourcesGathered + " resources";

			// Play titan death sound
			if (titanDeathSound != null) {
				titanDeathSound.play(0.5);
			}
		}

		return resourcesGathered;
	}

	private int performTitansAttacks()
	{
		int resourcesGathered = 0;
		ArrayList<Lane> lostLanes = new ArrayList<>();

		for (Lane l : this.getLanes())
		{
			int laneDamage = l.performLaneTitansAttacks();
			resourcesGathered += laneDamage;

			// Play wall hit sound if wall was damaged
			if (visualFeedbackEnabled && laneDamage > 0 && wallHitSound != null) {
				wallHitSound.play(0.6);
			}

			if (l.isLaneLost())
			{
				lostLanes.add(l);

				// Visual feedback for lost lane
				if (visualFeedbackEnabled) {
					int laneIndex = originalLanes.indexOf(l) + 1;
					lastActionMessage = "WARNING: Lane " + laneIndex + " has fallen!";
				}
			}
		}

		this.getLanes().removeAll(lostLanes);

		return resourcesGathered;
	}

	private void updateLanesDangerLevels()
	{
		ArrayList<Lane> tmp = new ArrayList<>();

		while(!this.getLanes().isEmpty())
		{
			Lane l = this.getLanes().poll();
			l.updateLaneDangerLevel();
			tmp.add(l);
		}

		this.getLanes().addAll(tmp);
	}

	private void finalizeTurns()
	{
		this.setNumberOfTurns(this.getNumberOfTurns() + 1);

		if (this.getNumberOfTurns() == 15)
		{
			this.setBattlePhase(BattlePhase.INTENSE);
		} else if (this.getNumberOfTurns() == 30)
		{
			this.setBattlePhase(BattlePhase.GRUMBLING);
		} else if (this.getNumberOfTurns() > 30 && this.getNumberOfTurns() % 5 == 0)
		{
			this.setNumberOfTitansPerTurn(this.getNumberOfTitansPerTurn() * 2);
		}
	}

	public void performTurn()
	{
		this.moveTitans();
		this.performWeaponsAttacks();
		this.performTitansAttacks();

		this.addTurnTitansToLane();
		this.updateLanesDangerLevels();

		this.finalizeTurns();
	}

	public boolean isGameOver() // checks if all lanes are destroyed
	{
		boolean gameOver = this.getLanes().size() == 0;

		// Visual feedback for game over
		if (gameOver && visualFeedbackEnabled) {
			lastActionMessage = "GAME OVER! All walls have fallen.";
		}

		return gameOver;
	}

	// Get the health percentage of a specific lane's wall (for UI display)
	public double getLaneWallHealthPercentage(int laneIndex) {
		if (laneIndex >= 0 && laneIndex < originalLanes.size()) {
			Lane lane = originalLanes.get(laneIndex);
			if (lane != null && lane.getLaneWall()!= null) {
				return (double) lane.getLaneWall().getCurrentHealth() / WALL_BASE_HEALTH;
			}
		}
		return 0.0;
	}

	// Get the damage capability of a specific lane (for UI display)
	public int getLaneTotalDamage(int laneIndex) {
		if (laneIndex >= 0 && laneIndex < originalLanes.size()) {
			Lane lane = originalLanes.get(laneIndex);
			if (lane != null) {
				int totalDamage = 0;
				for (Weapon weapon : lane.getWeapons()) {
					totalDamage += weapon.getDamage();
				}
				return totalDamage;
			}
		}
		return 0;
	}

	// For UI: Enable/disable sound effects and visual feedback
	public void setVisualFeedbackEnabled(boolean enabled) {
		this.visualFeedbackEnabled = enabled;
	}

	public boolean isVisualFeedbackEnabled() {
		return visualFeedbackEnabled;
	}
}