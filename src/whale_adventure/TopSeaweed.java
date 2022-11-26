package whale_adventure;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TopSeaweed {
    // global variables
    private Image topSeaweed;
    private int xLoc = 0, yLoc = 0;

    /**
     * 기본 생성자
     */
    public TopSeaweed(int initialWidth, int initialHeight) {
        topSeaweed = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources\\the_seaweed_top.png"));
        scaleTopSeaweed(initialWidth, initialHeight);
    }

    /**
     * topPipe 스프라이트를 원하는 크기로 조정
     * width topSeaweed의 너비
     * height topSeaweed의 높이
     */
    public void scaleTopSeaweed(int width, int height) {
        topSeaweed = topSeaweed.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * TopSeaweed 개체에 대한 Getter 메서드.
     */
    public Image getSeaweed() {
        return topSeaweed;
    }

    /**
     * TopPipe 객체의 너비
     */
    public int getWidth() {
        return topSeaweed.getWidth(null);
    }

    /**
     * TopPipe 객체의 높이
     */
    public int getHeight() {
        return topSeaweed.getHeight(null);
    }

    /**
     * TopPipe 객체의 x 위치를 설정
     */
    public void setX(int x) {
        xLoc = x;
    }

    /**
     * TopPipe 객체의 x 위치
     */
    public int getX() {
        return xLoc;
    }

    /**
     * TopPipe 객체의 y 위치를 가져오는 메소드
     */
    public void setY(int y) {
        yLoc = y;
    }

    /**
     * TopPipe 객체의 y 위치를 가져오는 메소드
     */
    public int getY() {
        return yLoc;
    }

    /**
     * TopPipe의 이미지 윤곽선을 나타내는 Rectangle을 획득하는 데 사용되는 방법
     * 화면에서 TopPipe의 위치를 ZWSPZWSP나타내는 @return Rectangle
     */
    public Rectangle getRectangle() {
        return (new Rectangle(xLoc, yLoc, topSeaweed.getWidth(null), topSeaweed.getHeight(null)));
    }

    /**
     * TopPipe의 이미지 객체를 나타내는 BufferedImage를 획득하는 메소드
     * @return TopPipe의 BufferedImage 객체
     */
    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(topSeaweed.getWidth(null), topSeaweed.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(topSeaweed, 0, 0, null);
        g.dispose();
        return bi;
    }
}
