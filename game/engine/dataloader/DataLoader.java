package game.engine.dataloader;

import game.engine.exceptions.InvalidCSVFormat;
import game.engine.titans.TitanRegistry;
import game.engine.weapons.WeaponRegistry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DataLoader {
	private static final String TITANS_FILE_NAME = "titans.csv";
	private static final String WEAPONS_FILE_NAME = "weapons.csv";

	public static HashMap<Integer, TitanRegistry> readTitanRegistry() throws IOException {
		HashMap<Integer, TitanRegistry> titanRegistry = new HashMap<>();

		try (InputStream is = DataLoader.class.getResourceAsStream("/" + TITANS_FILE_NAME);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			while (reader.ready()) {
				String line = reader.readLine();
				String[] parts = line.split(",");

				if (parts.length < 7) {
					throw new InvalidCSVFormat(line);
				}

				TitanRegistry titan = new TitanRegistry(
						Integer.parseInt(parts[0]),  // Code
						Integer.parseInt(parts[1]),  // Health
						Integer.parseInt(parts[2]),  // Damage
						Integer.parseInt(parts[3]),  // Speed
						Integer.parseInt(parts[4]),  // Resources
						Integer.parseInt(parts[5]),  // Width
						Integer.parseInt(parts[6])   // Height
				);

				titanRegistry.put(titan.getCode(), titan);
			}
		}

		return titanRegistry;
	}

	public static HashMap<Integer, WeaponRegistry> readWeaponRegistry() throws IOException {
		HashMap<Integer, WeaponRegistry> weaponRegistry = new HashMap<>();

		try (InputStream is = DataLoader.class.getResourceAsStream("/" + WEAPONS_FILE_NAME);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			while (reader.ready()) {
				String line = reader.readLine();
				String[] parts = line.split(",");

				if (parts.length < 4 || parts.length > 6) {
					throw new InvalidCSVFormat(line);
				}

				WeaponRegistry weapon;
				if (parts.length == 6) {
					weapon = new WeaponRegistry(
							Integer.parseInt(parts[0]),  // Code
							Integer.parseInt(parts[1]),  // Cost
							Integer.parseInt(parts[2]),  // Damage
							parts[3],                    // Name
							Integer.parseInt(parts[4]),  // Width
							Integer.parseInt(parts[5])   // Height
					);
				} else {
					weapon = new WeaponRegistry(
							Integer.parseInt(parts[0]),  // Code
							Integer.parseInt(parts[1]),  // Cost
							Integer.parseInt(parts[2]),  // Damage
							parts[3]                     // Name
					);
				}

				weaponRegistry.put(weapon.getCode(), weapon);
			}
		}

		return weaponRegistry;
	}
}