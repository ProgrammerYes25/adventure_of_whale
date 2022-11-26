package whale_adventure;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.annotation.Generated;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class TopClass implements ActionListener, KeyListener {

    //전역 상수
    //객체 높낮이
    //Toolkit.getDefaultToolkit()   //Toolkit 구현된 객체 가져온다
    //.getScreenSize()   //화면 크기를 구성한다.
    //.getWidth(), .getHeight() // 객체에 따른 사이즈를 반환
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();   //넓이 구성 코드
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();   //높이 구성 코드
    //파이프 사이의 거리(단위 : px)
    private static final int PIPE_GAP = SCREEN_HEIGHT/5;
    //파이프 크기(단위 : px)
    private static final int PIPE_WIDTH = SCREEN_WIDTH/8, PIPE_HEIGHT = 4*PIPE_WIDTH;
    //새크기(단위 : px)
    private static final int BIRD_WIDTH = 120, BIRD_HEIGHT = 75;
    //물고기 크기
    private static final int FISH_WIDTH = 50, FISH_HEIGHT = 50;
    //업데이트 간격(ms)
    private static final int UPDATE_DIFFERENCE = 25;
    //업데이트할 때마다 파이프가 이동하는 거리
    private static final int X_MOVEMENT_DIFFERENCE = 5;
    //로드 시간이 길기 때문에 파이프가 화면 중간에 팝업됩니다.
    private static final int SCREEN_DELAY = 300;
    //새의 위치 x(전진)
    private static final int BIRD_X_LOCATION = SCREEN_WIDTH/7;
    //새가 이동할때 얼만큼 움직일지
    //BIRD_JUMP_DIFF : 점프 높이
    //BIRD_FALL_DIFF : 떨어지는 량(속도)
    //BIRD_JUMP_HEIGHT : 앞으로 이동
    private static final int BIRD_JUMP_DIFF = 10, BIRD_FALL_DIFF = BIRD_JUMP_DIFF/2, BIRD_JUMP_HEIGHT = PIPE_GAP - BIRD_HEIGHT - BIRD_JUMP_DIFF*2;

    //지역 변수
    //false -> 파이프 루프 실행 중 X, true -> 파이프 루프 실행중O
    private boolean loopVar = true;
    //false -> 게임플레이 중 X, true -> 게임플레이 중O
    private boolean gamePlay = false;
    //false -> 키를 눌러 새를 이동 X, true -> 키를 눌러 새를 이동O
    private boolean birdThrust = false;
    //false -> 점프 키를 한번 더 누름  X, true -> 점프 키를 한번 더 누름 O
    private boolean birdFired = false;
    //스페이스 바 해제, true로 시작하여 레지스터를 처음 누릅니다.
    private boolean released = true;
    //새의 위치 y(점프)
    private int birdYTracker = SCREEN_HEIGHT/2 - BIRD_HEIGHT;
    //빌드 완료 객체
    private Object buildComplete = new Object();

    //지역 스윙 객체
    private JFrame f = new JFrame("Flappy Bird Redux");//프라임 생성 타이틀은 "Flappy Bird Redux"
    private JButton startGame;//게임 시작 버튼
    private JPanel topPanel; //declared globally to accommodate the repaint operation and allow for removeAll(), etc.

    //지역 그리드 객체
    private static TopClass tc = new TopClass();
    //게임을 시작할 때 움직이는 배경이 있는 패널
    private static PlayGameScreen pgs;

    //기본 생성자
    public TopClass() {

    }

    //메인메소드
    public static void main(String[] args) {
        //GUI구축
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tc.buildFrame();

                //게임이 실행되는 동안 GUI의 응답성을 유지하기 위해 새 스레드를 만듭니다.
                Thread t = new Thread() {
                    public void run() {
                        tc.gameScreen(true);
                    }
                };
                t.start();
            }
        });
    }

    //JFrame을 구성하고 프로그램 내용을 추가
    private void buildFrame() {
        Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources\\whale.PNG"));
        //프레임에 컴포넌트 팬을 만들어서 넣음
        f.setContentPane(createContentPane());
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setAlwaysOnTop(false);
        f.setVisible(true);
        f.setMinimumSize(new Dimension(SCREEN_WIDTH*1/4, SCREEN_HEIGHT*1/4));
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setIconImage(icon);
        f.addKeyListener(this);
    }
    //컴포너트팬 만드는 메소드
    private JPanel createContentPane() {
        topPanel = new JPanel(); //top-most JPanel in layout hierarchy
        //배경색 정함
        topPanel.setBackground(Color.BLACK);
        //패널을 레이어드할 수 있도록 허용
        LayoutManager overlay = new OverlayLayout(topPanel);
        topPanel.setLayout(overlay);

        //게임 시작 버튼
        startGame = new JButton("Start Playing!");
        //버튼 색깔 설정
        startGame.setBackground(Color.BLUE);
        startGame.setForeground(Color.WHITE);
        //포커스 되지 않게 설정
        startGame.setFocusable(false);
        //버튼 속 글씨 설정
        startGame.setFont(new Font("Calibri", Font.BOLD, 42));
        //버튼 위치 설정
        startGame.setAlignmentX(0.5f); //가로(X)
        startGame.setAlignmentY(0.5f); //세로(O)
        //현제 리스너의 리벤트 실행
        startGame.addActionListener(this);
        topPanel.add(startGame);//startGame 패널 추가

        pgs = new PlayGameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true); //pgs 페널 설정
        topPanel.add(pgs);   //topPanel 패널 추가

        return topPanel;// topPanel 반환
    }

    //작업 이벤트 구현
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startGame) {
            //startGame버튼을 클릭 했을 때 스크린 멈추고
            loopVar = false;
            fadeOperation();   //호출
        }
        else if(e.getSource() == buildComplete) {
            //빌드가 완료 되었을때 파이프 루프 돌리고  게임 플레이 중으로 설정 이동 수행 함
            Thread t = new Thread() {
                public void run() {
                    loopVar = true;
                    gamePlay = true;
                    tc.gameScreen(false);
                }
            };
            t.start(); //스레드 실행
        }
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE && gamePlay == true && released == true){
            //새 위치 이동
            if(birdThrust) { //더블 점프 여부
                birdFired = true;
            }
            birdThrust = true;
            released = false;
        }
        else if(e.getKeyCode() == KeyEvent.VK_B && gamePlay == false) {
            birdYTracker = SCREEN_HEIGHT/2 - BIRD_HEIGHT; //새 위치 재설정
            birdThrust = false; //점프키 리셋 (점프가 밀려서 발생하는 오류 방지)
            actionPerformed(new ActionEvent(startGame, -1, ""));
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            released = true;
        }
    }

    public void keyTyped1(KeyEvent e) {

    }

    //라운드 시작 전에 수행되는 페이드 작업 수행
    private void fadeOperation() {
        Thread t = new Thread() {
            public void run() {
                topPanel.remove(startGame);
                topPanel.remove(pgs);
                topPanel.revalidate();
                topPanel.repaint();

                //패널 페이드
                JPanel temp = new JPanel();
                int alpha = 0; //알파 채널 변수
                temp.setBackground(new Color(0, 0, 0, alpha)); //투명한 검은색 패널
                topPanel.add(temp);   //페널에 추가
                topPanel.add(pgs);   //게임 화면 추가
                topPanel.revalidate();   //새로 고침
                topPanel.repaint();      //한번더 호출

                long currentTime = System.currentTimeMillis();

                while(temp.getBackground().getAlpha() != 255) {
                    if((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE/2) {
                        if(alpha < 255 - 10) {
                            alpha += 10;
                        }
                        else {
                            alpha = 255;
                        }

                        temp.setBackground(new Color(0, 0, 0, alpha));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }

                topPanel.removeAll();
                topPanel.add(temp);
                pgs = new PlayGameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, false);
                pgs.sendText(""); //remove title text
                topPanel.add(pgs);

                while(temp.getBackground().getAlpha() != 0) {
                    if((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE/2) {
                        if(alpha > 10) {
                            alpha -= 10;
                        }
                        else {
                            alpha = 0;
                        }

                        temp.setBackground(new Color(0, 0, 0, alpha));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }

                actionPerformed(new ActionEvent(buildComplete, -1, "Build Finished"));
            }
        };

        t.start();
    }

    //화면 이동 수행 메소드
    private void gameScreen(boolean isSplash) {
        BottomSeaweed bp1 = new BottomSeaweed(PIPE_WIDTH, PIPE_HEIGHT);
        BottomSeaweed bp2 = new BottomSeaweed(PIPE_WIDTH, PIPE_HEIGHT);
        TopSeaweed tp1 = new TopSeaweed(PIPE_WIDTH, PIPE_HEIGHT);
        TopSeaweed tp2 = new TopSeaweed(PIPE_WIDTH, PIPE_HEIGHT);
        Whale whale = new Whale(BIRD_WIDTH, BIRD_HEIGHT);
        Fish fish = new Fish(FISH_WIDTH, FISH_HEIGHT);

        //바닥 파이프의 x 및 y 이미지 위치를 추적하는 변수
        int xLoc1 = SCREEN_WIDTH+SCREEN_DELAY, xLoc2 = (int) ((double) 3.0/2.0*SCREEN_WIDTH+PIPE_WIDTH/2.0)+SCREEN_DELAY;
        int yLoc1 = bottomPipeLoc(), yLoc2 = bottomPipeLoc();
        int birdX = BIRD_X_LOCATION, birdY = birdYTracker;

        //루프 시작 시간을 유지할 변수
        long startTime = System.currentTimeMillis();

        while(loopVar) {
            if((System.currentTimeMillis() - startTime) > UPDATE_DIFFERENCE) {
                //파이프 세트가 화면을 떠났는지 확인
                //있는 경우 파이프의 X 위치를 재설정하고 새 Y 위치를 할당
                if(xLoc1 < (0-PIPE_WIDTH)) {
                    xLoc1 = SCREEN_WIDTH;
                    yLoc1 = bottomPipeLoc();
                }
                else if(xLoc2 < (0-PIPE_WIDTH)) {
                    xLoc2 = SCREEN_WIDTH;
                    yLoc2 = bottomPipeLoc();
                }

                //파이프 위치를 미리 정해진 양만큼 줄임
                xLoc1 -= X_MOVEMENT_DIFFERENCE;
                xLoc2 -= X_MOVEMENT_DIFFERENCE;

                if(birdFired && !isSplash) {
                    birdYTracker = birdY;
                    birdFired = false;
                }

                if(birdThrust && !isSplash) {
                    //새 수직 이동
                    if(birdYTracker - birdY - BIRD_JUMP_DIFF < BIRD_JUMP_HEIGHT) {
                        if(birdY - BIRD_JUMP_DIFF > 0) {
                            birdY -= BIRD_JUMP_DIFF; //coordinates different
                        }
                        else {
                            birdY = 0;
                            birdYTracker = birdY;
                            birdThrust = false;
                        }
                    }
                    else {
                        birdYTracker = birdY;
                        birdThrust = false;
                    }
                }
                else if(!isSplash) {
                    birdY += BIRD_FALL_DIFF;
                    birdYTracker = birdY;
                }

                //BottomPipe 및 TopPipe 위치 업데이트
	            bp1.setX(xLoc1);
	            bp1.setY(yLoc1);
	            bp2.setX(xLoc2);
	            bp2.setY(yLoc2);
                tp1.setX(xLoc1);
                tp1.setY(yLoc1-PIPE_GAP-PIPE_HEIGHT);
                tp2.setX(xLoc2);
                tp2.setY(yLoc2-PIPE_GAP-PIPE_HEIGHT);
                fish.setX(xLoc1);
                fish.setX(xLoc2);

	            if(!isSplash) {
	               whale.setX(birdX);
	               whale.setY(birdY);
	               pgs.setBird(whale);
	            }

                //지역 변수를 구문 분석하여 PlayGameScreen에서 BottomPipe 및 TopPipe 지역 변수를 설정
	            pgs.setBottomPipe(bp1, bp2);
                pgs.setTopPipe(tp1, tp2);
                pgs.setFish(fish);

//	            //새가 화면에 나타나지 않는 오류 & 충돌 했을 시 해결을 위한 코드
	            if(!isSplash && whale.getWidth() != -1) {
	               collisionDetection(bp1, bp2, tp1, tp2, whale);   //파이프나 바닦에 부딪혔는지 확인
	               updateScore(bp1, bp2, whale);   // 파이프 통과 했을 때하는
	            }

                //pgs의 패널 업데이트
                topPanel.revalidate();
                topPanel.repaint();

                //모든 작업이 완료된 후 시간 간격 변수 업데이트
                startTime = System.currentTimeMillis();
            }
        }
    }


    //파이프 위치 계산
    private int bottomPipeLoc() {
        int temp = 0;
        //두 파이프를 모두 화면에 표시 할수 있을 때까지 반복
        while(temp <= PIPE_GAP+50 || temp >= SCREEN_HEIGHT-PIPE_GAP) {
            temp = (int) ((double) Math.random()*((double)SCREEN_HEIGHT));
        }
        return temp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }


	   /*
	    * @param bp1 -> BottomPipe 개체
	    * @param bp2 -> BottomPipe 개체
	    * @param Bird 객체
	    */
	   // 점수 추가 하는 메소드
	   private void updateScore(BottomSeaweed bp1, BottomSeaweed bp2, Whale bird) {
	      if(bp1.getX() + PIPE_WIDTH < bird.getX() && bp1.getX() + PIPE_WIDTH > bird.getX() - X_MOVEMENT_DIFFERENCE) {
	         pgs.incrementJump();
	      }
	      else if(bp2.getX() + PIPE_WIDTH < bird.getX() && bp2.getX() + PIPE_WIDTH > bird.getX() - X_MOVEMENT_DIFFERENCE) {
	         pgs.incrementJump();
	      }
	   }

	   /*
	    * @param bp1 -> BottomPipe 개체
	    * @param bp2 -> BottomPipe 개체
	    * @param tp1 -> TopPipe 객체
	    * @param tp2 -> TopPipe 객체
	    * @param Bird 객체
	    */
	   //충돌이 발생했는지 여부를 테스트
	   private void collisionDetection(BottomSeaweed bp1, BottomSeaweed bp2, TopSeaweed tp1, TopSeaweed tp2, Whale whale) {
	      collisionHelper(whale.getRectangle(), bp1.getRectangle(), whale.getBI(), bp1.getBI());
	      collisionHelper(whale.getRectangle(), bp2.getRectangle(), whale.getBI(), bp2.getBI());
	      collisionHelper(whale.getRectangle(), tp1.getRectangle(), whale.getBI(), tp1.getBI());
	      collisionHelper(whale.getRectangle(), tp2.getRectangle(), whale.getBI(), tp2.getBI());

	      if(whale.getY() + BIRD_HEIGHT > SCREEN_HEIGHT*7/8) { //바닦에 부딪혔을 때
	         pgs.sendText("Game Over");
	         loopVar = false;   //충돌시 루프를 멈추고
	         gamePlay = false;    //게임도 멈춰지게 됨 (여기에 스코어랑 다시시작이랑 홈으로 돌아가는 코드 필요)
	      }
	   }

	   /*
	   * @paramr1 The Bird's 직사각형 구성요소
	   * @paramr2 충돌 구성 요소 직사각형
	   * @param b1 Bird's Buffered Image 구성 요소
	   * @param b2 충돌 구성 요소 버퍼링된 이미지
	    */
	   //pipe와 Bird 충돌을 테스트
	   private void collisionHelper(Rectangle r1, Rectangle r2, BufferedImage b1, BufferedImage b2) {
	      if(r1.intersects(r2)) {
	         Rectangle r = r1.intersection(r2);

	         int firstI = (int) (r.getMinX() - r1.getMinX());
	         int firstJ = (int) (r.getMinY() - r1.getMinY());
	         int bp1XHelper = (int) (r1.getMinX() - r2.getMinX());
	         int bp1YHelper = (int) (r1.getMinY() - r2.getMinY());
	         //충돌 확인
	         for(int i = firstI; i < r.getWidth() + firstI; i++) {
	            for(int j = firstJ; j < r.getHeight() + firstJ; j++) {
	               if((b1.getRGB(i, j) & 0xFF000000) != 0x00 && (b2.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000) != 0x00) {
	                  pgs.sendText("Game Over");
	                  loopVar = false; //충돌시 루프를 멈추고
	                  gamePlay = false; //게임도 멈춰지게 됨 (여기에 스코어랑 다시시작이랑 홈으로 돌아가는 코드 필요)
	                  break;
	               }
	            }
	         }
	      }
	   }


}
