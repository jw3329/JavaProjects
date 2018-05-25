import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiPlayer {
    private JPanel panel1;
    private JButton play_pause;
    private JButton stop;
    private JButton openFile;

    static String musicPath = "C:\\Users\\Jun Won\\Documents\\testingMusic.mp3";

    public GuiPlayer() {
        setAllButtons();
    }

    private void setAllButtons() {
        setPlayPauseButton();
        setStopButton();
        setOpenFileButton();
    }

    private void setOpenFileButton() {

    }

    private void setStopButton() {

    }

    private void setPlayPauseButton() {

    }

    public static void main(String[] args) throws FileNotFoundException, JavaLayerException {

        FileInputStream fileInputStream = new FileInputStream(musicPath);
        Player player = new Player(fileInputStream);

        player.play();

    }
}
