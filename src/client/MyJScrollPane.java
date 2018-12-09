package client;

import javax.swing.*;
import java.awt.*;

public class MyJScrollPane extends JScrollPane {
    public volatile boolean adjust;

    public MyJScrollPane() {
        adjust = false;
    }

    public MyJScrollPane(Component view) {
        super(view);
        adjust = false;
    }
}