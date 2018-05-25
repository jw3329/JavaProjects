package musicplayergui;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gui implements ActionListener {

    JButton play = new JButton("Play");
    JButton pause = new JButton("Pause");
    JButton stop = new JButton("Stop");

    JFrame frame;
    JPanel panel;

    String musicPath = "C:\\Users\\Jun Won\\Documents\\testingMusic.mp3";

    FileInputStream finputStream;
    int totalSongLength;
    int pausedLocation;

    Player player;
    boolean isPause = false;
    boolean isCurrentlyPlaying = false;

    List<File> fileLists;

    public Gui() {
        setPanel();
        setFrame();
        setButtonOnclickListener();
        setMenuBar();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void setMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Open");
        menu.setMnemonic(KeyEvent.VK_O);
//        menu.addMenuListener();
        JMenuItem openFile = new JMenuItem("Open as file");
        JMenuItem openFolder = new JMenuItem("Open as folder");

        menu.add(openFile);
        menu.add(openFolder);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        openFile.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setMultiSelectionEnabled(true);
            int retVal = jfc.showOpenDialog(null);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                File[] selectedFile = jfc.getSelectedFiles();
                fileLists = new ArrayList<>(Arrays.asList(selectedFile));
                for(int i=0;i<fileLists.size();i++) {
                    System.out.println(fileLists.get(i).getAbsolutePath());
                }
            }
        });
        openFolder.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int retVal = jfc.showOpenDialog(null);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                getEveryContentsOfFolder(selectedFile.getAbsolutePath());
            }
        });
    }

    private void getEveryContentsOfFolder(String name) {

    }

    private void setButtonOnclickListener() {
        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);
    }

    private void setFrame() {
        frame = new JFrame("Junwon's Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setPanel() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(1,3,5,5));
        panel.add(play);
        panel.add(pause);
        panel.add(stop);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == play) {
            try {
                playMusic();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JavaLayerException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource() == pause) {
            try {
                if(!isPause) pauseMusic();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource() == stop) {
            stopMusic();
        }
    }

    private void stopMusic() {
        isPause = false;
        isCurrentlyPlaying = false;
        pausedLocation = 0;
        totalSongLength = 0;
        player.close();
    }

    private void pauseMusic() throws IOException {
        isPause = true;
        isCurrentlyPlaying = false;
        pausedLocation = finputStream.available();
        player.close();
    }

    private void playMusic() throws IOException, JavaLayerException {
        if(isCurrentlyPlaying) {
            return;
        }
        isCurrentlyPlaying = true;
        finputStream = new FileInputStream(musicPath);
        totalSongLength = finputStream.available();
        if(isPause) {
            isPause = false;
            finputStream.skip(totalSongLength - pausedLocation);
        }
        player = new Player(finputStream);
        new Thread(() -> {
            try {
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
