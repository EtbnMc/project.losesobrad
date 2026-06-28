package Controller;
import Model.*;
import View.GameGUI;
import View.GameView;
import java.io.*;
import java.util.Scanner;
import Controller.GameController;

public class GameController {

    private static final int Rows = 15;
    private static final int Columns = 15;

    private final Scanner scanner;
    private final GameView view;
    private GameGUI gui; 

    private Map map;
    private Hero hero;
    private Boss boss;
    private Enemy[] enemies;
    private Chest[] chests;
    private Door door;

    public GameController() {
        this.scanner = new Scanner(System.in);
        this.view = new GameView();
    }


    public void setGUI(GameGUI gui) {
        this.gui = gui;
    }

    public Map getMap() {
        return map;
    }


    public void startGUI() {
        setupWorld();
        gui.refreshMap();
        gui.updateHeroStatus("HERO — HP: " + hero.getHealth() + "/" + hero.getMaxHealth());
        gui.log("Welcome to MINECRAFT by los esobrad Use WASD to move. Defeat the Enderdragon to win ");
    }


    public void handleKey(char key) {
        key = toUpperCaseManual(key);

        if (key == 'Q') {
            gui.showMessage("Goodbye", "Thanks for playing our game");
            System.exit(0);
        }

        if (key == 'G') {
            saveGame();
            return;
        }

        if (key == 'C') {
            if (loadGame()) {
                gui.refreshMap();
                gui.updateHeroStatus("HERO — HP: " + hero.getHealth() + "/" + hero.getMaxHealth());
                gui.log("Game loaded");
            }
            return;
        }

        if ("WASD".indexOf(key) < 0) return;

        boolean turnConsumed = processHeroMove(key);

        if (turnConsumed) {
            moveEnemiesRandomly();
        }

        gui.refreshMap();
        gui.updateHeroStatus("HERO — HP: " + hero.getHealth() + "/" + hero.getMaxHealth());

        if (!hero.isAlive()) {
            gui.showMessage("DEFEAT", "GAME OVER\nStay determined ");
            System.exit(0);
        }

        if (boss.wasDefeated()) {
            gui.showMessage("VICTORY", hero.getName() + " has freed The End \nHP: " + hero.getHealth() + "/" + hero.getMaxHealth());
            System.exit(0);
        }
    }

    public void startGame() {
        setupWorld();
        view.displayWelcome(hero, boss);
        view.displayMap(map);
        runMainLoop();
        view.displayGoodbye();
        scanner.close();
    }

    private void setupWorld() {
        map = new Map(Rows, Columns);
        Region region = new Region("Strangely square world", map, 8);

        hero = new Hero("Steve", 0, 0);
        map.placeSymbol(0, 0, Hero.Symbol);

        region.spawnEnemy("Zombie",2, 3);
        region.spawnEnemy("Creeper",4, 7);
        region.spawnEnemy("Blaze",6, 2);
        region.spawnEnemy("Enderman",3, 11);
        region.spawnEnemy("Giant Slime",8, 9);
        region.spawnEnemy("Skeleton",10, 4);
        region.spawnEnemy("Piglin",5, 13);
        region.spawnEnemy("Ghast",9, 1);

        map.Wall(1, 1);
        map.Wall(1, 2);
        map.Wall(1, 3);
        map.Wall(2, 3);
        map.Wall(3, 3);
        map.Wall(4, 3);
        map.Wall(7, 7);
        map.Wall(7, 8);
        map.Wall(7, 9);
        map.Wall(8, 9);
        map.Wall(8, 10);
        map.Wall(8, 11);
        map.Wall(8, 12);
        map.Wall(8, 13);
        map.Wall(8, 14);
        map.Wall(0, 5);
        map.Wall(1, 5);
        map.Wall(2, 5);
        map.Wall(3, 5);
        map.Wall(5, 0);
        map.Wall(5, 1);
        map.Wall(5, 2);
        map.Wall(6, 10);
        map.Wall(6, 11);
        map.Wall(6, 12);
        map.Wall(7, 12);
        map.Wall(3, 7);
        map.Wall(3, 8);
        map.Wall(3, 9);
        map.Wall(4, 9);
        map.Wall(5, 9);
        

        for (int col = 10; col <= 14; col++) {
            map.Wall(10, col);
        }
        for (int row = 10; row <= 14; row++) {
            map.Wall(row, 10);
        }

        enemies = region.getEnemies();

        region.spawnChest("Mimic Chest", 1, 13);
        chests = region.getChests();

        door = new Door(10, 12);
        map.placeSymbol(10, 12, Map.DoorClosedSy);

        boss = region.spawnBoss("Enderdragon", 13, 13);
    }

    private void runMainLoop() {
        boolean gameRunning = true;

        while (gameRunning) {
            view.displayHeroStatus(hero);
            view.displayMovePrompt();

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            char key = toUpperCaseManual(input.charAt(0));

            if (key == 'Q') { view.displayQuitMessage(); break; }
            if (key == 'G') { saveGame(); continue; }
            if (key == 'C') { if (loadGame()) view.displayMap(map); continue; }

            if (key != 'W' && key != 'A' && key != 'S' && key != 'D') {
                view.displayInvalidKey();
                continue;
            }

            boolean turnConsumed = processHeroMove(key);
            if (turnConsumed) moveEnemiesRandomly();

            if (!hero.isAlive()) { view.displayDefeat(); gameRunning = false; continue; }
            if (boss.wasDefeated()) { view.displayVictory(hero); gameRunning = false; continue; }

            view.displayMap(map);
        }
    }

    private boolean processHeroMove(char key) {
        int[] destination = hero.calculateDestination(key);
        int destX = destination[0];
        int destY = destination[1];

        if (!map.isInsideBounds(destX, destY)) {
            if (gui != null) gui.log("You cannot leave the map.");
            else view.displayBoundaryReached();
            return false;
        }

        char targetCell = map.getCell(destX, destY);

        if (targetCell == Map.Empty) {
            map.updatePosition(hero.getPositionX(), hero.getPositionY(), destX, destY, Hero.Symbol);
            hero.moveTo(destX, destY);
            if (gui != null) gui.log("Moved to (" + destX + "," + destY + ")");
            else view.displayMoved(destX, destY);
            return true;
        }

        if (targetCell == Map.Wall) {
            if (gui != null) gui.log("You cannot go through the wall ");
            else view.displayWallBlocked();
            return false;
        }

        if (targetCell == Chest.Symbol) {
            Chest targetChest = findChestAt(destX, destY);
            if (targetChest == null || targetChest.isLooted()) {
                map.clearCell(destX, destY);
                return false;
            }
            if (gui != null) gui.log("It's a trap, The Chest was a mimic ");
            else view.displayChestRevealed(targetChest.getName());
            boolean heroWon = runBattle(targetChest, false);
            if (heroWon && !targetChest.isAlive()) {
                Key loot = targetChest.collectLoot();
                if (loot != null) {
                    hero.addKey();
                    if (gui != null) gui.log("You found a " + loot.getName() );
                    else view.displayKeyFound(loot.getName());
                }
                map.clearCell(targetChest.getPositionX(), targetChest.getPositionY());
                moveHeroIntoCell(destX, destY);
            }
            return true;
        }

        if (targetCell == Map.DoorClosedSy) {
            if (door.tryOpen(hero)) {
                map.placeSymbol(destX, destY, Map.DoorOpenSy);
                if (gui != null) gui.log("You unlock the door with your key");
                else view.displayDoorOpened();
                moveHeroIntoCell(destX, destY);
                return true;
            } else {
                if (gui != null) gui.log("The door is locked. You need a key");
                else view.displayDoorLocked();
                return false;
            }
        }

        if (targetCell == Map.DoorOpenSy) {
            moveHeroIntoCell(destX, destY);
            return true;
        }

        if (targetCell == Enemy.Symbol) {
            Enemy targetEnemy = findEnemyAt(destX, destY);
            if (targetEnemy == null) { map.clearCell(destX, destY); return false; }
            boolean heroWon = runBattle(targetEnemy, false);
            if (heroWon && !targetEnemy.isAlive()) {
                removeEnemyFromMap(targetEnemy);
                moveHeroIntoCell(destX, destY);
            }
            return true;
        }

        if (targetCell == Boss.Symbol) {
            runBattle(boss, true);
            if (boss.wasDefeated()) {
                map.clearCell(boss.getPositionX(), boss.getPositionY());
                moveHeroIntoCell(destX, destY);
            }
            return true;
        }

        return false;
    }

    private void moveHeroIntoCell(int x, int y) {
        map.updatePosition(hero.getPositionX(), hero.getPositionY(), x, y, Hero.Symbol);
        hero.moveTo(x, y);
    }

    private Enemy findEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.isAlive()
                    && enemy.getPositionX() == x && enemy.getPositionY() == y)
                return enemy;
        }
        return null;
    }

    private Chest findChestAt(int x, int y) {
        for (Chest chest : chests) {
            if (chest != null && chest.getPositionX() == x && chest.getPositionY() == y)
                return chest;
        }
        return null;
    }

    private void removeEnemyFromMap(Enemy enemy) {
        map.clearCell(enemy.getPositionX(), enemy.getPositionY());
        if (gui != null) gui.log(enemy.getName() + " disappears from the map");
        else view.displayEnemyRemovedFromMap(enemy.getName());
    }

    private boolean runBattle(Enemy enemy, boolean isBossFight) {
        if (gui != null) gui.log("=== BATTLE: " + hero.getName() + " vs " + enemy.getName() + " ===");
        else view.displayCombatStart(hero.getName(), enemy.getName(), isBossFight);

        boolean canRun = !isBossFight;

        while (hero.isAlive() && enemy.isAlive()) {
            int choice;
            if (gui != null) {
                choice = gui.showBattleMenu(
                        hero.getName() + " HP: " + hero.getHealth() + "/" + hero.getMaxHealth(),
                        enemy.getName() + " HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth(),
                        canRun);
            } else {
                view.displayBattleMenu(hero, enemy.getName(), enemy.getHealth(), enemy.getMaxHealth(), canRun);
                choice = readMenuChoice();
            }

            if (choice == 1) {
                int damage = hero.performAttack(enemy);
                if (gui != null) gui.log(hero.getName() + " attacks " + enemy.getName() + " for " + damage + " damage");
                else view.displayAttackAction(hero.getName(), enemy.getName(), damage);

                if (!enemy.isAlive()) {
                    if (isBossFight) {
                        if (gui != null) gui.log(enemy.getName() + " has been DEFEATED");
                        else view.displayBossDefeated(enemy.getName());
                    } else {
                        if (gui != null) gui.log(enemy.getName() + " has been defeated");
                        else view.displayEnemyDefeated(enemy.getName());
                    }
                    return true;
                }

            } else if (choice == 2) {
                hero.usePotion();
                if (gui != null) gui.log(hero.getName() + " drinks a Healing Potion HP: " + hero.getHealth() + "/" + hero.getMaxHealth());
                else view.displayPotionUsed(hero);

            } else if (choice == 3) {
                if (canRun) {
                    if (gui != null) gui.log("You run away from the battle");
                    else view.displayRunAway();
                    return false;
                } else {
                    if (gui != null) gui.log("There is no escape from the Boss");
                    else view.displayCannotRunFromBoss();
                    continue;
                }
            }

            if (enemy.isAlive()) {
                int enemyDamage = enemy.performAttack(hero);
                if (gui != null) gui.log(enemy.getName() + " attacks " + hero.getName() + " for " + enemyDamage + " damage");
                else view.displayAttackAction(enemy.getName(), hero.getName(), enemyDamage);

                if (!hero.isAlive()) {
                    if (gui != null) gui.log("The hero has fallen in battle");
                    else view.displayHeroFallen();
                    return false;
                }
            }
        }
        return !enemy.isAlive();
    }

    private int readMenuChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3"))
                return Integer.parseInt(input);
            view.displayInvalidChoice();
            System.out.print(" Choose an action: ");
        }
    }

    private void moveEnemiesRandomly() {
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) continue;

            char direction = enemy.getRandomMoveDirection();
            if (direction == ' ') continue;

            int[] step = computeStep(enemy.getPositionX(), enemy.getPositionY(), direction);
            int newX = step[0];
            int newY = step[1];

            if (!map.isInsideBounds(newX, newY)) continue;

            char destinationCell = map.getCell(newX, newY);
            if (destinationCell == Map.Empty) {
                map.updatePosition(enemy.getPositionX(), enemy.getPositionY(), newX, newY, Enemy.Symbol);
                enemy.moveTo(newX, newY);
            }
        }
    }

    private int[] computeStep(int currentX, int currentY, char direction) {
        int nextX = currentX;
        int nextY = currentY;
        switch (direction) {
            case 'W': nextX -= 1; break;
            case 'S': nextX += 1; break;
            case 'A': nextY -= 1; break;
            case 'D': nextY += 1; break;
            default: break;
        }
        return new int[] { nextX, nextY };
    }

    private char toUpperCaseManual(char c) {
        if (c >= 'a' && c <= 'z') return (char) (c - 32);
        return c;
    }

    private void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savegame.dat"))) {
            oos.writeObject(map);
            oos.writeObject(hero);
            oos.writeObject(boss);
            oos.writeObject(enemies);
            oos.writeObject(chests);
            oos.writeObject(door);
            if (gui != null) gui.log("Game saved successfully!");
            else System.out.println("--- Game saved successfully! ---");
        } catch (IOException e) {
            if (gui != null) gui.log("Error saving: " + e.getMessage());
            else System.out.println("Error saving the game: " + e.getMessage());
        }
    }

    private boolean loadGame() {
        File saveFile = new File("savegame.dat");
        if (!saveFile.exists()) {
            if (gui != null) gui.log("No saved game found");
            else System.out.println("--- No saved game found. ---");
            return false;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            this.map     = (Map)     ois.readObject();
            this.hero    = (Hero)    ois.readObject();
            this.boss    = (Boss)    ois.readObject();
            this.enemies = (Enemy[]) ois.readObject();
            this.chests  = (Chest[]) ois.readObject();
            this.door    = (Door)    ois.readObject();
            if (gui != null) gui.log("Game loaded successfully");
            else System.out.println("--- Game loaded successfully!  ");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            if (gui != null) gui.log("Error loading: " + e.getMessage());
            else System.out.println("Error loading the game: " + e.getMessage());
            return false;
        }
    }
}
