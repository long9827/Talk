package AWT;

import java.awt.*;

public class FileDialogTest {
    public static void main(String[] args) {
        Frame frame = new Frame();
        FileDialog fileDialog = new FileDialog(frame, "Opem Files", FileDialog.SAVE);
        frame.setVisible(true);
        fileDialog.setVisible(true);
    }
}
