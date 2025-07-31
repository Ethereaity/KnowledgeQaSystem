package org.example.knowledgeqasystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileInfo {
    private String name;
    private String path;
    private long size;
    private String uploadTime;

    public FileInfo(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.uploadTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getUploadTime() { return uploadTime; }
    public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }
}