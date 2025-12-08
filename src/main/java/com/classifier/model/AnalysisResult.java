package com.classifier.model;

import java.util.Comparator;
import java.util.Map;

/**
 * Результат анализа текста.
 * Содержит информацию о количестве совпадений по каждой тематике,
 * детализацию найденных слов и фраз, а также общее количество слов в тексте.
 *
 * @version 1.0.0
 */
public class AnalysisResult {

    /** Количество совпадений (баллов) по каждой тематике. */
    private final Map<Topic, Integer> topicScores;

    /** Детализация совпадений: слово/фраза и количество вхождений. */
    private final Map<Topic, Map<String, Integer>> matchDetails;

    /** Общее количество слов в анализируемом тексте. */
    private final int totalWords;

    /**
     * Создает результат анализа с указанными параметрами.
     *
     * @param topicScores карта с баллами по каждой тематике
     * @param matchDetails карта с детализацией совпадений по каждой тематике
     * @param totalWords общее количество слов в тексте
     */
    public AnalysisResult(Map<Topic, Integer> topicScores,
                          Map<Topic, Map<String, Integer>> matchDetails,
                          int totalWords) {
        this.topicScores = topicScores;
        this.matchDetails = matchDetails;
        this.totalWords = totalWords;
    }

    /**
     * Определяет и возвращает наиболее вероятную тематику текста.
     * Выбирает тематику с наибольшим количеством баллов.
     *
     * @return тематика с наибольшим количеством совпадений
     */
    public Topic getTopTopic() {
        return topicScores.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(Topic.PROGRAMMING);
    }

    /**
     * Вычисляет процент совпадений для указанной тематики.
     *
     * @param topic тематика для вычисления процента
     * @return процент совпадений относительно общего количества слов
     */
    public double getPercentage(Topic topic) {
        if (totalWords == 0) {
            return 0;
        }
        return (topicScores.get(topic) * 100.0) / totalWords;
    }

    /**
     * Возвращает карту баллов по всем тематикам.
     *
     * @return карта с количеством баллов для каждой тематики
     */
    public Map<Topic, Integer> getTopicScores() {
        return topicScores;
    }

    /**
     * Возвращает детализацию совпадений по всем тематикам.
     *
     * @return карта с детализацией совпадений для каждой тематики
     */
    public Map<Topic, Map<String, Integer>> getMatchDetails() {
        return matchDetails;
    }

    /**
     * Возвращает общее количество слов в анализируемом тексте.
     *
     * @return количество слов в тексте
     */
    public int getTotalWords() {
        return totalWords;
    }
}
