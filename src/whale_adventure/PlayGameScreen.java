package whale_adventure;

import javax.swing.*;
import java.awt.*;

public class PlayGameScreen extends JPanel {
    //게임 화면 제생
    //기본 참조 ID
    private static final long serialVersionUID = 1L;

    private Image background=new ImageIcon(TopClass.class.getResource("resources\\BackGround.PNG")).getImage();

    //전역 변수
    private int screenWidth, screenHeight;
    private boolean isSplash = true;
    private int successfulJumps = 0;
    private String message = "Whale Adventures";
    private Font primaryFont = new Font("BLOMBERG", Font.BOLD, 100), failFont = new Font("BLOMBERG", Font.BOLD, 56);
    private int messageWidth = 0, scoreWidth = 0;
    private BottomSeaweed bp1, bp2;
    private TopSeaweed tp1, tp2;
    private Fish fish;
    private Whale whale;

    //기본 생성자
    public PlayGameScreen(int screenWidth, int screenHeight, boolean isSplash) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.isSplash = isSplash;
    }

    //그래픽 객체와 객체를 사용하여 그림 뛰움
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0,screenWidth, screenHeight,null);

        //객체를 그리기 전 인스턴스화해
        if(bp1 != null && bp2 != null && tp1 != null && tp2 != null) {
            g.drawImage(bp1.getSeaweed(), bp1.getX(), bp1.getY(), null);
            g.drawImage(bp2.getSeaweed(), bp2.getX(), bp2.getY(), null);
            g.drawImage(tp1.getSeaweed(), tp1.getX(), tp1.getY(), null);
            g.drawImage(tp2.getSeaweed(), tp2.getX(), tp2.getY(), null);
        }

        if(fish != null) {
            g.drawImage(fish.getFish(), fish.getX(), fish.getY(), null);
        }
      if(!isSplash && whale != null) {
         g.drawImage(whale.getWhale(), whale.getX(), whale.getY(), null);
      }

        //주 글꼴이 없는 경우
        try {
            g.setFont(primaryFont);
            FontMetrics metric = g.getFontMetrics(primaryFont);
            messageWidth = metric.stringWidth(message);
            scoreWidth = metric.stringWidth(String.format("%d", successfulJumps));
        }
        catch(Exception e) {
            g.setFont(failFont);
            FontMetrics metric = g.getFontMetrics(failFont);
            messageWidth = metric.stringWidth(message);
            scoreWidth = metric.stringWidth(String.format("%d", successfulJumps));
        }

        g.drawString(message, screenWidth/2-messageWidth/2, screenHeight/3);

        if(!isSplash) {
            g.drawString(String.format("%d", successfulJumps), screenWidth/2-scoreWidth/2, 100);
        }
    }

    //PlayGameScreen의 BottomPipe 변수 값 지정
    public void setBottomSeaweed(BottomSeaweed bp1, BottomSeaweed bp2) {
        this.bp1 = bp1;
        this.bp2 = bp2;
    }

    //TopPipe 변수 값지정
    public void setTopSeaweed(TopSeaweed tp1, TopSeaweed tp2) {
        this.tp1 = tp1;
        this.tp2 = tp2;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }
    // bird 변수 값지정
    public void setWhale(Whale whale) {
        this.whale = whale;
    }

    //현재 추적 변수의 증가
    public void incrementJump() {
        successfulJumps++;
    }

    // 현재 점프 점수를 반환
    public int getScore() {
        return successfulJumps;
    }

    //메시지를 화면에 구문 분석
    public void sendText(String message) {
        this.message = message;
    }
}
