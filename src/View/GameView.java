package View;

import Model.Boss;
import Model.Hero;
import Model.Map;

public class GameView {

    //colores
    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[36m";
    private static final String RED = "\033[31m";
    private static final String PURPLE = "\033[35m";

    //aesthetic

    public void displayMap(Map map) {
        System.out.println();
        printHorizontalBorder(map.getColumns());

        char[][] grid = map.getGrid();
        for (int i = 0; i < map.getRows(); i++) {
            System.out.print("  | ");
            for (int j = 0; j < map.getColumns(); j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("|");
        }

        printHorizontalBorder(map.getColumns());
        System.out.print("  Legend:  ");
        System.out.print("  H = Hero  ");
        System.out.print("  E = Enemy   ");
        System.out.print("  B = Boss   ");
        System.out.println("   . = Empty");


    }

    private void printHorizontalBorder(int columns) {
        System.out.print("  +");
        for (int j = 0; j < columns; j++) {
            System.out.print("--");
        }
        System.out.println("-+");
    }

    public void displayHeroStatus(Hero hero) {
        System.out.println("=================================================");
        System.out.println(" " + hero);
        System.out.println("=================================================");
    }

    public void displayMovePrompt() {
        System.out.print(" Move (W/A/S/D) or Q to quit: ");
    }

    public void displayInvalidKey() {
        System.out.println("  Invalid key. Please use W, A, S, D or Q.");
    }

    public void displayBoundaryReached() {
        System.out.println("  You cannot leave the map ");
    }

    public void displayCellBlocked() {
        System.out.println("  Another enemy is already there. Choose a different direction.");
    }

    public void displayMoved(int x, int y) {
        System.out.println("  Moved to (" + x + "," + y + ")");
    }

    public void displayQuitMessage() {
        System.out.println("  You left the dungeon. ");
    }

    public void displayGoodbye() {
        System.out.println("  tnks 4 playing my game, gg ");
    }

    public void displayWelcome(Hero hero, Boss boss) {
        System.out.println();
        System.out.println("+================================================+");
        System.out.println("|                  MINECRAFT 2                   |");
        System.out.println("+================================================+");
        System.out.println("|  Hero: " + padRight(hero.getName(), 40) + "|");
        System.out.println("|  Final Boss: " + padRight(boss.getName(), 34) + "|");
        System.out.println("+================================================+");
        System.out.println("|  Move with W A S D and explore the map.        |");
        System.out.println("|  Defeat the Boss to win.                       |");
        System.out.println("+================================================+");
    }

    public void displayVictory(Hero hero) {
        System.out.println();
        System.out.println("+================================================+");
        System.out.println("|                    * VICTORY *                 |");
        System.out.println("+================================================+");
        System.out.println("|  " + padRight(hero.getName() + " has freed The End", 46) + "|");
        System.out.println("|  Remaining HP: " + padRight(hero.getHealth() + "/" + hero.getMaxHealth(), 32) + "|");
        System.out.println("+================================================+");
    }

    public void displayDefeat() {
        System.out.println();
        System.out.println("+================================================+");
        System.out.println("|                  *** DEFEAT ***                |");
        System.out.println("+================================================+");
        System.out.println("|  GAME OVER                                     |");
        System.out.println("|  Stay determined...                            |");
        System.out.println("+================================================+");
    }

    //battle

    public void displayBattleMenu(Hero hero, String enemyName, int enemyHealth, int enemyMaxHealth, boolean canRun) {
        System.out.println();
        System.out.println("=== BATTLE MENU ===");
        System.out.println(" " + hero.getName() + " HP: " + hero.getHealth() + "/" + hero.getMaxHealth());
        System.out.println(" " + enemyName + " HP: " + enemyHealth + "/" + enemyMaxHealth);
        System.out.println("-------------------");
        System.out.println(" 1. Attack");
        System.out.println(" 2. Full Heal");
        if (canRun) {
            System.out.println(" 3. Run ");
        } else {
            System.out.println(" 3. Run  (unavailable against the Boss)");
        }
        System.out.print(" Choose an action: ");
    }

    public void displayInvalidChoice() {
        System.out.println("  Invalid choice. Please select 1, 2 or 3.");
    }

    public void displayCombatStart(String heroName, String enemyName, boolean isBossFight) {
        System.out.println();
        if (isBossFight) {
            System.out.println("+================================================+");
            System.out.println("|   FINAL BATTLE: " + heroName + " vs " + enemyName);
            System.out.println("+================================================+");
        } else {
            System.out.println("+------------------------------------------------+");
            System.out.println("|   BATTLE: " + heroName + " vs " + enemyName);
            System.out.println("+------------------------------------------------+");
        }
    }

    //battle actions (view)

    public void displayAttackAction(String attackerName, String targetName, int damage) {
        System.out.println("  " + attackerName + " attacks " + targetName + " for " + damage + " damage!");
    }

    public void displayHealAction(Hero hero) {
        System.out.println("  " + hero.getName() + " uses Full Heal and restores all HP! ("
                + hero.getHealth() + "/" + hero.getMaxHealth() + ")");
    }

    public void displayRunAway() {
        System.out.println("  You run away from the battle and return to the map.");
    }

    public void displayCannotRunFromBoss() {
        System.out.println("  There is no escape from the Boss! You must fight.");
    }

    public void displayEnemyDefeated(String enemyName) {
        System.out.println("  " + enemyName + " has been defeated!");
    }

    public void displayBossDefeated(String bossName) {
        System.out.println("  " + bossName + " has been DEFEATED");
    }

    //death/kills

    public void displayHeroFallen() {
        System.out.println("  The hero has fallen in battle...");
    }

    public void displayEnemyRemovedFromMap(String enemyName) {
        System.out.println("  " + enemyName + " disappears from the map.");
    }

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}