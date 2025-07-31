package org.example.knowledgeqasystem.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.knowledgeqasystem.model.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QAService {

    private final List<FileInfo> uploadedFiles = new ArrayList<>();
    private static final String UPLOAD_DIR = "./upload/";
    private final AIController controller = new AIController();

    // 存储所有上传文件的内容
    private StringBuilder allFileContents = new StringBuilder();

    public ResponseEntity<?> uploadFiles(MultipartFile[] files) {
        List<FileInfo> uploaded = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            try {
                // 创建上传目录
                java.io.File uploadDir = new java.io.File(UPLOAD_DIR);
                if (!uploadDir.exists())
                    uploadDir.mkdirs();

                // 保存文件
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());

                // 记录文件信息
                FileInfo fileInfo = new FileInfo(
                        file.getOriginalFilename(),
                        filePath.toString(),
                        file.getSize()
                );
                uploadedFiles.add(fileInfo);
                uploaded.add(fileInfo);

                // 读取文件内容并添加到所有文件内容中
                String content = FileParser.parseFile(file);
                allFileContents.append("===== 文件: ").append(fileInfo.getName()).append(" =====\n");
                allFileContents.append(content).append("\n\n");

            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "文件上传失败: " + file.getOriginalFilename()));
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "成功上传 " + uploaded.size() + " 个文件",
                "files", uploaded
        ));
    }

    public ResponseEntity<?> processQuestion(String question) throws UnirestException, IOException {
        // 检查是否有上传的文件
        if (uploadedFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "请先上传文件",
                    "message", "系统需要文档内容来回答问题"
            ));
        }

        // 使用存储的文件内容生成回答
        String answer = controller.tallQuestion(question,getAllFileContents());

        return ResponseEntity.ok(Map.of(
                "question", question,
                "answer", answer,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        ));
    }

    public ResponseEntity<?> clearAll() {
        // 删除所有上传的文件
        for (FileInfo file : uploadedFiles) {
            try {
                Files.deleteIfExists(Paths.get(file.getPath()));
            } catch (IOException e) {
                // 忽略删除失败的文件
            }
        }
        uploadedFiles.clear();

        // 清空所有文件内容
        allFileContents = new StringBuilder();

        return ResponseEntity.ok(Map.of(
                "message", "已清除所有内容和文件",
                "deletedFiles", uploadedFiles.size()
        ));
    }

    public ResponseEntity<?> getUploadedFiles() {
        return ResponseEntity.ok(uploadedFiles);
    }

    // 获取所有文件内容
    public String getAllFileContents() {
        return allFileContents.toString();
    }

    // 获取文件数量
    public int getFileCount() {
        return uploadedFiles.size();
    }
}