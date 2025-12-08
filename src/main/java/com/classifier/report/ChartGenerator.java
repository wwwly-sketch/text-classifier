package com.classifier.report;

import com.classifier.model.AnalysisResult;
import com.classifier.model.Topic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;

/**
 * Генератор графиков статистики анализа.
 * Создает столбчатые диаграммы с распределением совпадений по тематикам.
 *
 * @version 1.0.0
 */
public class ChartGenerator {

    private static final Logger logger = LogManager.getLogger(ChartGenerator.class);

    /** Ширина генерируемого изображения в пикселях. */
    private static final int CHART_WIDTH = 800;

    /** Высота генерируемого изображения в пикселях. */
    private static final int CHART_HEIGHT = 600;

    /**
     * Создает новый экземпляр генератора графиков.
     */
    public ChartGenerator() {
        // Конструктор по умолчанию
    }

    /**
     * Генерирует столбчатую диаграмму и сохраняет её в файл PNG.
     *
     * @param result результат анализа текста
     * @param fileName имя файла для сохранения графика
     */
    public void generateChart(AnalysisResult result, String fileName) {
        DefaultCategoryDataset dataset = createDataset(result);
        JFreeChart chart = createChart(dataset);
        saveChart(chart, fileName);
    }

    /**
     * Создает набор данных для диаграммы из результатов анализа.
     *
     * @param result результат анализа
     * @return набор данных для построения диаграммы
     */
    private DefaultCategoryDataset createDataset(AnalysisResult result) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Topic topic : Topic.values()) {
            dataset.addValue(
                    result.getTopicScores().get(topic),
                    "Совпадения",
                    topic.getDisplayName()
            );
        }
        return dataset;
    }

    /**
     * Создает объект диаграммы с заданным набором данных.
     *
     * @param dataset набор данных для диаграммы
     * @return сконфигурированный объект диаграммы
     */
    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        return ChartFactory.createBarChart(
                "Статистика по тематикам",
                "Тематика",
                "Количество совпадений",
                dataset
        );
    }

    /**
     * Сохраняет диаграмму в файл формата PNG.
     *
     * @param chart объект диаграммы
     * @param fileName имя файла для сохранения
     */
    private void saveChart(JFreeChart chart, String fileName) {
        try {
            ChartUtils.saveChartAsPNG(
                    new File(fileName),
                    chart,
                    CHART_WIDTH,
                    CHART_HEIGHT
            );
            logger.info("График сохранен: {}", fileName);
        } catch (IOException e) {
            logger.error("Ошибка сохранения графика", e);
        }
    }
}
