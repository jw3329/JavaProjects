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
    JButton next = new JButton("Next");

    JFrame frame;
    JPanel panel;

    String musicPath = "";

    FileInputStream finputStream;
    int totalSongLength;
    int pausedLocation;

    Player player;
    boolean isPause = false;
    boolean isCurrentlyPlaying = false;

    List<File> fileLists;
    List<JLabel> labelLists = new ArrayList<>();

    int musicIndex = 0;
    int musicTime = 0;

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
                createQueueList(panel);
            }
        });
    }

    private void setButtonOnclickListener() {
        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);
        next.addActionListener(this);
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
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(600,400));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(1,4, 5,5));
        panel2.add(play);
        panel2.add(pause);
        panel2.add(stop);
        panel2.add(next);
        panel.add(panel2,BorderLayout.PAGE_START);
        createQueueList(panel);
        createJSlider(panel);
    }

    private void createJSlider(JPanel panel) {

    }

    private void createQueueList(JPanel panel) {
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.PAGE_AXIS));
        panel.add(tempPanel,BorderLayout.LINE_START);
        tempPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));
        if(fileLists == null) return;
        for(int i=0;i<fileLists.size();i++) {
            JLabel label = new JLabel(fileLists.get(i).getName());
            tempPanel.add(label);
            labelLists.add(label);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(fileLists.isEmpty()) return;
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
        } else if(e.getSource() == next) {
            try {
                playNextSong();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JavaLayerException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void playNextSong() throws IOException, JavaLayerException {
        stopMusic();
        if(musicIndex + 1 < fileLists.size()) musicIndex++;
        else musicIndex = 0;
        playMusic();
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
        System.out.println(pausedLocation);
        player.close();
    }

    private void playMusic() throws IOException, JavaLayerException {
        if(isCurrentlyPlaying) {
            return;
        }
        isCurrentlyPlaying = true;
        musicPath = fileLists.get(musicIndex).getAbsolutePath();
        finputStream = new FileInputStream(musicPath);
        highlightMusic();
        totalSongLength = finputStream.available();
        System.out.println(totalSongLength);
        if(isPause) {
            isPause = false;
            finputStream.skip(totalSongLength - pausedLocation);
        }
        player = new Player(finputStream);
        new Thread(() -> {
            try {
                new Thread(() -> {
                    try {
                        while(true) {
                            Thread.sleep(1000);
                            musicTime++;
                            System.out.println(musicTime);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                player.play();
                if(player.isComplete()) playNextSong();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void highlightMusic() {
        deleteAllBackground();
        labelLists.get(musicIndex).setBackground(Color.CYAN);
        labelLists.get(musicIndex).setOpaque(true);
    }

    private void deleteAllBackground() {
        for(int i=0;i<labelLists.size();i++) {
            labelLists.get(i).setOpaque(false);
            labelLists.get(i).repaint();
        }
    }
}
