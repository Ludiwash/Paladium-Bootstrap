package fr.paladium.ui;

import fr.paladium.ui.utils.WindowMover;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class BootstrapFrame extends JFrame {
   public BootstrapFrame() {
      this.init();
      this.start();
   }

   private void init() {
      try {
         URL url = new URL("https://download.paladium-pvp.fr/icons/paladium-games-icon.png");
         ImageIcon icon = new ImageIcon(ImageIO.read(url));
         this.setIconImage(icon.getImage());
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      this.setTitle("Paladium Games Launcher");
      this.setSize(616, 840);
      this.setMinimumSize(this.getSize());
      this.setLocationRelativeTo((Component)null);
      this.setUndecorated(true);
      this.setDefaultCloseOperation(3);
      WindowMover mover = new WindowMover(this);
      this.addMouseListener(mover);
      this.addMouseMotionListener(mover);
      this.setContentPane(new BootstrapPanel());
   }

   private void start() {
      this.setVisible(true);
   }
}
