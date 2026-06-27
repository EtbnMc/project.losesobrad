package Model;
import java.io.Serializable;

public class Region implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Map map;
    private final Enemy[] enemies;
    private int totalEnemies;

    public Region(String name, Map map, int enemyCapacity) {
        this.name = name;
        this.map = map;
        this.enemies = new Enemy[enemyCapacity];
        this.totalEnemies = 0;
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
    public String getName(){
        return name; }

    public String toString() {
        return "Region: " + name + " | Enemies: " + totalEnemies;
    }
}

