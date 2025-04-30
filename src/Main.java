import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import uk.ac.leedsbeckett.oop.LBUGraphics;



public class Main extends LBUGraphics
{
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

    public static void main(String[] args)
    {
        new Main(); //create instance of class that extends LBUGraphics (could be separate class without main), gets out of static context
    }

    public Main()
    {
        JFrame MainFrame = new JFrame();                //create a frame to display the turtle panel on
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Make sure the app exits when closed
        MainFrame.setLayout(new FlowLayout());  //not strictly necessary
        MainFrame.add(this);                                    //"this" is this object that extends turtle graphics so we are adding a turtle graphics panel to the frame
        MainFrame.pack();                                               //set the frame to a size we can see
        MainFrame.setVisible(true);                             //now display it
        about();                                                                //call the LBUGraphics about method to display version information.
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