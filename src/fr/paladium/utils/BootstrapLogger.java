package fr.paladium.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class BootstrapLogger {
   public static void setup() throws FileNotFoundException {
      File installDir = new File(OsCheck.getAppData(), "paladium-games");
      if (!installDir.exists()) {
         installDir.mkdirs();
      }

      File logFile = new File(installDir, "bootstrap.log");
      if (logFile.exists()) {
         logFile.delete();
      }

      final PrintStream out = new PrintStream(new FileOutputStream(logFile, true), true);
      final OutputStream stdOut = System.out;
      System.setOut(new PrintStream(new OutputStream() {
         public void write(int b) throws IOException {
            out.write(b);
            stdOut.write(b);
         }

         public void flush() throws IOException {
            super.flush();
            out.flush();
            stdOut.flush();
         }

         public void close() throws IOException {
            super.close();
            out.close();
         }
      }));
      final OutputStream stdErr = System.err;
      System.setErr(new PrintStream(new OutputStream() {
         public void write(int b) throws IOException {
            out.write(b);
            stdErr.write(b);
         }

         public void flush() throws IOException {
            super.flush();
            out.flush();
            stdErr.flush();
         }

         public void close() throws IOException {
            super.close();
            out.close();
         }
      }));
   }
}
