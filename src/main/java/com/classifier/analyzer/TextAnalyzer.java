package com.classifier.analyzer;

import com.classifier.dictionary.DictionaryManager;
import com.classifier.model.AnalysisResult;
import com.classifier.model.Topic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Анализатор текста для определения тематики.
 * Выполняет лексический анализ текста, подсчитывает совпадения с тематическими
 * словарями и определяет наиболее вероятную тематику документа.
 *
 * @version 1.0.0
 */
public class TextAnalyzer {

    private static final Logger logger = LogManager.getLogger(TextAnalyzer.class);

    /** Регулярное выражение для извлечения слов. */
    private static final Pattern WORD_PATTERN = Pattern.compile("[а-яА-Яa-zA-Z]+");

    /** Менеджер тематических словарей. */
    private final DictionaryManager dictionaryManager;

    /** Стеммер для обработки слов. */
    private final Stemmer stemmer;

    /**
     * Создает новый экземпляр анализатора текста.
     * Инициализирует менеджер словарей и стеммер.
     */
    public TextAnalyzer() {
        this.dictionaryManager = new DictionaryManager();
        this.stemmer = new Stemmer();
    }

    /**
     * Анализирует текст и определяет его тематику.
     * Извлекает слова, применяет стемминг, подсчитывает совпадения
     * со словарями и вычисляет процентное распределение по тематикам.
     *
     * @param text исходный текст для анализа
     * @return результат анализа с информацией о совпадениях и определенной тематике
     */
    public AnalysisResult analyze(String text) {
        logger.info("Начало анализа текста");

        List<String> words = extractWords(text);
        List<String> stemmedWords = stemWords(words);

        Map<Topic, Map<String, Integer>> topicMatches = new EnumMap<>(Topic.class);
        Map<Topic, Integer> topicScores = new EnumMap<>(Topic.class);

        for (Topic topic : Topic.values()) {
            topicMatches.put(topic, new HashMap<>());
            topicScores.put(topic, 0);
        }

        for (String word : stemmedWords) {
            for (Topic topic : Topic.values()) {
                if (dictionaryManager.containsWord(topic, word)) {
                    topicMatches.get(topic).merge(word, 1, Integer::sum);
                    topicScores.merge(topic, 1, Integer::sum);
                }
            }
        }

        String lowerText = text.toLowerCase();
        for (Topic topic : Topic.values()) {
            for (String phrase : dictionaryManager.getPhrases(topic)) {
                int count = countOccurrences(lowerText, phrase);
                if (count > 0) {
                    topicMatches.get(topic).put(phrase, count);
                    topicScores.merge(topic, count * 2, Integer::sum);
                }
            }
        }

        logger.info("Анализ завершен. Результаты: {}", topicScores);
        return new AnalysisResult(topicScores, topicMatches, words.size());
    }

    /**
     * Извлекает слова из текста с помощью регулярного выражения.
     * Преобразует все слова в нижний регистр.
     *
     * @param text исходный текст
     * @return список извлеченных слов в нижнем регистре
     */
    private List<String> extractWords(String text) {
        List<String> words = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase());
        }
        logger.debug("Извлечено {} слов", words.size());
        return words;
    }

    /**
     * Применяет стемминг к списку слов.
     *
     * @param words список слов для обработки
     * @return список слов после применения стемминга
     */
    private List<String> stemWords(List<String> words) {
        return words.stream()
                .map(stemmer::stem)
                .toList();
    }

    /**
     * Подсчитывает количество вхождений подстроки в текст.
     *
     * @param text текст для поиска
     * @param substring искомая подстрока
     * @return количество вхождений подстроки в текст
     */
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
