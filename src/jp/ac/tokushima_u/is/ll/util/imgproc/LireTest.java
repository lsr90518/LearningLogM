package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ProgressMonitor;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.imageanalysis.bovw.SurfFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

public class LireTest {
	public void createIndex() throws IOException {
		long start = System.currentTimeMillis();
		String indexPath = "D:/User/Desktop/index";
		// create the initial local features:
		ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
		builder.addBuilder(new SurfDocumentBuilder());
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
		ArrayList<String> images = FileUtils.getAllImages(new File(
				"D:/User/Documents/Vicon Revue Data/F5CFC060-FCF0-C8E9-19C8-8670289DADFC"), true);
		for (String identifier : images) {
			System.out.println("Start to read:"+identifier);
			Document doc = builder.createDocument(new FileInputStream(
					identifier), identifier);
			iw.addDocument(doc);
			System.out.println("Finished");
		}
		iw.close();

		// create the visual words.
		IndexReader ir = DirectoryReader.open(
				FSDirectory.open(new File(indexPath)));
		// create a BoVW indexer
		SurfFeatureHistogramBuilder sh = new SurfFeatureHistogramBuilder(ir,
				200, 8000);
		// progress monitoring is optional and opens a window showing you the
		// progress.
		sh.setProgressMonitor(new ProgressMonitor(null, "", "", 0, 100));
		sh.index();
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}

	public void searchIndex() throws FileNotFoundException, IOException {
		String indexPath = "D:/User/Desktop/index";
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		
		ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
		builder.addBuilder(new SurfDocumentBuilder());
		Document doc = builder.createDocument(new FileInputStream(
				"D:/User/Documents/Vicon Revue Data/F5CFC060-FCF0-C8E9-19C8-8670289DADFC/B000076f00001183f20111108110214F.JPG"), "D:/User/Documents/Vicon Revue Data/F5CFC060-FCF0-C8E9-19C8-8670289DADFC/B000076f00001183f20111108110214F.JPG");
		
		// let's take the first document for a query:
		VisualWordsImageSearcher searcher = new VisualWordsImageSearcher(
				1000,
				DocumentBuilder.FIELD_NAME_SURF_LOCAL_FEATURE_HISTOGRAM );
		ImageSearchHits hits = searcher.search(doc, reader);
		// show or analyze your results ....
		for (int i = 0; i < hits.length(); i++) {
			System.out.println(hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue()+":"+ hits.score(i));
        }
	}
	
	public static void main(String[] args){
		LireTest test = new LireTest();
		try {
			test.searchIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
