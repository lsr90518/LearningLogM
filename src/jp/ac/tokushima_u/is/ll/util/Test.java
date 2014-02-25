package jp.ac.tokushima_u.is.ll.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

public class Test {
	public void createIndex() throws IOException {
        String indexPath = "/Users/houbin/Desktop/testphotos1/bovw-test"; // change if oyou want a different one.
 
        // create the initial local features:
        DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
        IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
        ArrayList<String> images = FileUtils.getAllImages(new File("/Users/houbin/Desktop/testphotos1"), true);
        for (String identifier : images) {
            Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
            iw.addDocument(doc);
        }
        iw.close();
	}
	
	public void searchIndex() throws IOException {
        String indexPath = "/Users/houbin/Desktop/testphotos1/bovw-test";
		ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        // let's take the first document for a query:
//        ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
//        builder.addBuilder(new SurfDocumentBuilder());
//        SurfDocumentBuilder sb = new SurfDocumentBuilder();
//        Document query = sb.createDocument(ImageIO.read(new File("/Users/houbin/Desktop/testphotos1/00002236.JPG")), "query");
//        SurfFeatureHistogramBuilder sfb = new SurfFeatureHistogramBuilder(reader);
//        query = sfb.getVisualWords(query);
        DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
        Document query = builder.createDocument(ImageIO.read(new File("/Users/houbin/Desktop/testphotos1/00002235.JPG")), "query");
        System.out.println(query.get(DocumentBuilder.FIELD_NAME_IDENTIFIER));
        ImageSearchHits hits = searcher.search(query, reader);
        if(hits!=null){
	        for (int i = 0; i < hits.length(); i++) {
	        	//itemId
	            String itemId = hits.doc(i).get(DocumentBuilder.FIELD_NAME_IDENTIFIER);
	            System.out.println(hits.score(i)+"\t\t  :  "+itemId);
	        }
        }
	}
	
	public static void main(String[] args){
		
		
		Test test = new Test();
		
		 try {
//			 test.createIndex();
		 test.searchIndex();
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
	}
}
