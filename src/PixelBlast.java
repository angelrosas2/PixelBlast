import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

class Bomb {
    int x, y;
    boolean exploded;
    int countToExplode, intervalToExplode = 4;
}

public class PixelBlast extends Application {

    private Stage mainStage;
    private Menu menu;

    boolean isRunning = true;
    Canvas canvas;
    GraphicsContext g2;
    Image spriteSheet;
    Image concreteTile, blockTile, player;
    Bomb bomb;
    int[][] scene;
    int playerX, playerY;
    int tileSize = 16, rows = 13, columns = 15;
    int speed = 4;
    boolean right, left, up, down;
    boolean moving;
    int framePlayer = 0, intervalPlayer = 5, indexAnimPlayer = 0;
    Image[] playerAnimUp, playerAnimDown, playerAnimRight, playerAnimLeft;
    int frameBomb = 0, intervalBomb = 7, indexAnimBomb = 0;
    Image[] bombAnim;
    Image[] fontExplosion, rightExplosion, leftExplosion, upExplosion, downExplosion;
    int frameExplosion = 0, intervalExplosion = 3, indexAnimExplosion = 0;
    Image[] concreteExploding;
    int frameConcreteExploding = 0, intevalConcreteExploding = 4, indexConcreteExploding = 0;
    boolean concreteAnim = false;
    int bombX, bombY;

    final int SCALE = 3;
    final int WIDTH = (tileSize * SCALE) * columns;
    final int HEIGHT = (tileSize * SCALE) * rows;

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
        canvas = new Canvas(WIDTH, HEIGHT);
        g2 = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene gameScene = new Scene(root, WIDTH, HEIGHT);

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (bomb == null) {
                    bomb = new Bomb();
                    bomb.x = (playerX + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                    bomb.y = (playerY + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                    scene[bomb.y][bomb.x] = 3;
                }
            }
            if (e.getCode() == KeyCode.RIGHT) right = true;
            if (e.getCode() == KeyCode.LEFT) left = true;
            if (e.getCode() == KeyCode.UP) up = true;
            if (e.getCode() == KeyCode.DOWN) down = true;
            if (e.getCode() == KeyCode.ESCAPE) mainStage.setScene(menu.getMenuScene());
        });

        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.RIGHT) right = false;
            if (e.getCode() == KeyCode.LEFT) left = false;
            if (e.getCode() == KeyCode.UP) up = false;
            if (e.getCode() == KeyCode.DOWN) down = false;
        });

        mainStage.setScene(gameScene);
        startGameLogic(selectedMap);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
            }
        };
        timer.start();
    }

    public void startGameLogic(int selectedMap) {
        try {
            spriteSheet = new Image(getClass().getResourceAsStream("/sheets.png"));

            concreteTile = getSubImage(spriteSheet, 4 * tileSize, 3 * tileSize, tileSize, tileSize);
            blockTile = getSubImage(spriteSheet, 3 * tileSize, 3 * tileSize, tileSize, tileSize);
            player = getSubImage(spriteSheet, 4 * tileSize, 0, tileSize, tileSize);

            playerAnimUp = new Image[3];
            playerAnimDown = new Image[3];
            playerAnimRight = new Image[3];
            playerAnimLeft = new Image[3];
            bombAnim = new Image[3];
            fontExplosion = new Image[4];
            rightExplosion = new Image[4];
            leftExplosion = new Image[4];
            upExplosion = new Image[4];
            downExplosion = new Image[4];
            concreteExploding = new Image[6];

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

            // Tres mapas de ejemplo
            int[][][] maps = {
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
                            {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
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

            scene = new int[rows][columns];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < columns; j++)
                    scene[i][j] = maps[selectedMap][i][j];

            // Limpiar zona inicial
            scene[1][1] = 0;
            scene[2][1] = 0;
            scene[1][2] = 0;

            playerX = (tileSize * SCALE);
            playerY = (tileSize * SCALE);
            bomb = null;
            frameBomb = 0; indexAnimBomb = 0; frameExplosion = 0; indexAnimExplosion = 0;
            frameConcreteExploding = 0; indexConcreteExploding = 0; concreteAnim = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFree(int nextX, int nextY) {
        int size = SCALE * tileSize;
        int nextX_1 = nextX / size;
        int nextY_1 = nextY / size;
        int nextX_2 = (nextX + size - 1) / size;
        int nextY_2 = nextY / size;
        int nextX_3 = nextX / size;
        int nextY_3 = (nextY + size - 1) / size;
        int nextX_4 = (nextX + size - 1) / size;
        int nextY_4 = (nextY + size - 1) / size;
        return !((scene[nextY_1][nextX_1] == 1 || scene[nextY_1][nextX_1] == 2) ||
                (scene[nextY_2][nextX_2] == 1 || scene[nextY_2][nextX_2] == 2) ||
                (scene[nextY_3][nextX_3] == 1 || scene[nextY_3][nextX_3] == 2) ||
                (scene[nextY_4][nextX_4] == 1 || scene[nextY_4][nextX_4] == 2));
    }

    public void update() {
        moving = false;
        if (right && isFree(playerX + speed, playerY)) {
            playerX += speed;
            moving = true;
        }
        if (left && isFree(playerX - speed, playerY)) {
            playerX -= speed;
            moving = true;
        }
        if (up && isFree(playerX, playerY - speed)) {
            playerY -= speed;
            moving = true;
        }
        if (down && isFree(playerX, playerY + speed)) {
            playerY += speed;
            moving = true;
        }

        if (bomb != null) {
            frameBomb++;
            if (frameBomb == intervalBomb) {
                frameBomb = 0;
                indexAnimBomb++;
                if (indexAnimBomb > 2) {
                    indexAnimBomb = 0;
                    bomb.countToExplode++;
                }
                if (bomb.countToExplode >= bomb.intervalToExplode) {
                    concreteAnim = true;
                    bombX = bomb.x;
                    bombY = bomb.y;
                    bomb.exploded = true;
                    if (scene[bomb.y + 1][bomb.x] == 2) scene[bomb.y + 1][bomb.x] = -1;
                    if (scene[bomb.y - 1][bomb.x] == 2) scene[bomb.y - 1][bomb.x] = -1;
                    if (scene[bomb.y][bomb.x + 1] == 2) scene[bomb.y][bomb.x + 1] = -1;
                    if (scene[bomb.y][bomb.x - 1] == 2) scene[bomb.y][bomb.x - 1] = -1;
                }
            }

            if(bomb.exploded) {
                frameExplosion++;
                if (frameExplosion == intervalExplosion) {
                    frameExplosion = 0;
                    indexAnimExplosion++;
                    if (indexAnimExplosion == 4) {
                        scene[bomb.y][bomb.x] = 0;
                        bomb = null;
                        indexAnimExplosion = 0;
                    }
                }
            }
        }

        if (concreteAnim) {
            frameConcreteExploding++;
            if (frameConcreteExploding == intevalConcreteExploding) {
                frameConcreteExploding = 0;
                indexConcreteExploding++;
                if (indexConcreteExploding == 5) {
                    indexConcreteExploding = 0;
                    if (scene[bombY + 1][bombX] == -1) scene[bombY + 1][bombX] = 0;
                    if (scene[bombY - 1][bombX] == -1) scene[bombY - 1][bombX] = 0;
                    if (scene[bombY][bombX + 1] == -1) scene[bombY][bombX + 1] = 0;
                    if (scene[bombY][bombX - 1] == -1) scene[bombY][bombX - 1] = 0;
                    concreteAnim = false;
                }
            }
        }

        if (moving) {
            framePlayer++;
            if (framePlayer > intervalPlayer) {
                framePlayer = 0;
                indexAnimPlayer++;
                if (indexAnimPlayer > 2) indexAnimPlayer = 0;
            }

            if (right) player = playerAnimRight[indexAnimPlayer];
            else if (left) player = playerAnimLeft[indexAnimPlayer];
            else if (up) player = playerAnimUp[indexAnimPlayer];
            else if (down) player = playerAnimDown[indexAnimPlayer];
        } else {
            player = playerAnimDown[1];
        }
    }

    public void draw() {
        g2.setFill(Color.rgb(56, 135, 0));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int size = tileSize * SCALE;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (scene[j][i] == 1) {
                    g2.drawImage(blockTile, i * size, j * size, size, size);
                } else if (scene[j][i] == 2) {
                    g2.drawImage(concreteTile, i * size, j * size, size, size);
                } else if (scene[j][i] == 3 && bomb != null) {
                    g2.drawImage(bombAnim[indexAnimBomb], i * size, j * size, size, size);
                } else if (scene[j][i] == -1) {
                    g2.drawImage(concreteExploding[indexConcreteExploding], i * size, j * size, size, size);
                }
            }
        }

        g2.drawImage(player, playerX, playerY, size, size);
    }

    private Image getSubImage(Image img, int x, int y, int w, int h) {
        PixelReader reader = img.getPixelReader();
        return new WritableImage(reader, x, y, w, h);
    }

    public static void main(String[] args) {
        launch(args);
    }
}