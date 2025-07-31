package org.example.knowledgeqasystem.service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

public class FileParser {
    public static String parseFile(MultipartFile file) throws IOException {
        String filename = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();

        if (filename.endsWith(".txt")) {
            return new String(file.getBytes());
        } else if (filename.endsWith(".docx") || filename.endsWith(".doc")) {
            File nowfile = new File("./upload/" + filename);
            if (!nowfile.exists()) {
                throw new IOException("文件不存在: " + nowfile.getAbsolutePath());
            }
            if (nowfile.length() == 0) {
                throw new IOException("文件为空: " + nowfile.getAbsolutePath());
            }
            InputStream fis = new FileInputStream(nowfile);
            XWPFDocument document = new XWPFDocument(fis);
            StringBuilder content = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                content.append(paragraph.getText());
                content.append("\n");
            }
            document.close();
            fis.close();
            return content.toString();
        } else if (filename.endsWith(".pdf")) {
            File nowfile = new File("./upload/" + filename);
            if (!nowfile.exists()) {
                throw new IOException("文件不存在: " + nowfile.getAbsolutePath());
            }
            if (nowfile.length() == 0) {
                throw new IOException("文件为空: " + nowfile.getAbsolutePath());
            }
            try (PDDocument document = PDDocument.load(nowfile)) {
                PDFTextStripper textStripper = new PDFTextStripper();
                return textStripper.getText(document);
            }
        }
        throw new UnsupportedOperationException("不支持的格式: " + filename);
    }
}
