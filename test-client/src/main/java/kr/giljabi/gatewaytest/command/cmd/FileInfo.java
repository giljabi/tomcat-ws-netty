package kr.giljabi.gatewaytest.command.cmd;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author : eahn.park@gmail.com
 */
@Getter
@Setter
public class FileInfo {
    private boolean isDirectory;
    private String fileName;
    private String filePath;
    private String fileExtension;
    private long fileSize;
    //private FileTime fileLastModifiedTime;
    //private FileTime fileLastAccessTime;
    //private FileTime fileCreationTime;
}