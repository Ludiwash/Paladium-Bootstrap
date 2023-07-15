package fr.paladium.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class BootstrapPanel extends JPanel {
   private Image img;

   public BootstrapPanel() {
      this.setLayout(new BorderLayout());
      this.setBackground(Color.BLACK);

      try {
         this.img = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("background.png"));
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public void paint(Graphics g) {
      super.paint(g);
      if (this.img != null) {
         g.drawImage(this.img, 0, 0, this.getWidth(), this.getHeight(), this);
      }

   }
}
