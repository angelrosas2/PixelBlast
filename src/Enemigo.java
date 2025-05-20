import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Enemigo {
    private int x, y;
    private int tileSize, scale;
    private Image[][] anim; // [direccion][frame]
    private int animIndex = 0, animFrame = 0, animInterval = 8;
    private int dir = 2; // 0: izquierda, 1: derecha, 2: abajo, 3: arriba

    public Enemigo(int x, int y, int tileSize, int scale, Image[][] anim) {
        this.x = x;
        this.y = y;
        this.tileSize = tileSize;
        this.scale = scale;
        this.anim = anim;
    }

    public void update(GameMap gameMap) {
        // Movimiento aleatorio simple
        if (Math.random() < 0.02) dir = (int)(Math.random() * 4);
        int dx = 0, dy = 0;
        if (dir == 0) dx = -1;
        if (dir == 1) dx = 1;
        if (dir == 2) dy = 1;
        if (dir == 3) dy = -1;
        int nextX = x + dx * tileSize / 2;
        int nextY = y + dy * tileSize / 2;
        int size = tileSize * scale;
        int col = (nextX + size / 2) / size;
        int row = (nextY + size / 2) / size;
        if (gameMap.getTile(row, col) == 0) {
            x = nextX;
            y = nextY;
        }
        animFrame++;
        if (animFrame >= animInterval) {
            animFrame = 0;
            animIndex = (animIndex + 1) % anim[dir].length;
        }
    }

    public void draw(GraphicsContext g2) {
        int size = tileSize * scale;
        g2.drawImage(anim[dir][animIndex], x, y, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}