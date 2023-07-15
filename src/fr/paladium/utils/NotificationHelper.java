package fr.paladium.utils;

import fr.paladium.distribution.DistributionOS;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

public class NotificationHelper {
   public static void sendSystemNotification(String title, String message, MessageType type) {
      if (OsCheck.getOperatingSystemType() == DistributionOS.MACOS) {
         try {
            Runtime.getRuntime().exec(new String[]{"osascript", "-e", "display notification \"" + message + "\" with title \"" + title + "\" subtitle \"" + type + "\" sound name \"Funk\""});
            return;
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

      if (SystemTray.isSupported()) {
         SystemTray tray = SystemTray.getSystemTray();
         Image image = Toolkit.getDefaultToolkit().createImage("https://download.paladium-pvp.fr/icons/paladium-launcher.png");
         TrayIcon trayIcon = new TrayIcon(image, title);
         trayIcon.setImageAutoSize(true);
         trayIcon.setToolTip(title);

         try {
            tray.add(trayIcon);
         } catch (AWTException var7) {
            var7.printStackTrace();
         }

         trayIcon.displayMessage(title, message, type);
      }

   }
}
