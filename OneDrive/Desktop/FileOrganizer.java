import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizer {

    // Map file extensions to folder names
    private static final Map<String, String> extensionMap = new HashMap<>() {{
        put(".jpg", "Images");
        put(".jpeg", "Images");
        put(".png", "Images");
        put(".gif", "Images");
        put(".bmp", "Images");
        put(".txt", "TextDocs");
        put(".pdf", "PDFs");
        put(".doc", "MSDocs");
        put(".docx", "MSDocs");
        put(".xls", "MSDocs");
        put(".xlsx", "MSDocs");
        put(".ppt", "MSDocs");
        put(".pptx", "MSDocs");
    }};

    public static void main(String[] args) {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        organizeFiles(desktopPath);
    }

    public static void organizeFiles(String directoryPath) {
        File folder = new File(directoryPath);
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("No files found in the directory.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                String extension = getFileExtension(file);
                String category = extensionMap.getOrDefault(extension, "Others");

                String dateFolder = getFileDate(file);
                Path targetDir = Paths.get(directoryPath, category, dateFolder);

                try {
                    Files.createDirectories(targetDir);
                    Files.move(file.toPath(), targetDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved: " + file.getName() + " to " + targetDir);
                } catch (IOException e) {
                    System.err.println("Error moving file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName().toLowerCase();
        int lastIndex = name.lastIndexOf('.');
        return lastIndex != -1 ? name.substring(lastIndex) : "";
    }

    private static String getFileDate(File file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime time = attrs.creationTime();
            Date date = new Date(time.toMillis());
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (IOException e) {
            return "UnknownDate";
        }
    }
}