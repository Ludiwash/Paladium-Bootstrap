package fr.paladium.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.codec.digest.DigestUtils;

public class FileUtils {
   public static boolean checkSha1(File file, String sha1) throws IOException {
      FileInputStream fileInputStream = new FileInputStream(file);
      String fileSha1 = DigestUtils.sha1Hex(fileInputStream);
      fileInputStream.close();
      return fileSha1.equals(sha1);
   }

   public static boolean downloadFile(String url, File file) {
      try {
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), file);
         return true;
      } catch (IOException var3) {
         var3.printStackTrace();
         return false;
      }
   }

   public static void unzip(File file) throws IOException {
      byte[] buffer = new byte[1024];
      ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

      for(ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
         File newFile = newFileFromZip(file.getParentFile(), zipEntry);
         if (zipEntry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
               throw new IOException("Failed to create directory " + newFile);
            }
         } else {
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
               throw new IOException("Failed to create directory " + parent);
            }

            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            while((len = zis.read(buffer)) > 0) {
               fos.write(buffer, 0, len);
            }

            fos.close();
         }
      }

      zis.closeEntry();
      zis.close();
   }

   private static File newFileFromZip(File destinationDir, ZipEntry zipEntry) throws IOException {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();
      if (!destFilePath.startsWith(destDirPath + File.separator)) {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      } else {
         return destFile;
      }
   }

   public static Collection<File> listFileTree(File dir) {
      Set<File> fileTree = new HashSet();
      if (dir != null && dir.listFiles() != null) {
         File[] var5;
         int var4 = (var5 = (File[])Objects.requireNonNull(dir.listFiles())).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File entry = var5[var3];
            if (entry.isFile()) {
               fileTree.add(entry);
            } else {
               fileTree.addAll(listFileTree(entry));
            }
         }

         return fileTree;
      } else {
         return fileTree;
      }
   }
}
