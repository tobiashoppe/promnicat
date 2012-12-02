package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.word;

import java.util.HashMap;
import java.util.logging.Logger;

import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.list.PointerTargetTreeNode;
import net.didion.jwnl.data.list.PointerTargetTreeNodeList.Operation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.util.WordNetUtil;

/** Custom algorithm, basic idea of a taxonomy-based similarity. Similarity is computed by:<br>
 *  <code>sim(x,y) = 1 - (difference(x,y)/information(x,y))</code><br>
 *  I.e. the more edges between, and the higher the tree-level of x and y,
 *  the lower the similarity. */
public class LinearDistanceSimilarity extends SemanticWordSimilarity {
	private static final Logger LOGGER = Logger.getLogger(LinearDistanceSimilarity.class.getName());
	
	/**
	 * A special {@link Operation} which counts the distance
	 * to a given target and the total depth of the given tree.
	 * @author stefan.schaefer
	 */
	private static final class PathFinder implements Operation {
		private final HashMap<Synset, Integer> treePath;
		private final PathFinder targetPathFinder;
		private final Synset target;
		private int distance = -1;
		private int currentDepth = -1;

		private PathFinder(Synset target) {
			this(target, null);
		}

		public PathFinder(Synset target, PathFinder targetPathFinder) {
			this.treePath = new HashMap<Synset, Integer>();
			this.targetPathFinder = targetPathFinder;
			this.target = target;
		}

		@Override
		public Object execute(PointerTargetTreeNode node) {
			currentDepth++;
			LOGGER.info("Traversing "+node.getSynset().getWords()[0].getLemma());
			if (distance < 0 && node.getSynset().equals(target)) 
				distance = currentDepth;
			if (distance < 0 && targetPathFinder != null && targetPathFinder.pathContains(node)) {
				distance = currentDepth + targetPathFinder.getPathLengthTo(node);
			}
			treePath.put(node.getSynset(), currentDepth);
			return null;
		}

		public int getDistance() {
			return (distance == -1 ? currentDepth : distance) ;
		}

		public int getDepth() {
			return currentDepth;
		}
		
		public boolean foundTarget() {
			return distance != -1;
		}

		private Integer getPathLengthTo(PointerTargetTreeNode aNode) {
			return treePath.get(aNode.getSynset());
		}
		
		private boolean pathContains(PointerTargetTreeNode aNode) {
			return treePath.containsKey(aNode.getSynset());
		}
	}

	@Override
	protected float getSemanticSimilarity(final IndexWord indexWord1, final IndexWord indexWord2) {
		//we can only compare words with the same part-of-speech (verb, noun, ...)
		Synset[] synsets1 = WordNetUtil.getSenses(indexWord1);
		Synset[] synsets2 = WordNetUtil.getSenses(indexWord2);
		float maxSim = -1;
		for (Synset synset1 : synsets1) {
			for (Synset synset2 : synsets2) {
				LOGGER.info("Computing path from " + synset1.getWords()[0].getLemma() + " to "+synset2.getWords()[0].getLemma());
				float sim = getSimilarity(synset1, synset2);
				LOGGER.info(new Float(sim).toString());
				if (sim > maxSim) maxSim = sim;
			}
		}		
		return maxSim;
	}
	
	private float getSimilarity(Synset first, Synset second) {
		PointerTargetTree tree = WordNetUtil.getHypernymTree(first);
		PathFinder treeIterator1 = new PathFinder(second);
		tree.getFirstMatch(treeIterator1);
		float result = 0;
		if (treeIterator1.foundTarget()) {
			float distanceBetweenSynsets = treeIterator1.getDistance();
			float distanceToRoot = treeIterator1.getDepth();
			if (distanceBetweenSynsets == distanceToRoot) {
				//second label equals "entity"
				result = 1 / (distanceToRoot+1);
			} else {
				result = 1 - (         distanceBetweenSynsets
						/(distanceToRoot*2 - distanceBetweenSynsets));
			}
		} else {
			tree = WordNetUtil.getHypernymTree(second);
			PathFinder treeIterator2 = new PathFinder(first, treeIterator1);
			tree.getFirstMatch(treeIterator2);
			if (treeIterator2.foundTarget()) {
				float distanceBetweenSynsets = treeIterator2.getDistance();
				float distanceToRoot1 = treeIterator1.getDepth();
				float distanceToRoot2 = treeIterator2.getDepth();
				if (distanceBetweenSynsets == distanceToRoot2) {
					//first label equals "entity"
					result = 1 / (distanceToRoot2+1);
				} else {
					result = 1 - (          (distanceBetweenSynsets)
							/(distanceToRoot1 + distanceToRoot2));
				}
			}
		}
		return result;
	}
}
