import uk.ac.leedsbeckett.oop.LBUGraphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane; // For the confirmation dialog
import java.io.File; // For file operations
import java.io.IOException; // For handling IO exceptions

/**
 * TurtleGraphics class extending LBUGraphics to add turtle-specific drawing functionalities.
 */
public class TurtleGraphics extends LBUGraphics {
    public boolean recordingEnabled = true;
    public final List<String> commandHistory = new ArrayList<>();

    public TurtleGraphics() {
        super();
        System.out.println("TurtleGraphics initialized!");
    }

    public void drawSquare(int side) {
        int originalX = getxPos();
        int originalY = getyPos();
        int originalAngle = getDirection();

        for (int i = 0; i < 4; i++) {
            forward(side);
            right(90);
        }

        setxPos(originalX);
        setyPos(originalY);
        pointTurtle(originalAngle);
    }

    public void drawEquilateralTriangle(int side) {
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

    public void saveImageToFile(String filename) {
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

    public void loadImageFromFile(String filename) {
        try {
            // Check if there are unsaved changes
            int response = JOptionPane.showConfirmDialog(null,
                    "You have unsaved changes. Would you like to save them before loading a new image?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.CANCEL_OPTION) {
                System.out.println("Load operation canceled.");
                return; // Cancel operation
            } else if (response == JOptionPane.YES_OPTION) {
                // Call save method (or prompt user to specify save location)
                saveImageToFile("default_save.png"); // Example: Save with a default filename
            }

            // Proceed with loading the new image
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("⚠ File not found: " + filename);
                return;
            }

            BufferedImage image = ImageIO.read(file);
            setBufferedImage(image); // Draw the image on canvas
            System.out.println("✔ Image loaded from " + filename);

        } catch (IOException e) {
            System.out.println("⚠ Error loading image: " + e.getMessage());
        }
    }

    public void saveCommandHistory(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (String cmd : commandHistory) {
                out.println(cmd);
            }
            System.out.println("✔️ Commands saved to " + filename);
        } catch (IOException e) {
            System.out.println("❗ Error saving commands: " + e.getMessage());
        }
    }

    public void loadCommandsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> commandsToExecute = new ArrayList<>();
            String line;

            recordingEnabled = false; // ⛔ Stop saving loaded commands to history

            // Read all commands into a list
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    commandsToExecute.add(line);
                }
            }

            // Remove the last command if it is "savecommands"
            if (!commandsToExecute.isEmpty()) {
                String lastCommand = commandsToExecute.get(commandsToExecute.size() - 1).toLowerCase();
                if (lastCommand.startsWith("savecommands")) {
                    commandsToExecute.remove(commandsToExecute.size() - 1);
                }
            }

            // Execute the remaining commands
            for (String command : commandsToExecute) {
                try {
                    processCommand(command); // Execute each command
                } catch (Exception e) {
                    System.out.println("❗ Error processing command '" + command + "': " + e.getMessage());
                }
            }

            recordingEnabled = true; // ✅ Resume normal command tracking
            System.out.println("✔️ Commands loaded from " + filename);

        } catch (IOException e) {
            System.out.println("❗ Error loading commands: " + e.getMessage());
            recordingEnabled = true;
        }
    }

    public void drawPolygon(int sides, int length) {
        if (sides < 3) {
            System.out.println("❗ A polygon must have at least 3 sides.");
            return;
        }

        int angle = 360 / sides;

        for (int i = 0; i < sides; i++) {
            forward(length);
            right(angle);
        }

        System.out.println("✔️ Drew a polygon with " + sides + " sides of length " + length);
    }

    @Override
    public void about() {
        super.about();
        displayMessage("TurtleGraphics - Extended features for drawing shapes!");
    }

    @Override
    public void processCommand(String command) {
        // No implementation needed
    }
}