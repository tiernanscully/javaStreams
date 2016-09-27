/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordValidator {

    private int wordCount;
    private String maxWord1 = "", maxWord2 = "";

    private final Predicate<String> nonBlankString = word -> word.length() > 0;
    private final Stream<String> wordStream;
    private final WordCache cache;

    public WordValidator(final int keySizeLimit, final String inputFileName) throws IOException {
        this.wordStream = getFilteredStream(inputFileName);
        this.cache = createWordCache(getFilteredStream(inputFileName), keySizeLimit);
    }

    public void printResult() {
        this.wordStream.forEach(this::isWordValid);
        System.out.println("Number of Concatenated Words: " + this.wordCount);
        System.out.println("Longest Word: " + this.maxWord1);
        System.out.println("Second Longest Word: " + this.maxWord2);
    }

    private boolean isWordValid(final String word) {
        for (int keyLength = this.cache.getKeySizeLimit(); keyLength > 0; keyLength--) {
            if (doesKeyExistInMap(word, keyLength)) {
                for (final String listEntry : this.cache.get(word, keyLength)) {
                    if (doesWordStartWithOtherWord(word, listEntry)) {
                        final String newWord = word.substring(listEntry.length());
                        if (newWord.isEmpty() || isSubstringValid(newWord)) {
                            updateLongestWords(word);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean doesWordStartWithOtherWord(final String word, final String listEntry) {
        return word.length() > listEntry.length() && word.startsWith(listEntry);
    }

    private boolean doesKeyExistInMap(final String word, final int keySize) {
        return word.length() >= keySize && this.cache.containsKey(word.substring(0, keySize));
    }

    private boolean isSubstringValid(final String word) {
        for (int i = this.cache.getKeySizeLimit(); i > 1; i--) {
            if (doesKeyExistInMap(word, i)) {
                for (final String listEntry : this.cache.get(word, i)) {
                    if (word.startsWith(listEntry)) {
                        final String newWord = word.substring(listEntry.length());
                        if (newWord.isEmpty() || isSubstringValid(newWord)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void updateLongestWords(final String word) {
        if (word.length() > this.maxWord1.length()) {
            this.maxWord2 = this.maxWord1;
            this.maxWord1 = word;
        }
        this.wordCount++;
    }

    private Stream<String> getFilteredStream(final String inputFileName) throws IOException {
        return Files.lines(Paths.get(inputFileName)).filter(this.nonBlankString);
    }

    private WordCache createWordCache(final Stream<String> wordStream, final int keySizeLimit) {
        final WordCache cache = new WordCache(keySizeLimit);
        wordStream.forEach(word -> cache.put(word));
        return cache;
    }
}

class WordCache {
	
	private static final Map<String, List<String>> cache = new HashMap<>();
    private final int keySizeLimit;

    public WordCache(final int keySizeLimit) {
        this.keySizeLimit = keySizeLimit;
    }

    public int getKeySizeLimit() {
        return this.keySizeLimit;
    }

    public void put(final String word) {
        for (int i = getKeySizeLimit(); i > 1; i--) {
            if (word.length() >= i) {
                final String key = word.substring(0, i);
                cache.merge(key, newList(word),mergeLists());
                break;
            }
        }
    }
    
    public boolean containsKey(final String word){
    	return cache.containsKey(word);
    }
    
    public List<String> get(final String word, final int keySize) {
        return cache.get(word.substring(0, keySize));
    }
    
    private List<String> newList(final String word){
    	final List<String> wordList = new ArrayList<>();
    	wordList.add(word);
    	return wordList;
    }
    
	private BiFunction<? super List<String>, ? super List<String>, ? extends List<String>> mergeLists() {
		return (oldList, newList) -> Stream.concat(oldList.stream(), newList.stream())
				.collect(Collectors.toList());
	}
}
