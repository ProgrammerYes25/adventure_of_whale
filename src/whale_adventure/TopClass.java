package whale_adventure;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

public class TopClass implements ActionListener, KeyListener {

    //전역 상수
    //객체 높낮이
    //Toolkit.getDefaultToolkit()   //Toolkit 구현된 객체 가져온다
    //.getScreenSize()   //화면 크기를 구성한다.
    //.getWidth(), .getHeight() // 객체에 따른 사이즈를 반환
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();   //넓이 구성 코드
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();   //높이 구성 코드
    //파이프 사이의 거리(단위 : px)
    private static final int SEAWEED_GAP = SCREEN_HEIGHT / 4;
    //파이프 크기(단위 : px)
    private static final int SEAWEED_WIDTH = SCREEN_WIDTH / 6, SEAWEED_HEIGHT = 3 * SEAWEED_WIDTH;
    //고래크기(단위 : px)
    private static final int WHALE_WIDTH = 140, WHALE_HEIGHT = 120;
    //물고기 크기
    private static final int FISH_WIDTH = 90, FISH_HEIGHT = 90;
    //업데이트 간격(ms)
    private static final int UPDATE_DIFFERENCE = 18;
    //업데이트할 때마다 파이프가 이동하는 거리
    private static final int X_MOVEMENT_DIFFERENCE = 8;
    //로드 시간이 길기 때문에 파이프가 화면 중간에 팝업됩니다.
    private static final int SCREEN_DELAY = 300;
    //고래의 위치 x(전진)
    private static final int WHALE_X_LOCATION = SCREEN_WIDTH / 7;
    //고래가 이동할때 얼만큼 움직일지
    //WHALE_JUMP_DIFF : 점프 높이
    //WHALE_FALL_DIFF : 떨어지는 량(속도)
    //WHALE_JUMP_HEIGHT : 앞으로 이동
    private static final int WHALE_JUMP_DIFF = 15, WHALE_FALL_DIFF = WHALE_JUMP_DIFF / 2, WHALE_JUMP_HEIGHT = SEAWEED_GAP - WHALE_HEIGHT - WHALE_JUMP_DIFF * 2;

    //지역 변수
    //false -> 파이프 루프 실행 중 X, true -> 파이프 루프 실행중O
    private boolean loopVar = true;
    //false -> 게임플레이 중 X, true -> 게임플레이 중O
    private boolean gamePlay = false;
    //false -> 키를 눌러 새를 이동 X, true -> 키를 눌러 새를 이동O
    private boolean whaleThrust = false;
    //false -> 점프 키를 한번 더 누름  X, true -> 점프 키를 한번 더 누름 O
    private boolean whaleFired = false;
    //스페이스 바 해제, true로 시작하여 레지스터를 처음 누릅니다.
    private boolean released = true;
    //새의 위치 y(점프)
    private int whaleYTracker = SCREEN_HEIGHT / 2 - WHALE_HEIGHT;
    //빌드 완료 객체
    private Object buildComplete = new Object();

    //지역 스윙 객체
    private JFrame f = new JFrame("Whale Adventures");//프라임 생성 타이틀은 "Whale Adventures"
    private JButton startGame, restartGame;//게임 시작 버튼
    private JPanel topPanel; //declared globally to accommodate the repaint operation and allow for removeAll(), etc.

    //지역 그리드 객체
    private static TopClass tc = new TopClass();
    //게임을 시작할 때 움직이는 배경이 있는 패널
    private static PlayGameScreen pgs;
    private String url = "jdbc:mysql://localhost/AdaventureOfWhaleDB?serverTimezone=UTC";
    private String userName = "root";
    private String password = "mirim";

    private int Life = 1, score = 0;
    private String name = null;

    private int seaweedCount = 0, crashCount=1;

    //기본 생성자
    public TopClass() {

    }

    //메인메소드
    public static void main(String[] args) {
        DBHelper db = new DBHelper();
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
        f.setMinimumSize(new Dimension(SCREEN_WIDTH * 1 / 4, SCREEN_HEIGHT * 1 / 4));
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
        startGame = new JButton("Start");
        startGame.setHorizontalAlignment(JButton.CENTER);

        //버튼 색깔 설정
        startGame.setBackground(Color.darkGray);
        startGame.setForeground(Color.white);
        //포커스 되지 않게 설정
        startGame.setFocusable(false);
        //버튼 속 글씨 설정
        startGame.setFont(new Font("BLOMBERG", Font.BOLD, 45));
        //버튼 위치 설정
        startGame.setAlignmentX(0.5f); //가로(X)
        startGame.setAlignmentY(0.6f); //세로(O)
        //현제 리스너의 리벤트 실행
        startGame.addActionListener(this);
        topPanel.add(startGame);//startGame 패널 추가
        setRestartGame();
        pgs = new PlayGameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true); //pgs 페널 설정
        topPanel.add(pgs);   //topPanel 패널 추가


        return topPanel;// topPanel 반환
    }

    private void setRestartGame() {
        //게임 다시 시작 버튼
        restartGame = new JButton("Replay");
        restartGame.setHorizontalAlignment(JButton.CENTER);

        //버튼 색깔 설정
        restartGame.setBackground(Color.darkGray);
        restartGame.setForeground(Color.white);
        //포커스 되지 않게 설정
        restartGame.setFocusable(false);
        //버튼 속 글씨 설정
        restartGame.setFont(new Font("BLOMBERG", Font.BOLD, 45));
        //버튼 위치 설정
        restartGame.setAlignmentX(0.5f); //가로(X)
        restartGame.setAlignmentY(0.6f); //세로(O)
        //현제 리스너의 리벤트 실행;
    }


    //작업 이벤트 구현
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startGame) {
            //startGame버튼을 클릭 했을 때 스크린 멈추고
            name = JOptionPane.showInputDialog("이름을 입력하세요.");
            System.out.println(name);
            if (!(name.equals(null))) {
                loopVar = false;
                fadeOperation();   //호출
            }
        } else if (e.getSource() == restartGame) {
            buildFrame();
        } else if (e.getSource() == buildComplete) {
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gamePlay == true && released == true) {
            //새 위치 이동
            if(crashCount!=0){
                crashCount--;
            }
            if (whaleThrust) { //더블 점프 여부
                whaleFired = true;
            }
            whaleThrust = true;
            released = false;
        } else if (e.getKeyCode() == KeyEvent.VK_B && gamePlay == false) {
            whaleYTracker = SCREEN_HEIGHT / 2 - WHALE_HEIGHT; //새 위치 재설정
            whaleThrust = false; //점프키 리셋 (점프가 밀려서 발생하는 오류 방지)
            actionPerformed(new ActionEvent(startGame, -1, ""));
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            released = true;
        }
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

                while (temp.getBackground().getAlpha() != 255) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE / 2) {
                        if (alpha < 255 - 10) {
                            alpha += 10;
                        } else {
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

                while (temp.getBackground().getAlpha() != 0) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE / 2) {
                        if (alpha > 10) {
                            alpha -= 10;
                        } else {
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
        BottomSeaweed bp1 = new BottomSeaweed(SEAWEED_WIDTH, SEAWEED_HEIGHT);
        BottomSeaweed bp2 = new BottomSeaweed(SEAWEED_WIDTH, SEAWEED_HEIGHT);
        TopSeaweed tp1 = new TopSeaweed(SEAWEED_WIDTH, SEAWEED_HEIGHT);
        TopSeaweed tp2 = new TopSeaweed(SEAWEED_WIDTH, SEAWEED_HEIGHT);
        Whale whale = new Whale(WHALE_WIDTH, WHALE_HEIGHT);
        Fish fish = new Fish(FISH_WIDTH, FISH_HEIGHT);

        //바닥 파이프의 x 및 y 이미지 위치를 추적하는 변수
        int xLoc1 = SCREEN_WIDTH + SCREEN_DELAY, xLoc2 = (int) ((double) 3.0 / 2.0 * SCREEN_WIDTH + SEAWEED_WIDTH / 2.0) + SCREEN_DELAY;
        int yLoc1 = bottomPipeLoc(), yLoc2 = bottomPipeLoc();
        int fishx = xLoc1 + FISH_WIDTH * 4;
        int fishy = yLoc1 - FISH_HEIGHT;
        int birdX = WHALE_X_LOCATION, birdY = whaleYTracker;

        //루프 시작 시간을 유지할 변수
        long startTime = System.currentTimeMillis();

        while (loopVar) {
            if ((System.currentTimeMillis() - startTime) > UPDATE_DIFFERENCE) {
                //파이프 세트가 화면을 떠났는지 확인
                //있는 경우 파이프의 X 위치를 재설정하고 새 Y 위치를 할당
                if (xLoc1 < (0 - SEAWEED_WIDTH)) {
                    xLoc1 = SCREEN_WIDTH;
                    yLoc1 = bottomPipeLoc();
                    seaweedCount++;
                } else if (xLoc2 < (0 - SEAWEED_WIDTH)) {
                    xLoc2 = SCREEN_WIDTH;
                    yLoc2 = bottomPipeLoc();
                    seaweedCount++;
                }
                if (fishx < (0 - SEAWEED_WIDTH) && seaweedCount==3) {
                    fishx = xLoc1 + FISH_WIDTH * 4;
                    fishy = yLoc1 - FISH_HEIGHT;
                    seaweedCount=0;
                }


                //파이프 위치를 미리 정해진 양만큼 줄임
                xLoc1 -= X_MOVEMENT_DIFFERENCE;
                xLoc2 -= X_MOVEMENT_DIFFERENCE;
                if (fishx >= (0 - SEAWEED_WIDTH)) {
                    fishx -= X_MOVEMENT_DIFFERENCE;
                }

                if (whaleFired && !isSplash) {
                    whaleYTracker = birdY;
                    whaleFired = false;
                }

                if (whaleThrust && !isSplash) {
                    //새 수직 이동
                    if (whaleYTracker - birdY - WHALE_JUMP_DIFF < WHALE_JUMP_HEIGHT) {
                        if (birdY - WHALE_JUMP_DIFF > 0) {
                            birdY -= WHALE_JUMP_DIFF; //coordinates different
                        } else {
                            birdY = 0;
                            whaleYTracker = birdY;
                            whaleThrust = false;
                        }
                    } else {
                        whaleYTracker = birdY;
                        whaleThrust = false;
                    }
                } else if (!isSplash) {
                    birdY += WHALE_FALL_DIFF;
                    whaleYTracker = birdY;
                }

                //BottomPipe 및 TopPipe 위치 업데이트
                bp1.setX(xLoc1);
                bp1.setY(yLoc1);
                bp2.setX(xLoc2);
                bp2.setY(yLoc2);
                tp1.setX(xLoc1);
                tp1.setY(yLoc1 - SEAWEED_GAP - SEAWEED_HEIGHT);
                tp2.setX(xLoc2);
                tp2.setY(yLoc2 - SEAWEED_GAP - SEAWEED_HEIGHT);
                fish.setX(fishx);
                fish.setY(fishy);


                if (!isSplash) {
                    whale.setX(birdX);
                    whale.setY(birdY);
                    pgs.setWhale(whale);
                }

                //지역 변수를 구문 분석하여 PlayGameScreen에서 BottomPipe 및 TopPipe 지역 변수를 설정
                pgs.setBottomSeaweed(bp1, bp2);
                pgs.setTopSeaweed(tp1, tp2);
                pgs.setFish(fish);


//               //새가 화면에 나타나지 않는 오류 & 충돌 했을 시 해결을 위한 코드
                if (!isSplash && whale.getWidth() != -1) {
                    collisionDetection(bp1, bp2, tp1, tp2, whale);   //파이프나 바닦에 부딪혔는지 확인
                    updateScore(bp1, bp2, whale);   // 파이프 통과 했을 때하는
                    addLife(whale.getRectangle(), fish.getRectangle(), whale.getBI(), fish.getBI(), fish);
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
        while (temp <= SEAWEED_GAP + 50 || temp >= SCREEN_HEIGHT - SEAWEED_GAP) {
            temp = (int) ((double) Math.random() * ((double) SCREEN_HEIGHT));
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
        if (bp1.getX() + SEAWEED_WIDTH < bird.getX() && bp1.getX() + SEAWEED_WIDTH > bird.getX() - X_MOVEMENT_DIFFERENCE) {
            pgs.incrementJump();
            score++;
        } else if (bp2.getX() + SEAWEED_WIDTH < bird.getX() && bp2.getX() + SEAWEED_WIDTH > bird.getX() - X_MOVEMENT_DIFFERENCE) {
            pgs.incrementJump();
            score++;
        }
    }

    private void addLife(Rectangle r1, Rectangle r2, BufferedImage b1, BufferedImage b2, Fish fish) {
        if (r1.intersects(r2)) {
            Rectangle r = r1.intersection(r2);

            int firstI = (int) (r.getMinX() - r1.getMinX());
            int firstJ = (int) (r.getMinY() - r1.getMinY());
            int bp1XHelper = (int) (r1.getMinX() - r2.getMinX());
            int bp1YHelper = (int) (r1.getMinY() - r2.getMinY());
            //충돌 확인
            if(seaweedCount==0) {
                int crash = 1;
                for (int i = firstI; i < r.getWidth() + firstI; i++) {
                    for (int j = firstJ; j < r.getHeight() + firstJ; j++) {
                        if ((b1.getRGB(i, j) & 0xFF000000) != 0x00 && (b2.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000) != 0x00) {
                            crash--;
                            break;
                        }
                    }
                    if (crash == 0) {
                        fish.setX((0 - SEAWEED_WIDTH) + 1);
                        seaweedCount=1;
                        pgs.setFish(fish);
                        pgs.incrementLife();
                        break;
                    }
                }
            }
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

        if (whale.getY() + WHALE_HEIGHT > SCREEN_HEIGHT - 50) { //바닦에 부딪혔을 때
            restart();
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
        if (r1.intersects(r2)) {
            Rectangle r = r1.intersection(r2);

            int firstI = (int) (r.getMinX() - r1.getMinX());
            int firstJ = (int) (r.getMinY() - r1.getMinY());
            int bp1XHelper = (int) (r1.getMinX() - r2.getMinX());
            int bp1YHelper = (int) (r1.getMinY() - r2.getMinY());
            //충돌 확인
            int crash = 1;
            for (int i = firstI; i < r.getWidth() + firstI; i++) {
                for (int j = firstJ; j < r.getHeight() + firstJ; j++) {
                    if ((b1.getRGB(i, j) & 0xFF000000) != 0x00 && (b2.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000) != 0x00) {
                        crash--;
                        break;
                    }
                }
                if (crash == 0 && crashCount ==0) {
                    crashCount = 2;
                    pgs.reductionLife();
                    if(pgs.life==0){
                        restart();
                    }
                    break;
                }
            }
        }
    }

    private void restart() {
        pgs.sendText("Game Over");
        crashCount=1;
        restartGame.addActionListener(this);
        topPanel.add(restartGame);
        loopVar = false; //충돌시 루프를 멈추고
        gamePlay = false; //게임도 멈춰지게 됨 (여기에 스코어랑 다시시작이랑 홈으로 돌아가는 코드 필요)
        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(" INSERT INTO user_scoer_tabel(user_name,user_scoer)  VALUES ('" + name + "', '" + score + "')");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


}