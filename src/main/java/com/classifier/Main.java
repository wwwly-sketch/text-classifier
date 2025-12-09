package com.classifier;

import com.classifier.analyzer.TextAnalyzer;
import com.classifier.model.AnalysisResult;
import com.classifier.parser.DocumentParser;
import com.classifier.report.ChartGenerator;
import com.classifier.report.ReportGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Главный класс приложения для определения тематики текста.
 *
 * @version 1.0.0
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки (путь к файлу)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        Path filePath = Path.of(args[0]);

        // Проверка существования файла
        if (!Files.exists(filePath)) {
            logger.error("Файл не найден: {}", filePath);
            System.err.println("Ошибка: Файл не найден - " + filePath);
            System.err.println("Проверьте правильность указанного пути.");
            return;
        }

        if (!Files.isRegularFile(filePath)) {
            logger.error("Указанный путь не является файлом: {}", filePath);
            System.err.println("Ошибка: Указанный путь не является файлом - " + filePath);
            return;
        }

        try {
            logger.info("Начало анализа файла: {}", filePath);

            DocumentParser parser = new DocumentParser();
            String text = parser.parse(filePath);

            TextAnalyzer analyzer = new TextAnalyzer();
            AnalysisResult result = analyzer.analyze(text);

            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport(result, "report.txt");

            ChartGenerator chartGenerator = new ChartGenerator();
            chartGenerator.generateChart(result, "statistics.png");

            System.out.println("========================================");
            System.out.println("Анализ завершён успешно!");
            System.out.println("----------------------------------------");
            System.out.println("Тематика текста: " + result.getTopTopic().getDisplayName());
            System.out.println("Всего слов: " + result.getTotalWords());
            System.out.println("Совпадений: " + result.getTopicScores().get(result.getTopTopic()));
            System.out.println("----------------------------------------");
            System.out.println("Отчёт сохранён: report.txt");
            System.out.println("Диаграмма сохранена: statistics.png");
            System.out.println("========================================");

            logger.info("Анализ завершен. Тематика: {}", result.getTopTopic());

        } catch (IllegalArgumentException e) {
            logger.error("Неподдерживаемый формат файла: {}", e.getMessage());
            System.err.println("Ошибка: " + e.getMessage());
            System.err.println("Поддерживаемые форматы: .txt, .doc, .docx");

        } catch (NoSuchFileException e) {
            logger.error("Файл не найден: {}", e.getMessage());
            System.err.println("Ошибка: Файл не найден - " + e.getMessage());

        } catch (IOException e) {
            logger.error("Ошибка чтения файла: {}", e.getMessage());
            System.err.println("Ошибка: Не удалось прочитать файл.");
            System.err.println("Возможно, файл повреждён или недоступен для чтения.");

        } catch (Exception e) {
            logger.error("Непредвиденная ошибка: {}", e.getMessage(), e);
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }

    /**
     * Выводит справку по использованию приложения.
     */
    private static void printUsage() {
        logger.warn("Приложение запущено без аргументов");
        System.out.println("========================================");
        System.out.println("Определение тематики текста v1.0.0");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Использование:");
        System.out.println("  java -jar text-classifier-1.0.0.jar <путь_к_файлу>");
        System.out.println();
        System.out.println("Поддерживаемые форматы:");
        System.out.println("  .txt  - текстовые файлы");
        System.out.println("  .doc  - документы Word 97-2003");
        System.out.println("  .docx - документы Word 2007+");
        System.out.println();
        System.out.println("Пример:");
        System.out.println("  java -jar text-classifier-1.0.0.jar document.docx");
        System.out.println();
        System.out.println("Тематики: Медицина, История, Программирование,");
        System.out.println("          Сети, Криптография, Финансы");
        System.out.println("========================================");
    }
}
