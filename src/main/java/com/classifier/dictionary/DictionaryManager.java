package com.classifier.dictionary;

import com.classifier.model.Topic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Менеджер тематических словарей.
 * Загружает и хранит словари ключевых слов и фраз для каждой тематики.
 * Поддерживает загрузку из файлов ресурсов и встроенные словари по умолчанию.
 *
 * @version 1.0.0
 */
public class DictionaryManager {

    private static final Logger logger = LogManager.getLogger(DictionaryManager.class);

    /** Словари ключевых слов по тематикам. */
    private final Map<Topic, Set<String>> dictionaries;

    /** Словари ключевых фраз по тематикам. */
    private final Map<Topic, List<String>> phrases;

    /**
     * Создает новый экземпляр менеджера словарей.
     * Автоматически загружает все словари при создании.
     */
    public DictionaryManager() {
        dictionaries = new EnumMap<>(Topic.class);
        phrases = new EnumMap<>(Topic.class);
        loadDictionaries();
    }

    /**
     * Загружает все словари из ресурсов приложения.
     * Для каждой тематики создает пустые коллекции и вызывает загрузку.
     */
    private void loadDictionaries() {
        for (Topic topic : Topic.values()) {
            dictionaries.put(topic, new HashSet<>());
            phrases.put(topic, new ArrayList<>());
            loadDictionary(topic);
        }
    }

    /**
     * Загружает словарь для конкретной тематики из файла ресурсов.
     * Если файл не найден, загружает словарь по умолчанию.
     * Слова с пробелами добавляются как фразы.
     *
     * @param topic тематика для загрузки словаря
     */
    private void loadDictionary(Topic topic) {
        String fileName = "dictionaries/" + topic.name().toLowerCase() + ".txt";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                logger.warn("Словарь не найден: {}", fileName);
                loadDefaultDictionary(topic);
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    if (line.contains(" ")) {
                        phrases.get(topic).add(line);
                    } else {
                        dictionaries.get(topic).add(line);
                    }
                }
            }
            logger.info("Загружен словарь {}: {} слов, {} фраз",
                    topic, dictionaries.get(topic).size(), phrases.get(topic).size());

        } catch (IOException e) {
            logger.error("Ошибка загрузки словаря: {}", fileName, e);
            loadDefaultDictionary(topic);
        }
    }

    /**
     * Загружает встроенный словарь по умолчанию для тематики.
     * Используется когда файл словаря не найден или произошла ошибка загрузки.
     *
     * @param topic тематика для загрузки словаря по умолчанию
     */
    private void loadDefaultDictionary(Topic topic) {
        Set<String> defaultWords = switch (topic) {
            case MEDICINE -> Set.of("врач", "болезн", "лечен", "пациент", "диагноз",
                    "симптом", "терап", "хирург", "анализ", "рецепт", "медикамент", "госпитал");
            case HISTORY -> Set.of("истор", "век", "эпох", "войн", "импер", "государств",
                    "революц", "древн", "цивилизац", "археолог");
            case PROGRAMMING -> Set.of("код", "программ", "алгоритм", "функц", "класс",
                    "метод", "переменн", "цикл", "массив", "компилятор", "отладк");
            case NETWORKS -> Set.of("сет", "протокол", "сервер", "клиент", "маршрутизац",
                    "ip", "tcp", "dns", "firewall", "пакет");
            case CRYPTOGRAPHY -> Set.of("шифр", "ключ", "дешифр", "криптограф", "хеш",
                    "блокчейн", "алгоритм", "rsa", "aes", "подпис");
            case FINANCE -> Set.of("финанс", "банк", "кредит", "инвестиц", "акц", "бирж",
                    "капитал", "процент", "депозит", "валют");
        };
        dictionaries.get(topic).addAll(defaultWords);
        logger.info("Загружен словарь по умолчанию для {}: {} слов", topic, defaultWords.size());
    }

    /**
     * Проверяет наличие слова в словаре указанной тематики.
     * Выполняет как точное сравнение, так и проверку по корню слова.
     *
     * @param topic тематика для проверки
     * @param word проверяемое слово
     * @return true если слово найдено в словаре или совпадает по корню
     */
    public boolean containsWord(Topic topic, String word) {
        Set<String> dict = dictionaries.get(topic);
        if (dict.contains(word)) {
            return true;
        }
        for (String dictWord : dict) {
            if (word.startsWith(dictWord) || dictWord.startsWith(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает список фраз для указанной тематики.
     *
     * @param topic тематика
     * @return список фраз для данной тематики
     */
    public List<String> getPhrases(Topic topic) {
        return phrases.get(topic);
    }
}
