package Model;
public class Map {

    public static final char Empty = '.';
    public static final char HeroSy = Hero.Symbol;
    public static final char EnemySy = Enemy.Symbol;
    public static final char BossSy = Boss.Symbol;
    public static final char Wall = '—';

    private final char[][] grid;
    private final int rows;
    private final int columns;

    public Map(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new char[rows][columns];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = Empty;
            }
        }
    }

    public void placeSymbol(int x, int y, char symbol) {
        if (isInsideBounds(x, y)) {
            grid[x][y] = symbol;
        }
    }

    public void clearCell(int x, int y) {
        if (isInsideBounds(x, y)) {
            grid[x][y] = Empty;
        }
    }

    public char getCell(int x, int y) {
        if (isInsideBounds(x, y)) {
            return grid[x][y];
        }
        return Empty;
    }

    public void updatePosition(int fromX, int fromY, int toX, int toY, char symbol) {
        clearCell(fromX, fromY);
        placeSymbol(toX, toY, symbol);
    }

    public boolean isInsideBounds(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < columns;
    }

    public char[][] getGrid(){
        return grid; }
    public int getRows(){
        return rows; }
    public int getColumns(){
        return columns; }
}

