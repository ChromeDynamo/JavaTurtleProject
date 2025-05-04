import uk.ac.leedsbeckett.oop.LBUGraphics;

/**
 * TurtleGraphics class extending LBUGraphics to add turtle-specific drawing functionalities.
 */
public class TurtleGraphics extends LBUGraphics {

    public TurtleGraphics() {
        // Call the parent constructor if required
        super();
        System.out.println("TurtleGraphics initialized!");
    }

    /**
     * Draw a square with the given side length.
     *
     * @param side The side length of the square.
     */
    public void drawSquare(int side) {
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

    /**
     * Draw a triangle with the given side length.
     *
     * @param side The side length of the triangle.
     */
    public void drawTriangle(int side) {
        for (int i = 0; i < 3; i++) {
            forward(side);
            right(120);
        }
    }

    /**
     * Draw a circle with the given radius.
     *
     * @param radius The radius of the circle.
     */
    public void drawCircle(int radius) {
        for (int i = 0; i < 360; i++) {
            forward((int) (2 * Math.PI * radius / 360)); // Approximate arc length
            right(1);
        }
    }

    @Override
    public void about() {
        // Override the 'about' method to provide additional details
        super.about();
        displayMessage("TurtleGraphics - Extended features for drawing shapes!");
    }

    @Override
    public void processCommand(String command) {
        // Define how commands from the GUI will be processed
        if (command == null || command.isEmpty()) {
            displayMessage("No command entered.");
            return;
        }

        String[] parts = command.trim().split("\\s+");
        String mainCommand = parts[0].toLowerCase();

        try {
            switch (mainCommand) {
                case "square":
                    if (parts.length > 1) {
                        int side = Integer.parseInt(parts[1]);
                        drawSquare(side);
                        displayMessage("Drew a square with side length " + side);
                    } else {
                        displayMessage("Specify a side length for the square.");
                    }
                    break;

                case "triangle":
                    if (parts.length > 1) {
                        int side = Integer.parseInt(parts[1]);
                        drawTriangle(side);
                        displayMessage("Drew a triangle with side length " + side);
                    } else {
                        displayMessage("Specify a side length for the triangle.");
                    }
                    break;

                case "circle":
                    if (parts.length > 1) {
                        int radius = Integer.parseInt(parts[1]);
                        drawCircle(radius);
                        displayMessage("Drew a circle with radius " + radius);
                    } else {
                        displayMessage("Specify a radius for the circle.");
                    }
                    break;

                default:
                    displayMessage("Unknown command: " + mainCommand);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid numeric value in command: " + command);
        } catch (Exception e) {
            displayMessage("Error processing command: " + e.getMessage());
        }
    }
}