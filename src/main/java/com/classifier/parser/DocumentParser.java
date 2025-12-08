package com.classifier.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Парсер документов различных форматов.
 * Поддерживает извлечение текста из файлов форматов TXT, DOC и DOCX.
 *
 * @version 1.0.0
 */
public class DocumentParser {

    private static final Logger logger = LogManager.getLogger(DocumentParser.class);

    /**
     * Создает новый экземпляр парсера документов.
     */
    public DocumentParser() {
        // Конструктор по умолчанию
    }

    /**
     * Извлекает текстовое содержимое из файла.
     * Автоматически определяет формат файла по расширению и применяет
     * соответствующий метод парсинга.
     *
     * @param filePath путь к файлу для парсинга
     * @return извлеченный текст из файла
     * @throws IOException если произошла ошибка при чтении файла
     * @throws IllegalArgumentException если формат файла не поддерживается
     */
    public String parse(Path filePath) throws IOException {
        String fileName = filePath.toString().toLowerCase();
        logger.debug("Парсинг файла: {}", fileName);

        if (fileName.endsWith(".txt")) {
            return parseTxt(filePath);
        } else if (fileName.endsWith(".docx")) {
            return parseDocx(filePath);
        } else if (fileName.endsWith(".doc")) {
            return parseDoc(filePath);
        }

        throw new IllegalArgumentException("Неподдерживаемый формат файла: " + fileName);
    }

    /**
     * Извлекает текст из TXT файла.
     *
     * @param filePath путь к TXT файлу
     * @return содержимое текстового файла
     * @throws IOException если произошла ошибка при чтении файла
     */
    private String parseTxt(Path filePath) throws IOException {
        logger.debug("Чтение TXT файла");
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    /**
     * Извлекает текст из DOCX файла с использованием Apache POI.
     *
     * @param filePath путь к DOCX файлу
     * @return извлеченный текст из документа
     * @throws IOException если произошла ошибка при чтении файла
     */
    private String parseDocx(Path filePath) throws IOException {
        logger.debug("Чтение DOCX файла");
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }

    /**
     * Извлекает текст из DOC файла с использованием Apache POI HWPF.
     *
     * @param filePath путь к DOC файлу
     * @return извлеченный текст из документа
     * @throws IOException если произошла ошибка при чтении файла
     */
    private String parseDoc(Path filePath) throws IOException {
        logger.debug("Чтение DOC файла");
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             HWPFDocument document = new HWPFDocument(fis)) {

            return document.getDocumentText();
        }
    }
}
