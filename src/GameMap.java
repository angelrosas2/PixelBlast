import javafx.scene.paint.Color;

public class GameMap {
    private final int[][][] maps;
    private final Color[] mapBackgrounds;
    private int[][] currentScene;
    private int currentMapIndex;

    public GameMap() {
        maps = new int[][][] {
                {
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                },
                {
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                        {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                },
                {
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,2,0,2,0,2,0,2,0,2,0,2,1,2,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                        {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                        {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                }
        };

        mapBackgrounds = new Color[] {
                Color.rgb(56, 135, 0),    // Mapa 1
                Color.rgb(40, 40, 120),   // Mapa 2
                Color.rgb(120, 80, 40)    // Mapa 3
        };
        setMap(0);
    }

    public void setMap(int index) {
        currentMapIndex = index;
        int rows = maps[index].length;
        int cols = maps[index][0].length;
        currentScene = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(maps[index][i], 0, currentScene[i], 0, cols);

        // Limpiar zona inicial
        currentScene[1][1] = 0;
        currentScene[2][1] = 0;
        currentScene[1][2] = 0;
        currentScene[2][2] = 0;
    }

    public int[][] getScene() {
        return currentScene;
    }

    public Color getBackgroundColor() {
        return mapBackgrounds[currentMapIndex];
    }

    public int getRows() {
        return currentScene.length;
    }

    public int getColumns() {
        return currentScene[0].length;
    }

    public int getCurrentMapIndex() {
        return currentMapIndex;
    }

    public void setTile(int row, int col, int value) {
        currentScene[row][col] = value;
    }

    public int getTile(int row, int col) {
        return currentScene[row][col];
    }
}