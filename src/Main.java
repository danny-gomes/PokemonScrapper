import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        // https://pokeapi.co/api/v2/move/919
        String moveId = "";
        int moveIdCount = 1;
        Map<String, ArrayList<String>> pokemonLearnSets = new HashMap<>();

        do {
            System.out.println("Reading Move: " + moveIdCount + " of 919");
            moveId = String.valueOf(moveIdCount);

            JSONObject moveJSON = (JSONObject) getPokemonMove(moveId);
            String moveName = "";
            int accuracy;
            int effectChance;


            if (moveJSON != null) {
                moveName = (String) moveJSON.get("name");
                JSONObject moveCategoryJSON = (JSONObject) moveJSON.get("damage_class");
                String moveCategory = (String) moveCategoryJSON.get("name");
                int pp = ((Long) moveJSON.get("pp")).intValue();
                int priority = ((Long) moveJSON.get("priority")).intValue();
                int power;
                int isDmgHPbased;
                JSONObject targetJSON = (JSONObject) moveJSON.get("target");
                String target = (String) targetJSON.get("name");
                JSONObject typeJSON = (JSONObject) moveJSON.get("type");
                String type = (String) typeJSON.get("name");
                String effectEntry;

                JSONArray learnedByPokemonArray = (JSONArray) moveJSON.get("learned_by_pokemon");

                if (learnedByPokemonArray != null) {
                    for (Object obj : learnedByPokemonArray) {
                        JSONObject pokemon = (JSONObject) obj;
                        String pokemonName = (String) pokemon.get("name");

                        if (!pokemonLearnSets.containsKey(pokemonName)) {
                            pokemonLearnSets.put(pokemonName, new ArrayList<>());
                        }

                        pokemonLearnSets.get(pokemonName).add(moveName);
                    }
                }

                JSONObject meta = (JSONObject) moveJSON.get("meta");

                String ailment = "none";
                int ailmentChance = 0;
                String moveType = "damage";
                String crit = "0";
                String drain = "0";
                String flinch = "0";
                String healing = "0";
                String maxHits = "null"; // Default value when not specified
                String minHits = "null"; // Default value when not specified
                String minTurns = "null"; // Default value when not specified
                String maxTurns = "null"; // Default value when not specified
                String statChance = "0";
                int attackChangeValue = 0;
                int defenseChangeValue = 0;
                int specialAttackChangeValue = 0;
                int specialDefenseChangeValue = 0;
                int speedChangeValue = 0;
                int accuracyChangeValue = 0;
                int evasionChangeValue = 0;
                JSONArray statChangesArray = (JSONArray) moveJSON.get("stat_changes");

                if (statChangesArray != null) {
                    for (Object obj : statChangesArray) {
                        JSONObject statChange = (JSONObject) obj;
                        int change = ((Long) statChange.get("change")).intValue();
                        JSONObject statObj = (JSONObject) statChange.get("stat");
                        String statName = (String) statObj.get("name");

                        // Update the corresponding variable based on the stat name
                        switch (statName) {
                            case "attack":
                                attackChangeValue = change;
                                break;
                            case "defense":
                                defenseChangeValue = change;
                                break;
                            case "special-attack":
                                specialAttackChangeValue = change;
                                break;
                            case "special-defense":
                                specialDefenseChangeValue = change;
                                break;
                            case "speed":
                                speedChangeValue = change;
                                break;
                            case "accuracy":
                                accuracyChangeValue = change;
                                break;
                            case "evasion":
                                evasionChangeValue = change;
                                break;
                        }
                    }
                }

                if (meta != null) {
                    JSONObject ailmentJSON = (JSONObject) meta.get("ailment");
                    ailment = ailmentJSON != null ? (String) ailmentJSON.get("name") : "none";

                    ailmentChance = meta.get("ailment_chance") != null ? ((Long) meta.get("ailment_chance")).intValue() : 0;

                    JSONObject moveTypeJSON = (JSONObject) meta.get("category");
                    moveType = moveTypeJSON != null ? (String) moveTypeJSON.get("name") : "damage";

                    Long critValue = (Long) meta.get("crit_rate");
                    crit = critValue != null ? critValue.toString() : "0";

                    Long drainValue = (Long) meta.get("drain");
                    drain = drainValue != null ? drainValue.toString() : "0";

                    Long flinchValue = (Long) meta.get("flinch_chance");
                    flinch = flinchValue != null ? flinchValue.toString() : "0";

                    Long healingValue = (Long) meta.get("healing");
                    healing = healingValue != null ? healingValue.toString() : "0";

                    Long maxHitsValue = (Long) meta.get("max_hits");
                    maxHits = maxHitsValue != null ? maxHitsValue.toString() : "null";

                    Long minHitsValue = (Long) meta.get("min_hits");
                    minHits = minHitsValue != null ? minHitsValue.toString() : "null";

                    Long minTurnsValue = (Long) meta.get("min_turns");
                    minTurns = minTurnsValue != null ? minTurnsValue.toString() : "null";

                    Long maxTurnsValue = (Long) meta.get("max_turns");
                    maxTurns = maxTurnsValue != null ? maxTurnsValue.toString() : "null";

                    Long statChanceValue = (Long) meta.get("stat_chance");
                    statChance = statChanceValue != null ? statChanceValue.toString() : "0";
                }

                try {
                    JSONArray effectEntryArray = (JSONArray) moveJSON.get("effect_entries");
                    JSONObject effectEntryJSON = (JSONObject) effectEntryArray.getFirst();
                    effectEntry = (String) effectEntryJSON.get("effect");
                } catch (Exception e) {
                    effectEntry = "None";
                }


                if (moveName.equalsIgnoreCase("water-spout")) {
                    isDmgHPbased = 1;
                } else {
                    isDmgHPbased = 0;
                }

                if (moveJSON.get("accuracy") == null) {
                    accuracy = -1;
                } else {
                    accuracy = ((Long) moveJSON.get("accuracy")).intValue();
                }

                if (moveJSON.get("effect_chance") == null) {
                    effectChance = -1;
                } else {
                    effectChance = ((Long) moveJSON.get("effect_chance")).intValue();
                }

                if (moveJSON.get("power") == null) {
                    power = -1;
                } else {
                    power = ((Long) moveJSON.get("power")).intValue();
                }

                try (FileWriter writer = new FileWriter("moves_info.csv", true)) {
                    if (moveIdCount == 1)  {
                        writer.append("Move Name,Ailment,Ailment Chance,Move Type,Crit Rate,Drain,Flinch Chance,Healing,Max Hits,Min Hits,Min Turns,Max Turns,Stat Chance,Attack Change, Defense Change, SpAttack Change, SpDef Change, Speed Change, Accuracy Change, Evasiness Change\n");
                    }

                    writer.append(moveName).append(",");
                    writer.append(ailment).append(",");
                    writer.append(String.valueOf(ailmentChance)).append(",");
                    writer.append(moveType).append(",");
                    writer.append(crit).append(",");
                    writer.append(drain).append(",");
                    writer.append(flinch).append(",");
                    writer.append(healing).append(",");
                    writer.append(maxHits).append(",");
                    writer.append(minHits).append(",");
                    writer.append(minTurns).append(",");
                    writer.append(maxTurns).append(",");
                    writer.append(statChance).append(",");
                    writer.append(String.valueOf(attackChangeValue)).append(",");
                    writer.append(String.valueOf(defenseChangeValue)).append(",");
                    writer.append(String.valueOf(specialAttackChangeValue)).append(",");
                    writer.append(String.valueOf(specialDefenseChangeValue)).append(",");
                    writer.append(String.valueOf(speedChangeValue)).append(",");
                    writer.append(String.valueOf(accuracyChangeValue)).append(",");
                    writer.append(String.valueOf(evasionChangeValue)).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try (FileWriter csvWriter = new FileWriter("pokemon_moves.csv", true)) {
                    if (moveIdCount == 1) {
                        csvWriter.append("Move Name,Type,Accuracy,Move Category,Effect Chance,PP,Priority,Power,HP Based Damage,Target,Effect Entry\n");
                    }

                    csvWriter.append(moveName).append(",");
                    csvWriter.append(type).append(",");
                    csvWriter.append(String.valueOf(accuracy)).append(",");
                    csvWriter.append(moveCategory).append(",");
                    csvWriter.append(String.valueOf(effectChance)).append(",");
                    csvWriter.append(String.valueOf(pp)).append(",");
                    csvWriter.append(String.valueOf(priority)).append(",");
                    csvWriter.append(String.valueOf(power)).append(",");
                    csvWriter.append(isDmgHPbased == 0 ? "No" : "Yes").append(",");
                    csvWriter.append(target).append(",");
                    csvWriter.append(effectEntry.replace("\n", " ").replace(",", " ")).append("\n");
                } catch (IOException e) {
                    System.out.println("Error writing to CSV file pokemon_moves.csv: " + e.getMessage());
                }

            }


            moveIdCount++;
        } while (moveIdCount < 920);

        writePokemonMovesToCSV(pokemonLearnSets);
        System.out.println("Pokemon move sets read: " + pokemonLearnSets.size());

    }

    private static void writePokemonMovesToCSV(Map<String, ArrayList<String>> pokemonLearnSets) {
        try (FileWriter writer = new FileWriter("pokemon_learnsets.csv")) {


            for (Map.Entry<String, ArrayList<String>> entry : pokemonLearnSets.entrySet()) {
                String pokemonName = entry.getKey();
                ArrayList<String> moves = entry.getValue();
                writer.append(pokemonName);
                System.out.println("Indexing moves for " + pokemonName);
                for (String move : moves) {
                    System.out.println("\tMove " + move);
                    writer.append(",").append(move);
                }

                writer.append("\n");
            }

        } catch (IOException e) {
            System.out.println("Error writing to CSV file pokemon_learnsets.csv: " + e.getMessage());
        }
    }

    private static JSONObject getPokemonMove(String moveId) {
        String urlString = "https://pokeapi.co/api/v2/move/" + moveId;

        try {
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Could not connect to API");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            return resultsJsonObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {
        StringBuilder resultJson = new StringBuilder();

        try {
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            return resultJson.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            return conn;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
