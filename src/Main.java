import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main extends TurtleGraphics
{
    public static void main(String[] args)
    {
        new Main(); //create instance of class that extends LBUGraphics (could be separate class without main), gets out of static context
    }

    private final JList<String> fileList = new JList<>();

    private void populateFileList() {
        File dir = new File(".");
        File[] files = dir.listFiles((_, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".txt"));

        DefaultListModel<String> model = new DefaultListModel<>();

        if (files != null) {
            for (File file : files) {
                model.addElement(file.getName());
            }
        }

        fileList.setModel(model);
    }

    public Main() {
        recordingEnabled = true;

        JFrame MainFrame = new JFrame("Turtle Drawing");
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setLayout(new BorderLayout());

// Adding a MenuBar
        JMenuBar menuBar = new JMenuBar();

// File Menu
        JMenu fileMenu = new JMenu("File");

        // New Option
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> {
            processCommand("clear"); // Clear the canvas
            processCommand("reset"); // Reset the turtle to the center
        });
        fileMenu.add(newItem);

        // Save Option
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> processCommand("saveimage savedimage.png")); // Save the current image
        fileMenu.add(saveItem);

        // Exit Option
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0)); // Exit application
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");

        // Clear Option
        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(e -> processCommand("clear")); // Clear canvas
        editMenu.add(clearItem);

        // Reset Option
        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(e -> processCommand("reset")); // Reset turtle
        editMenu.add(resetItem);

        menuBar.add(editMenu);

// Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("Command List");
        aboutItem.addActionListener(e -> processCommand("help")); // Show about
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

// Setting the menu bar
        MainFrame.setJMenuBar(menuBar);

        // 🖼️ Canvas area
        setPreferredSize(new Dimension(800, 400));
        JPanel canvasPanel = new JPanel(new FlowLayout());
        canvasPanel.setBorder(BorderFactory.createTitledBorder("Canvas"));
        canvasPanel.add(this);

        // 📌 Left panel (help) with JTable instead of JTextArea
        String[][] helpData = {
                {"Command", "Description"},
                {"move <pixels>", "Move forward"},
                {"reverse <pixels>", "Move backward"},
                {"left <deg>", "Turn left"},
                {"right <deg>", "Turn right"},
                {"penup", "Lift pen (no drawing)"},
                {"pendown", "Lower pen (draw)"},
                {"penwidth <px>", "Set pen width"},
                {"pencolour r,g,b", "Set pen RGB colour"},
                {"reset", "Reset position"},
                {"clear", "Clear canvas"},
                {"square <length>", "Draw square"},
                {"triangle <length>", "Draw triangle"},
                {"circle <radius>", "Draw circle"},
                {"red / green / black / white", "Quick set pen colour"},
                {"saveimage <file>", "Save canvas to image"},
                {"loadimage <file>", "Load image to canvas"},
                {"savecommands <file>", "Save command history"},
                {"loadcommands <file>", "Load and run commands"},
                {"help", "Show this help"},
                {"exit", "Exit program"}
        };

        String[] columnNames = {"Command", "Description"};
        JTable helpTable = new JTable(helpData, columnNames);
        helpTable.setEnabled(false);
        helpTable.setFillsViewportHeight(true);
        JScrollPane helpScroll = new JScrollPane(helpTable);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Help"));
        leftPanel.add(helpScroll, BorderLayout.CENTER);

        // 🧭 Right panel (buttons)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(0, 2, 5, 5));

        String[] simpleCommands = {
                "penup", "pendown",
                "reset", "clear",
                "red", "green", "black", "white",
                "saveimage mydrawing.png", "loadimage mydrawing.png",
                "savecommands mycommands.txt"
        };

        for (String cmd : simpleCommands) {
            JButton btn = new JButton(cmd.split(" ")[0]);
            btn.addActionListener(_ -> processCommand(cmd)); // 🔥 Directly process the command
            rightPanel.add(btn);
        }
        // 🧱 Add all panels to frame
        MainFrame.add(leftPanel, BorderLayout.WEST);
        MainFrame.add(canvasPanel, BorderLayout.CENTER);
        MainFrame.add(rightPanel, BorderLayout.EAST);

        // Add the bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("File Table"));

        // Define table column names
        String[] bottomColumnNames = {"Commands (txt files)", "PNG Images", "JPG Images"};
        DefaultTableModel bottomTableModel = new DefaultTableModel(bottomColumnNames, 0);

        // Scan the root directory for files
        File rootDirectory = new File("./");

        String[] commands = rootDirectory.list((_, name) -> name.endsWith(".txt"));
        String[] pngImages = rootDirectory.list((_, name) -> name.endsWith(".png"));
        String[] jpgImages = rootDirectory.list((_, name) -> name.endsWith(".jpg"));

        // Find the maximum row count
        int maxRows = Math.max(commands != null ? commands.length : 0,
                Math.max(pngImages != null ? pngImages.length : 0,
                        jpgImages != null ? jpgImages.length : 0));

        // Populate the table model
        for (int i = 0; i < maxRows; i++) {
            String commandFile = (commands != null && i < commands.length) ? commands[i] : "";
            String pngFile = (pngImages != null && i < pngImages.length) ? pngImages[i] : "";
            String jpgFile = (jpgImages != null && i < jpgImages.length) ? jpgImages[i] : "";
            bottomTableModel.addRow(new Object[]{commandFile, pngFile, jpgFile});
        }

        // Create the JTable
        JTable bottomTable = new JTable(bottomTableModel);
        bottomTable.setEnabled(false);
        bottomTable.setFillsViewportHeight(true);

        // Dynamically calculate the height of the table based on the number of rows
        int rowHeight = bottomTable.getRowHeight();
        int tableHeight = (maxRows * rowHeight) + 50; // Add 100 pixels for padding

        bottomPanel.add(createBottomScroll(bottomTable, tableHeight), BorderLayout.CENTER);

        // Add the bottom panel to the main frame
        MainFrame.add(bottomPanel, BorderLayout.SOUTH);

        MainFrame.pack();
        MainFrame.setResizable(false);
        MainFrame.setVisible(true);

        setTurtleSpeed(2);
        setInternalTurtle(0);
        populateFileList();
        about();
    }

    private JScrollPane createBottomScroll(JTable bottomTable, int tableHeight) {
        JScrollPane bottomScroll = new JScrollPane(bottomTable);
        bottomScroll.setPreferredSize(new Dimension(bottomScroll.getPreferredSize().width, tableHeight));
        bottomScroll.setMaximumSize(new Dimension(bottomScroll.getMaximumSize().width, tableHeight));
        return bottomScroll;
    }

    @Override
    public void about() {
        super.about(); // Call the original about
        displayMessage("Project by ChromeDynamo"); // Add your name here
    }
    @Override
    public void processCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            System.out.println("No command entered.");
            return;
        }

        String[] parts = command.trim().toLowerCase().split("\\s+");
        String mainCmd = parts[0];

        try {
            if (recordingEnabled) {
                commandHistory.add(command.trim());
            }

            switch (mainCmd) {
                case "about":
                    about();
                    break;

                case "penup":
                    drawOff();
                    break;

                case "pendown":
                    drawOn();
                    break;

                case "left":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing angle for 'left'");
                    } else {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            left(angle);
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'left' requires a numeric angle.");
                        }
                    }
                    break;

                case "right":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing angle for 'right'");
                    } else {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            right(angle);
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'right' requires a numeric angle.");
                        }
                    }
                    break;

                case "move":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing distance for 'move'");
                    } else {
                        try {
                            int dist = Integer.parseInt(parts[1]);
                            forward(dist);
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'move' requires a numeric distance.");
                        }
                    }
                    break;

                case "reverse":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing distance for 'reverse'");
                    } else {
                        try {
                            int dist = Integer.parseInt(parts[1]);
                            forward(-dist);
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'reverse' requires a numeric distance.");
                        }
                    }
                    break;

                case "reset":
                    reset(); // Reset position and orientation
                    setPenColour(Color.RED); // Reset pen color to default (black)
                    setStroke(1); // Reset pen width to default (1)
                    System.out.println("Reset: Position, orientation, pen color, and pen width.");
                    break;

                case "clear":
                    clear();
                    break;

                case "red":
                    setPenColour(Color.RED);
                    break;

                case "green":
                    setPenColour(Color.GREEN);
                    break;

                case "black":
                    setPenColour(Color.BLACK);
                    break;

                case "white":
                    setPenColour(Color.WHITE);
                    break;

                case "square":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing side length for 'square'");
                    } else {
                        try {
                            int side = Integer.parseInt(parts[1]);
                            if (side <= 0) {
                                System.out.println("❗ Side length must be positive.");
                            } else {
                                drawSquare(side);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'square' requires a numeric side length.");
                        }
                    }
                    break;

                case "triangle":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing parameters for 'triangle'. Usage: triangle <side1>,<side2>,<side3>");
                    } else {
                        try {
                            // Split the second part (parameters) by commas
                            String[] sides = parts[1].split(",");
                            if (sides.length != 3) {
                                System.out.println("❗ Missing parameters for 'triangle'. Usage: triangle <side1>,<side2>,<side3>");
                            } else {
                                // Parse each side
                                int side1 = Integer.parseInt(sides[0].trim());
                                int side2 = Integer.parseInt(sides[1].trim());
                                int side3 = Integer.parseInt(sides[2].trim());

                                // Validate that all sides are positive
                                if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
                                    System.out.println("❗ Side lengths must be positive integers.");
                                } else {
                                    drawTriangle(side1, side2, side3);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'triangle' requires numeric side lengths.");
                        }
                    }
                    break;

                case "circle":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing radius for 'circle'");
                    } else {
                        try {
                            int radius = Integer.parseInt(parts[1]);
                            if (radius <= 0) {
                                System.out.println("❗ Radius must be positive.");
                            } else {
                                circle(radius); // turtle draws a circle at current position
                                System.out.println("✔️ Drew circle with radius " + radius);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'circle' requires a numeric radius.");
                        }
                    }
                    break;

                case "polygon":
                    if (parts.length < 3) {
                        System.out.println("❗ Missing parameters for 'polygon'. Usage: polygon <sides> <length>");
                    } else {
                        try {
                            int sides = Integer.parseInt(parts[1]);
                            int length = Integer.parseInt(parts[2]);

                            if (sides < 3) {
                                System.out.println("❗ A polygon must have at least 3 sides.");
                            } else if (length <= 0) {
                                System.out.println("❗ Length of sides must be positive.");
                            } else {
                                drawPolygon(sides, length); // Call the method from TurtleGraphics
                                System.out.println("✔️ Drew a polygon with " + sides + " sides of length " + length);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'polygon' requires numeric values for sides and length.");
                        }
                    }
                    break;

                case "penwidth":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing width for 'penwidth'");
                    } else {
                        try {
                            int width = Integer.parseInt(parts[1]);
                            if (width <= 0) {
                                System.out.println("❗ Pen width must be positive.");
                            } else {
                                setStroke(width);
                                System.out.println("✔️ Pen width set to " + width);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'penwidth' requires a numeric width.");
                        }
                    }
                    break;

                case "pencolour":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing RGB values for 'pencolour'");
                    } else {
                        try {
                            String[] rgbParts = parts[1].split(",");
                            if (rgbParts.length != 3) {
                                System.out.println("❗ 'pencolour' needs 3 values separated by commas, like pencolour 255,0,0");
                            } else {
                                int r = Integer.parseInt(rgbParts[0].trim());
                                int g = Integer.parseInt(rgbParts[1].trim());
                                int b = Integer.parseInt(rgbParts[2].trim());

                                if (isValidColorValue(r) && isValidColorValue(g) && isValidColorValue(b)) {
                                    setPenColour(new Color(r, g, b));
                                    System.out.println("✔️ Pen colour set to RGB(" + r + "," + g + "," + b + ")");
                                } else {
                                    System.out.println("❗ RGB values must be between 0 and 255.");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'pencolour' values must be numeric.");
                        }
                    }
                    break;

                case "saveimage":
                    if (parts.length < 2) {
                        System.out.println("❗ Please specify a filename, e.g. saveimage myfile.png");
                    } else {
                        saveImageToFile(parts[1]);
                    }
                    break;

                case "loadimage":
                    if (parts.length < 2) {
                        System.out.println("❗ Please provide the filename, e.g. loadimage mydrawing.png");
                    } else {
                        loadImageFromFile(parts[1]);
                    }
                    break;

                case "savecommands":
                    if (parts.length < 2) {
                        System.out.println("❗ Please provide a filename, e.g. savecommands history.txt");
                    } else {
                        saveCommandHistory(parts[1]);
                    }
                    break;

                case "loadcommands":
                    if (parts.length < 2) {
                        System.out.println("❗ Please provide the filename, e.g. loadcommands commands.txt");
                    } else {
                        loadCommandsFromFile(parts[1]);
                    }
                    break;

                case "speed":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing value for 'speed'");
                    } else {
                        try {
                            int speed = Integer.parseInt(parts[1]);
                            if (speed >= 1 && speed <= 10) {
                                setTurtleSpeed(speed);
                                System.out.println("✅ Turtle speed set to " + speed);
                            } else {
                                System.out.println("❗ Speed must be between 1 and 10.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'speed' requires a numeric value between 1 and 10.");
                        }
                    }
                    break;

                case "help":
                    System.out.println("📋 Available Commands:");
                    System.out.println("move <pixels>, reverse <pixels>");
                    System.out.println("left <deg>, right <deg>");
                    System.out.println("penup, pendown, penwidth <px>, pencolour r,g,b");
                    System.out.println("reset, clear");
                    System.out.println("square <length>, triangle <length>, circle <radius>");
                    System.out.println("red, green, black, white");
                    System.out.println("saveimage <file>, loadimage <file>");
                    System.out.println("savecommands <file>, loadcommands <file>");
                    System.out.println("help, exit");
                    break;

                case "exit":
                    System.out.println("👋 Exiting program. Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Unknown command: '" + command + "'");
                    break;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric parameter.");
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }
    private boolean isValidColorValue(int value) {
        return value >= 0 && value <= 255;
    }


}