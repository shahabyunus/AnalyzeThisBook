import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Probably use StringBuilder if sticking to pure Java lib and not using
 * something like Guava. Only String are heavy.
 * **/

public class AnalyzeThisBook {

	private static int topSentences = 5;

	public static void main(String a[]) {

		if (a.length < 2) {
			System.out
					.println("\nUsage: source_filename operation "
							+ "Where valid values for 'operation' are one of these [wc, ls].\n "
							+ "The first 2 parameters are mandatory.\n"
							+ "For wc operation, x can be given if common words should be excluded. If optionally want to specify your own comma separated list of words then the file path and name can be provided.\n"
							+ "For ls operation optional n parameter can be specified which is the number of top long sentences should be printed. "
							+ "Order of the parameters matter!");
			return;
		}

		AnalyzeThisBook gtp = new AnalyzeThisBook();

		String fileName = a[0];
		String op = a[1];

		if (op.equalsIgnoreCase("wc")) {
			if (a.length == 3) {
				if (a[2].toLowerCase().equalsIgnoreCase("x")) {
					Util.isExclude = true;
				}
			}

			if (a.length == 4) {
				Util.excludeList.clear();
				Util.excludeList = Util.parseExcludeList(a[3]);
			}
			gtp.generateWordCount(fileName);
		} else if (op.equalsIgnoreCase("ls")) {
			if (a.length == 3) {
				try {
					topSentences = Integer.parseInt(a[2]);
				} catch (NumberFormatException nfe) {
					topSentences = 5;
					System.out
							.print("Invalid value for top sentences parameter provided ("
									+ a[2]
									+ ", using default of "
									+ topSentences);
				}
			}
			gtp.generateSentenceAnalysis(fileName);
		} else {
			System.out.println("Unrecognized operation=" + op);
		}
	}

	protected void generateWordCount(String fileName) {

		BufferedReader in = null;

		Map<String, Integer> words = new TreeMap<String, Integer>();
		List<String> wordCountFileText = new ArrayList<String>();

		String theLine, empty = "";
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName), "UTF-16"));

			while ((theLine = in.readLine()) != null) {
				String[] brokenLine = theLine.split(" ");

				for (String theWord : brokenLine) {

					theWord = theWord.trim().replace(",", empty)
							.replace(";", empty).replace("“", empty)
							.replace("&", empty).replace("(", empty)
							.replace(")", empty).replace("*", empty)
							.replace("'", empty).replace("”", empty)
							.replace("—", empty).replace("\"", empty)
							.replace("‘", empty).replace("#", empty)
							.replace("’", empty).replace("?", empty)
							.replace(":", empty).replace("!", empty);

					// If the word ends in '.', then check whether an
					// abbreviation or
					// end of sentence.
					if (theWord.endsWith(".")) {
						if (!Util.abbreviations.contains(theWord)) {
							// it is end of sentence so strip the period
							theWord = theWord
									.substring(0, theWord.length() - 1);
						}
					}

					if (Util.isExclude) {
						if (Util.excludeList.contains(theWord)) {
							continue;
						}
					}

					theWord = theWord.toLowerCase();
					Integer count = words.get(theWord);
					if (count == null) {
						count = 0;
					}

					words.put(theWord, ++count);
				}

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		wordCountFileText
				.add("Number of unique words=" + words.size() + "\n\n");

		for (String word : words.keySet()) {
			wordCountFileText.add(word + "=" + words.get(word));
		}

		Util.saveTextFile("wordcount.txt", wordCountFileText);

		List<Map.Entry<String, Integer>> invertedWordList = new ArrayList<Map.Entry<String, Integer>>(
				words.entrySet());
		Collections.sort(invertedWordList, Util.mapStrIntComp);
		wordCountFileText.clear();
		wordCountFileText.add("Number of unique words="
				+ invertedWordList.size() + "\n\n");

		for (Map.Entry<String, Integer> entry : invertedWordList) {
			wordCountFileText.add(entry.getKey() + "=" + entry.getValue());
		}

		Util.saveTextFile("invertedwordcount.txt", wordCountFileText);

	}

	protected void generateSentenceAnalysis(String fileName) {
		BufferedReader in = null;

		List<Integer> sentenceLengths = new ArrayList<Integer>();
		List<String> sentence = new ArrayList<String>();
		Map<Integer, Set<String>> longestSentences = new TreeMap<Integer, Set<String>>(
				Collections.reverseOrder());
		List<String> sentenceLengthsText = new ArrayList<String>();

		String theLine;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName), "UTF-16"));
			StringBuilder tempSentence = new StringBuilder();
			while ((theLine = in.readLine()) != null) {
				String[] brokenLine = theLine.split(" ");

				for (int j = 0; j < brokenLine.length; j++) {
					String theWord = brokenLine[j];

					/**
					 * If the word ends in one of the terminators (.,?,!) or " "
					 * " or " ' ", then check whether an a) abbreviation or b)
					 * end of sentence within a dialog which is an actual end of
					 * sentence as well c) end of sentence only within a dialog
					 * and not actual end or d) actual end of sentence outside
					 * of any dialog. Case c should not occur though as we are
					 * always check for end in one of the terminators and ",'
					 **/

					if (endOfSentence(theWord,
							j < brokenLine.length - 1 ? brokenLine[j + 1] : "")) {
						// It is end of sentence
						int sentenceLength = sentence.size();
						for (int i = 0; i < sentence.size(); i++) {
							tempSentence.append(sentence.remove(i--)).append(
									" ");
						}
						if (theWord.length() > 1) {
							// Only include the current word if it is not
							// stray terminator
							sentenceLength++;
							tempSentence.append(theWord);
						}

						Set<String> savedSentences = longestSentences
								.get(sentenceLength);
						if (savedSentences == null) {
							savedSentences = new HashSet<String>();
							longestSentences
									.put(sentenceLength, savedSentences);
						}
						savedSentences.add(tempSentence.toString());
						tempSentence.setLength(0);
						sentenceLengths.add(sentenceLength);
					} else {
						// Business as usual, keep on collecting them
						// except we don't want to count - as a word.
						if (!theWord.trim().equalsIgnoreCase("-")
								&& !theWord.trim().equalsIgnoreCase("—")) {
							sentence.add(theWord);
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		sentenceLengthsText.add("Number of sentences=" + sentenceLengths.size()
				+ "\n\n");

		calculateStatistics(sentenceLengths, sentenceLengthsText);
		sentenceLengths.clear();

		int i = 0;
		for (Integer length : longestSentences.keySet()) {
			if (i++ > topSentences) {
				break;
			}
			sentenceLengthsText.add("________________________\nlength="
					+ length + ".\n");
			for (String longSentence : longestSentences.get(length)) {
				sentenceLengthsText.add(longSentence + "\n\n");
			}
		}

		longestSentences.clear();

		Util.saveTextFile("sentencelength.txt", sentenceLengthsText);
	}

	protected void calculateStatistics(List<Integer> sentenceLengths,
			List<String> sentenceLengthsText) {

		int sum = 0, totalSentences = sentenceLengths.size();
		Map<Integer, Integer> modeMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < sentenceLengths.size(); i++) {
			int length = sentenceLengths.get(i);
			sum += length;
			Integer count = modeMap.get(length);
			if (count == null) {
				count = 0;
			}
			modeMap.put(length, ++count);
		}

		if (totalSentences > 0) {
			int mean = sum / totalSentences;
			sentenceLengthsText.add("Mean=" + mean + "\n");
			int median = 0;
			Collections.sort(sentenceLengths);
			if (totalSentences % 2 == 0) {
				median = (sentenceLengths.get((totalSentences / 2) - 1) + sentenceLengths
						.get(totalSentences / 2)) / 2;
			} else {
				median = sentenceLengths.get(totalSentences / 2);
			}
			sentenceLengthsText.add("Median=" + median + "\n");

			StringBuilder mode = new StringBuilder("Mode=");
			int oldCount = -1;
			List<Map.Entry<Integer, Integer>> modeList = new ArrayList<Map.Entry<Integer, Integer>>(
					modeMap.entrySet());
			Collections.sort(modeList, Util.mapIntIntComp);
			for (Integer length : modeMap.keySet()) {
				int newCount = modeMap.get(length);
				if (oldCount < 0 || newCount == oldCount) {
					if (oldCount > 0) {
						mode.append(",");
					} else {
						mode.append(newCount + ", lengths=[");
					}
					mode.append(newCount);
					oldCount = newCount;
				} else {
					break;
				}
			}
			sentenceLengthsText.add(mode.append("]\n").toString());

			float variance = 0f;
			for (int i = 0; i < sentenceLengths.size(); i++) {
				int length = sentenceLengths.get(i);
				variance += Math.pow(mean - length, 2);
			}
			sentenceLengthsText.add("Standard Deviation="
					+ Math.sqrt(variance / totalSentences) + "\n");
		}
	}

	protected boolean endOfSentence(String text, String nextText) {
		if (text.endsWith(".") && !Util.abbreviations.contains(text)) {
			return true;
		}

		if (text.endsWith("!") || text.endsWith("?")) {
			return true;
		}

		// TODO î, '... for Mac. This is for Windows
		if (text.endsWith("\"") || text.endsWith("'") || text.endsWith("”")
				|| text.endsWith("’") || text.endsWith(")")) {
			// case b, check for the terminators at the second-last position
			char ch;

			if (nextText.length() > 0) {
				ch = nextText.charAt(0);
			} else {
				return true;
			}

			boolean nextWordUpperCase = (Character.isLetter(ch) && Character
					.getType(ch) == Character.UPPERCASE_LETTER);
			int position = text.length() - 2;
			if (position < 0) {
				return nextWordUpperCase && true;
			}

			char secondToLast = text.charAt(position);
			if (secondToLast == '!' || secondToLast == '.'
					|| secondToLast == '?') {
				return nextWordUpperCase && true;
			}
		}

		return false;
	}

}
