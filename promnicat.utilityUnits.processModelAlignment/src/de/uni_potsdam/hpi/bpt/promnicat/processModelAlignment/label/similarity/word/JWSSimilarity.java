package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.word;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.util.WordNetUtil;
import edu.sussex.nlp.jws.JWS;

public abstract class JWSSimilarity<JWSSim extends Object> extends SemanticWordSimilarity {
	protected static JWS JWS = new JWS(WordNetUtil.LIB_PATH, "3.0", "ic-bnc-resnik-add1.dat");
}
