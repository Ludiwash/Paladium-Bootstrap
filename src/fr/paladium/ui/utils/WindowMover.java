package fr.paladium.ui.utils;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

public class WindowMover extends MouseAdapter {
   private Point click;
   private final JFrame window;

   public WindowMover(JFrame window) {
      this.window = window;
   }

   public void mouseDragged(MouseEvent e) {
      if (this.click != null) {
         Point draggedPoint = MouseInfo.getPointerInfo().getLocation();
         if (this.window.getExtendedState() == 6) {
            this.window.setExtendedState(0);
         }

         this.window.setLocation(new Point((int)draggedPoint.getX() - (int)this.click.getX(), (int)draggedPoint.getY() - (int)this.click.getY()));
      }

   }

   public void mousePressed(MouseEvent e) {
      this.click = e.getPoint();
   }
}
