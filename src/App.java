import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class App extends JFrame implements NativeKeyListener {
    static String middlebg = "res/bongo_middle.png";
    static String leftbg = "res/bongo_left.png";
    static String rightbg = "res/bongo_right.png";

    JPanel panel;

    public void nativeKeyPressed(NativeKeyEvent e) {
        panel.revalidate();
        panel.repaint();

		if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }
	}

    public void nativeKeyReleased(NativeKeyEvent e) {
		// System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		// System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	}

    public App() {
        setTitle("Bongo Cat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(457, 298));
        setAlwaysOnTop (true);

        panel = new JPanel(){
            BufferedImage middle = loadImage("res/bongo_middle.png");
            BufferedImage left = loadImage("res/bongo_left.png");
            BufferedImage right = loadImage("res/bongo_right.png");

            BufferedImage currentImage = middle;
            Boolean init = true;

            Timer timer = new Timer();
            TimerTask task;

            public BufferedImage loadImage(String path){
                URL imagePath = getClass().getResource(path);
                BufferedImage result = null;
                try {
                    result = ImageIO.read(imagePath);
                } catch (IOException e) {
                    System.err.println("Errore, immagine non trovata");
                }
                return result;
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension size = getSize();

                if (currentImage == left) {
                    currentImage = right;
                } else if (currentImage == right) {
                    currentImage = left;
                } else if (currentImage == middle && !init) {
                    currentImage = left;
                }

                init = false;

                if (task != null) {
                    task.cancel();
                    task = null;
                    init = true;
                }

                task = new TimerTask() {
                    @Override
                    public void run() {
                        currentImage = middle;
                        init = true;
                        task = null;
                        panel.revalidate();
                        panel.repaint();
                    }
                };

                timer.schedule(task, 500);

                g.drawImage(currentImage, 0, 0,size.width, size.height,0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
            }
        };

        getContentPane().add(panel);

        pack();
        setVisible(true);

        try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

        GlobalScreen.addNativeKeyListener(this);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App();
            }
        });
    }
}