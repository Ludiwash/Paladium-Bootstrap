package fr.paladium;

import fr.paladium.distribution.DistributionFile;
import fr.paladium.distribution.DistributionOS;
import fr.paladium.distribution.LauncherDistribution;
import fr.paladium.ui.BootstrapFrame;
import fr.paladium.utils.BootstrapLogger;
import fr.paladium.utils.FileUtils;
import fr.paladium.utils.JsonOnlineParser;
import fr.paladium.utils.NotificationHelper;
import fr.paladium.utils.OsCheck;
import java.awt.Desktop;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Bootstrap {
   private static final String URL = "https://download.paladium-pvp.fr/games/bootstrap.json";
   private static final String VERSION = "1.0.1";
   private static boolean errored;

   public static void main(final String[] args) {
      System.out.println("[Bootstrap] v1.0.1");

      try {
         BootstrapLogger.setup();
         System.out.println("[Bootstrap] Loading logger");
      } catch (FileNotFoundException var2) {
         var2.printStackTrace();
         NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Impossible de créer le logger.", MessageType.ERROR);
         System.exit(1);
      }

      final JFrame frame = new BootstrapFrame();
      System.out.println("[Bootstrap] Starting thread");
      (new Thread(new Runnable() {
         public void run() {
            try {
               System.out.println("[Bootstrap] Checking jcef sanity");
               OsCheck.killJcefHelper();
               long start = System.currentTimeMillis();
               System.out.println("[Bootstrap] Parsing distribution");
               LauncherDistribution distribution = (LauncherDistribution)JsonOnlineParser.GSON.fromJson(JsonOnlineParser.parse("https://download.paladium-pvp.fr/games/bootstrap.json"), LauncherDistribution.class);
               if (distribution == null) {
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de la recherche de mise à jour (distribution).", MessageType.ERROR);
                  System.exit(1);
                  return;
               }

               if (distribution.version != null && !"1.0.1".equals(distribution.version)) {
                  System.out.println("[Bootstrap] Outdated bootstrap version : 1.0.1/" + distribution.version);
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une mise à jour du launcher est disponible.", MessageType.INFO);
                  int response = JOptionPane.showConfirmDialog(frame, "Voulez-vous télécharger la mise à jour");
                  if (response == 0) {
                     Desktop.getDesktop().browse(new URI("https://paladium-pvp.fr/#join-us"));
                     System.exit(0);
                  }
               }

               DistributionOS os = OsCheck.getOperatingSystemType();
               String java = (String)distribution.java.get(os);
               DistributionFile launcher = (DistributionFile)distribution.launcher.get(os);
               System.out.println("[Bootstrap] Checking java");
               if (java == null) {
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de la recherche de mise à jour (java).", MessageType.ERROR);
                  System.exit(1);
                  return;
               }

               System.out.println("[Bootstrap] Checking launcher");
               if (launcher == null) {
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de la recherche de mise à jour (launcher).", MessageType.ERROR);
                  System.exit(1);
                  return;
               }

               System.out.println("[Bootstrap] Checking installDir");
               File installDir = new File(OsCheck.getAppData(), "paladium-games");
               if (!installDir.exists()) {
                  installDir.mkdirs();
               }

               File javaDir = new File(installDir, "runtime");
               File javaFile = new File(javaDir, "java.zip");
               if (!javaDir.exists()) {
                  javaDir.mkdirs();
                  System.out.println("[Bootstrap] Downloading " + java + " at " + javaFile.getAbsolutePath());
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Téléchargement de java.", MessageType.INFO);
                  if (!FileUtils.downloadFile(java, javaFile)) {
                     NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors du téléchargement de java.", MessageType.ERROR);
                     System.exit(1);
                     return;
                  }

                  FileUtils.unzip(javaFile);
                  javaFile.delete();
               }

               String javaCommand = javaDir.getAbsolutePath() + File.separator + "bin" + File.separator + (os == DistributionOS.WINDOWS ? "javaw.exe" : "java");
               File javaPath = new File(javaCommand);
               if (!javaPath.exists()) {
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Impossible de vérifier la version de java.", MessageType.ERROR);
                  System.out.println("[Bootstrap] Unable to find " + javaPath.getAbsolutePath() + " (" + javaDir.exists() + ")");
                  org.apache.commons.io.FileUtils.forceDelete(javaDir);
                  Bootstrap.restart(args);
                  return;
               }

               Iterator var13 = FileUtils.listFileTree(javaDir).iterator();

               File launcherFile;
               while(var13.hasNext()) {
                  launcherFile = (File)var13.next();
                  if (!launcherFile.isDirectory()) {
                     launcherFile.setExecutable(true, false);
                  }
               }

               if (!javaPath.setExecutable(true)) {
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Impossible de configurer la version de java.", MessageType.ERROR);
                  System.exit(1);
                  return;
               }

               launcherFile = new File(installDir, "launcher.jar");
               boolean downloadLauncher = false;
               if (!launcherFile.exists()) {
                  downloadLauncher = true;
               } else {
                  downloadLauncher = !FileUtils.checkSha1(launcherFile, launcher.sha1);
                  if (downloadLauncher && !launcherFile.delete()) {
                     downloadLauncher = false;
                     OsCheck.killJava();
                     NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Impossible de télécharger la mise à jour car un autre launcher est ouvert.", MessageType.ERROR);
                     Bootstrap.restart(args);
                     return;
                  }
               }

               if (downloadLauncher) {
                  System.out.println("[Bootstrap] Downloading " + launcher.url + " at " + launcherFile.getAbsolutePath());
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Téléchargement d'une mise à jour.", MessageType.INFO);
                  if (!FileUtils.downloadFile(launcher.url, launcherFile)) {
                     NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors du téléchargement de la mise à jour.", MessageType.ERROR);
                     System.exit(1);
                     return;
                  }
               }

               long end = System.currentTimeMillis();
               if (end - start < 1000L) {
                  Thread.sleep(1000L);
               }

               ProcessBuilder builder = new ProcessBuilder(new String[0]);
               builder.directory(installDir);
               List<String> commands = new ArrayList();
               commands.add(javaCommand);
               commands.add("-jar");
               commands.add(launcherFile.getAbsolutePath());
               String parsedCommand = "";

               String cmd;
               for(Iterator var20 = commands.iterator(); var20.hasNext(); parsedCommand = parsedCommand + cmd + " ") {
                  cmd = (String)var20.next();
               }

               System.out.println("[Bootstrap] Running " + parsedCommand);

               try {
                  builder.command(commands).start();
               } catch (IOException var21) {
                  var21.printStackTrace();
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de l'exécution de java.", MessageType.ERROR);
                  org.apache.commons.io.FileUtils.forceDelete(javaDir);
                  Bootstrap.restart(args);
                  return;
               } catch (Exception var22) {
                  var22.printStackTrace();
                  NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de l'exécution du programme.", MessageType.ERROR);
                  System.exit(1);
               }
            } catch (Exception var23) {
               var23.printStackTrace();
               NotificationHelper.sendSystemNotification("Paladium Games Launcher", "Une erreur est survenue lors de l'exécution du programme.", MessageType.ERROR);
               System.exit(1);
            }

            System.exit(0);
         }
      }, "Paladium-Games-Thread")).start();
   }

   private static void restart(String[] args) {
      if (errored) {
         System.exit(1);
      } else {
         errored = true;
         main(args);
      }
   }
}
