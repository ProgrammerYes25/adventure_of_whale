package whale_adventure;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;
public class BottomSeaweed {
    private Image bottomSeaweed;
    private int xLoc = 0, yLoc = 0;

    public BottomSeaweed(int initialWidth, int initialHeight) {
        bottomSeaweed = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/tube_bottom.png"));
        scaleBottomSeaweed(initialWidth, initialHeight);
    }

    public void scaleBottomSeaweed(int width, int height) {
        bottomSeaweed = bottomSeaweed.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getSeaweed() {
        return bottomSeaweed;
    }

    public int getWidth() {
        return bottomSeaweed.getWidth(null);
    }

    public int getHeight() {
        return bottomSeaweed.getHeight(null);
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
        return (new Rectangle(xLoc, yLoc, bottomSeaweed.getWidth(null), bottomSeaweed.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(bottomSeaweed.getWidth(null), bottomSeaweed.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(bottomSeaweed, 0, 0, null);
        g.dispose();
        return bi;
    }
}
