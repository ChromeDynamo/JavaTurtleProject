import java.awt.FlowLayout;
import javax.swing.JFrame;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class Main extends LBUGraphics
{
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
                        System.out.println("Missing angle for 'left'");
                    } else {
                        int angle = Integer.parseInt(parts[1]);
                        left(angle);
                    }
                    break;
                case "right":
                    if (parts.length < 2) {
                        System.out.println("Missing angle for 'right'");
                    } else {
                        int angle = Integer.parseInt(parts[1]);
                        right(angle);
                    }
                    break;
                case "move":
                    if (parts.length < 2) {
                        System.out.println("Missing distance for 'move'");
                    } else {
                        int dist = Integer.parseInt(parts[1]);
                        forward(dist);
                    }
                    break;
                case "reverse":
                    if (parts.length < 2) {
                        System.out.println("Missing distance for 'reverse'");
                    } else {
                        int dist = Integer.parseInt(parts[1]);
                        forward(-dist); // Negative distance = backward
                    }
                    break;
                case "reset":
                    reset();
                    break;
                case "clear":
                    clear();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric parameter.");
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

}