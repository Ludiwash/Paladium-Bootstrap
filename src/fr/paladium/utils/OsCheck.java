package fr.paladium.utils;

import fr.paladium.distribution.DistributionOS;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class OsCheck {
   private static DistributionOS detectedOS;

   public static DistributionOS getOperatingSystemType() {
      if (detectedOS == null) {
         String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
         if (!OS.contains("mac") && !OS.contains("darwin")) {
            if (OS.contains("win")) {
               detectedOS = DistributionOS.WINDOWS;
            } else if (OS.contains("nux")) {
               detectedOS = DistributionOS.LINUX;
            } else {
               detectedOS = DistributionOS.WINDOWS;
            }
         } else {
            detectedOS = DistributionOS.MACOS;
         }
      }

      return detectedOS;
   }

   public static File getAppData() {
      DistributionOS os = getOperatingSystemType();
      if (os == DistributionOS.WINDOWS) {
         return new File(System.getenv("APPDATA"));
      } else {
         return os == DistributionOS.MACOS ? new File(System.getProperty("user.home") + "/Library/Application Support") : new File(System.getProperty("user.home"));
      }
   }

   public static void killJava() {
      try {
         if (getOperatingSystemType() == DistributionOS.WINDOWS) {
            Runtime.getRuntime().exec("taskkill /f /im javaw.exe");
         } else {
            Runtime.getRuntime().exec("kill -9 java");
         }
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   public static void killJcefHelper() {
      try {
         if (getOperatingSystemType() == DistributionOS.WINDOWS) {
            Runtime.getRuntime().exec("taskkill /f /im jcef_helper.exe");
         } else {
            Runtime.getRuntime().exec("kill -9 jcef_helper");
         }
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }
}
