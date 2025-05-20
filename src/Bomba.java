import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Bomba {
    private int x, y;
    private boolean colocada = false;
    private boolean exploded = false;
    private int countToExplode = 0;
    private final int intervalToExplode = 28;
    private int frameBomb = 0, intervalBomb = 7, indexAnimBomb = 0;
    private int frameExplosion = 0, intervalExplosion = 3, indexAnimExplosion = 0;
    private boolean concreteAnim = false;
    private int bombX, bombY;
    private int frameConcreteExploding = 0, intevalConcreteExploding = 4, indexConcreteExploding = 0;

    private Image[] bombAnim;
    private Image[] fontExplosion, rightExplosion, leftExplosion, upExplosion, downExplosion;
    private Image[] concreteExploding;
    private GameMap gameMap;
    private int tileSize, scale;
    private int WIDTH, HEIGHT;

    public Bomba(GameMap gameMap, int tileSize, int scale, int WIDTH, int HEIGHT,
                 Image[] bombAnim, Image[] fontExplosion, Image[] rightExplosion, Image[] leftExplosion,
                 Image[] upExplosion, Image[] downExplosion, Image[] concreteExploding) {
        this.gameMap = gameMap;
        this.tileSize = tileSize;
        this.scale = scale;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.bombAnim = bombAnim;
        this.fontExplosion = fontExplosion;
        this.rightExplosion = rightExplosion;
        this.leftExplosion = leftExplosion;
        this.upExplosion = upExplosion;
        this.downExplosion = downExplosion;
        this.concreteExploding = concreteExploding;
    }

    public void colocar(int px, int py) {
        this.x = px;
        this.y = py;
        this.colocada = true;
        this.exploded = false;
        this.countToExplode = 0;
        this.frameBomb = 0;
        this.indexAnimBomb = 0;
        this.frameExplosion = 0;
        this.indexAnimExplosion = 0;
        this.concreteAnim = false;
        this.frameConcreteExploding = 0;
        this.indexConcreteExploding = 0;
        this.bombX = px;
        this.bombY = py;
    }

    public void update() {
        if (!colocada) return;

        if (!exploded) {
            // Animación de la bomba antes de explotar
            frameBomb++;
            if (frameBomb >= intervalBomb) {
                frameBomb = 0;
                indexAnimBomb = (indexAnimBomb + 1) % bombAnim.length;
                countToExplode++;
                if (countToExplode >= intervalToExplode) {
                    explotar();
                }
            }
        } else {
            // Animación de la explosión
            frameExplosion++;
            if (frameExplosion >= intervalExplosion) {
                frameExplosion = 0;
                indexAnimExplosion++;
                if (indexAnimExplosion >= 4) {
                    exploded = false;
                    colocada = false;
                    indexAnimExplosion = 0;
                }
            }
        }

        if (concreteAnim) {
            frameConcreteExploding++;
            if (frameConcreteExploding >= intevalConcreteExploding) {
                frameConcreteExploding = 0;
                indexConcreteExploding++;
                if (indexConcreteExploding >= concreteExploding.length) {
                    indexConcreteExploding = 0;
                    limpiarConcretos();
                    concreteAnim = false;
                }
            }
        }
    }

    private void explotar() {
        exploded = true;
        concreteAnim = true;
        bombX = x;
        bombY = y;
        // Marcar concretos para animación
        if (gameMap.getTile(y + 1, x) == 2) gameMap.setTile(y + 1, x, -1);
        if (gameMap.getTile(y - 1, x) == 2) gameMap.setTile(y - 1, x, -1);
        if (gameMap.getTile(y, x + 1) == 2) gameMap.setTile(y, x + 1, -1);
        if (gameMap.getTile(y, x - 1) == 2) gameMap.setTile(y, x - 1, -1);
    }

    private void limpiarConcretos() {
        if (gameMap.getTile(bombY + 1, bombX) == -1) gameMap.setTile(bombY + 1, bombX, 0);
        if (gameMap.getTile(bombY - 1, bombX) == -1) gameMap.setTile(bombY - 1, bombX, 0);
        if (gameMap.getTile(bombY, bombX + 1) == -1) gameMap.setTile(bombY, bombX + 1, 0);
        if (gameMap.getTile(bombY, bombX - 1) == -1) gameMap.setTile(bombY, bombX - 1, 0);
    }

    public void draw(GraphicsContext g2) {
        if (!colocada) return;
        int size = tileSize * scale;
        if (!exploded) {
            g2.drawImage(bombAnim[indexAnimBomb], x * size, y * size, size, size);
        } else {
            g2.drawImage(fontExplosion[indexAnimExplosion], x * size, y * size, size, size);
            g2.drawImage(rightExplosion[indexAnimExplosion], (x + 1) * size, y * size, size, size);
            g2.drawImage(leftExplosion[indexAnimExplosion], (x - 1) * size, y * size, size, size);
            g2.drawImage(upExplosion[indexAnimExplosion], x * size, (y - 1) * size, size, size);
            g2.drawImage(downExplosion[indexAnimExplosion], x * size, (y + 1) * size, size, size);
        }
        if (concreteAnim) {
            g2.drawImage(concreteExploding[indexConcreteExploding], bombX * size, bombY * size, size, size);
        }
    }

    // Devuelve true solo cuando la bomba ya no está colocada (puedes poner otra)
    public boolean isAvailable() {
        return !colocada;
    }

    // Devuelve true si la bomba está explotando (no usar para saber si puedes poner otra)
    public boolean isExploded() {
        return exploded;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public int[][] getExplosionTiles() {
        // Centro, derecha, izquierda, arriba, abajo
        return new int[][] {
                {x, y},
                {x + 1, y},
                {x - 1, y},
                {x, y - 1},
                {x, y + 1}
        };
    }
}