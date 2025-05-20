import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;

public class PixelBlast extends Application {

    private Stage mainStage;
    private Menu menu;
    private GameMap gameMap;
    private Jugador jugador;
    private Bomba bomba;

    Canvas canvas;
    GraphicsContext g2;
    Image spriteSheet;
    Image spriteSheet2;
    Image concreteTile, blockTile;
    int tileSize = 16;
    final int SCALE = 3;
    final int WIDTH = (tileSize * SCALE) * 15;
    final int HEIGHT = (tileSize * SCALE) * 13;

    // Animaciones
    Image[] bombAnim;
    Image[] fontExplosion, rightExplosion, leftExplosion, upExplosion, downExplosion;
    Image[] concreteExploding;

    // Barra de vidas
    int vidas = 3;
    boolean gameOver = false;
    AnimationTimer timer;

    // Enemigos
    private List<Enemigo> enemigos = new ArrayList<>();
    // Animaciones por dirección: 0=izq, 1=der, 2=abajo, 3=arriba
    Image[][] enemigoAnim = new Image[4][3];

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        menu = new Menu(this, WIDTH, HEIGHT);
        mainStage.setTitle("PixelBlast");
        mainStage.setScene(menu.getMenuScene());
        mainStage.setResizable(false);
        mainStage.show();
    }

    public Stage getStage() {
        return mainStage;
    }

    public void startGame(int selectedMap) {
        vidas = 3;
        gameOver = false;
        canvas = new Canvas(WIDTH, HEIGHT);
        g2 = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene gameScene = new Scene(root, WIDTH, HEIGHT);

        // Inicializa la lógica del juego y los objetos antes de los eventos
        startGameLogic(selectedMap);

        gameScene.setOnKeyPressed(e -> {
            if (gameOver) return;
            if (jugador == null) return;
            if (e.getCode() == KeyCode.SPACE) {
                if (bomba == null || bomba.isAvailable()) {
                    int bx = (jugador.getX() + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                    int by = (jugador.getY() + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                    if (gameMap != null && gameMap.getTile(by, bx) == 0) {
                        gameMap.setTile(by, bx, 3);
                        bomba.colocar(bx, by);
                    }
                }
            }
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) jugador.right = true;
            if (e.getCode() == KeyCode.LEFT  || e.getCode() == KeyCode.A) jugador.left = true;
            if (e.getCode() == KeyCode.UP    || e.getCode() == KeyCode.W) jugador.up = true;
            if (e.getCode() == KeyCode.DOWN  || e.getCode() == KeyCode.S) jugador.down = true;
            if (e.getCode() == KeyCode.ESCAPE) mainStage.setScene(menu.getMenuScene());
        });

        gameScene.setOnKeyReleased(e -> {
            if (gameOver) return;
            if (jugador == null) return;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) jugador.right = false;
            if (e.getCode() == KeyCode.LEFT  || e.getCode() == KeyCode.A) jugador.left = false;
            if (e.getCode() == KeyCode.UP    || e.getCode() == KeyCode.W) jugador.up = false;
            if (e.getCode() == KeyCode.DOWN  || e.getCode() == KeyCode.S) jugador.down = false;
        });

        mainStage.setScene(gameScene);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
            }
        };
        timer.start();
    }

    //metodo para reiniciar el juego y animaciones
    public void startGameLogic(int selectedMap) {
        try {
            spriteSheet = new Image(getClass().getResourceAsStream("/sheets.png"));
            spriteSheet2 = new Image(getClass().getResourceAsStream("/sheets2.png"));

            concreteTile = getSubImage(spriteSheet, 4 * tileSize, 3 * tileSize, tileSize, tileSize);
            blockTile = getSubImage(spriteSheet, 3 * tileSize, 3 * tileSize, tileSize, tileSize);

            Image[] playerAnimUp = new Image[3];
            Image[] playerAnimDown = new Image[3];
            Image[] playerAnimRight = new Image[3];
            Image[] playerAnimLeft = new Image[3];
            Image[] playerAnimDied = new Image[3];
            Image[] barraVidas = new Image[4];
            bombAnim = new Image[3];
            fontExplosion = new Image[4];
            rightExplosion = new Image[4];
            leftExplosion = new Image[4];
            upExplosion = new Image[4];
            downExplosion = new Image[4];
            concreteExploding = new Image[6];

            for (int i = 0; i < 3; i++) {
                playerAnimDied[i] = getSubImage(spriteSheet, 2 * tileSize, (i + 2) * tileSize, tileSize, tileSize);
            }

            for (int i = 0; i < 6; i++) {
                concreteExploding[i] = getSubImage(spriteSheet, (i + 5) * tileSize, 3 * tileSize, tileSize, tileSize);
            }

            fontExplosion[0] = getSubImage(spriteSheet, 2 * tileSize, 6 * tileSize, tileSize, tileSize);
            fontExplosion[1] = getSubImage(spriteSheet, 7 * tileSize, 6 * tileSize, tileSize, tileSize);
            fontExplosion[2] = getSubImage(spriteSheet, 2 * tileSize, 11 * tileSize, tileSize, tileSize);
            fontExplosion[3] = getSubImage(spriteSheet, 7 * tileSize, 11 * tileSize, tileSize, tileSize);

            rightExplosion[0] = getSubImage(spriteSheet, 4 * tileSize, 6 * tileSize, tileSize, tileSize);
            rightExplosion[1] = getSubImage(spriteSheet, 9 * tileSize, 6 * tileSize, tileSize, tileSize);
            rightExplosion[2] = getSubImage(spriteSheet, 4 * tileSize, 11 * tileSize, tileSize, tileSize);
            rightExplosion[3] = getSubImage(spriteSheet, 9 * tileSize, 11 * tileSize, tileSize, tileSize);

            leftExplosion[0] = getSubImage(spriteSheet, 0, 6 * tileSize, tileSize, tileSize);
            leftExplosion[1] = getSubImage(spriteSheet, 5 * tileSize, 6 * tileSize, tileSize, tileSize);
            leftExplosion[2] = getSubImage(spriteSheet, 0, 11 * tileSize, tileSize, tileSize);
            leftExplosion[3] = getSubImage(spriteSheet, 5 * tileSize, 11 * tileSize, tileSize, tileSize);

            upExplosion[0] = getSubImage(spriteSheet, 2 * tileSize, 4 * tileSize, tileSize, tileSize);
            upExplosion[1] = getSubImage(spriteSheet, 7 * tileSize, 4 * tileSize, tileSize, tileSize);
            upExplosion[2] = getSubImage(spriteSheet, 2 * tileSize, 9 * tileSize, tileSize, tileSize);
            upExplosion[3] = getSubImage(spriteSheet, 7 * tileSize, 9 * tileSize, tileSize, tileSize);

            downExplosion[0] = getSubImage(spriteSheet, 2 * tileSize, 8 * tileSize, tileSize, tileSize);
            downExplosion[1] = getSubImage(spriteSheet, 7 * tileSize, 8 * tileSize, tileSize, tileSize);
            downExplosion[2] = getSubImage(spriteSheet, 2 * tileSize, 13 * tileSize, tileSize, tileSize);
            downExplosion[3] = getSubImage(spriteSheet, 7 * tileSize, 13 * tileSize, tileSize, tileSize);

            for (int i = 0; i < 3; i++) {
                playerAnimLeft[i] = getSubImage(spriteSheet, i * tileSize, 0, tileSize, tileSize);
                playerAnimRight[i] = getSubImage(spriteSheet, i * tileSize, tileSize, tileSize, tileSize);
                playerAnimDown[i] = getSubImage(spriteSheet, (i + 3) * tileSize, 0, tileSize, tileSize);
                playerAnimUp[i] = getSubImage(spriteSheet, (i + 3) * tileSize, tileSize, tileSize, tileSize);
                bombAnim[i] = getSubImage(spriteSheet, i * tileSize, 3 * tileSize, tileSize, tileSize);
            }

            // Animaciones de enemigo desde sheets2.png
            for (int i = 0; i < 3; i++) {
                enemigoAnim[0][i] = getSubImage(spriteSheet2, i * tileSize, 0, tileSize, tileSize); // Izquierda
                enemigoAnim[1][i] = getSubImage(spriteSheet2, i * tileSize, tileSize, tileSize, tileSize); // Derecha
                enemigoAnim[2][i] = getSubImage(spriteSheet2, (i + 3) * tileSize, 0, tileSize, tileSize); // Abajo
                enemigoAnim[3][i] = getSubImage(spriteSheet2, (i + 3) * tileSize, tileSize, tileSize, tileSize); // Arriba
            }

            barraVidas[0] = new Image(getClass().getResourceAsStream("/corazon0.png"));
            barraVidas[1] = new Image(getClass().getResourceAsStream("/corazon1.png"));
            barraVidas[2] = new Image(getClass().getResourceAsStream("/corazon2.png"));
            barraVidas[3] = new Image(getClass().getResourceAsStream("/corazon3.png"));

            gameMap = new GameMap();
            gameMap.setMap(selectedMap);

            jugador = new Jugador(
                    tileSize, SCALE, playerAnimUp, playerAnimDown, playerAnimRight, playerAnimLeft, playerAnimDied, barraVidas
            );

            bomba = new Bomba(
                    gameMap, tileSize, SCALE, WIDTH, HEIGHT,
                    bombAnim, fontExplosion, rightExplosion, leftExplosion, upExplosion, downExplosion, concreteExploding
            );

            enemigos.clear();
            int cols = gameMap.getColumns();
            int rows = gameMap.getRows();

            // Esquinas: superior derecha, inferior izquierda, inferior derecha

            int[][] esquinas = {
                    {cols - 2, 1},        // superior derecha desplazada
                    {1, rows - 2},        // inferior izquierda desplazada
                    {cols - 2, rows - 2}  // inferior derecha desplazada
            };

            for (int i = 0; i < 3; i++) {
                int ex = esquinas[i][0];
                int ey = esquinas[i][1];
                enemigos.add(new Enemigo(
                        ex * tileSize * SCALE,
                        ey * tileSize * SCALE,
                        tileSize,
                        SCALE,
                        enemigoAnim
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            jugador = null;
            bomba = null;
            gameMap = null;
            enemigos.clear();
        }
    }

    public boolean isFree(int nextX, int nextY) {
        if (gameMap == null) return false;
        int size = SCALE * tileSize;
        int nextX_1 = nextX / size;
        int nextY_1 = nextY / size;
        int nextX_2 = (nextX + size - 1) / size;
        int nextY_2 = nextY / size;
        int nextX_3 = nextX / size;
        int nextY_3 = (nextY + size - 1) / size;
        int nextX_4 = (nextX + size - 1) / size;
        int nextY_4 = (nextY + size - 1) / size;
        int[][] scene = gameMap.getScene();
        return !((scene[nextY_1][nextX_1] == 1 || scene[nextY_1][nextX_1] == 2) ||
                (scene[nextY_2][nextX_2] == 1 || scene[nextY_2][nextX_2] == 2) ||
                (scene[nextY_3][nextX_3] == 1 || scene[nextY_3][nextX_3] == 2) ||
                (scene[nextY_4][nextX_4] == 1 || scene[nextY_4][nextX_4] == 2));
    }

    public void update() {
        if (gameOver) return;
        if (jugador != null) {
            jugador.update(this::isFree);
            if (bomba != null) {
                bomba.update();
                jugador.checkExplosionCollision(bomba, () -> {
                    vidas--;
                    if (vidas < 0) {
                        gameOver = true;
                        timer.stop();
                    }
                });
            }
        }
        // Actualiza enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.update(gameMap);
        }
    }

    public void draw() {
        if (g2 == null || gameMap == null) return;

        g2.setFill(gameMap.getBackgroundColor());
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int size = tileSize * SCALE;
        int[][] scene = gameMap.getScene();
        int columns = gameMap.getColumns();
        int rows = gameMap.getRows();
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (scene[j][i] == 1) {
                    g2.drawImage(blockTile, i * size, j * size, size, size);
                } else if (scene[j][i] == 2) {
                    g2.drawImage(concreteTile, i * size, j * size, size, size);
                }
            }
        }

        if (bomba != null) bomba.draw(g2);

        // Dibuja enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.draw(g2);
        }

        if (jugador != null)
            g2.drawImage(jugador.getSprite(), jugador.getX(), jugador.getY(), size, size);

        // Dibuja la barra de vida
        if (jugador != null && jugador.getBarraVidas() != null) {
            int vidasIndex = Math.max(0, Math.min(vidas, 3));
            g2.drawImage(jugador.getBarraVidas()[vidasIndex], 10, 10, 120, 40);
        }
    }

    private Image getSubImage(Image img, int x, int y, int w, int h) {
        PixelReader reader = img.getPixelReader();
        return new WritableImage(reader, x, y, w, h);
    }

    public static void main(String[] args) {
        launch(args);
    }
}