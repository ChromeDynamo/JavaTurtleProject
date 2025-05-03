import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import uk.ac.leedsbeckett.oop.LBUGraphics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.*;
import javax.swing.*;

public class Main extends LBUGraphics
{
    private boolean recordingEnabled = true;

    private List<String> commandHistory = new ArrayList<>();

    private void drawSquare(int side) {
        int originalX = getxPos();
        int originalY = getyPos();
        int originalAngle = getDirection();

        for (int i = 0; i < 4; i++) {
            forward(side);
            right(90);
        }

        // Return to original state
        setxPos(originalX);
        setyPos(originalY);
        pointTurtle(originalAngle);
    }

    private void drawEquilateralTriangle(int side) {
        int originalX = getxPos();
        int originalY = getyPos();
        int originalAngle = getDirection();

        for (int i = 0; i < 3; i++) {
            forward(side);
            right(120);
        }

        setxPos(originalX);
        setyPos(originalY);
        pointTurtle(originalAngle);
    }

    private void saveImageToFile(String filename) {
        try {
            BufferedImage image = getBufferedImage(); // from LBUGraphics
            File file = new File(filename);

            // Extract format (e.g. "png", "jpg")
            String format = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

            boolean success = ImageIO.write(image, format, file);

            if (success) {
                System.out.println("✔️ Image saved as " + filename);
            } else {
                System.out.println("❗ Failed to save image. Unsupported format?");
            }
        } catch (IOException e) {
            System.out.println("❗ Error saving image: " + e.getMessage());
        }
    }

    private void loadImageFromFile(String filename) {
        try {
            File file = new File(filename);

            if (!file.exists()) {
                System.out.println("❗ File not found: " + filename);
                return;
            }

            BufferedImage image = ImageIO.read(file);
            setBufferedImage(image); // draw the image on canvas
            System.out.println("✔️ Image loaded from " + filename);

        } catch (IOException e) {
            System.out.println("❗ Error loading image: " + e.getMessage());
        }
    }

    private void saveCommandHistory(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (String cmd : commandHistory) {
                out.println(cmd);
            }
            System.out.println("✔️ Commands saved to " + filename);
        } catch (IOException e) {
            System.out.println("❗ Error saving commands: " + e.getMessage());
        }
    }

    private void loadCommandsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            recordingEnabled = false; // ⛔ Stop saving loaded commands to history

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.toLowerCase().startsWith("loadcommands")) {
                    processCommand(line);
                } else {
                    System.out.println("⚠️ Skipped recursive command: " + line);
                }
            }

            recordingEnabled = true; // ✅ Resume normal command tracking
            System.out.println("✔️ Commands loaded from " + filename);

        } catch (IOException e) {
            System.out.println("❗ Error loading commands: " + e.getMessage());
            recordingEnabled = true;
        }
    }

    private String getHelpText() {
        return """
    📋 Available Commands:
    move <pixels>, reverse <pixels>
    left <deg>, right <deg>
    penup, pendown, penwidth <px>, pencolour r,g,b
    reset, clear
    square <length>, triangle <length>, circle <radius>
    red, green, black, white
    saveimage <file>, loadimage <file>
    savecommands <file>, loadcommands <file>
    help, exit
    """;
    }

    public static void main(String[] args)
    {
        new Main(); //create instance of class that extends LBUGraphics (could be separate class without main), gets out of static context
    }

    public Main() {
        JFrame MainFrame = new JFrame("Turtle Drawing");
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setLayout(new BorderLayout());

        // 🖼️ Canvas area
        setPreferredSize(new Dimension(800, 400));
        JPanel canvasPanel = new JPanel(new FlowLayout());
        canvasPanel.setBorder(BorderFactory.createTitledBorder("Canvas"));
        canvasPanel.add(this);

        // 📌 Left panel (help)
        JTextArea helpArea = new JTextArea(30, 20);
        helpArea.setEditable(false);
        JScrollPane helpScroll = new JScrollPane(helpArea);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(helpScroll, BorderLayout.CENTER);

        // 🧭 Right panel (buttons)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(0, 2, 5, 5));// vertical buttons

        // 🚀 Add buttons for commands
        String[] commands = {
                "move 100", "reverse 100",
                "left 90", "right 90",
                "penup", "pendown",
                "reset", "clear",
                "square 100", "triangle 100", "circle 50",
                "penwidth 3", "pencolour 0,255,0",
                "red", "green", "black", "white",
                "saveimage mydrawing.png", "loadimage mydrawing.png",
                "savecommands mycommands.txt", "loadcommands mycommands.txt",
                "help", "exit"
        };

        for (String cmd : commands) {
            JButton btn = new JButton(cmd.split(" ")[0]);
            btn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> processCommand(cmd));
                if (cmd.startsWith("help")) {
                    helpArea.setText(getHelpText());
                }
            });
            rightPanel.add(btn);
        }

        // 🧱 Add all panels to frame
        MainFrame.add(leftPanel, BorderLayout.WEST);
        MainFrame.add(canvasPanel, BorderLayout.CENTER);
        MainFrame.add(rightPanel, BorderLayout.EAST);

        MainFrame.pack();
        MainFrame.setResizable(false);
        MainFrame.setVisible(true);

        setTurtleSpeed(2);
        setInternalTurtle(1);
        about();
    }


    @Override
    public void about() {
        //super.about(); // Call the original about
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
                    reset();
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
                        System.out.println("❗ Missing side length for 'triangle'");
                    } else {
                        try {
                            int side = Integer.parseInt(parts[1]);
                            if (side <= 0) {
                                System.out.println("❗ Side length must be positive.");
                            } else {
                                drawEquilateralTriangle(side);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'triangle' requires a numeric side length.");
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
                                int r = Integer.parseInt(rgbParts[0]);
                                int g = Integer.parseInt(rgbParts[1]);
                                int b = Integer.parseInt(rgbParts[2]);

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