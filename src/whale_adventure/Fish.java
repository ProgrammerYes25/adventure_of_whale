package whale_adventure;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fish {
    // global variables
    private Image fish;
    private int xLoc = 0, yLoc = 0;

    /**
     * 기본 생성자
     */
    public Fish(int initialWidth, int initialHeight) {
        fish = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources\\fish.png"));
        scaleFish(initialWidth, initialHeight);
    }

    /**
     * BottomPipe 스프라이트를 원하는 크기로 조정하는 방법
     * @param width 원하는 BottomPipe 너비
     * @param height 원하는 BottomPipe 높이
     */
    public void scaleFish(int width, int height) {

        fish = fish.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * BottomPipe 개체에 대한 Getter 메서드.
     * @return Image
     */
    public Image getFish() {
        return fish;
    }

    /**
     * BottomPipe 객체의 너비를 구하는 방법
     * @return int
     */
    public int getWidth() {
        return fish.getWidth(null);
    }

    /**
     * BottomPipe 객체의 높이를 구하는 방법
     * @return int
     */
    public int getHeight() {
        return fish.getHeight(null);
    }

    /**
     * BottomPipe 객체의 x 위치를 설정하는 방법
     * @param x
     */
    public void setX(int x) {
        xLoc = x;
    }

    /**
     * BottomPipe 객체의 x 위치를 가져오는 방법
     * @return int
     */
    public int getX() {
        return xLoc;
    }

    /**
     * BottomPipe 객체의 y 위치를 설정하는 방법
     * @param y
     */
    public void setY(int y) {
        yLoc = y;
    }

    /**
     * BottomPipe 객체의 y 위치를 가져오는 메소드
     * @return int
     */
    public int getY() {
        return yLoc;
    }

    /**
     * BottomPipe의 이미지 윤곽선을 나타내는 Rectangle을 획득하는 데 사용되는 메서드
     * 화면에서 BottomPipe의 위치를 ​​나타내는 @return Rectangle
     */
    public Rectangle getRectangle() {
        return (new Rectangle(xLoc, yLoc, fish.getWidth(null), fish.getHeight(null)));
    }

    /**
     * TopPipe의 이미지 객체를 나타내는 BufferedImage를 획득하는 메소드
     * @return TopPipe의 BufferedImage 객체
     */
    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(fish.getWidth(null), fish.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        return bi;
    }
}
