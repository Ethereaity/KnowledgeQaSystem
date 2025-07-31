package org.example.knowledgeqasystem.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.knowledgeqasystem.service.QAService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class QAController {

    private final QAService qaService;

    @Autowired
    public QAController(QAService qaService) {
        this.qaService = qaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        System.out.println(Arrays.toString(files));
        return qaService.uploadFiles(files);
    }

    @PostMapping("/ask")
    public ResponseEntity<?> processQuestion(@RequestBody Map<String, String> request) throws UnirestException, IOException, JSONException {
        String question = request.get("question");
        return qaService.processQuestion(question);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearAll() {
        return qaService.clearAll();
    }

    @GetMapping("/files")
    public ResponseEntity<?> getUploadedFiles() {
        return qaService.getUploadedFiles();
    }

    @GetMapping("/file-contents")
    public ResponseEntity<?> getAllFileContents() {
        return ResponseEntity.ok(Map.of(
                "contents", qaService.getAllFileContents(),
                "fileCount", qaService.getFileCount()
        ));
    }
}