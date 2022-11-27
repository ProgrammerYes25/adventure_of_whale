package whale_adventure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class scoreDialog extends JDialog {
    private JTextArea scoreTest = new JTextArea(10, 10);
    private JButton okButton = new JButton("확인");
    private Frame f = new JFrame();
    public scoreDialog(JFrame frame, String title){
        super(frame, title, true);
        add(scoreTest, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);
        setLocation(500, 200);
        scoreTest.setSize(200, 200);
        scoreTest.setEditable(false);
        setSize(300, 300);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }
    public void setScoreTest(String score){
        scoreTest.setText(score);

    }
}
