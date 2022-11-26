package whale_adventure;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;
public class Whale {
    private Image flappyWhale;
    private int xLoc = 0, yLoc = 0;

    public Whale(int initialWidth, int initialHeight) {
        flappyWhale = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources\\whale.png"));
        scaleWhale(initialWidth, initialHeight);
    }

    public void scaleWhale(int width, int height) {
        flappyWhale = flappyWhale.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getWhale() {
        return flappyWhale;
    }

    public int getWidth() {
        try {
            return flappyWhale.getWidth(null);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getHeight() {
        try {
            return flappyWhale.getHeight(null);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setX(int x) {
        xLoc = x;
    }

    public int getX() {
        return xLoc;
    }

    public void setY(int y) {
        yLoc = y;
    }

    public int getY() {
        return yLoc;
    }

    public Rectangle getRectangle() {
        return (new Rectangle(xLoc, yLoc, flappyWhale.getWidth(null), flappyWhale.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(flappyWhale.getWidth(null), flappyWhale.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(flappyWhale, 0, 0, null);
        g.dispose();
        return bi;
    }
}