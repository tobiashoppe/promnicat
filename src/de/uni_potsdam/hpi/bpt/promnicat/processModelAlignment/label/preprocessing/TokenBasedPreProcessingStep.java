package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing;

/** splits a string into words and process each 
 * @see #processWord(String) */
public abstract class TokenBasedPreProcessingStep extends
		SinglePreProcessingStep {

	public TokenBasedPreProcessingStep() {
		super();
	}

	/** splits a string into words and process each 
	 * @see #processWord(String) */
	@Override
	public String process(String in) {
		String[] words = in.split("\\s");
		processWords(words);
		return rebuildString(words);
	}

	protected String rebuildString(String[] words) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (words[i] != null) {
				sb.append(words[i]).append(" ");
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}

	protected void processWords(String[] words) {
		for (int i = 0; i < words.length; i++) {
			words[i].replaceAll("\\W", "");
			words[i] = processWord(words[i]);
		}
	}

	/** process a single word */
	protected abstract String processWord(String word);

}