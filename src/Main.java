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

    private boolean isDrawableTriangle(int a, int b, int c) {
        // Triangle inequality
        if (a + b <= c || a + c <= b || b + c <= a)
            return false;

        // Law of Cosines for angles
        double angleA = Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2.0 * b * c)));
        double angleB = Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2.0 * a * c)));
        double angleC = 180 - angleA - angleB;

        // Check angle sum tolerance (to catch floating-point error)
        double angleSum = angleA + angleB + angleC;
        if (Math.abs(angleSum - 180) > 1.0) return false;

        // Check angles are not too extreme
        if (angleA < 20 || angleB < 20 || angleC < 20) return false;
        if (angleA > 150 || angleB > 150 || angleC > 150) return false;

        return true;
    }

    private void forwardTo(int x, int y) {
        int currentX = getxPos();
        int currentY = getyPos();

        double dx = x - currentX;
        double dy = y - currentY;
        double angleRad = Math.atan2(dy, dx);
        int distance = (int)Math.hypot(dx, dy);

        pointTurtle((int)Math.toDegrees(angleRad));
        forward(distance);
    }

    private void drawCustomTriangle(int a, int b, int c) {
        int originalX = getxPos();
        int originalY = getyPos();
        int originalAngle = getDirection();
        drawOff();

        // Start at point A (current position)
        int xA = originalX;
        int yA = originalY;

        // Point B is directly right (side a)
        int xB = xA + a;
        int yB = yA;

        // Calculate angles using Law of Cosines
        double angleA = Math.acos((b*b + c*c - a*a) / (2.0 * b * c));
        double angleB = Math.acos((a*a + c*c - b*b) / (2.0 * a * c));

        // Calculate point C using side b and angle A
        int xC = xA + (int)(b * Math.cos(angleA));
        int yC = yA + (int)(b * Math.sin(angleA));

        // Draw the triangle
        setxPos(xA);
        setyPos(yA);
        drawOn();

        forwardTo(xB, yB);  // Draw side AB (length a)
        forwardTo(xC, yC);  // Draw side BC (length c)
        forwardTo(xA, yA);  // Draw side CA (length b)

        // Restore turtle state
        drawOff();
        setxPos(originalX);
        setyPos(originalY);
        pointTurtle(originalAngle);
        drawOn();
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


    // Helper to calculate internal angle C
    private int getInternalAngle(int a, int b, int c) {
        double angleC = Math.acos((a*a + b*b - c*c) / (2.0 * a * b));
        return (int)Math.toDegrees(angleC);
    }

    public static void main(String[] args)
    {
        new Main(); //create instance of class that extends LBUGraphics (could be separate class without main), gets out of static context
    }

    public Main() {
        JFrame MainFrame = new JFrame();
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setLayout(new FlowLayout());
        MainFrame.add(this);
        MainFrame.pack();
        MainFrame.setVisible(true);

        setTurtleSpeed(2);       // Max speed
        setInternalTurtle(1);    // Minimal visual
        about();
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

                case "triangle":
                    if (parts.length < 2) {
                        System.out.println("❗ Missing parameters for 'triangle'");
                    } else if (parts[1].contains(",")) {
                        // Assume format is: triangle a,b,c
                        try {
                            String[] sides = parts[1].split(",");
                            if (sides.length != 3) {
                                System.out.println("❗ Usage: triangle <a>,<b>,<c>");
                            } else {
                                int a = Integer.parseInt(sides[0]);
                                int b = Integer.parseInt(sides[1]);
                                int c = Integer.parseInt(sides[2]);

                                if (isDrawableTriangle(a, b, c)) {
                                    drawCustomTriangle(a, b, c);
                                } else {
                                    System.out.println("❗ Invalid triangle sides (fails triangle inequality).");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'triangle' with sides must be numeric.");
                        }
                    }
                    else {
                        try {
                            int side = Integer.parseInt(parts[1]);
                            if (side <= 0) {
                                System.out.println("❗ Side length must be positive.");
                            } else {
                                drawEquilateralTriangle(side);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❗ 'triangle' requires a numeric side length or three comma-separated values.");
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