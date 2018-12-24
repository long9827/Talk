package AWT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame {


    public Test() {
        setSize(500, 400);
        setLocationRelativeTo(null);    //居中
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        JDialog progressDialog;
        progressDialog = new JDialog(this, "进度条");
        Container container = progressDialog.getContentPane();
        progressDialog.setBounds(0, 0, 300, 150);
        progressDialog.setLocationRelativeTo(null);
        progressDialog.setModal(true);
        progressDialog.setResizable(false);
        progressDialog.setLayout(new BorderLayout());
        container.add(progressBar, BorderLayout.CENTER);
        progressBar.setValue(50);
        new ProgressThread(progressBar).start();

        progressDialog.setVisible(true);
    }

    public static void main(String[] args) {
        new Test().setVisible(true);
    }
}

class ProgressThread extends Thread {

    JProgressBar progressBar;

    public ProgressThread(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        for (int i=1; i<90; ++i) {
            try {
                sleep(1000);
                progressBar.setValue(i);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}