package movieReviewClassification;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SentimentAnalysisApp extends Thread {

    // global review handler
    private static final ReviewHandler rh = new ReviewHandler();

    /**
     * Main method loads in the database if one is already created
     * @param args the command line arguments
     */
    public static void main(String[] args)  {

        // updates the GUI
        SwingUtilities.invokeLater(() -> {
            // Loads database if there is one previously saved
            File databaseFile;
            databaseFile = new File(ReviewHandler.DATA_FILE_NAME);
            try {
                    rh.loadSerialDB();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            // calls the GUI
            createAndShowGUI();
        });
    }

    // Declaring the component variables
    static private final JPanel topPanel = new JPanel();
    static private final JPanel bottomPanel = new JPanel();;
    static private final JLabel optionLabel = new JLabel("Please select an Option: ", JLabel.LEFT);
    static private final JComboBox<String> comboBox = new JComboBox<>();
    static private final JButton showButton = new JButton("Show Database");
    static private final JButton saveButton = new JButton("Save Database");
    // declaring the variables for the panel area
    static protected final JTextArea textArea = new JTextArea();
    static private final JScrollPane scrollPane = new JScrollPane(textArea);
    // gets the dimensions of the window
    private static final int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    // width and height of the window (JFrame)
    private static final int windowWidth = 1000;
    private static final int windowHeight = 800;

    /**
     * Creates the GUI by Intializing the JPanels and JFrame
     */
    private static void createAndShowGUI() {

        createTopPanel();
        createBottomPanel();

        // gets container for for the top and bottom of the panel
        topPanel.getIgnoreRepaint();
        JPanel panelContainer = new JPanel();
        // sets the layout of the window
        panelContainer.setLayout(new GridLayout(2, 0));
        panelContainer.add(topPanel);
        panelContainer.add(bottomPanel);

        // allows the look to be relevant to windows, mac ect.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // title of the window
        JFrame frame = new JFrame("Movie Review Classifier");

        // saves the database
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                textArea.append("Closing window. Database will be saved.\n");
                super.windowClosing(e);
                rh.saveSerialDB();
                System.exit(0);
            }

        });

        // sets the size of the GUI
        frame.setBounds((width - windowWidth) / 2, (windowHeight - height) / 2, windowWidth, windowHeight);
        frame.setContentPane(panelContainer);
        frame.setBackground(Color.PINK);
        frame.setVisible(true);

    }

    /**
     * Creates the top of the GUI where the menu and options will be displayed
     */
    private static void createTopPanel() {
        // the menu options are shown in comboBox
        comboBox.addItem("Please select...");
        comboBox.addItem(" 0. Exit program.");
        comboBox.addItem(" 1. Load new movie review collection (given a folder or a file path).");
        comboBox.addItem(" 2. Delete movie review from database (given its id).");
        comboBox.addItem(" 3. Search movie reviews in database by id.");
        comboBox.addItem(" 4. Search movie reviews in database by substring.");
        comboBox.setSelectedIndex(0);

        // gets the selected
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem().equals("Please select...")) {
                    textArea.setText("");
                    topPanel.removeAll();
                    topPanel.add(optionLabel);
                    topPanel.add(comboBox);
                    // Handles the spacing of the top program
                    topPanel.add(new JLabel());
                    topPanel.add(new JLabel());
                    topPanel.add(new JLabel());
                    topPanel.add(new JLabel());
                    topPanel.add(new JLabel());
                    // adds the save and show button to the panel
                    topPanel.add(showButton);
                    topPanel.add(saveButton);
                    topPanel.updateUI();
                    // calls the method selected by the user
                } else if (e.getItem()
                        .equals(" 1. Load new movie review collection (given a folder or a file path).")) {
                    loadReviews();
                } else if (e.getItem().equals(" 2. Delete movie review from database (given its id).")) {
                    deleteReviews();
                } else if (e.getItem().equals(" 3. Search movie reviews in database by id.")) {
                    searchReviewsId();
                } else if (e.getItem().equals(" 4. Search movie reviews in database by substring.")) {
                    searchReviewsSubstring();
                } else if (e.getItem().equals(" 0. Exit program.")) {
                    exit();
                }
            }

        });

        // action event will occur when the user uses the save button
        // detects when the user clicks the buttons
        showButton.addActionListener(e -> {
            Runnable myRunnable = () -> printJTable(rh.searchBySubstring(""));

            Thread thread = new Thread(myRunnable);
            thread.start();
        });

        // detects when the user clicks the save button
        saveButton.addActionListener(e -> {
            Runnable myRunnable = () -> {
                rh.saveSerialDB();
                textArea.append("Database has been saved.\n");

            };

            // creates a path
            Thread thread = new Thread(myRunnable);
            thread.start();
        });

        // creates the layout for the panel
        GridLayout topPanelGridLayout = new GridLayout(0, 1, 2, 2);

        // adds the menu and labels
        topPanel.setLayout(topPanelGridLayout);
        topPanel.add(optionLabel);
        topPanel.add(comboBox);
        // handles spacing
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        // adds the show and save button and makes the top panel pink
        topPanel.add(showButton);
        topPanel.setBackground(Color.PINK);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * This method initialize the bottom panel, which is the output area. Just a
     * TextArea that not editable.
     */
    private static void createBottomPanel() {
        // makes the font Times New Roman and size 20
        final Font f = new Font("Times New Roman", Font.BOLD, 20);

        // formats the text
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setFont(f);

        // displays the welcome message
        textArea.setText("Welcome to the Movie Review Classifier!\n\n");
        textArea.append("Please select a command to continue. (0,1,2,3, or 4):\n");
        // shows the size of the database loaded in
        textArea.append(rh.database.size() + " records in database.\n");
        // makes the text in the panel not editable
        textArea.setEditable(false);
        // implements text wrapping
        textArea.setLineWrap(true);

        // Creates a Green border line around the bottom panel
        final Border border = BorderFactory.createLineBorder(Color.GREEN);

        // creates the border of the scroll Bar
        textArea.setBorder(BorderFactory.createCompoundBorder(border,
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(border,
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        scrollPane.createVerticalScrollBar();
        scrollPane.createHorizontalScrollBar();

        // creates the bottom panel format
        bottomPanel.setLayout(new GridLayout(1, 0));
        bottomPanel.add(scrollPane);
    }

    /**
     * Option 1
     * Loads in reviews method
     */
    static int realClass = 0;

    public static void loadReviews() {

        Thread loadThread = new Thread(() -> {

            // outputs the information to load in a folder or file
            textArea.setText("");
            textArea.append(rh.database.size() + " records currently in database.\n");
            textArea.append("\nOption 1:\n");
            textArea.append("Please input the path of file or folder:\n");

            topPanel.removeAll();
            topPanel.add(optionLabel);
            topPanel.add(comboBox);

           //
            final JLabel pathLabel = new JLabel("File path:", JLabel.LEFT);
            final JTextField pathInput = new JTextField("");

           // creates the combo box for the polarity
            final JLabel realClassLabel = new JLabel("Real class:", JLabel.LEFT);
            final JComboBox<String> realClassComboBox = new JComboBox<>();
            realClassComboBox.addItem("Negative");
            realClassComboBox.addItem("Positive");
            realClassComboBox.addItem("Unknown");
            realClassComboBox.setSelectedIndex(0);

           // assigns the polarity to negative and positive or unknown
            realClassComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (e.getItem().equals("Negative")) {
                        realClass = 0;
                    } else if (e.getItem().equals("Positive")) {
                        realClass = 1;
                    } else if (e.getItem().equals("Unknown")) {
                        realClass = 2;
                    }
                }

            });

            // button created for user to submit their information
            final JButton enterButton = new JButton("Enter");

            enterButton.addActionListener(e -> {

                String path = pathInput.getText();
                rh.loadReviews(path, realClass);
            });
            topPanel.add(pathLabel);
            topPanel.add(pathInput);
            topPanel.add(realClassLabel);
            topPanel.add(realClassComboBox);
            topPanel.add(new JLabel());
            topPanel.add(enterButton);
            topPanel.add(showButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

        });
        loadThread.start();
    }

    /**
     * Option 2
     * Method that deletes the reviews by ID
     */
    public static void deleteReviews() {

        Thread threadDelete = new Thread(() -> {

            textArea.setText("");
            textArea.append(rh.database.size() + " records currently in database.\n");
            textArea.append("\nOption 2:\n");
            textArea.append("Please input the review ID:\n");

            topPanel.removeAll();
            topPanel.add(optionLabel);
            topPanel.add(comboBox);

            // label to get ID
            final JLabel reviewIdLabel = new JLabel("Review ID:", JLabel.LEFT);
            final JTextField reviewIdInput = new JTextField("");

            // Submit to delete
            final JButton enterButton = new JButton("Enter");

            // checks to make sure its a valid ID then deletes it
            enterButton.addActionListener(e -> {
                Runnable myRunnable = () -> {
                    String idStr = reviewIdInput.getText();
                    if (!idStr.matches("-?(0|[1-9]\\d*)")) {
                        // Input is not an integer
                        textArea.append("\nIllegal input.\n");
                    } else {
                        int id = Integer.parseInt(idStr);
                        rh.deleteReview(id);
                        // displays the new size of the database
                        textArea.append(rh.database.size() + " records after deletion.\n");
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            });
            // formats the panel and spacing
            topPanel.add(reviewIdLabel);
            topPanel.add(reviewIdInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(enterButton);
            topPanel.add(showButton);
            topPanel.add(saveButton);
            topPanel.updateUI();
        });
        threadDelete.start();
    }

    /**
     * Option 3
     * searches for a review when given an ID
     */
    public static void searchReviewsId() {

        Thread threadSearchID = new Thread(() -> {

            textArea.setText("");
            textArea.append(rh.database.size() + " records currently in database.\n");
            textArea.append("\nOption 3:\n");
            textArea.append("Please input the review ID:\n");

            topPanel.removeAll();
            topPanel.add(optionLabel);
            topPanel.add(comboBox);

            final JLabel reviewIdLabel = new JLabel("Review ID:", JLabel.LEFT);
            final JTextField reviewIdInput = new JTextField("");

            final JButton enterButton = new JButton("Enter");

            // searches for the review when given a valid ID
            enterButton.addActionListener(e -> {
                Runnable myRunnable = () -> {
                    String idStr = reviewIdInput.getText();
                    if (!idStr.matches("-?(0|[1-9]\\d*)")) {
                        // Input is not an integer
                        textArea.append("Illegal input.\n");
                    } else {
                        int id = Integer.parseInt(idStr);
                        MovieReview mr = rh.searchById(id);
                        if (mr != null) {
                            List<MovieReview> reviewList = new ArrayList<>();
                            reviewList.add(mr);
                            printJTable(reviewList);
                        } else {
                            textArea.append("Review was not found.\n");
                        }
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            });
            topPanel.add(reviewIdLabel);
            topPanel.add(reviewIdInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(enterButton);
            topPanel.add(showButton);
            topPanel.add(saveButton);
            topPanel.updateUI();
        });
        threadSearchID.start();
    }

    /**
     * Option 4
     * searches for a review when given a sub string
     */
    public static void searchReviewsSubstring() {

        Thread threadSearchStr = new Thread(() -> {

            textArea.setText("");
            textArea.append(rh.database.size() + " records currently in database.\n");
            textArea.append("\nOption 4:\n");
            textArea.append("Please input the review substring:\n");

            topPanel.removeAll();
            topPanel.add(optionLabel);
            topPanel.add(comboBox);

            final JLabel subStringLabel = new JLabel("Enter substring:", JLabel.LEFT);
            final JTextField subStringInput = new JTextField("");

            final JButton enterButton = new JButton("Enter");

            enterButton.addActionListener(e -> {
                Runnable myRunnable = () -> {

                    // searches for the review when the user enters a substring that is valid
                    String substring = subStringInput.getText();
                    List<MovieReview> reviewList = rh.searchBySubstring(substring);
                    if (reviewList != null) {
                        printJTable(reviewList);
                        textArea.append(reviewList.size() + " reviews found.\n");

                    } else {
                        textArea.append("Review was not found.\n");
                    }

                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            });
            topPanel.add(subStringLabel);
            topPanel.add(subStringInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(enterButton);
            topPanel.add(showButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

        });
        threadSearchStr.start();
    }

    /**
     * Option 0
     * exits the program and saves the database
     */
    public static void exit() {

        textArea.setText("");
        textArea.append(rh.database.size() + " records currently in database.\n");
        textArea.append("\nOption 0:\n");
        textArea.append("Click Enter to save and exit the system.\n");

        topPanel.removeAll();
        topPanel.add(optionLabel);
        topPanel.add(comboBox);

        final JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> {
            Runnable myRunnable = () -> {
                rh.saveSerialDB();
                textArea.append("\nDatabase saved. Window will be closed in 5 seconds...\n\n");
                textArea.append("Thank you for using the system!\n");
                System.exit(0);
            };
            Thread thread = new Thread(myRunnable);
            thread.start();
        });
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(enterButton);
        topPanel.add(showButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
        topPanel.updateUI();
    }


    /**
     * Prints the Jtable when the show button is clicked, search is used
     @param target_List
     */
    public static void printJTable(List<MovieReview> target_List) {
        // title of each column
        String columnNames[] = {"ID", "Predicted", "Real", "Text"};
        // Creates the values for predicted, real, text, and ID
        String databaseValues[][]= new String[target_List.size()][4];
        // sets the predicted polarity to neg, pos, and un
        for(int i = 0; i < target_List.size(); i++) {
            String predicted = "";
            if (target_List.get(i).getPredictedPolarity() == 0) {
                predicted = "Negative";
            } else if (target_List.get(i).getPredictedPolarity() == 1) {
                predicted = "Positive";
            }
            // sets the real polarity
            String real = "";
            if (target_List.get(i).getRealPolarity() == 0) {
                real = "Negative";
            } else if (target_List.get(i).getRealPolarity() == 1) {
                real = "Positive";
            } else if (target_List.get(i).getRealPolarity() == 2) {
                real = "Unknown";
            }
            databaseValues[i][0] = String.valueOf(target_List.get(i).getId());
            databaseValues[i][1] = predicted;
            databaseValues[i][2] = real;
            databaseValues[i][3] = target_List.get(i).getText();

        }
        // creates a table for the database values
        JTable table = new JTable(databaseValues, columnNames) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // allows scrolling
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.createVerticalScrollBar();
        scrollPane.createHorizontalScrollBar();
        scrollPane.createVerticalScrollBar();
        scrollPane.createHorizontalScrollBar();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame resultFrame = new JFrame("Search Results:");
        resultFrame.setBounds((windowWidth - width) / 4,
                (height - windowHeight) / 4, windowWidth, windowHeight/2);
        resultFrame.setContentPane(scrollPane);
        resultFrame.setVisible(true);
    }
}
