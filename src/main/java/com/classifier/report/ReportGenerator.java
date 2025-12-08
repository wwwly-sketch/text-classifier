package com.classifier.report;

import com.classifier.model.AnalysisResult;
import com.classifier.model.Topic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Генератор текстовых отчетов по результатам анализа.
 * Создает файл отчета со статистикой совпадений по всем тематикам.
 *
 * @version 1.0.0
 */
public class ReportGenerator {

    private static final Logger logger = LogManager.getLogger(ReportGenerator.class);

    /**
     * Создает новый экземпляр генератора отчетов.
     */
    public ReportGenerator() {
        // Конструктор по умолчанию
    }

    /**
     * Генерирует текстовый отчет и сохраняет его в файл.
     * Отчет содержит общую статистику и детализацию совпадений.
     *
     * @param result результат анализа текста
     * @param fileName имя файла для сохранения отчета
     */
    public void generateReport(AnalysisResult result, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writeHeader(writer, result);
            writeTopicStatistics(writer, result);
            writeMatchDetails(writer, result);
            writeConclusion(writer, result);
            logger.info("Отчет сохранен: {}", fileName);
        } catch (IOException e) {
            logger.error("Ошибка записи отчета", e);
        }
    }

    /**
     * Записывает заголовок отчета.
     *
     * @param writer поток вывода
     * @param result результат анализа
     */
    private void writeHeader(PrintWriter writer, AnalysisResult result) {
        writer.println("=== ОТЧЕТ АНАЛИЗА ТЕКСТА ===\n");
        writer.printf("Всего слов в тексте: %d%n%n", result.getTotalWords());
    }

    /**
     * Записывает статистику по всем тематикам.
     *
     * @param writer поток вывода
     * @param result результат анализа
     */
    private void writeTopicStatistics(PrintWriter writer, AnalysisResult result) {
        writer.println("--- Статистика по тематикам ---");
        for (Topic topic : Topic.values()) {
            writer.printf("%s: %d совпадений (%.2f%%)%n",
                    topic.getDisplayName(),
                    result.getTopicScores().get(topic),
                    result.getPercentage(topic));
        }
    }

    /**
     * Записывает детализацию совпадений по каждой тематике.
     *
     * @param writer поток вывода
     * @param result результат анализа
     */
    private void writeMatchDetails(PrintWriter writer, AnalysisResult result) {
        writer.println("\n--- Детализация совпадений ---");
        for (Topic topic : Topic.values()) {
            Map<String, Integer> matches = result.getMatchDetails().get(topic);
            if (!matches.isEmpty()) {
                writer.printf("\n%s:%n", topic.getDisplayName());
                matches.forEach((word, count) ->
                        writer.printf("  %s: %d%n", word, count));
            }
        }
    }

    /**
     * Записывает итоговый результат анализа.
     *
     * @param writer поток вывода
     * @param result результат анализа
     */
    private void writeConclusion(PrintWriter writer, AnalysisResult result) {
        writer.printf("%n=== РЕЗУЛЬТАТ: %s ===%n", result.getTopTopic().getDisplayName());
    }
}
