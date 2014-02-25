package jp.ac.tokushima_u.is.ll.util.imgproc;

import net.semanticmetadata.lire.imageanalysis.bovw.SurfFeatureHistogramBuilder;

import org.apache.lucene.index.IndexReader;

public class LearningLogSurfFeatureHistogramBuilder extends SurfFeatureHistogramBuilder{

	public LearningLogSurfFeatureHistogramBuilder(IndexReader reader) {
		super(reader);
	}
	
	public LearningLogSurfFeatureHistogramBuilder(IndexReader reader, String clusterFile) {
		super(reader);
		this.clusterFile = clusterFile;
	}
	
	public LearningLogSurfFeatureHistogramBuilder(IndexReader reader, int numDocsForVocabulary, String clusterFile) {
		super(reader, numDocsForVocabulary);
		this.clusterFile = clusterFile;
	}
	
	public LearningLogSurfFeatureHistogramBuilder(IndexReader reader, int numDocsForVocabulary, int numClusters, String clusterFile) {
		super(reader, numDocsForVocabulary, numClusters);
		this.clusterFile = clusterFile;
	}
}
