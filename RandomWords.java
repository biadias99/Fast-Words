import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to generate a list of random words. Oracle JDK 8 MOOC Lesson2 homework
 *
 * @author cesarnog
 */
public class RandomWords {
	private List<String> sourceWords;
	private static final String WORD_REGEXP = "[- .:,]+";

	/**
	 * Constructor
	 *
	 * @throws IOException
	 *             If the source words file cannot be read
	 */
	public RandomWords() {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get("words.txt"))) {
			sourceWords = reader.lines().flatMap(line -> Stream.of(line.split(WORD_REGEXP)))
					.collect(Collectors.toList());
			System.out.println("Loaded " + sourceWords.size() + " words");
		} catch(IOException ex) {}
	}

	/**
	 * Create a list of a given size containing random words
	 *
	 * @param listSize
	 *            The size of the list to create
	 * @return The created list
	 */
	public List<String> createList(int listSize) {
		List<String> wordList = Collections.emptyList();

		if (listSize > 0) {
			Random rand = new Random(System.currentTimeMillis());
			wordList = rand.ints(listSize, 0, sourceWords.size()).mapToObj(i -> sourceWords.get(i))
					.collect(Collectors.toList());
		}

		return wordList;
	}

	/**
	 * Return the list of all source words, which cannot be modified
	 *
	 * @return The unmodifiable list of all source words
	 */
	public List<String> allWords() {
		return Collections.unmodifiableList(sourceWords);
	}
}
