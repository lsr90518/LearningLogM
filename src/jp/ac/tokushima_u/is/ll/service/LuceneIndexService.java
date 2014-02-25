package jp.ac.tokushima_u.is.ll.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoDao;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LuceneIndexService {
	
	private static final Logger logger = LoggerFactory.getLogger(LuceneIndexService.class);
	
	private static final Version LUCENE_VERSION = Version.LUCENE_43;
	
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private PacallPhotoDao pacallPhotoDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private FileDataDao fileDataDao;
	@Autowired
	private StaticServerService staticServerService;
	/**
	 * BlockingQueue<Item.id>
	 */
	private static BlockingQueue<String> itemPhotoIndexQueue = new LinkedBlockingQueue<String>();
	
	public LuceneIndexService(){
		new Thread(){
			@Override
			public void run() {
				while(true){
					try {
						String itemId = itemPhotoIndexQueue.take();
						logger.debug("Start to process pacallPhoto, id:"+itemId);
						
						Item item = itemDao.findById(itemId);
						//find if item is already indexed
						if(item==null || item.getImage()==null)continue;
						FileData fileData = fileDataDao.findById(item.getImage());
						if(!"image".equals(fileData.getFileType())){
							continue;
						}
						Directory indexDir = createDefaultImageIndexDirectory();
						IndexReader indexReader = DirectoryReader.open(indexDir);
						IndexSearcher searcher = new IndexSearcher(indexReader);
						TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,item.getId()));  
						TopDocs docs = searcher.search(query, 1);
						if(docs.totalHits>0){
							continue;
						}
						
						InputStream file = new URL(staticServerService.accessurl(fileData.getId()+"_640_480.jpg")).openStream();
						logger.debug("Start to process");
						DocumentBuilder builder = createImageDocumentBuilder();
						IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
						IndexWriter iw = new IndexWriter(indexDir, config);
						Document doc = builder.createDocument(new BufferedInputStream(file), item.getId());
						iw.addDocument(doc);
						iw.close();
						logger.debug("Process succeed");
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public void addPacallPhotoListToImageIndex(List<PacallPhoto> photoList) {
		if(photoList == null || photoList.size() == 0)return;
		try {
			Directory indexDir = createPacallImageIndexDirectory(photoList.get(0).getUserId());
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
			try (
					IndexReader indexReader = DirectoryReader.open(indexDir);
					IndexWriter indexWriter = new IndexWriter(indexDir, config);){
				
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				DocumentBuilder builder = createImageDocumentBuilder();
				
				for(PacallPhoto photo: photoList){
					TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photo.getId()));  
					TopDocs docs = indexSearcher.search(query, 1);
					if(docs.totalHits>0){
						indexWriter.deleteDocuments(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photo.getId()));
					}
					
					InputStream file = new URL(staticServerService.accessurl(photo.getId()+"_640_480.jpg")).openStream();
					logger.debug("Start to process");
					Document doc = builder.createDocument(new BufferedInputStream(file), photo.getId());
					indexWriter.addDocument(doc);
					doc = null;
					logger.debug("Process succeed");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addPacallPhotoIdToIndex(String pacallPhotoId) {
		IndexReader indexReader = null;
		IndexWriter indexWriter = null;
		IndexSearcher indexSearcher = null;
		try {
			logger.debug("Start to process pacallPhoto, id:"+pacallPhotoId);
			
			PacallPhoto photo = pacallPhotoDao.findById(pacallPhotoId);
			Directory indexDir = createPacallImageIndexDirectory(photo.getUserId());
			indexReader = DirectoryReader.open(indexDir);
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
			indexWriter = new IndexWriter(indexDir, config);
			indexSearcher = new IndexSearcher(indexReader);
			
			TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photo.getId()));  
			TopDocs docs = indexSearcher.search(query, 1);
			if(docs.totalHits>0){
//				TODO return;
				indexWriter.deleteDocuments(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photo.getId()));
			}
			
			InputStream file = new URL(staticServerService.accessurl(photo.getId()+"_640_480.jpg")).openStream();
			logger.debug("Start to process");
			DocumentBuilder builder = createImageDocumentBuilder();
			Document doc = builder.createDocument(new BufferedInputStream(file), photo.getId());
			indexWriter.addDocument(doc);
			doc = null;
			logger.debug("Process succeed");
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
				if(indexReader!=null){
					indexReader.close();
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addItemIdToIndex(String itemId){
		try {
			itemPhotoIndexQueue.put(itemId);
		} catch (InterruptedException e) {
			logger.error("Error when adding itemId to index", e);
		}
	}
	


	public void addItemListToIndex(List<Item> itemList) throws IOException {
		for(Item item:itemList){
			this.addItemIdToIndex(item.getId());
		}
	}
	
	/**
	 * Search for file
	 * @param is
	 * @return Map<ItemId, score>
	 * @throws IOException
	 */
	public LinkedHashMap<String, Float> searchItemIdsByImage(InputStream is, int maxNumber) throws IOException{
		try(IndexReader reader = DirectoryReader.open(createDefaultImageIndexDirectory())){
			ImageSearcher searcher = createImageSearcher(maxNumber);
	//		VisualWordsImageSearcher searcher = new VisualWordsImageSearcher(100, DocumentBuilder.FIELD_NAME_SIFT_LOCAL_FEATURE_HISTOGRAM_VISUAL_WORDS);
			ImageSearchHits hits = null;
			hits = searcher.search(is, reader);
			LinkedHashMap<String, Float> result = new LinkedHashMap<String, Float>();
			for (int i = 0; i < hits.length(); i++) {
				if(hits.score(i)>0.25){
					result.put(hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue(), hits.score(i));
				}
	        }
			return result;
		}
	}
	
	public LinkedHashMap<String, Float> searchItemIdsByIndexedItem(Item item, int maxNumber) throws IOException{
		LinkedHashMap<String, Float> result = new LinkedHashMap<String, Float>();
		try(IndexReader reader = DirectoryReader.open(createDefaultImageIndexDirectory())){
			IndexSearcher indexSearcher = new IndexSearcher(reader); 
			ImageSearcher searcher = createImageSearcher(maxNumber);
			TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER, item.getId()));
			TopDocs docs = indexSearcher.search(query, 1);
			if(docs.totalHits>0){
				Document doc = indexSearcher.doc(docs.scoreDocs[0].doc);
				ImageSearchHits hits = null;
				hits = searcher.search(doc, reader);
				for (int i = 0; i < hits.length(); i++) {
					if(hits.score(i)>0.25){
						result.put(hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue(), hits.score(i));
					}
		        }
			}
		}
		return result;
	}
	
	public LinkedHashMap<String, Float> searchPacallPhotoByIndexedPacallPhoto(PacallPhoto photo , int maxNumber) throws IOException{
		LinkedHashMap<String, Float> result = new LinkedHashMap<String, Float>();
		List<String> toDelete = new ArrayList<String>();
		try(IndexReader reader = DirectoryReader.open(createPacallImageIndexDirectory(photo.getUserId()))){
			IndexSearcher indexSearcher = new IndexSearcher(reader); 
			ImageSearcher searcher = createImageSearcher(maxNumber);
			TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER, photo.getId()));
			TopDocs docs = indexSearcher.search(query, 1);
			if(docs.totalHits>0){
				Document doc = indexSearcher.doc(docs.scoreDocs[0].doc);
				ImageSearchHits hits = null;
				hits = searcher.search(doc, reader);
				for (int i = 0; i < hits.length(); i++) {
					if(hits.score(i)>0.25){
						String photoId = hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
						if(photoId.equals(photo.getId()))continue;
						PacallPhoto p = pacallPhotoDao.findById(photoId);
						if(p==null){
							toDelete.add(photoId);
						}else{
							result.put(photoId, hits.score(i));
						}
					}
		        }
			}
		}
		this.deletePhotoIndex(toDelete, photo.getUserId());
		return result;
	}
	
	public LinkedHashMap<String, Float> searchItemIdsByIndexedPacallPhoto(PacallPhoto photo , int maxNumber) throws IOException{
		LinkedHashMap<String, Float> result = new LinkedHashMap<String, Float>();
		List<String> toDelete = new ArrayList<String>();
		try(IndexReader reader = DirectoryReader.open(createPacallImageIndexDirectory(photo.getUserId()))){
				try(IndexReader reader2 = DirectoryReader.open(createDefaultImageIndexDirectory())){
					IndexSearcher indexSearcher = new IndexSearcher(reader);
					ImageSearcher searcher = createImageSearcher(maxNumber);
					TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER, photo.getId()));
					TopDocs docs = indexSearcher.search(query, 1);
					if(docs.totalHits>0){
						Document doc = indexSearcher.doc(docs.scoreDocs[0].doc);
						ImageSearchHits hits = null;
						hits = searcher.search(doc, reader2);
						for (int i = 0; i < hits.length(); i++) {
							if(hits.score(i)>0.25){
								String itemId = hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
								Item item = itemDao.findById(itemId);
								if(item==null){
									toDelete.add(itemId);
								}else{
									result.put(itemId, hits.score(i));
								}
							}
					}
				}
			}
		}
		deleteItemIndex(toDelete);
		return result;
	}
	
	private void deleteItemIndex(List<String> toDelete) throws IOException {
		Directory indexDir = createDefaultImageIndexDirectory();
		try(IndexReader indexReader = DirectoryReader.open(indexDir)){
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
			try(IndexWriter indexWriter = new IndexWriter(indexDir, config)){
				for(String itemId: toDelete){
					indexWriter.deleteDocuments(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,itemId));
				}
			}
		}
	}

	private void deletePhotoIndex(List<String> toDelete, String userId) throws IOException {
		Directory indexDir = createPacallImageIndexDirectory(userId);
		try(IndexReader indexReader = DirectoryReader.open(indexDir)){
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
			try(IndexWriter indexWriter = new IndexWriter(indexDir, config)){
				for(String photoId: toDelete){
					indexWriter.deleteDocuments(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photoId));
				}
			}
		}
	}

	public boolean isPacallPhotoIndexed(PacallPhoto photo) throws IOException{
		try(IndexReader reader = DirectoryReader.open(createPacallImageIndexDirectory(photo.getUserId()))){
			IndexSearcher indexSearcher = new IndexSearcher(reader);
			TermQuery query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,photo.getId()));  
			TopDocs docs = indexSearcher.search(query, 1);
			if(docs.totalHits>0){
				return true;
			}
		}
		return false;
	}
	
	private DocumentBuilder createImageDocumentBuilder(){
		return DocumentBuilderFactory.getCEDDDocumentBuilder();
	}
	
	private ImageSearcher createImageSearcher(int maxNumber){
		return ImageSearcherFactory.createCEDDImageSearcher(maxNumber);
	}
	
	private Directory createDefaultImageIndexDirectory() throws IOException {
		Directory directory =  createDirectory(this.propertyService.getProjectName(), "imageindex");
		IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
		try(IndexWriter indexWriter = new IndexWriter(directory, config)){
			indexWriter.commit();
		}
		return directory;
	}
	
	private Directory createPacallImageIndexDirectory(String userId) throws IOException {
		Directory directory = createDirectory(propertyService.getProjectName()+"_pacall", userId);
		IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new SimpleAnalyzer(LUCENE_VERSION));
		try(IndexWriter indexWriter = new IndexWriter(directory, config)){
			indexWriter.commit();
		}
		return directory;
	}
	
	private static Map<String, Directory> directories = new HashMap<String, Directory>();
	
	private Directory createDirectory(String db, String index) throws IOException{
		String key = db+"_"+index;
		if(directories.get(key)==null){
			Directory dir = new NIOFSDirectory(new File("~/tmp/lucene"));
			directories.put(key, dir);
		}
		return directories.get(key);
	}
}
