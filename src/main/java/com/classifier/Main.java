package com.classifier;

import com.classifier.analyzer.TextAnalyzer;
import com.classifier.model.AnalysisResult;
import com.classifier.parser.DocumentParser;
import com.classifier.report.ChartGenerator;
import com.classifier.report.ReportGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * Главный класс приложения для определения тематики текста.
 * Приложение анализирует текстовые файлы (txt, doc, docx) и определяет
 * их тематику на основе словарного анализа.
 *
 * @version 1.0.0
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Точка входа в приложение.
     * Принимает путь к файлу из командной строки и выполняет анализ текста.
     *
     * @param args аргументы командной строки, где args[0] - путь к анализируемому файлу
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("Не указан путь к файлу");
            System.out.println("Использование: java -jar text-classifier.jar <путь_к_файлу>");
            return;
        }

        try {
            Path filePath = Path.of(args[0]);
            logger.info("Начало анализа файла: {}", filePath);

            DocumentParser parser = new DocumentParser();
            String text = parser.parse(filePath);

            TextAnalyzer analyzer = new TextAnalyzer();
            AnalysisResult result = analyzer.analyze(text);

            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport(result, "report.txt");

            ChartGenerator chartGenerator = new ChartGenerator();
            chartGenerator.generateChart(result, "statistics.png");

            System.out.println("Тематика текста: " + result.getTopTopic().getDisplayName());
            logger.info("Анализ завершен. Тематика: {}", result.getTopTopic());

        } catch (Exception e) {
            logger.error("Ошибка при обработке файла", e);
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
