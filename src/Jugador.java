import javafx.scene.image.Image;

public class Jugador {
    private int x, y;
    private int tileSize, scale;
    private int animFrame = 0, animTick = 0;
    private Image[] animUp, animDown, animRight, animLeft, animDied, barraVidas;
    public boolean right, left, up, down;
    private Image currentSprite;
    private boolean lastExplosion = false;

    public Jugador(int tileSize, int scale, Image[] animUp, Image[] animDown, Image[] animRight, Image[] animLeft, Image[] animDied, Image[] barraVidas) {
        this.tileSize = tileSize;
        this.scale = scale;
        this.animUp = animUp;
        this.animDown = animDown;
        this.animRight = animRight;
        this.animLeft = animLeft;
        this.animDied = animDied;
        this.barraVidas = barraVidas;
        this.x = tileSize * scale;
        this.y = tileSize * scale;
        this.currentSprite = animDown[0];
    }

    public void update(java.util.function.BiFunction<Integer, Integer, Boolean> isFree) {
        int speed = 3;
        int size = tileSize * scale;

        // Alineación a la cuadrícula antes de permitir movimiento diagonal
        if ((left || right) && y % size != 0) {
            if (y % size < size / 2) y -= Math.min(speed, y % size);
            else y += Math.min(speed, size - (y % size));
            return;
        }
        if ((up || down) && x % size != 0) {
            if (x % size < size / 2) x -= Math.min(speed, x % size);
            else x += Math.min(speed, size - (x % size));
            return;
        }

        int nextX = x, nextY = y;
        boolean moved = false;

        // Solo un movimiento vertical y uno horizontal a la vez
        int vertical = (up ? 1 : 0) + (down ? 1 : 0);
        int horizontal = (left ? 1 : 0) + (right ? 1 : 0);

        if (vertical > 1) {
            // Si se presionan ambas teclas verticales, ignora el movimiento vertical
            up = down = false;
        }
        if (horizontal > 1) {
            // Si se presionan ambas teclas horizontales, ignora el movimiento horizontal
            left = right = false;
        }

        if (up && isFree.apply(x, y - speed)) {
            nextY -= speed;
            moved = true;
        } else if (down && isFree.apply(x, y + speed)) {
            nextY += speed;
            moved = true;
        }
        if (right && isFree.apply(x + speed, nextY)) {
            nextX += speed;
            moved = true;
        } else if (left && isFree.apply(x - speed, nextY)) {
            nextX -= speed;
            moved = true;
        }

        // Verifica colisiones para ambos ejes
        if (isFree.apply(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else if (isFree.apply(nextX, y)) {
            x = nextX;
        } else if (isFree.apply(x, nextY)) {
            y = nextY;
        }

        // Animación según prioridad de dirección
        if (moved) {
            if (up) animate(animUp);
            else if (down) animate(animDown);
            else if (right) animate(animRight);
            else if (left) animate(animLeft);
        } else {
            if (up) currentSprite = animUp[0];
            else if (down) currentSprite = animDown[0];
            else if (right) currentSprite = animRight[0];
            else if (left) currentSprite = animLeft[0];
        }
    }

    private void animate(Image[] anim) {
        animTick++;
        if (animTick >= 8) {
            animTick = 0;
            animFrame = (animFrame + 1) % anim.length;
        }
        currentSprite = anim[animFrame];
    }

    public Image getSprite() {
        return currentSprite;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Image[] getBarraVidas() {
        return barraVidas;
    }

    // Ahora recibe un callback para restar vida
    public void checkExplosionCollision(Bomba bomba, Runnable onDamage) {
        if (bomba == null || !bomba.isExploded()) {
            lastExplosion = false;
            return;
        }
        for (int[] tile : bomba.getExplosionTiles()) {
            int explosionX = tile[0] * tileSize * scale;
            int explosionY = tile[1] * tileSize * scale;
            if (Math.abs(x - explosionX) < tileSize * scale && Math.abs(y - explosionY) < tileSize * scale) {
                if (!lastExplosion) {
                    onDamage.run();
                    lastExplosion = true;
                }
                return;
            }
        }
        lastExplosion = false;
    }
}