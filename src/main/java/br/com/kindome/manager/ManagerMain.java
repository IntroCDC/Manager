package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 13:27
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

public class ManagerMain {

    public final static String API = "api.kindome.com.br";
    public final static String FILE = "Manager.jar";
    public static final String REMOTE_MANAGER = "http://kindo.me/Manager.jar";
    public static final String UPDATER = "Updater.jar";
    public static final String VERSION = "1.1";

    public static final JsonParser PARSER = new JsonParser();

    public static void main(String[] args) {
        if (getFileName().equalsIgnoreCase(UPDATER)) {
            update();
        } else {
            deleteOldVersions();
            if (verifyUpdate()) {
                return;
            }
            new Thread(() -> deleteFile(new File(UPDATER))).start();
            startManager();
        }
    }

    public static void startManager() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    public static void deleteOldVersions() {
        for (File file : new File(System.getProperty("user.dir")).listFiles()) {
            if (file.getName().startsWith("Manager") && file.getName().endsWith(".jar") && !file.getName().equalsIgnoreCase(getFileName())) {
                deleteFile(file);
            }
        }
    }

    public static String hashSha512(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(String.valueOf(passwordToHash.hashCode()).getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return generatedPassword;
    }

    public static JsonElement readJson(String url) throws IOException {
        URLConnection connection = new URL(
                (url.startsWith("http") ? "" : "http://" + ManagerMain.API + "/") + url
        ).openConnection();
        connection.addRequestProperty("User-Agent", "Manager");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return PARSER.parse(reader);
    }

    public static String toDate(long number) {
        return TIME_FORMATTER.format(
                Instant.ofEpochMilli(number).atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral(" - ")
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter();

    public static boolean verifyUpdate() {
        long remoteSize = getRemoteFileSize(REMOTE_MANAGER);
        long localSize = getLocalFileSize(getFileName());
        if (localSize == -1) {
            return false;
        }
        if (remoteSize != localSize) {
            JOptionPane.showMessageDialog(null, "Baixando última versão do Manager...");
            downloadFile(REMOTE_MANAGER, new File(UPDATER));
            runJar(UPDATER);
            return true;
        }
        return false;
    }

    public static void update() {
        downloadFile(REMOTE_MANAGER, new File(FILE));
        JOptionPane.showMessageDialog(null, "Versão atualizada baixada, reiniciando...");
        runJar(FILE);
    }

    public static long getRemoteFileSize(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getContentLengthLong();
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor api... " + exception.getMessage());
            exception.printStackTrace();
        }
        return -1;
    }

    public static long getLocalFileSize(String localFileName) {
        File f = new File(localFileName);
        if (f.exists() && f.isFile()) {
            return f.length();
        }
        return -1;
    }

    public static void runJar(String jarName) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-jar",
                    jarName
            );
            pb.inheritIO();
            pb.start();
            System.exit(0);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro ao executar o " + jarName);
        }
    }

    public static boolean downloadFile(String URL, File destiny) {
        try {
            if (!destiny.exists()) {
                if (destiny.getParentFile() != null) {
                    destiny.getParentFile().mkdirs();
                }
                destiny.createNewFile();
            }
            URLConnection connection = new URL(URL).openConnection();
            connection.addRequestProperty("User-Agent", "IntroCDC-Kindome");
            if (destiny.exists() && destiny.length() == connection.getContentLengthLong()) {
                return false;
            }
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream()); FileOutputStream fout = new FileOutputStream(destiny)) {
                byte[] data = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                }
                fout.flush();
            }
            return true;
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro ao baixar a última versão do Manager!");
        }
        return false;
    }

    public static String getFileName() {
        try {
            String jarPath = ManagerMain.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            return jarPath.substring(jarPath.lastIndexOf("/") + 1);
        } catch (Exception ignored) {
        }
        return "Manager.jar";
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            deleteFile(file.toPath());
        }
    }

    public static void deleteFile(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                private FileVisitResult handleException(IOException exception) {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exception) throws IOException {
                    if (exception != null) {
                        return handleException(exception);
                    }
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exception) {
                    return handleException(exception);
                }
            });
        } catch (Exception ignored) {
        }
    }

}
