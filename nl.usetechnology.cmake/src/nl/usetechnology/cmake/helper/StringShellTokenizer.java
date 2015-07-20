package nl.usetechnology.cmake.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringShellTokenizer {
	
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Pattern SPACES = Pattern.compile("(?:\"|\\s+)");

	
	public static String[] tokenize(CharSequence sequence) {
		Matcher m = SPACES.matcher(sequence);
		List<String> items = new ArrayList<String>();
		boolean inQuotes = false;
		int lastPos = 0;
		while(m.find()) {
			int currentPos = m.start();
			char currentChar = sequence.charAt(currentPos);
			switch(currentChar) {
			case '\"':
				inQuotes = !inQuotes;
			break;
			default:
				if(!inQuotes) {
					items.add(sequence.subSequence(lastPos, currentPos).toString());
					lastPos = m.end();
				}
			}
		}
		items.add(sequence.subSequence(lastPos, sequence.length()).toString());
		return items.toArray(EMPTY_STRING_ARRAY);
	}
	
}
