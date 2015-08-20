import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by thenamanpat on 5th August, 2015
 * Contact at thenamanpat@gmai.com with suggestions/enquiries.
 */


public class ImageGallery {

    JFrame mainFrame;
    JPanel CategoryPanel;
    JPanel ImagePanel;
    JPanel Menu;
    JPanel gamePanel;
    JPanel GameButton;
    JPanel CategoryPanelRadio;

    JButton imageButton;
    JButton gameButtons[];

    JRadioButton gameRadio;
    JRadioButton galleryRadio;
    JRadioButton trueFalseRadio;

    String audio_names[];
    String image_names[];

    int curPos;
    int curPosAudio;
    boolean play;
    MyAudioPlayer audio_player;

    private String file_name;

    public void setupViews() {
        mainFrame = new JFrame("Hope AudioVisual Software");
        mainFrame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        CategoryPanel = new JPanel();
        CategoryPanel.setBackground(Color.WHITE);
        ImagePanel = new JPanel();
        ImagePanel.setBackground(Color.WHITE);
        gamePanel = new JPanel();
        gamePanel.setBackground(Color.WHITE);
    }


    public String chooseDir() {
        // Opens DialogBox for choosing path for DB
        JFileChooser dir_choose = new JFileChooser();
        dir_choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = dir_choose.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            return dir_choose.getSelectedFile().getAbsolutePath();
        return null;
    }

    public String getDB_path() throws IOException {
        String db_path = null;
        // This file stores the previous selected path
        File parent_path = new File(file_name);
        if (parent_path.exists()) {
            // If previous location found in the file
            Scanner in = new Scanner(parent_path);
            db_path = in.nextLine();
            in.close();
        }
        else {
            // If previous location not found, choose again
            while (db_path == null)
                db_path = chooseDir();
            parent_path.createNewFile();
            FileWriter write = new FileWriter(parent_path);
            write.write(db_path);
            write.close();
        }
        // return path
        return db_path;
    }

    public boolean setup_Folders() throws IOException {
        String parent = getDB_path();
        // Sets up the folders for audio and images
        File database = new File(parent);

        if (!database.exists()) {
            if (!database.mkdir()) {
                // Error-handling if unable to access file
                //System.out.println("Error in setup_folder!");
                JOptionPane.showMessageDialog(mainFrame, "There seems to be an error while opening Database folder folder. Please choose the directory again!");
                File parent_path = new File(file_name);
                parent_path.delete();
                return false;
            }
        }
        return true;
    }

    public void welcomeScreen() throws IOException {
        setupViews();
        //Getting list of all categories
        File database = new File(getDB_path());
        String[] listOfCat = database.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        Arrays.sort(listOfCat);

        CategoryPanel.setLayout(new GridLayout(7, 3));
        //CategoryPanel.setLayout(new BoxLayout(CategoryPanel, BoxLayout.PAGE_AXIS));

        //Making buttons for each category

        for (String aListOfCat : listOfCat) {
            JButton button = new JButton(aListOfCat);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        Gallery(actionEvent.getActionCommand());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            CategoryPanel.add(button);
        }

        trueFalseRadio = new JRadioButton("True or False");
        trueFalseRadio.setSelected(false);

        gameRadio = new JRadioButton("Game", true);
        gameRadio.setSelected(false);

        galleryRadio = new JRadioButton("Gallery");
        galleryRadio.setSelected(true);


        ButtonGroup group = new ButtonGroup();
        group.add(galleryRadio);
        group.add(gameRadio);
        group.add(trueFalseRadio);

        CategoryPanelRadio = new JPanel();
        CategoryPanelRadio.setLayout(new GridLayout(4,1));

        CategoryPanelRadio.add(gameRadio);
        CategoryPanelRadio.add(galleryRadio);
        CategoryPanelRadio.add(trueFalseRadio);
        CategoryPanelRadio.setBackground(Color.WHITE);

        mainFrame.add(CategoryPanel, BorderLayout.CENTER);
        mainFrame.add(CategoryPanelRadio, BorderLayout.WEST);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public JButton setup_HomeButton() {
        //Home button
        ImageIcon homeBtnIcon = new ImageIcon(getClass().getResource("home.png"));
        JButton home = new JButton(homeBtnIcon);
        home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    musicImage(-1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                mainFrame.setVisible(false);
                try {
                    welcomeScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return home;
    }


    public void setup_GameView() {

        gamePanel.setLayout(new GridLayout(2,2));
        gameButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            gameButtons[i] = new JButton();

            gameButtons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            gameButtons[i].setBorder(BorderFactory.createEmptyBorder());
            gameButtons[i].setContentAreaFilled(false);

            final int finalI = i;
            gameButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Object property = gameButtons[finalI].getClientProperty("id");
                    if (property instanceof Integer)
                        answer_input((Integer) property);
                }
            });

            gamePanel.add(gameButtons[i]);
        }

        //Setting up media buttons
        JButton PREVIOUS, PLAY, STOP, NEXT, HOME;

        //Setting up media icons for buttons
        System.out.println(getClass().getResource("next.png").toString());
        ImageIcon nextBtnIcon = new ImageIcon(getClass().getResource("next.png"));
        ImageIcon previousBtnIcon = new ImageIcon(getClass().getResource("previous.png"));
        ImageIcon playBtnIcon = new ImageIcon(getClass().getResource("play.png"));
        ImageIcon stopBtnIcon = new ImageIcon(getClass().getResource("mute.png"));

        HOME = setup_HomeButton();

        PREVIOUS = new JButton(previousBtnIcon);
        PREVIOUS.setPreferredSize(new Dimension(20, 20));
        PREVIOUS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setup_gameButtons();
            }
        });


        PLAY = new JButton(playBtnIcon);
        PLAY.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gameplay_music(-1);
            }
        });

        STOP = new JButton(stopBtnIcon);
        STOP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gameplay_music(-2);
            }
        });


        NEXT = new JButton(nextBtnIcon);
        NEXT.setBounds(20, 20, 20, 20);
        NEXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setup_gameButtons();
            }
        });


        //Adding button to menu
        Menu = new JPanel();
        Menu.setLayout(new GridLayout(1, 4));
        Menu.setBackground(Color.WHITE);
        Menu.add(PREVIOUS);
        Menu.add(PLAY);
        Menu.add(STOP);
        Menu.add(HOME);
        Menu.add(NEXT);


        mainFrame.add(gamePanel, BorderLayout.CENTER);
        mainFrame.add(Menu, BorderLayout.PAGE_END);
        mainFrame.setBackground(Color.WHITE);

        play = false;


        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void setup_gameButtons() {
        int button_num[] = new int[4];
        for (int i = 0; i < 4; i++) {
            int c = (int) (Math.random() * image_names.length);
            for (int j = 0; j < i; j++) {
                int k = j;
                if (button_num[k] == c) {
                    c = (int) (Math.random() * image_names.length);
                    j = -1;
                }
            }
            button_num[i] = c;
        }

        for (int i = 0; i < 4; i++) {
            Image newimg = new ImageIcon(image_names[button_num[i]]).getImage().getScaledInstance(400/*Width*/, 400/*Height*/, Image.SCALE_SMOOTH);
            gameButtons[i].setIcon(new ImageIcon(newimg));
            gameButtons[i].putClientProperty("id", button_num[i]);
        }

        curPos = button_num[(int) (Math.random() * 4)];
        gameplay_music(curPos);
        // return button_num;
    }


    private void gameplay_music(int i) {
        if (play && i != -1) {
            audio_player.close();
            if (i == curPos) {
                audio_player = new MyAudioPlayer(audio_names[curPos], true);
                audio_player.start();
            }
            else
                play = false;
        }
        else if (!play && i != -2) {
            audio_player = new MyAudioPlayer(audio_names[curPos], true);
            audio_player.start();
            play = true;
        }
    }

    public void answer_input(int n) {
        File temp;
        if (curPos == n) {
            gameplay_music(-2);
            try {
                temp = new File(getDB_path());
                temp = new File(temp, "RightAnswer.mp3");
                FileInputStream buff = new FileInputStream(temp.getAbsolutePath());                
                Player temp_words = new Player(buff);
                temp_words.play();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setup_gameButtons();
        }
        else {
            gameplay_music(-2);
            try {
                temp = new File(getDB_path());
                temp = new File(temp, "WrongAnswer.mp3");
                FileInputStream buff = new FileInputStream(temp.getAbsolutePath());
                Player temp_words = new Player(buff);
                temp_words.play();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            gameplay_music(-1);
        }
    }

    public void setup_GalleryView() {
        //imageButton displays the image
        imageButton = new JButton(new ImageIcon(getClass().getResource("Blank.png")));
        imageButton.setBorder(BorderFactory.createEmptyBorder());
        imageButton.setContentAreaFilled(false);
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    musicImage(0);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        ImagePanel.add(imageButton);

        //Home button
        //ImagePanel.add(setup_HomeButton(ImagePanel));


        //Setting up media buttons
        JButton PREVIOUS, PLAY, STOP, NEXT, HOME;

        //Setting up media icons for buttons
        ImageIcon nextBtnIcon = new ImageIcon(getClass().getResource("next.png"));
        ImageIcon previousBtnIcon = new ImageIcon(getClass().getResource("previous.png"));
        ImageIcon playBtnIcon = new ImageIcon(getClass().getResource("play.png"));
        ImageIcon stopBtnIcon = new ImageIcon(getClass().getResource("mute.png"));

        HOME = setup_HomeButton();

        PREVIOUS = new JButton(previousBtnIcon);
        PREVIOUS.setPreferredSize(new Dimension(20, 20));
        PREVIOUS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    scrollImage(-1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        PLAY = new JButton(playBtnIcon);
        PLAY.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    musicImage(1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        STOP = new JButton(stopBtnIcon);
        STOP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    musicImage(-1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        NEXT = new JButton(nextBtnIcon);
        NEXT.setBounds(20, 20, 20, 20);
        NEXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    scrollImage(1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });


        //Adding button to menu
        Menu = new JPanel();
        Menu.setLayout(new GridLayout(1, 5));
        Menu.add(PREVIOUS);
        Menu.add(PLAY);
        Menu.add(STOP);
        Menu.add(HOME);
        Menu.add(NEXT);

//        JLabel test = new JLabel("Welcome to Hope Helping Hand");
//        test.setSize();
        ImagePanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        //mainFrame.add(test, BorderLayout.NORTH);
        mainFrame.add(ImagePanel, BorderLayout.CENTER);
        mainFrame.add(Menu, BorderLayout.SOUTH);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void scrollImage(int change) throws MalformedURLException {
        //Responsible for scrolling through images by updating curPos
        curPos += change;
        if (curPos < 0 )
            curPos += image_names.length - 1;
        if (curPos >= image_names.length)
            curPos = 0;

        imageButton.setIcon(new ImageIcon(image_names[curPos]));
        musicImage(10);
    }

    private void musicImage(int change) throws MalformedURLException {
        //Responsible for changing the state of audio
        if (play && change != 1) {
            audio_player.close();
            if (change == 10) {
                audio_player = new MyAudioPlayer(audio_names[curPos], true);
                audio_player.start();
            }
            else
                play = false;
        }
        else if (!play && change != -1) {
            audio_player = new MyAudioPlayer(audio_names[curPos], true);
            audio_player.start();
            play = true;
        }
    }


    private void setup_TFGame() {
        //imageButton displays the image
        imageButton = new JButton(new ImageIcon(getClass().getResource("Blank.png")));
        imageButton.setBorder(BorderFactory.createEmptyBorder());
        imageButton.setContentAreaFilled(false);
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                musicImageRandom(0);
            }
        });
        ImagePanel.add(imageButton);

        //Setting up media buttons
        JButton PREVIOUS, PLAY, STOP, NEXT, HOME;

        //Setting up media icons for buttons
        ImageIcon nextBtnIcon = new ImageIcon(getClass().getResource("next.png"));
        ImageIcon previousBtnIcon = new ImageIcon(getClass().getResource("previous.png"));
        ImageIcon playBtnIcon = new ImageIcon(getClass().getResource("play.png"));
        ImageIcon stopBtnIcon = new ImageIcon(getClass().getResource("mute.png"));

        HOME = setup_HomeButton();

        PREVIOUS = new JButton(previousBtnIcon);
        PREVIOUS.setPreferredSize(new Dimension(20, 20));
        PREVIOUS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    scrollImageRandom();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });


        PLAY = new JButton(playBtnIcon);
        PLAY.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                musicImageRandom(1);
            }
        });

        STOP = new JButton(stopBtnIcon);
        STOP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                musicImageRandom(-1);
            }
        });

        NEXT = new JButton(nextBtnIcon);
        NEXT.setBounds(20, 20, 20, 20);
        NEXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    scrollImageRandom();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        //Game Button setup
        JButton true_button = new JButton(new ImageIcon(getClass().getResource("Right.png")));
        JButton false_button = new JButton(new ImageIcon(getClass().getResource("Wrong.png")));

        true_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    TrueandFalseAnswer(true);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        false_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    TrueandFalseAnswer(false);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        true_button.setContentAreaFilled(false);
        false_button.setContentAreaFilled(false);

        GameButton = new JPanel();
        GameButton.setLayout(new GridLayout(1, 2));
        GameButton.add(true_button);
        GameButton.add(false_button);
        GameButton.setBackground(Color.WHITE);

        //Adding button to menu
        Menu = new JPanel();
        Menu.setLayout(new GridLayout(1, 5));
        Menu.add(PREVIOUS);
        Menu.add(PLAY);
        Menu.add(STOP);
        Menu.add(HOME);
        Menu.add(NEXT);

        mainFrame.add(ImagePanel, BorderLayout.PAGE_START);//, BorderLayout.NORTH);
        mainFrame.add(GameButton, BorderLayout.CENTER);//, BorderLayout.SOUTH);
        mainFrame.add(Menu, BorderLayout.PAGE_END);//, BorderLayout.SOUTH);

        play = false;

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void musicImageRandom(int change) {
        //Responsible for changing the state of audio
        if (play && change != 1) {
            audio_player.close();
            if (change == 10) {
                audio_player = new MyAudioPlayer(audio_names[curPosAudio], true);
                audio_player.start();
            }
            else
                play = false;
        }
        else if (!play && change != -1) {
            audio_player = new MyAudioPlayer(audio_names[curPosAudio], true);
            audio_player.start();
            play = true;
        }
    }

    private void scrollImageRandom() throws MalformedURLException {
        //Responsible for scrolling through images by updating curPos
        curPos = (int) (Math.random() * audio_names.length);
        //Control to decide True or False audio
        int temp = (int) (Math.random() * 500);
        if ((temp % 5) == 0)
            curPosAudio = curPos;
        else
            curPosAudio = (int) (Math.random() * audio_names.length);
        imageButton.setIcon(new ImageIcon(image_names[curPos]));
        musicImageRandom(10);
    }

    private void TrueandFalseAnswer(boolean ans) throws MalformedURLException {
        File temp;
        if ((curPos==curPosAudio) == ans) {
            musicImageRandom(-1);
            try {
                temp = new File(getDB_path());
                temp = new File(temp, "RightAnswer.mp3");
                FileInputStream buff = new FileInputStream(temp.getAbsolutePath());
                Player temp_words = new Player(buff);
                temp_words.play();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            musicImageRandom(1);
            scrollImageRandom();
        }
        else {
            musicImageRandom(-1);
            try {
                temp = new File(getDB_path());
                temp = new File(temp, "WrongAnswer.mp3");
                FileInputStream buff = new FileInputStream(temp.getAbsolutePath());
                Player temp_words = new Player(buff);
                temp_words.play();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            musicImageRandom(1);
        }

    }

    public void Gallery(String name) throws IOException {

        //Factor in the DB into files
        File category = new File(getDB_path(), name);
        File cat_audio = new File(category, "audio");
        File cat_image;
        if (galleryRadio.isSelected())
            cat_image = new File(category, "image");
        else
            cat_image = new File(category, "game");

        //Get names of all files
        audio_names = cat_audio.list();
        image_names = cat_image.list();

        //Sort audio names
        Arrays.sort(audio_names);
        Arrays.sort(image_names);

        //set names as full paths
        for (int i = 0; i < audio_names.length; i++) {
            category = new File(cat_audio, audio_names[i]);
            if (category.getName().equals("Thumbs.db")) {
                category.delete();
                continue;
            }
            audio_names[i] = category.getAbsolutePath();

            category = new File(cat_image, image_names[i]);
            if (category.getName().equals("Thumbs.db")) {
                category.delete();
                continue;
            }
            image_names[i] = category.getAbsolutePath();
        }

        //set current playback
        curPos = -1;
        play = false;


        //Remove the Category Panel
        mainFrame.remove(CategoryPanel);
        mainFrame.remove(CategoryPanelRadio);
        mainFrame.setVisible(false);
        if (gameRadio.isSelected())
            setup_GameView();
        else if (galleryRadio.isSelected())
            setup_GalleryView();
        else
            setup_TFGame();
    }


    public ImageGallery() throws Exception {

        file_name = "parent_path.txt";
        while (!setup_Folders());

        /*
        The new Database folder will have many sub-folders like Fruits, Vegetables, etc
        which will be the different categories.
        path......../Database/Animals
        path......../Database/Fruits

         */

        UIManager.LookAndFeelInfo plafinfo[] = UIManager.getInstalledLookAndFeels();
        boolean nimbusfound=false;
        int nimbusindex=0;
        //System.out.println(getClass().getResource("Blank.png"));

        for (int look = 0; look < plafinfo.length; look++) {
            if(plafinfo[look].getClassName().toLowerCase().contains("nimbus")) {
                nimbusfound=true;
                nimbusindex=look;
            }
        }

        try {
            if(nimbusfound)
                UIManager.setLookAndFeel(plafinfo[nimbusindex].getClassName());

            else
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        }
        catch(Exception ignored) {}
    }

    public static void main(String args[]) throws Exception {
        ImageGallery temp = new ImageGallery();
        temp.welcomeScreen();
    }


    public class MyAudioPlayer extends Thread{
        private String fileLocation;
        private boolean loop;
        private Player prehravac;

        public MyAudioPlayer(String fileLocation, boolean loop) {
            this.fileLocation = fileLocation;
            this.loop = loop;
        }

        public void run() {

            try {
                do {
                    FileInputStream buff = new FileInputStream(fileLocation);
                    prehravac = new Player(buff);
                    prehravac.play();
                } while (loop);
            } catch (Exception ioe) {
                // Exception Handling
            }
        }

        public void close(){
            loop = false;
            try {
                prehravac.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
