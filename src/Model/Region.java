package Model;
import java.io.Serializable;

public class Region implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Map map;
    private final Enemy[] enemies;
    private int totalEnemies;
    private final Chest[] chests;
    private int totalChests;

    public Region(String name, Map map, int enemyCapacity) {
        this.name = name;
        this.map = map;
        this.enemies = new Enemy[enemyCapacity];
        this.totalEnemies = 0;
        this.chests = new Chest[enemyCapacity];
        this.totalChests = 0;
    }

    public Enemy spawnEnemy(String enemyName, int x, int y) {
        if (!map.isInsideBounds(x, y) || map.getCell(x, y) != Map.Empty) {
            return null;
        }
        if (totalEnemies >= enemies.length) {
            return null;
        }
        Enemy enemy = new Enemy(enemyName, x, y);
        enemies[totalEnemies] = enemy;
        totalEnemies++;
        map.placeSymbol(x, y, Enemy.Symbol);
        return enemy;
    }

    //chests stay hidden: the map cell keeps showing Empty, never the Chest symbol
    public Chest spawnChest(String chestName, int x, int y) {
        if (!map.isInsideBounds(x, y) || map.getCell(x, y) != Map.Empty) {
            return null;
        }
        if (totalChests >= chests.length) {
            return null;
        }
        Chest chest = new Chest(chestName, x, y);
        chests[totalChests] = chest;
        totalChests++;
        map.placeSymbol(x, y, Chest.Symbol);
        return chest;
    }

    public Boss spawnBoss(String bossName, int x, int y) {
        if (!map.isInsideBounds(x, y) || map.getCell(x, y) != Map.Empty) {
            return null;
        }
        Boss boss = new Boss(bossName, x, y);
        map.placeSymbol(x, y, Boss.Symbol);
        return boss;
    }

    public Enemy[] getEnemies(){
        return enemies; }
    public int getTotalEnemies(){
        return totalEnemies; }
    public Chest[] getChests(){
        return chests; }
    public int getTotalChests(){
        return totalChests; }
    public String getName(){
        return name; }

    public String toString() {
        return "Region: " + name + " | Enemies: " + totalEnemies;
    }
}