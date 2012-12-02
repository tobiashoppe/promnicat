package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.word;

import edu.sussex.nlp.jws.Lin;
import net.didion.jwnl.data.IndexWord;

public class LinSimilarity extends JWSSimilarity<Lin> {
	private static final Lin LIN = JWS.getLin();

	@Override
	protected float getSemanticSimilarity(IndexWord indexWord1, IndexWord indexWord2) {
		String pos = indexWord1.getPOS().getLabel().toLowerCase().charAt(0)+"";
		return (float) LIN.max(indexWord1.getLemma(), indexWord2.getLemma(), pos);
	}

}
