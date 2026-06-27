import Controller.GameController;
import View.GameGUI;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        GameController controller = new GameController();
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI(controller);
            controller.startGUI();
        });
    }
}
