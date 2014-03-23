import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {
	public static Set<String> abbreviations = createAbbrvsSet();

	public static Set<String> excludeList = createExcludedWordsList();

	public static boolean isExclude = false;

	private static Set<String> createAbbrvsSet() {
		Set<String> result = new HashSet<String>();
		result.add("M.");
		result.add("Mme.");
		result.add("Mlle.");
		return Collections.unmodifiableSet(result);
	}

	private static Set<String> createExcludedWordsList() {
		Set<String> result = new HashSet<String>();
		result.add("that");
		result.add("than");
		result.add("was");
		result.add("at");
		result.add("do");
		result.add("the");
		result.add("in");
		result.add("to");
		result.add("and");
		result.add("by");
		result.add("be");
		result.add("are");
		result.add("of");
		result.add("on");
		result.add("he");
		result.add("she");
		result.add("is");
		result.add("they");
		result.add("it");
		result.add("then");
		result.add("him");
		result.add("his");
		result.add("her");
		result.add("their");
		result.add("them");
		result.add("will");
		result.add("has");
		result.add("had");
		result.add("have");
		result.add("did");
		result.add("a");
		return Collections.unmodifiableSet(result);
	}

	public static Set<String> parseExcludeList(String fileName) {

		Set<String> excludedWords = new HashSet<String>();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(AnalyzeThisBook.class
					.getResource(fileName).getPath()));

			String lineRead = null;
			while ((lineRead = reader.readLine()) != null) {
				String[] brokenLine = lineRead.split(",");
				for (String word : brokenLine) {
					excludedWords.add(lineRead.trim());
				}
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return excludedWords;

	}

	public static final Comparator<Map.Entry<String, Integer>> mapStrIntComp = new Comparator<Map.Entry<String, Integer>>() {
		// This is descending sort
		@Override
		public int compare(Map.Entry<String, Integer> object1,
				Map.Entry<String, Integer> object2) {
			return -object1.getValue().compareTo(object2.getValue());
		}
	};

	public static final Comparator<Map.Entry<Integer, Integer>> mapIntIntComp = new Comparator<Map.Entry<Integer, Integer>>() {
		// This is descending sort
		@Override
		public int compare(Map.Entry<Integer, Integer> object1,
				Map.Entry<Integer, Integer> object2) {
			return -object1.getValue().compareTo(object2.getValue());
		}
	};

	public static void saveTextFile(String outputFile, List<String> lines) {

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));

			for (String line : lines) {

				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}

		System.out.println("Generated file saved: " + outputFile);

	}

}
