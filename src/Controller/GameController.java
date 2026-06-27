package Controller;

import Model.*;
import View.GameView;
import java.io.*;
import java.util.Scanner;

public class GameController {

    //map, entity location
    private static final int Rows = 15;
    private static final int Columns = 15;
    private final Scanner scanner;
    private final GameView view;
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

    public void startGame() {
        setupWorld();
        view.displayWelcome(hero, boss);
        view.displayMap(map);

        runMainLoop();

        view.displayGoodbye();
        scanner.close();
    }

    //default enemy spawn and world setup

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

        //lugar paredes x:fila, y:column
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

        //boss room: sealed box in the bottom-right corner, rows 10-14, columns 10-14
        //the only way in is the single Door cell, every other border cell is a wall
        for (int col = 10; col <= 14; col++) {
            map.Wall(10, col);
        }
        for (int row = 10; row <= 14; row++) {
            map.Wall(row, 10);
        }
        //(10,12) is left open for the Door, carved out right after the loops below

        enemies = region.getEnemies();

        region.spawnChest("Mimic Chest", 1, 13);
        chests = region.getChests();

        //door is the only entrance to the boss room
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
            if (input.isEmpty()) {
                continue;
            }

            char key = toUpperCaseManual(input.charAt(0));

            if (key == 'Q') {
                view.displayQuitMessage();
                break;
            }

            if (key == 'G') {
                saveGame();
                continue;
            }


            if (key == 'C') {
                if (loadGame()) {
                    view.displayMap(map);
                }
                continue;
            }
            // la tecla g es para guardar el juego y la c es para cargar el juego que el usuario guardo

            if (key != 'W' && key != 'A' && key != 'S' && key != 'D') {
                view.displayInvalidKey();
                continue;
            }

            boolean turnConsumed = processHeroMove(key);

            if (turnConsumed) {
                moveEnemiesRandomly();
            }

            if (!hero.isAlive()) {
                view.displayDefeat();
                gameRunning = false;
                continue;
            }

            if (boss.wasDefeated()) {
                view.displayVictory(hero);
                gameRunning = false;
                continue;
            }

            view.displayMap(map);
        }
    }

    //movement

    private boolean processHeroMove(char key) {
        int[] destination = hero.calculateDestination(key);
        int destX = destination[0];
        int destY = destination[1];

        if (!map.isInsideBounds(destX, destY)) {
            view.displayBoundaryReached();
            return false;
        }

        char targetCell = map.getCell(destX, destY);

        if (targetCell == Map.Empty) {
            map.updatePosition(hero.getPositionX(), hero.getPositionY(), destX, destY, Hero.Symbol);
            hero.moveTo(destX, destY);
            view.displayMoved(destX, destY);
            return true;
        }

        if (targetCell == Map.Wall) {
            view.displayWallBlocked();
            return false;
        }

        if (targetCell == Chest.Symbol) {
            Chest targetChest = findChestAt(destX, destY);
            if (targetChest == null || targetChest.isLooted()) {
                map.clearCell(destX, destY);
                return false;
            }
            view.displayChestRevealed(targetChest.getName());
            boolean heroWon = runBattle(targetChest, false);
            if (heroWon && !targetChest.isAlive()) {
                Key loot = targetChest.collectLoot();
                if (loot != null) {
                    hero.addKey();
                    view.displayKeyFound(loot.getName());
                }
                map.clearCell(targetChest.getPositionX(), targetChest.getPositionY());
                moveHeroIntoCell(destX, destY);
            }
            return true;
        }

        if (targetCell == Map.DoorClosedSy) {
            if (door.tryOpen(hero)) {
                map.placeSymbol(destX, destY, Map.DoorOpenSy);
                view.displayDoorOpened();
                moveHeroIntoCell(destX, destY);
                return true;
            } else {
                view.displayDoorLocked();
                return false;
            }
        }

        if (targetCell == Map.DoorOpenSy) {
            moveHeroIntoCell(destX, destY);
            return true;
        }

        if (targetCell == Enemy.Symbol) {
            Enemy targetEnemy = findEnemyAt(destX, destY);
            if (targetEnemy == null) {
                map.clearCell(destX, destY);
                return false;
            }
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
                    && enemy.getPositionX() == x && enemy.getPositionY() == y) {
                return enemy;
            }
        }
        return null;
    }

    private Chest findChestAt(int x, int y) {
        for (Chest chest : chests) {
            if (chest != null && chest.getPositionX() == x && chest.getPositionY() == y) {
                return chest;
            }
        }
        return null;
    }

    //remove defeated enemy

    private void removeEnemyFromMap(Enemy enemy) {
        map.clearCell(enemy.getPositionX(), enemy.getPositionY());
        view.displayEnemyRemovedFromMap(enemy.getName());
    }

    //run

    private boolean runBattle(Enemy enemy, boolean isBossFight) {
        view.displayCombatStart(hero.getName(), enemy.getName(), isBossFight);

        boolean canRun = !isBossFight;

        while (hero.isAlive() && enemy.isAlive()) {
            view.displayBattleMenu(hero, enemy.getName(), enemy.getHealth(), enemy.getMaxHealth(), canRun);

            int choice = readMenuChoice();

            //actions
            //atk

            if (choice == 1) {

                int damage = hero.performAttack(enemy);
                view.displayAttackAction(hero.getName(), enemy.getName(), damage);

                if (!enemy.isAlive()) {
                    handleEnemyDefeatMessage(enemy, isBossFight);
                    return true;
                }

                //heal (infinite Healing Potion item, never consumed)

            } else if (choice == 2) {
                hero.usePotion();
                view.displayPotionUsed(hero);

                //run

            } else if (choice == 3) {
                if (canRun) {
                    view.displayRunAway();
                    return false;
                } else {
                    view.displayCannotRunFromBoss();
                    continue;
                }
            }

            if (enemy.isAlive()) {
                int enemyDamage = enemy.performAttack(hero);
                view.displayAttackAction(enemy.getName(), hero.getName(), enemyDamage);

                if (!hero.isAlive()) {
                    view.displayHeroFallen();
                    return false;
                }
            }
        }
        return !enemy.isAlive();
    }

    private void handleEnemyDefeatMessage(Enemy enemy, boolean isBossFight) {
        if (isBossFight) {
            view.displayBossDefeated(enemy.getName());
        } else {
            view.displayEnemyDefeated(enemy.getName());
        }
    }

    //battle menu
    private int readMenuChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                return Integer.parseInt(input);
            }
            view.displayInvalidChoice();
            System.out.print(" Choose an action: ");
        }
    }

    //random movement enemy not boss

    private void moveEnemiesRandomly() {
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }

            char direction = enemy.getRandomMoveDirection();
            if (direction == ' ') {
                continue;
            }

            int[] step = computeStep(enemy.getPositionX(), enemy.getPositionY(), direction);
            int newX = step[0];
            int newY = step[1];

            if (!map.isInsideBounds(newX, newY)) {
                continue;
            }

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
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 32);
        }
        return c;

    }
    private void saveGame() { //estos son los metodos que correspoden al guardado y al de cargar partida
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savegame.dat"))) {

            oos.writeObject(map);
            oos.writeObject(hero);
            oos.writeObject(boss);
            oos.writeObject(enemies);
            oos.writeObject(chests);
            oos.writeObject(door);

            System.out.println("--- Game saved successfully! ---");
        } catch (IOException e) {
            System.out.println("Error saving the game: " + e.getMessage());
        }
    }

    private boolean loadGame() {
        File saveFile = new File("savegame.dat");
        if (!saveFile.exists()) {
            System.out.println("--- No saved game found. ---");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {

            this.map = (Map) ois.readObject();
            this.hero = (Hero) ois.readObject();
            this.boss = (Boss) ois.readObject();
            this.enemies = (Enemy[]) ois.readObject();
            this.chests = (Chest[]) ois.readObject();
            this.door = (Door) ois.readObject();

            System.out.println("--- Game loaded successfully! ---");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading the game: " + e.getMessage());
            return false;
        }
    }
}