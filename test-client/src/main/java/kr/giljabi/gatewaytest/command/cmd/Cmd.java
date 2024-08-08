package kr.giljabi.gatewaytest.command.cmd;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author : eahn.park@gmail.com
 */
public class Cmd {
    public static List<FileInfo> dir(String directoryPath) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path file : stream) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setDirectory(Files.isDirectory(file));
                fileInfo.setFileName(file.getFileName().toString());
                fileInfo.setFilePath(file.toString());
                fileInfo.setFileExtension(getFileExtension(file));
                fileInfo.setFileSize(Files.size(file));

//                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
//                fileInfo.setFileLastModifiedTime(attrs.lastModifiedTime());
//                fileInfo.setFileLastAccessTime(attrs.lastAccessTime());
//                fileInfo.setFileCreationTime(attrs.creationTime());

                fileInfoList.add(fileInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileInfoList;
    }

    private static String getFileExtension(Path file) {
        String fileName = file.toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

}
