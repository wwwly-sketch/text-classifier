package com.classifier.analyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Стеммер для русского языка.
 * Реализует упрощенный алгоритм стемминга для приведения слов к базовой форме (корню).
 * Основан на правилах удаления русских суффиксов и окончаний.
 *
 * @version 1.0.0
 */
public class Stemmer {

    private static final Logger logger = LogManager.getLogger(Stemmer.class);

    /** Русские гласные буквы. */
    private static final String VOWELS = "аеиоуыэюя";

    /** Суффиксы деепричастий совершенного вида. */
    private static final String[] PERFECTIVE_GERUND = {
            "ив", "ивши", "ившись", "ыв", "ывши", "ывшись"
    };

    /** Окончания прилагательных. */
    private static final String[] ADJECTIVE = {
            "ее", "ие", "ые", "ое", "ими", "ыми", "ей", "ий", "ый", "ой",
            "ем", "им", "ым", "ом", "его", "ого", "ему", "ому", "их", "ых",
            "ую", "юю", "ая", "яя", "ою", "ею"
    };

    /** Окончания глаголов. */
    private static final String[] VERB = {
            "ла", "на", "ете", "йте", "ли", "й", "л", "ем", "н", "ло",
            "но", "ет", "ют", "ны", "ть", "ешь", "нно"
    };

    /** Окончания существительных. */
    private static final String[] NOUN = {
            "а", "ев", "ов", "ие", "ье", "е", "иями", "ями", "ами", "еи",
            "ии", "и", "ией", "ей", "ой", "ий", "й", "иям", "ям", "ием",
            "ем", "ам", "ом", "о", "у", "ах", "иях", "ях", "ы", "ь",
            "ию", "ью", "ю", "ия", "ья", "я"
    };

    /**
     * Создает новый экземпляр стеммера.
     */
    public Stemmer() {
        // Конструктор по умолчанию
    }

    /**
     * Выполняет стемминг слова, приводя его к базовой форме.
     * Удаляет окончания и суффиксы согласно правилам русского языка.
     *
     * @param word исходное слово для стемминга
     * @return основа слова (стем), или исходное слово если стемминг невозможен
     */
    public String stem(String word) {
        if (word == null || word.length() < 3) {
            return word;
        }

        word = word.toLowerCase().trim();
        String rv = findRV(word);

        if (rv.isEmpty()) {
            return word;
        }

        String stemmed = removeSuffix(rv, PERFECTIVE_GERUND);
        if (stemmed.equals(rv)) {
            stemmed = removeSuffix(rv, ADJECTIVE);
            stemmed = removeSuffix(stemmed, VERB);
            stemmed = removeSuffix(stemmed, NOUN);
        }

        if (stemmed.endsWith("ь")) {
            stemmed = stemmed.substring(0, stemmed.length() - 1);
        }

        String result = word.substring(0, word.length() - rv.length()) + stemmed;
        logger.trace("Стемминг: {} -> {}", word, result);
        return result;
    }

    /**
     * Находит область RV слова (часть после первой гласной).
     * RV - это область слова, в которой применяются правила стемминга.
     *
     * @param word исходное слово
     * @return область RV слова, или пустая строка если гласная не найдена
     */
    private String findRV(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (VOWELS.indexOf(word.charAt(i)) >= 0 && i + 1 < word.length()) {
                return word.substring(i + 1);
            }
        }
        return "";
    }

    /**
     * Удаляет суффикс из слова, если он присутствует в массиве суффиксов.
     * Проверяет суффиксы в порядке их расположения в массиве.
     *
     * @param word слово для обработки
     * @param suffixes массив суффиксов для проверки
     * @return слово без суффикса, или исходное слово если суффикс не найден
     */
    private String removeSuffix(String word, String[] suffixes) {
        for (String suffix : suffixes) {
            if (word.endsWith(suffix)) {
                return word.substring(0, word.length() - suffix.length());
            }
        }
        return word;
    }
}
