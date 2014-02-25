package jp.ac.tokushima_u.is.ll.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;

import jp.ac.tokushima_u.is.ll.entity.Item;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stromberglabs.jopensurf.Surf;


@Service
@Transactional(readOnly = true)
public class ImageIndexService {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageIndexService.class);
	
	@Autowired
	private PropertyService propertyService;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private StaticServerService staticServerService;
	
	public void recreateIndex() throws IOException{
		File file = FileUtils.getFile(propertyService.getLuceneImageIndex(), propertyService.getProjectName());
		if(file.exists()) file.mkdirs();

		 // create the initial local features:
		DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
        Directory dir=FSDirectory.open(FileUtils.getFile(propertyService.getLuceneImageIndex(), propertyService.getProjectName()));  
        IndexWriterConfig cfg=new IndexWriterConfig(Version.LUCENE_43,new WhitespaceAnalyzer(Version.LUCENE_43));  
        cfg.setOpenMode(OpenMode.CREATE);
        
        try(IndexWriter writer=new IndexWriter(dir,cfg)){
        	List<Item> itemList = itemService.findItemListHasImage();
        	int i=0;
        	for(Item item: itemList){
        		logger.info("Indexing, itemId = "+item.getId()+" "+i+"/"+itemList.size());
        		try {
					URL url = new URL(staticServerService.accessurl(item.getImage()+"_320x240.png"));
					Document doc = builder.createDocument(url.openStream(), item.getId());
					writer.addDocument(doc);
				} catch (Exception e) {
					logger.error("File fileId="+item.getImage()+", itemId="+item.getId()+" is not exist:"+e.getMessage());
					continue;
				}finally{
					i++;
				}
        	}
        }
 
//        // create the visual words.
//        IndexReader ir = DirectoryReader.open(FSDirectory.open(FileUtils.getFile(propertyService.getLuceneImageIndex(), propertyService.getProjectName())));
//        // create a BoVW indexer, "-1" means that half of the images in the index are
//        // employed for creating the vocabulary. "100" is the number of visual words to be created.
//        SurfFeatureHistogramBuilder sh = new LearningLogSurfFeatureHistogramBuilder(ir, 200, 100, propertyService.getLuceneImageSurfData()); 
//        // progress monitoring is optional and opens a window showing you the progress.
//        sh.index();
	}
	
	public void addToIndex(Item item) throws IOException{
		File file = FileUtils.getFile(propertyService.getLuceneImageIndex(), propertyService.getProjectName());
		if(file.exists()) file.mkdirs();

		 // create the initial local features:
		DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
        Directory dir = FSDirectory.open(new File(propertyService.getLuceneImageIndex(), propertyService.getProjectName()));  
        IndexWriterConfig cfg=new IndexWriterConfig(Version.LUCENE_43,new WhitespaceAnalyzer(Version.LUCENE_43));  
        cfg.setOpenMode(OpenMode.CREATE_OR_APPEND);
        
        try(IndexWriter writer=new IndexWriter(dir,cfg)){
    		try {
				URL url = new URL(staticServerService.accessurl(item.getImage()+"_320x240.png"));
				Document doc = builder.createDocument(url.openStream(), item.getId());
				writer.addDocument(doc);
			} catch (Exception e) {
				logger.error("File fileId="+item.getImage()+", itemId="+item.getId()+" is not exist:"+e.getMessage());
				return;
			}
        }
 
//        // create the visual words.
//        IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(propertyService.getLuceneImageIndex(), propertyService.getProjectName())));
//        // create a BoVW indexer, "-1" means that half of the images in the index are
//        // employed for creating the vocabulary. "100" is the number of visual words to be created.
//        SurfFeatureHistogramBuilder sh = new LearningLogSurfFeatureHistogramBuilder(ir, 200, 50, propertyService.getLuceneImageSurfData()); 
//        // progress monitoring is optional and opens a window showing you the progress.
//        sh.indexMissing();
	}
	
	public LinkedHashMap<String, Double> searchImage(BufferedImage image) throws IOException{
		LinkedHashMap<String, Double> result = new LinkedHashMap<>();
		File file = new File(propertyService.getLuceneImageIndex(), propertyService.getProjectName());
		if(file.exists()) file.mkdirs();

		 ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);
	        try(IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(propertyService.getLuceneImageIndex(), propertyService.getProjectName())))){
	        	DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
		        Document query = builder.createDocument(image, "query");
		        ImageSearchHits hits = searcher.search(query, reader);
		        if(hits!=null){
			        for (int i = 0; i < hits.length(); i++) {
			        	if(hits.score(i) < 0.7){
			        		continue;
			        	}
			        	//itemId
			            String itemId = hits.doc(i).get(DocumentBuilder.FIELD_NAME_IDENTIFIER);
			            result.put(itemId, new Double(hits.score(i)));
			        }
		        }
		        return result;
	        }
	}

	/**
	 * return ItemIds
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public LinkedHashMap<String, Double> searchImage(InputStream is) throws IOException{
		return this.searchImage(ImageIO.read(is));
	}
	
	public static void main(String[] args) throws IOException{
		Surf surf = new Surf(ImageIO.read(new File("/Users/houbin/Desktop/testphotos/123.png")));
		Surf surf1 = Surf.readFromFile("/Users/houbin/Desktop/testphotos/abc.surf");
		long start = System.currentTimeMillis();
		for(int i=0;i<3000;i++){
//		  BufferedImage imageA = ImageIO.read(new File("/Users/houbin/Desktop/testphotos/123.png"));
          
//          Surf.saveToFile(surf1, "/Users/houbin/Desktop/testphotos/abc.surf");
          surf1.getMatchingPoints(surf, true);
		}
		System.out.println(System.currentTimeMillis()-start);
	}
}
