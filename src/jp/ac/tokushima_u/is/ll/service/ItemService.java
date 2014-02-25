package jp.ac.tokushima_u.is.ll.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import jp.ac.tokushima_u.is.ll.dao.AnswerDao;
import jp.ac.tokushima_u.is.ll.dao.CategoryDao;
import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.ItemCommentDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQueueDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTagDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.LogUserReadItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.entity.Answer;
import jp.ac.tokushima_u.is.ll.entity.Category;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemComment;
import jp.ac.tokushima_u.is.ll.entity.ItemQuestionType;
import jp.ac.tokushima_u.is.ll.entity.ItemTag;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.entity.Question;
import jp.ac.tokushima_u.is.ll.entity.QuestionType;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ItemCommentForm;
import jp.ac.tokushima_u.is.ll.form.ItemEditForm;
import jp.ac.tokushima_u.is.ll.form.ItemSearchCondForm;
import jp.ac.tokushima_u.is.ll.form.ItemSyncCondForm;
import jp.ac.tokushima_u.is.ll.form.QuestionAnswerForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizConstants;
import jp.ac.tokushima_u.is.ll.service.visualization.ReviewHistoryService;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.GeoUtils;
import jp.ac.tokushima_u.is.ll.util.HashUtils;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.PicExifUtil;
import jp.ac.tokushima_u.is.ll.util.TextUtils;
import jp.ac.tokushima_u.is.ll.ws.service.model.ItemForm;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.IOUtils;

import freemarker.template.TemplateException;

@Service
@Transactional(readOnly = true)
public class ItemService {

	private Logger logger = LoggerFactory.getLogger(ItemService.class);

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private FileDataDao fileDataDao;
	@Autowired
	private AnswerDao answerDao;
	@Autowired
	private QuestionDao questionDao;
	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private ItemTitleDao itemTitleDao;
	@Autowired
	private ItemTagDao itemTagDao;
	@Autowired
	private ItemQueueDao itemQueueDao;
	@Autowired
	private LogUserReadItemDao logUserReadItemDao;
	@Autowired
	private QuestionTypeDao questionTypeDao;
	@Autowired
	private ItemQuestionTypeDao itemQuestionTypeDao;
	@Autowired
	private ItemCommentDao itemCommentDao;
	@Autowired
	private PacallPhotoDao pacallPhotoDao;

	@Autowired
	private ItemQueueService itemQueueService;
	@Autowired
	private ReviewHistoryService reviewHistoryService;
	@Autowired
	private MailService mailService;
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private StaticServerService staticServerService;
	@Autowired
	private ImageIndexService imageIndexService;

	public Map<String, List<Question>> searchToAnswerQuestions() {
		String userId = SecurityUserHolder.getCurrentUser().getId();
		Map<String, List<Question>> result = new HashMap<String, List<Question>>();
		result.put("toAnswerQuestions", this.questionDao.findListNotAnsweredForNativeSpeaker(userId, new PageRequest(0, 10)));
		result.put("toStudyQuestions", this.questionDao.findListAnsweredForLearner(userId, new PageRequest(0, 10)));
		result.put("answerQuestions", this.questionDao.findListAnsweredForAuthor(userId, new PageRequest(0, 10)));
		return result;
	}

	public List<QuestionType> searchAllQuestionTypes() {
		return this.questionTypeDao.findListAll();
	}

	public List<Item> searchAllToAnswer() {
		return this.questionDao.findListAllToAnswer(SecurityUserHolder.getCurrentUser().getId());
	}

	public List<Item> searchRelatedItemForTask(String taskId, String querystr, Integer limit){
		List<Item> list= this.itemDao.findsearchRelatedItemForTask(taskId, querystr, limit);
		
		
		
		return list;
	}
	public List<ItemTitle> searchRelated(String taskId, String querystr, Integer limit){
		List<ItemTitle> list= this.itemTitleDao.findsearchRelatedItemContent(taskId, querystr, limit);
		
		return list;
	}
	
	
	@Transactional(readOnly = false)
	public Item uploadFromPacall(String pacallPhotoId) {
		Date current = new Date(System.currentTimeMillis());

		Item item = new Item();

		PacallPhoto photo = pacallPhotoDao.findById(pacallPhotoId);
		item.setItemLat(photo.getLat());
		item.setItemLng(photo.getLng());

		item.setItemZoom(10);
		item.setCreateTime(current);
		item.setUpdateTime(current);

		// Author
		item.setAuthorId(SecurityUserHolder.getCurrentUser().getId());
		
		FileData fileData = fileDataDao.findById(photo.getId());
		if(fileData==null){
			fileData = new FileData();
			fileData.setMd5(photo.getHash());
			fileData.setId(KeyGenerateUtil.generateIdUUID());
			fileData.setOrigName(photo.getFilename()+"."+photo.getExt());
			fileData.setCreateAt(current);
			String fileType = "";
			if (!StringUtils.isBlank(photo.getFilename()+"."+photo.getExt())) {
				fileType = FilenameUtil.checkMediaType(photo.getFilename()+"."+photo.getExt());
				fileData.setFileType(fileType);
			}
			try {
				URL url = new URL(staticServerService.accessurl(propertyService.getPacallProject(), photo.getId()+"_800x600.png"));
				byte[] b = IOUtils.readByteArray(url.openStream());
				staticServerService.upload(b, fileData.getId(), photo.getExt());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileDataDao.insert(fileData);
		}

		item.setImage(fileData.getId());
		item.setDisabled(1);// Disable
		item.setId(KeyGenerateUtil.generateIdUUID());
		itemDao.insert(item);
		
		if ("image".equals(fileData.getFileType())) {
			List<FileData> fileDataList = new ArrayList<FileData>();
			fileDataList.add(fileData);
		}
		if(item.getImage()!=null){
			luceneIndexService.addItemIdToIndex(item.getId());
			try {
				imageIndexService.addToIndex(item);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	@Transactional(readOnly = false)
	public Item createByForm(ItemEditForm form) throws IOException {
		Date current = new Date(System.currentTimeMillis());
		String userId = SecurityUserHolder.getCurrentUser().getId();

		Item item = new Item();
		List<Long> quesTypeIds = form.getQuestionTypeIds();

		boolean imageFlg = false;

		// Attached File
		if (form.getImage() != null) {
			MultipartFile file = form.getImage();
			if (!file.isEmpty() && file.getSize() != 0) {
				FileData image = new FileData();
				image.setId(KeyGenerateUtil.generateIdUUID());
				byte[] b = form.getImage().getBytes();
				staticServerService.upload(b, image.getId(), FilenameUtils.getExtension(form.getImage().getOriginalFilename()));
				
				MultipartFile uploadFile = form.getImage();
				image.setOrigName(uploadFile.getOriginalFilename());
				image.setCreateAt(current);
				String fileType = "";
				if (!StringUtils.isBlank(uploadFile.getOriginalFilename())) {
					fileType = FilenameUtil.checkMediaType(uploadFile
							.getOriginalFilename());
					image.setFileType(fileType);
					if (FilenameUtil.IMAGE.equals(fileType))
						imageFlg = true;
				}
				image.setMd5(HashUtils.md5Hex(b));
				fileDataDao.insert(image);
				
				item.setImage(image.getId());
			}
		}

		if (quesTypeIds != null
				&& quesTypeIds.contains(Constants.QuizTypeImageMutiChoice)
				&& !imageFlg) {
			quesTypeIds.remove(Constants.QuizTypeImageMutiChoice);
		}

		if (!StringUtils.isBlank(form.getCategoryId())) {
			Category category = categoryDao.findById(form.getCategoryId());
			if (category != null) {
				item.setCategory(category.getId());
			} else {
				item.setCategory(null);
			}
		} else {
			item.setCategory(null);
		}

		item.setBarcode(form.getBarcode());
		item.setQrcode(form.getQrcode());
		item.setRfid(form.getRfid());
		double[] position = null;
		if (form.getImage() != null && !form.getImage().isEmpty()) {
			try {
				position = PicExifUtil.readGps(new BufferedInputStream(form
						.getImage().getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (position == null) {
			item.setItemLat(form.getItemLat());
			item.setItemLng(form.getItemLng());
		} else {
			item.setItemLat(position[0]);
			item.setItemLng(position[1]);
		}
		if (form.getSpeed() != null){
			item.setSpeed(form.getSpeed());
		}

		item.setItemZoom(form.getItemZoom());
		item.setNote(form.getNote());
		item.setPlace(form.getPlace());
		item.setCreateTime(current);
		item.setUpdateTime(current);
		item.setShareLevel(form.getShareLevel());
		item.setAuthorId(SecurityUserHolder.getCurrentUser()
				.getId());
		item.setDisabled(0);

		if (!StringUtils.isBlank(form.getQuestion())) {
			Language lang = languageDao.findByCode(form.getQuesLan());
			if(lang!=null){
				Question question = new Question();
				question.setContent(form.getQuestion());
				question.setLanguageId(lang.getId());
				question.setId(KeyGenerateUtil.generateIdUUID());
				questionDao.insert(question);
				item.setQuestionId(question.getId());
			}
		}

		boolean locationFlg = false;
		if (form.getLocationBased() != null
				&& form.getLocationBased().booleanValue()) {
			locationFlg = GeoUtils.isHighPrecision(item.getItemLat(),
					item.getItemLng());
		}
		item.setLocationbased(locationFlg);

		// Save to database
		item.setId(KeyGenerateUtil.generateIdUUID());
		itemDao.insert(item);
		
		for (String key : form.getTitleMap().keySet()) {
			Language lang = this.languageDao.findByCode(key);
			if (lang == null
					|| StringUtils.isBlank(form.getTitleMap().get(key)))
				continue;
			ItemTitle title = new ItemTitle();
			title.setContent(form.getTitleMap().get(key));
			title.setLanguage(lang.getId());
			title.setItem(item.getId());
			title.setId(KeyGenerateUtil.generateIdUUID());
			itemTitleDao.insert(title);
			
			if (quesTypeIds != null
					&& quesTypeIds.contains(Constants.QuizTypeImageMutiChoice)) {
				ItemQuestionType qt = new ItemQuestionType();
				qt.setItemId(item.getId());
				qt.setLanguageId(lang.getId());
				qt.setQuestiontypeId(Constants.QuizTypeImageMutiChoice);
				qt.setId(KeyGenerateUtil.generateIdUUID());
				itemQuestionTypeDao.insert(qt);
			}
			if (quesTypeIds != null
					&& quesTypeIds.contains(Constants.QuizTypeTextMutiChoice)) {
				ItemQuestionType qt = new ItemQuestionType();
				qt.setItemId(item.getId());
				qt.setLanguageId(lang.getId());
				qt.setQuestiontypeId(Constants.QuizTypeTextMutiChoice);
				qt.setId(KeyGenerateUtil.generateIdUUID());
				itemQuestionTypeDao.insert(qt);
			}
		}
		
		if (quesTypeIds != null
				&& quesTypeIds.contains(Constants.QuizTypeYesNoQuestion)) {
			ItemQuestionType qt = new ItemQuestionType();
			qt.setItemId(item.getId());
			qt.setQuestiontypeId(Constants.QuizTypeYesNoQuestion);
			qt.setId(KeyGenerateUtil.generateIdUUID());
			itemQuestionTypeDao.insert(qt);
		}
		
		// Tags
		if (!StringUtils.isBlank(form.getTag())) {
			List<String> tagArray = TextUtils.splitString(form.getTag()); 
			for (String t : tagArray) {
				ItemTag tag = itemTagDao.findByTag(t.trim());
				if (tag == null) {
					tag = new ItemTag();
					tag.setTag(t.trim());
					tag.setCreateTime(current);
					tag.setUpdateTime(current);
					tag.setId(KeyGenerateUtil.generateIdUUID());
					itemTagDao.insert(tag);
				}
				itemTagDao.insertRelationWithItem(item, tag);
			}
		}

		itemQueueService.updateItemQueue(item.getId(), userId,
				QuizConstants.QueueTypeNewObject);
		reviewHistoryService.updateItemState(item.getId(), userId,
				Constants.ExperiencedState, null);

		// this.llquizService.insertQuizzes(item);
		if(item.getImage()!=null){
			luceneIndexService.addItemIdToIndex(item.getId());
			try {
				imageIndexService.addToIndex(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	@Transactional(readOnly = false)
	public Item updateByForm(String id, ItemEditForm form) throws IOException {
		
		Date current = new Date(System.currentTimeMillis());
		Item item = this.itemDao.findByIdIgnoreDisableFlg(id);

		if (StringUtils.isNotBlank(form.getCategoryId())) {
			Category category = categoryDao.findById(form.getCategoryId());
			if (category != null) {
				item.setCategory(category.getId());
			} else {
				item.setCategory(null);
			}
		} else {
			item.setCategory(null);
		}

		String fileType = "";
		
		// Attached File
		FileData image = fileDataDao.findById(item.getImage());
		
		if (form.getImage() != null) {
			this.fileDataDao.delete(item.getImage());
			//TODO Delete from storage
			MultipartFile file = form.getImage();
			if (file != null && !file.isEmpty() && file.getSize() != 0) {
				image = new FileData();
				image.setId(KeyGenerateUtil.generateIdUUID());
				byte[] b = file.getBytes();
				staticServerService.upload(b, image.getId(), FilenameUtils.getExtension(form.getImage().getOriginalFilename()));
				
				image.setMd5(HashUtils.md5Hex(b));
				MultipartFile uploadFile = form.getImage();
				image.setOrigName(uploadFile.getOriginalFilename());
				image.setCreateAt(current);
				if (!StringUtils.isBlank(uploadFile.getOriginalFilename())) {
					fileType = FilenameUtil.checkMediaType(uploadFile
							.getOriginalFilename());
					image.setFileType(fileType);
				}
				this.fileDataDao.insert(image);
				item.setImage(image.getId());
			}
		}
		if(image!=null){
			fileType = image.getFileType();
		}

		List<Long> quesTypeIds = form.getQuestionTypeIds();
		if (!FilenameUtil.IMAGE.equals(fileType) && quesTypeIds != null
				&& quesTypeIds.contains(Constants.QuizTypeImageMutiChoice)) {
			quesTypeIds.remove(Constants.QuizTypeImageMutiChoice);
		}

		itemTitleDao.deleteAllByItemId(item.getId());
		itemQuestionTypeDao.deleteAllByItemId(item.getId());

		for (String key : form.getTitleMap().keySet()) {
			Language lang = this.languageDao.findByCode(key);
			if (lang == null
					|| StringUtils.isBlank(form.getTitleMap().get(key)))
				continue;
			ItemTitle title = new ItemTitle();
			title.setContent(form.getTitleMap().get(key));
			title.setLanguage(lang.getId());
			title.setItem(item.getId());
			title.setId(KeyGenerateUtil.generateIdUUID());
			itemTitleDao.insert(title);

			if (quesTypeIds != null
					&& quesTypeIds.contains(Constants.QuizTypeImageMutiChoice)) {
				ItemQuestionType qt = new ItemQuestionType();
				qt.setItemId(item.getId());
				qt.setLanguageId(lang.getId());
				qt.setQuestiontypeId(Constants.QuizTypeImageMutiChoice);
				qt.setId(KeyGenerateUtil.generateIdUUID());
				itemQuestionTypeDao.insert(qt);
			}
			if (quesTypeIds != null
					&& quesTypeIds.contains(Constants.QuizTypeTextMutiChoice)) {
				ItemQuestionType qt = new ItemQuestionType();
				qt.setItemId(item.getId());
				qt.setLanguageId(lang.getId());
				qt.setQuestiontypeId(Constants.QuizTypeTextMutiChoice);
				qt.setId(KeyGenerateUtil.generateIdUUID());
				itemQuestionTypeDao.insert(qt);
			}
		}

		if (quesTypeIds != null
				&& quesTypeIds.contains(Constants.QuizTypeYesNoQuestion)) {
			ItemQuestionType qt = new ItemQuestionType();
			qt.setItemId(item.getId());
			qt.setQuestiontypeId(Constants.QuizTypeYesNoQuestion);
			qt.setId(KeyGenerateUtil.generateIdUUID());
			itemQuestionTypeDao.insert(qt);
		}

		item.setBarcode(form.getBarcode());
		item.setQrcode(form.getQrcode());
		item.setRfid(form.getRfid());
		item.setPlace(form.getPlace());
		double[] position = null;
		if (form.getImage() != null) {
			position = PicExifUtil.readGps(new BufferedInputStream(form
					.getImage().getInputStream()));
		}
		if (position == null) {
			item.setItemLat(form.getItemLat());
			item.setItemLng(form.getItemLng());
		} else {
			item.setItemLat(position[0]);
			item.setItemLng(position[1]);
		}
		item.setItemZoom(form.getItemZoom());
		item.setNote(form.getNote());
		item.setUpdateTime(current);
		item.setShareLevel(form.getShareLevel());
		this.questionDao.deleteById(item.getQuestionId());
		item.setQuestionId(null);
		
		if (StringUtils.isNotBlank(form.getQuestion())) {
			Question question = new Question();;
			question.setContent(form.getQuestion());
			Language language = this.languageDao.findByCode(form.getQuesLan());
			question.setLanguageId(language.getId());
			question.setId(KeyGenerateUtil.generateIdUUID());
			this.questionDao.insert(question);
			item.setQuestionId(question.getId());
		}
		
		boolean locationFlg = false;
		if (form.getLocationBased() != null
				&& form.getLocationBased().booleanValue()) {
			locationFlg = GeoUtils.isHighPrecision(item.getItemLat(),
					item.getItemLng());
		}
		item.setLocationbased(locationFlg);
		// if(form.getQuestionTypeIds()!=null){
		// List<String>quesTypeIds = form.getQuestionTypeIds();
		// if(quesTypeIds!=null&&quesTypeIds.size()>0){
		// item.getQuestionTypes().clear();
		// for(String quesTypeId:quesTypeIds){
		// QuestionType qt = this.questionTypeDao.findUniqueBy("id",
		// quesTypeId);
		// if(qt!=null){
		// item.addQuestionType(qt);
		// }
		// }
		// }
		// }else
		// item.setQuestionTypes(new HashSet<QuestionType>());
		item.setDisabled(0);
		item.setUpdateTime(new Date());
		this.itemDao.update(item);

		// this.llquizService.updateQuizzes(item);
		if(item.getImage()!=null){
			luceneIndexService.addItemIdToIndex(item.getId());
		}
		return item;
	}

	@Transactional(readOnly = false)
	public void createCommentByForm(String itemId, ItemCommentForm form) {
		Date current = Calendar.getInstance().getTime();
		Users user = usersDao.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		// Tags
		Item item = this.findById(itemId);

		if (!StringUtils.isBlank(form.getTag())) {
			List<String> tagArray = TextUtils.splitString(form.getTag());
			for (String t : tagArray) {
				ItemTag tag = itemTagDao.findByTag(t.trim());
				if (tag == null) {
					tag = new ItemTag();
					tag.setTag(t.trim());
					tag.setCreateTime(current);
					tag.setUpdateTime(current);
					tag.setId(KeyGenerateUtil.generateIdUUID());
					itemTagDao.insert(tag);
				}
				itemTagDao.insertRelationWithItem(item, tag);
			}
		}

		if (!StringUtils.isBlank(form.getComment())) {
			ItemComment comment = new ItemComment();
			comment.setComment(form.getComment());
			comment.setCreateTime(current);
			comment.setUpdateTime(current);
			comment.setUserId(user.getId());
			comment.setItemId(item.getId());
			comment.setId(KeyGenerateUtil.generateIdUUID());
			itemCommentDao.insert(comment);
			
			try {
				ModelMap model = new ModelMap();
				model.addAttribute("username", usersDao.findById(item.getAuthorId()).getNickname());
				model.addAttribute("url", propertyService.getSystemUrl()
						+ "/item/" + item.getId());
				model.addAttribute("commentusername", user.getNickname());
				mailService.sendSysMail(usersDao.findById(item.getAuthorId()).getPcEmail(),
						"Your have a comment", "commentMail", model);
			} catch (MessagingException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (TemplateException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Transactional(readOnly = false)
	public void createAnswerByForm(String itemId, String content) {
		Date current = Calendar.getInstance().getTime();
		Item item = this.findById(itemId);
		Question question = this.questionDao.findById(item.getQuestionId());
		Answer answer = new Answer();
		answer.setAnswer(content);
		answer.setQuestionId(question.getId());
		answer.setCreateDate(current);
		String userId = SecurityUserHolder.getCurrentUser().getId();
		answer.setAuthorId(SecurityUserHolder.getCurrentUser().getId());

		if (!userId.equals(item.getAuthorId())) {
			try {
				Users author = usersDao.findById(item.getAuthorId());
				ModelMap model = new ModelMap();
				model.addAttribute("username",author.getNickname());
				model.addAttribute("url", propertyService.getSystemUrl()
						+ "/item/" + item.getId());
				model.addAttribute("answerusername",usersDao.findById(userId).getNickname());
				mailService.sendSysMail(author.getPcEmail(),
						"Your question has been answered",
						"questionAnsweredMail", model);
			} catch (MessagingException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (TemplateException ex) {
				ex.printStackTrace();
			}
		}
		answer.setId(KeyGenerateUtil.generateIdUUID());
		this.answerDao.insert(answer);
	}

	public List<Item> searchMyTodayItem(Users user, Date todaystart,
			Date todayend) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
//		criteria.add(Restrictions.eq("author", user));
//		criteria.add(Restrictions.ge("createTime", todaystart));
//		criteria.add(Restrictions.lt("createTime", todayend));
//		criteria.add(Restrictions.ne("disabled", new Integer(1)));
		return this.itemDao.findListByAuthorInPeriod(user.getId(), todaystart, todayend);
	}

	public void searchMyItem(ModelMap model) {
		Users user = usersDao.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		List<Item> itemList = this.itemDao.findListByAuthor(user.getId(), new Sort(Sort.Direction.DESC, "update_time"));
//		String hql = "from Item item where item.disabled!=1 and item.author =:user order by item.updateTime desc";
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("user", user);
//		Query query = itemDao.createQuery(hql, param);
		model.addAttribute("myitems", itemList);
		model.addAttribute("user", user);
	}

	public List<Item> searchMyItem(Users user) {
//		String hql = "from Item item where item.disabled!=1 and item.author =:user order by item.updateTime desc";
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("user", user);
//		Query query = itemDao.createQuery(hql, param);
//		return query.list();
		
		List<Item> itemList = this.itemDao.findListByAuthor(user.getId(), new Sort(Sort.Direction.DESC, "update_time"));
		return itemList;
	}

	public Page<ItemDTO> searchItemPageByCond(ItemSearchCondForm form) {

//		DetachedCriteria criteria = buildCriteriaByForm(form);
//		Page<Item> itemPage = new Page<Item>(10);
//		if (criteria == null)
//			return itemPage;
//		if (form.getPage() == null || form.getPage() < 1) {
//			form.setPage(1);
//		}
//		itemPage.setPageNo(form.getPage());
//		itemPage.setOrder(Page.DESC);
//		itemPage.setOrderBy("updateTime");
//		return this.itemDao.findPage(itemPage, criteria);
		
		int page = 0;
		if(form.getPage()!=null && form.getPage()>=1){
			page = form.getPage()-1;
		}
		Pageable pageable = new PageRequest(page, 10, new Sort(new Sort.Order(Sort.Direction.DESC, "i.update_time")));
		List<ItemDTO> list = this.itemDao.searchListByCond(form, pageable);
		Long count = this.itemDao.countByCond(form);
		return new PageImpl<ItemDTO>(list, pageable, count);
	}

	public List<Item> searchSyncItems(ItemSyncCondForm form) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
//
//		// By authorId
//		if (!StringUtils.isBlank(form.getUserId())) {
//			criteria.createAlias("author", "author").add(
//					Restrictions.eq("author.id", form.getUserId()));
//		} else if (!StringUtils.isBlank(form.getNotuserId())) {
//			criteria.createAlias("author", "author").add(
//					Restrictions.ne("author.id", form.getNotuserId()));
//		}
//
//		// By update date
//		if (form.getUpdateDateFrom() != null) {
//			criteria.add(Restrictions.ge("updateTime", form.getUpdateDateFrom()));
//		}
//		if (form.getUpdateDateTo() != null) {
//			criteria.add(Restrictions.le("updateTime", form.getUpdateDateTo()));
//		}
//
//		if (form.getNum() != null) {
//			Page<Item> itemPage = new Page<Item>(form.getNum());
//			itemPage.setPageNo(1);
//			itemPage.setOrder(Page.DESC);
//			itemPage.setOrderBy("updateTime");
//			return this.itemDao.findPage(itemPage, criteria).getResult();
//		}
//		criteria.addOrder(Order.desc("updateTime"));
//		
		int page = 0;
		int pageSize = Integer.MAX_VALUE;
		if(form.getNum()!=null){
			pageSize = form.getNum();
		}
		Pageable pageable = new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "update_time"));
		return this.itemDao.findListBySyncCond(form, pageable);
	}

	/**
	 * Find objects around the given coordinate(lat, lng)
	 * 
	 * @param lat
	 *            緯度(-90~90)
	 * @param lng
	 *            　経度(-180~180)
	 * @param distance
	 *            unit:km
	 * @param userId
	 *            Nullable, if it is null, search in all users' items
	 * @param itemSize
	 * @return
	 */
	public Page<Item> searchNearestItems(String avoidItemId, double lat,
			double lng, double distance, String userId, int itemSize) {
		if (itemSize == 0)
			itemSize = 1;
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
//		criteria.add(Restrictions.ne("disabled", new Integer(1)));
//		String userCond = "";
//		if (userId != null) {
//			userCond = " and i.author_id='" + userId + "'";
//		}
//		String avoidCond = "";
//		if (avoidItemId != null) {
//			avoidCond = " and i.id<>'" + avoidItemId + "'";
//		}
////		criteria.add(Restrictions.isNull("relogItem"));
//		String sql = "select "
//				+ " t.*"
//				+ "	from"
//				+ "		("
//				+ "		select i.*, get_distance("
//				+ lat
//				+ ","
//				+ lng
//				+ ", i.item_lat, i.item_lng) as distance"
//				+ "		from t_item i"
//				+ "		where i.disabled!=1 and i.item_lat is not null and i.item_lng is not null and i.relog_item is null"
//				+ userCond + avoidCond + "		order by distance asc" + "	) as t"
//				+ "	where" + "		t.distance>=0 and t.distance<=" + distance
//				+ " limit 0, " + itemSize;
//		
		Pageable pageable = new PageRequest(0, itemSize);
		List<Item> list = this.itemDao.findListForNearestItems(lat, lng, distance, userId, avoidItemId, pageable);
		return new PageImpl<Item>(list);
//		Page<Item> itemPage = new Page<Item>(itemSize);
//
//		Session session = this.sessionFactory.getCurrentSession();
//		@SuppressWarnings("unchecked")
//		List<Item> itemList = session.createSQLQuery(sql).addEntity(Item.class)
//				.list();
//		itemPage.setResult(itemList);
	}

	public List<Item> searchNearestItemsWithoutNotified(double lat, double lng,
			double distance, int itemSize) {
		String userId = SecurityUserHolder.getCurrentUser().getId();
//		String sql = "select "
//				+ " t.*"
//				+ "	from"
//				+ "		("
//				+ "		select i.*, get_distance("
//				+ lat
//				+ ","
//				+ lng
//				+ ", i.item_lat, i.item_lng) as distance"
//				+ "		from t_item i"
//				+ "		where i.disabled!=1 and i.item_lat is not null and i.item_lng is not null and i.relog_item is null and (i.locationbased is null or i.locationbased =1)"
//				+ " 	and i.id not in ( select distinct ia.item  from t_itemalarm ia 		"
//				+ "	where ia.author_id = '" + user.getId()
//				+ "' and i.id = ia.item and ia.alarm_type = 0)"
//				+ "		order by distance asc" + "	) as t" + "	where"
//				+ "		t.distance>=0 and t.distance<=" + distance + " limit 0, "
//				+ itemSize;
//		Session session = this.sessionFactory.getCurrentSession();
//		return session.createSQLQuery(sql).addEntity(Item.class).list();
		return this.itemDao.findListForNearestItemsWithoutNotified(lat, lng, distance, itemSize, userId);
	}

	/**
	 * Search latest uploaded items
	 * 
	 * @param userId
	 *            Nullable, if it is null, search in all users' items
	 * @param itemSize
	 *            more than 0
	 * @return
	 */
	public Page<Item> searchLatestItems(String avoidItemId, Long userId,
			int itemSize) {
		if (itemSize == 0) itemSize = 1;
		
		List<Item> list =  itemDao.findListForLatestItems(avoidItemId, userId, itemSize);
		return new PageImpl<Item>(list);
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
//		criteria.add(Restrictions.ne("disabled", new Integer(1)));
//		if (userId != null) {
//			criteria.createAlias("author", "author").add(
//					Restrictions.eq("author.id", userId));
//		}
//		criteria.add(Restrictions.isNull("relogItem"));
//		if (avoidItemId != null)
//			criteria.add(Restrictions.ne("id", avoidItemId));
//		Page<Item> itemPage = new Page<Item>(itemSize);
//		itemPage.setOrder(Page.DESC);
//		itemPage.setOrderBy("createTime");
//		return itemDao.findPage(itemPage, criteria);
	}
/*
	private DetachedCriteria buildCriteriaByForm(ItemSearchCondForm form) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
		criteria.add(Restrictions.ne("disabled", new Integer(1)));

		// By Title
		if (!StringUtils.isBlank(form.getTitle())) {
			criteria.createAlias("titles", "title").add(
					Restrictions.like("title.content", form.getTitle(),
							MatchMode.ANYWHERE));
			// if (StringUtils.isBlank(form.getLang())) {
			// criteria.add(Restrictions.or(Restrictions.or(Restrictions.like(
			// "jpTitle", form.getTitle(), MatchMode.ANYWHERE),
			// Restrictions.like("enTitle", form.getTitle(),
			// MatchMode.ANYWHERE)), Restrictions.like(
			// "zhTitle", form.getTitle(), MatchMode.ANYWHERE)));
			// } else if ("en".equals(form.getLang())) {
			// criteria.add(Restrictions.like("enTitle", form.getTitle(),
			// MatchMode.ANYWHERE));
			// } else if ("jp".equals(form.getLang())) {
			// criteria.add(Restrictions.like("jpTitle", form.getTitle(),
			// MatchMode.ANYWHERE));
			// } else if ("zh".equals(form.getLang())) {
			// criteria.add(Restrictions.like("zhTitle", form.getTitle(),
			// MatchMode.ANYWHERE));
			// }
		}

		// By author or authorId
		if (!StringUtils.isBlank(form.getUserId())) {
			criteria.createAlias("author", "author").add(
					Restrictions.eq("author.id", form.getUserId()));
		} else if (!StringUtils.isBlank(form.getNickname())) {
			criteria.createAlias("author", "author").add(
					Restrictions.like("author.nickname", form.getNickname(),
							MatchMode.ANYWHERE));
		} else if (!StringUtils.isBlank(form.getUsername())) {
			criteria.createAlias("author", "author").add(
					Restrictions.eq("author.nickname", form.getUsername()));
		} else if (!StringUtils.isBlank(form.getNotuserId())) {
			criteria.createAlias("author", "author").add(
					Restrictions.ne("author.id", form.getUserId()));
		}

		// By Map range
		if (form.isMapenabled() && form.getX1() != null && form.getY1() != null
				&& form.getX2() != null && form.getY2() != null) {
			if (form.getY1() > form.getY2()) {
				criteria.add(Restrictions.and(
						Restrictions.between("itemLat", form.getX2(),
								form.getX1()),
						Restrictions.between("itemLng", form.getY2(),
								form.getY1())));
			} else {
				criteria.add(Restrictions.or(Restrictions.and(
						Restrictions.between("itemLat", form.getX2(),
								form.getX1()),
						Restrictions.between("itemLng", form.getY2(), 180d)),
						Restrictions.and(Restrictions.between("itemLat",
								form.getX2(), form.getX1()), Restrictions
								.between("itemLng", -180d, form.getY1()))));
			}
		}

		// By created Date
		if (form.getDateFrom() != null && form.getDateTo() != null) {
			Date dateFrom;
			Date dateTo;
			if (form.getDateFrom().before(form.getDateTo())) {
				dateFrom = form.getDateFrom();
				dateTo = form.getDateTo();
			} else {
				dateFrom = form.getDateTo();
				dateTo = form.getDateFrom();
			}
			criteria.add(Restrictions.between("createTime", dateFrom, dateTo));
		} else if (form.getDateFrom() != null) {
			criteria.add(Restrictions.ge("createTime", form.getDateFrom()));
		} else if (form.getDateTo() != null) {
			criteria.add(Restrictions.le("createTime", form.getDateTo()));
		}

		// By update date
		if (form.getUpdateDate() != null) {
			criteria.add(Restrictions.ge("updateTime", form.getUpdateDate()));
		}

		// By tag
		if (!StringUtils.isBlank(form.getTag())) {
			criteria.createAlias("itemTags", "tag").add(
					Restrictions.eq("tag.tag", form.getTag()));
		}

		// By answerUser or answerUserId
		if (!StringUtils.isBlank(form.getAnsweruserId())) {
			Users answerAuthor = this.userService.findById(form
					.getAnsweruserId());
			if (answerAuthor == null) {
				return null;
			}
			criteria.createAlias("question", "question")
					.createAlias("question.answerSet", "answer")
					.add(Restrictions.eq("answer.author", answerAuthor));
		} else if (!StringUtils.isBlank(form.getAnsweruser())) {
			Users answerAuthor = this.userService.findByNickname(form
					.getAnsweruser());
			if (answerAuthor == null) {
				return null;
			}
			criteria.createAlias("question", "question")
					.createAlias("question.answerSet", "answer")
					.add(Restrictions.eq("answer.author", answerAuthor));
		}

		// By question status
		if (!StringUtils.isBlank(form.getQuestionStatus())) {
			if ("inquestion".equals(form.getQuestionStatus())) {
				criteria.add(Restrictions.and(Restrictions
						.isNotNull("question"), Restrictions.or(
						Restrictions.isNull("questionResolved"),
						Restrictions.eq("questionResolved", Boolean.FALSE))));
			} else if ("resolved".equals(form.getQuestionStatus())) {
				criteria.add(Restrictions.or(
						Restrictions.eq("questionResolved", Boolean.TRUE),
						Restrictions.isNull("question")));
			}
		}

		// By teacher confirm status
		if (!StringUtils.isBlank(form.getTeacherConfirm())) {
			if ("confirmed".equals(form.getTeacherConfirm())) {
				criteria.add(Restrictions.eq("teacherConfirm", Boolean.TRUE));
			} else if ("needfixing".equals(form.getTeacherConfirm())) {
				criteria.add(Restrictions.eq("teacherConfirm", Boolean.FALSE));
			}
		}

		// By to answer quesLangs or quesLangsCode
		if (form.getToAnswerQuesLangs() != null
				&& form.getToAnswerQuesLangs().size() > 0) {
			criteria.createAlias("question", "question").add(
					Restrictions.and(
							Restrictions.isEmpty("question.answerSet"),
							Restrictions.in("question.language",
									form.getToAnswerQuesLangs())));
		} else if (form.getToAnswerQuesLangsCode() != null
				&& form.getToAnswerQuesLangsCode().size() > 0) {
			Set<Language> langSet = new HashSet<Language>();
			for (String code : form.getToAnswerQuesLangsCode()) {
				Language lang = languageService.findLangByCode(code);
				if (lang != null)
					langSet.add(lang);
			}
			if (langSet.size() > 0) {
				criteria.createAlias("question", "question").add(
						Restrictions.and(
								Restrictions.isEmpty("question.answerSet"),
								Restrictions.in("question.language", langSet)));
			}
		}

		// By to study quesLangs or quesLangsCode
		if (form.getToStudyQuesLangs() != null
				&& form.getToStudyQuesLangs().size() > 0) {
			criteria.createAlias("question", "question").add(
					Restrictions.and(
							Restrictions.isNotEmpty("question.answerSet"),
							Restrictions.in("question.language",
									form.getToStudyQuesLangs())));
		} else if (form.getToStudyQuesLangsCode() != null
				&& form.getToStudyQuesLangsCode().size() > 0) {
			Set<Language> langSet = new HashSet<Language>();
			for (String code : form.getToStudyQuesLangsCode()) {
				Language lang = languageService.findLangByCode(code);
				if (lang != null)
					langSet.add(lang);
			}
			if (langSet.size() > 0) {
				criteria.createAlias("question", "question").add(
						Restrictions.and(
								Restrictions.isNotEmpty("question.answerSet"),
								Restrictions.in("question.language", langSet)));
			}
		}

		if (form.getCategorySet() != null && form.getCategorySet().size() > 0) {
			criteria.add(Restrictions.in("category", form.getCategorySet()));
		}

		// has answers
		if (form.getHasAnswers() != null && form.getHasAnswers()) {
			criteria.createAlias("question", "question").add(
					Restrictions.isNotEmpty("question.answerSet"));
		}

		if (!form.isIncludeRelog()) {
			criteria.add(Restrictions.isNull("relogItem"));
		}

		if (!StringUtils.isBlank(form.getQrcode())) {
			criteria.add(Restrictions.eq("qrcode", form.getQrcode()));
		}

		return criteria;
	}
*/

	public List<Map<String, Object>> uploadRanking() {
		return this.itemDao.findMapListUploadRanking();
	}

	public List<Map<String, Object>> answerRanking() {
		return this.itemDao.findMapListAnswerRanking();
	}

	public Item findById(String id) {
		return itemDao.findById(id);
	}

	/*
	 * public void delete(Item item) { Item i = itemDao.get(item.getId()); if
	 * (i.getRelogItem() != null) { i.setImage(null); i.setRelogItem(null); }
	 * itemDao.delete(i); }
	 */

	@Transactional(readOnly = false)
	public void delete(String itemId) {
		Item item = this.itemDao.findById(itemId);
		if(item==null) return;
		
		item.setDisabled(1);
		item.setUpdateTime(new Date());
		this.itemDao.update(item);

//		List<ItemQuestionType> types = this.itemQuestionTypeDao.findBy("item",item);
//		for (ItemQuestionType type : types) {
//			this.itemQuestionTypeDao.delete(type);
//		}
		this.itemQuestionTypeDao.deleteAllByItemId(item.getId());

		// ItemQueue update
//		List<ItemQueue> iqs = this.itemQueueDao.findListByItem();
//		for (ItemQueue iq : iqs) {
//			iq.setDisabled(1);
//			this.itemQueueDao.update(iq);
//		}
		this.itemQueueDao.deleteAllByItem(item.getId());
	}

	public boolean isRelogable(String itemId, String userId) {
		Item item = itemDao.findById(itemId);
		if (userId.equals(item.getAuthorId())) {
			return false;
		}
//		String hql = "from Item item where item.author=? and item.relogItem = ?";
		List<Item> itemList = this.itemDao.findByAuthorAndRelogItem(userId, itemId);
		if (itemList != null && itemList.size() > 0) {
			return false;
		}
		return true;
	}

	@Transactional(readOnly = false)
	public Item relog(String itemId, String userId) {
		
		Item item = itemDao.findById(itemId);
		if (!this.isRelogable(itemId, userId)) {
			return null;
		}
		Date current = new Date(System.currentTimeMillis());
		Item reItem = new Item();
		BeanUtils.copyProperties(item, reItem);
		reItem.setAuthorId(userId);
		reItem.setCreateTime(current);
		reItem.setUpdateTime(current);
		reItem.setRelogItem(item.getId());
		reItem.setId(KeyGenerateUtil.generateIdUUID());
		itemDao.insert(reItem);
		
		List<ItemTitle> itemTitles = this.itemTitleDao.findListByItem(item.getId());
		for(ItemTitle title: itemTitles){
			title.setId(KeyGenerateUtil.generateIdUUID());
			title.setItem(reItem.getId());
			this.itemTitleDao.insert(title);
		}
		
		itemQueueService.updateItemQueue(itemId, userId,
				QuizConstants.QueueTypeNewObject);
		return reItem;
	}

	public Page<Item> searchRelatedItemList(String itemId) {
		// TODO
		Item item = this.itemDao.findById(itemId);
		if (item == null) {
			return new PageImpl<Item>(new ArrayList<Item>());
		}

		List<ItemTitle> titles = itemTitleDao.findListByItem(itemId);
		List<String> titleListDirty = new ArrayList<String>();
		for (ItemTitle title : titles) {
			String t = title.getContent();
			titleListDirty.addAll(Arrays.asList(t.split(" ")));
		}

		Set<String> titlesCond = new HashSet<String>();
		for (String t : titleListDirty) {
			if (!StringUtils.isBlank(t)) {
				titlesCond.add(t.trim());
			}
		}
		if (titlesCond.isEmpty()) {
			return new PageImpl<Item>(new ArrayList<Item>());
		}
		
		List<Item> itemResult = this.itemDao.searchItemByTitles(itemId, titlesCond, new PageRequest(0, 5, new Sort(Sort.Direction.DESC, "update_time")));
		return new PageImpl<Item>(itemResult);
		
//		Set<Item> itemResult = new HashSet<Item>();
//		for (String t : titlesCond) {
//			
//			DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
//			criteria.add(Restrictions.ne("disabled", new Integer(1)));
//			criteria.add(Restrictions.ne("id", item.getId()));
//			criteria.add(Restrictions.isNull("relogItem"));
//			criteria.createAlias("titles", "title");
//			criteria.add(Restrictions.like("title.content", t,
//					MatchMode.ANYWHERE));
//			Page<Item> itemPage = new Page<Item>(5);
//			itemPage.setPageNo(1);
//			itemPage.setOrder(Page.DESC);
//			itemPage.setOrderBy("updateTime");
//			itemDao.findPage(itemPage, criteria);
//			itemResult.addAll(itemPage.getResult());
//		}
//		Page<Item> result = new Page<Item>(itemResult.size());
//		result.setResult(new ArrayList<Item>(itemResult));
//		result.setTotalCount(itemResult.size());
//		return result;
	}

	@Transactional(readOnly = false)
	public void questionConfirm(Item it, Users u) {
		Item item = itemDao.findById(it.getId());
		item.setQuestionResolved(true);
		item.setUpdateTime(new Date(System.currentTimeMillis()));
		itemDao.update(item);
	}

	@Transactional(readOnly = false)
	public void teacherConfirm(Item it, Users u) {
		Item item = itemDao.findById(it.getId());
		item.setTeacherConfirm(Boolean.TRUE);
		itemDao.update(item);
	}

	@Transactional(readOnly = false)
	public void teacherReject(Item it, Users u) {
		Item item = itemDao.findById(it.getId());
		item.setTeacherConfirm(Boolean.FALSE);
		itemDao.update(item);
	}

	@Transactional(readOnly = false)
	public void teacherDeleteStatus(Item it, Users u) {
		Item item = itemDao.findById(it.getId());
		item.setTeacherConfirm(null);
		itemDao.update(item);
	}

	public List<Item> findByCategory(Category category) {
		return itemDao.findByCategory(category.getId());
	}

	public Map<String, Long> findTagCloud() {
		List<Map<String, Object>> map =  this.itemTagDao.findMapForTagAndContainsNumber();
		Map<String, Long> result = new HashMap<String, Long>();
		for(Map<String, Object> r: map){
			result.put((String)r.get("tag"), (Long)r.get("number"));
		}
		return result;
//		String hql = "select tag.tag,count(tag.tag) from ItemTag tag inner join tag.items as item group by tag.tag";
//		List<Object[]> result = this.itemTagDao.find(hql);
//		Map<String, Long> tagcloud = new HashMap<String, Long>();
//		for (Object[] m : result) {
//			tagcloud.put(String.valueOf(m[0]), Long.valueOf(m[1].toString()));
//		}
//		return tagcloud;
	}

	public Long findReadCount(String id) {
//		String hql = "select count(*) from LogUserReadItem log where log.item.id=?";
		Long count = logUserReadItemDao.countByItemId(id);
		return count;
	}

	public Long findRelogCount(String id) {
//		String hql = "select count(*) from Item item where item.relogItem.id=? and item.disabled!=1";
		Long count = itemDao.countReloggedTimes(id);
		return count;
	}

	public Long findAllItemCount() {
//		String hql = "select count(*) from Item item where item.disabled!=1";
		return itemDao.countAll();
	}

	public Long findAllReadCount() {
//		String hql = "select count(*) from LogUserReadItem log";
		return logUserReadItemDao.countAll();
	}

	public Long findAllTagCount() {
//		String hql = "select count(distinct tag.tag) from ItemTag tag inner join tag.items as item";
		return itemTagDao.countAll();
	}

	/**
	 * monthsヶ月前からinWeeks週間に登録されたLearning Logを検索
	 * 
	 * @param months
	 * @param inWeeks
	 *            Nullable
	 * @param userId
	 *            Nullable
	 * @param size
	 *            Default 20
	 * @return
	 */
	public List<Item> findAllItemsBeforeMonths(int months, Integer inWeeks,
			String userId, Integer size) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);

		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		Date end = cal.getTime();
		
		cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR)
				- inWeeks);
		Date start = cal.getTime();
		
//		if (inWeeks != null) {
//			cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR)
//					- inWeeks);
//			Date start = cal.getTime();
//			criteria.add(Restrictions.and(Restrictions.ge("createTime", end),
//					Restrictions.le("createTime", start)));
//		} else {
//			criteria.add(Restrictions.le("createTime", end));
//		}
//		if (userId != null) {
//			criteria.createAlias("author", "author").add(
//					Restrictions.eq("author.id", userId));
//		}
//
//		criteria.add(Restrictions.ne("disabled", new Integer(1)));

		if (size == null || size == 0)
			size = 20;
//		Page<Item> itemPage = new Page<Item>(size);
//		itemPage.setOrder(Page.DESC);
//		itemPage.setOrderBy("createTime");
//		Page<Item> result = itemDao.findPage(itemPage, criteria);
		return itemDao.findAllItemsBeforeMonths(inWeeks, start, end, userId, new PageRequest(0, size, new Sort(Sort.Direction.DESC, "create_time")));
	}

	@SuppressWarnings("unused")
	public List<Item> findItemByLocation(Double lat, Double lng,
			Boolean isMyItem) throws Exception {
		if (lat == null || lng == null) {
			return new ArrayList<Item>();
		}

		Users user = usersDao.findById(SecurityUserHolder.getCurrentUser()
				.getId());

		Double x1, y1, x2, y2, xt, yt;
		Double m = 360 / 39940.638;
		x1 = lat + m;
		x2 = lat - m;
		if (x1 < -90 || x1 > 90) {
			x1 = 180 * x1 / Math.abs(x1) - x1;
		}
		if (x2 < -90 || x2 > 90) {
			x2 *= 180 * x2 / Math.abs(x2) - x2;
		}
		if (x1 < x2) {
			xt = x1;
			x1 = x2;
			x2 = xt;
		}
		Double clat = 360 / (2 * Math.PI * Math.cos(lat) * 40075.004);
		if (clat < 0) {
			clat *= -1;
		}

		y1 = lng - clat;
		y2 = lng + clat;
		if (y1 < -180 || y1 > 180) {
			y1 = (360 * (y1 / Math.abs(y1)) - y1) * (-1);
		}
		if (y2 < -180 || y2 > 180) {
			y2 = (360 * y2 / Math.abs(y2) - y2) * (-1);
		}
		if (y1 < y2) {
			yt = y1;
			y1 = y2;
			y2 = yt;
		}

		String sql = "";
		
		List<Category> categoryList = categoryDao.findListByUserId(user.getId());
		List<String> categoryIds = new ArrayList<String>();
		if (Constants.usingCategory && !categoryList.isEmpty()){
			for(Category category: categoryList){
				categoryIds.add(category.getId());
			}
		}

		if (Boolean.TRUE.equals(isMyItem)) {
//			sql = "select i.* from t_llquiz lq, t_myquiz mq, t_item i, "
//					+ "( select l.item_id, max(m.update_date) as update_date "
//					+ "from t_myquiz m, t_llquiz l " + "where m.author_id ='"+ user.getId()+ "' "
//					+ "and m.llquiz_id = l.id group by l.item_id ) y "
//					+ "where mq.update_date = y.update_date and mq.author_id ='"+ user.getId()+ "' "
//					+ "and mq.llquiz_id = lq.id and lq.item_id = y.item_id "
//					+ "and mq.answerstate !=1 and mq.answerstate !=2 and i.id = lq.item_id "
//					+ "and i.item_lat<='"+ x1
//					+ "' and i.item_lat>='"+ x2+ "' "
//					+ "and i.item_lng<='"+ y1
//					+ "' and i.item_lng>='"+ y2
//					+ "' and i.author_id = '"+ user.getId()+ "' "
//					+ "and i.id not in ( "
//					+ "select inm.items "
//					+ "from t_itemnotify itn, t_itemnotify_items inm "
//					+ "where itn.author_id = '"+ user.getId()+ "' "
//					+ "and itn.id = inm.t_itemnotify  "
//					+ "and itn.feedback = 1 )";
			return itemDao.findListByLocationForMyItem(user.getId(), x1, x2, y1, y2, categoryIds);
		} else {
//			sql = "select i.* from t_llquiz lq, t_myquiz mq, t_item i, "
//					+ "( select l.item_id, max(m.update_date) as update_date "
//					+ "from t_myquiz m, t_llquiz l " + "where m.author_id ='"+ user.getId()+ "' "
//					+ "and m.llquiz_id = l.id group by l.item_id )y "
//					+ "where mq.update_date = y.update_date and mq.author_id ='"+ user.getId()+ "' "
//					+ "and mq.llquiz_id = lq.id and lq.item_id = y.item_id "
//					+ "and mq.answerstate !=1 and mq.answerstate !=2 and i.id = lq.item_id "
//					+ "and i.item_lat<='"+ x1+ "' and i.item_lat>='"+ x2+ "' "
//					+ "and i.item_lng<='"+ y1
//					+ "' and i.item_lng>='"+ y2
//					+ "' and i.author_id != '"+ user.getId()+ "' "
//					+ "and i.id not in ( "
//					+ "select inm.items "
//					+ "from t_itemnotify itn, t_itemnotify_items inm "
//					+ "where itn.author_id = '"+ user.getId()+ "' "
//					+ "and itn.id = inm.t_itemnotify  "
//					+ "and itn.feedback = 1 )";
			return itemDao.findListByLocationNotForMyItem(user.getId(), x1, x2, y1, y2, categoryIds);
		}

//		if (Constants.usingCategory && user.getMyCategoryList() != null
//				&& user.getMyCategoryList().size() > 0) {
//			String categoryids = "";
//			for (Category c : user.getMyCategoryList()) {
//				categoryids += "'" + c.getId() + "',";
//			}
//			if (categoryids.length() > 0)
//				categoryids = categoryids
//						.substring(0, categoryids.length() - 1);
//			sql = sql + " and i.category in (" + categoryids + ") ";
//		}
//
//		Session session = this.sessionFactory.getCurrentSession();
//		this.itemDao.findListNearestItems(userId, lng, lat, distance, pageRequest);
//		return session.createSQLQuery(sql).addEntity(Item.class).list();
	}

	public List<Item> searchMyitemsForTimemap(String id) {
//		String hql = "from Item item where item.author.id = ? and item.disabled!=1 and item.itemLat is not null and item.itemLng is not null";
//		List<Item> itemList = itemDao.find(hql, id);
//		return itemList;
		return itemDao.findListForMyTimemap(id);
	}

	public List<Item> searchItemsByMD5(String md5) {
//		String hql = "from Item item where item.image.md5=? and item.disabled!=1";
		return itemDao.findListByMD5(md5);
	}

	public List<Item> findItemListByFiledataId(String filedataId) {
//		String hql = "from Item item where item.image.id=?";
		return itemDao.findListByImage(filedataId);
	}

	// ■wakebe 自分の回答数取得
	public Integer getAnswerCount(String id) {
//		try {
//			Session session = this.sessionFactory.getCurrentSession();
//			String sql = "select count(author_id) as cnt from t_answer where author_id = :id group by author_id";
//			Integer result = ((BigInteger) session.createSQLQuery(sql)
//					.setParameter("id", id).uniqueResult()).intValue();

			// 経験値 = 質問の回答数 * 20
		Long result = this.answerDao.countByUserId(id);
		return Long.valueOf(result * 20).intValue();
//		} catch (Exception e) {
//			logger.error("Error in GetAnswerCount", e);
//			return 0;
//		}
	}

	// ■wakebe 自分の全アイテムのリード数取得
	public Long findReadAllCount(String id) {
//		String hql = "select count(*) from LogUserReadItem log where log.user.id=?";
		Long count = this.logUserReadItemDao.countByUserId(id);
		return count;
	}

	public Long findRelogAllCount(String id) {
//		String hql = "select count(*) from Item item where item.relogItem.id=? and item.disabled!=1";
		Long count = itemDao.countReloggedTimes(id);
		return count;
	}
	
	public boolean itemRealted(Item item1, Item item2){
		List<ItemTitle> titles1 = itemTitleDao.findListByItem(item1.getId());
		List<ItemTitle> titles2 = itemTitleDao.findListByItem(item2.getId());
		for(ItemTitle it1 :titles1){
			for(ItemTitle it2:titles2){
				if(it1.getLanguage().equals(it2.getLanguage())&&it1.getContent().equals(it2.getContent()))
					return true;
			}
		}
		return false;
	}
	
	public boolean inItemList(Item item, List<Item>items){
		for(Item i:items){
			if(itemRealted(i, item))
				return true;
		}
		return false;
	}
	
	
	public String getItemNote(Item item){
		String note = "";
		List<ItemTitle> list = itemTitleDao.findListByItem(item.getId());
		for(ItemTitle t:list){
			Language lang = languageDao.findById(t.getLanguage());
			note+=lang.getName()+":"+t.getContent()+"  ";
		}
		return note;
	}

	public ItemForm convertItemToItemForm(Item item) {
		ItemForm form = new ItemForm();
    	form.setItemId(item.getId());
    	if(item.getBarcode()!=null)
    		form.setBarcode(item.getBarcode());
    	form.setItemLat(item.getItemLat());
    	form.setItemLng(item.getItemLng());
    	Users author = usersDao.findById(item.getAuthorId());
    	if(item.getImage()!=null){
    		FileData fileData = fileDataDao.findById(item.getImage());
    		form.setFile_type(fileData.getFileType());
    		form.setPhotourl(fileData.getId());
    	}	
    	if(author!=null){
    		form.setUserid(author.getId());
    		form.setNickname(author.getNickname());
    	}
    	form.setPlace(item.getPlace());
    	form.setQrcode(item.getQrcode());
    	form.setRfid(item.getRfid());
    	form.setUpdatetime(item.getUpdateTime().getTime());
    	
    	List<ItemTag> itemtags = itemTagDao.findByItemId(item.getId());
    	Map<String, String> tags = new LinkedHashMap<String, String>();
    	for(ItemTag tag:itemtags){
    		tags.put(tag.getId(), tag.getTag());
    	}
    	form.setTags(tags);
    	
     	List<ItemComment> itemcomments = itemCommentDao.findListByItemId(item.getId());
    	for(ItemComment comment:itemcomments){
    		Users user = usersDao.findById(comment.getUserId());
    		if(comment!=null&&user!=null&&user.getNickname()!=null)
    			form.getComments().put(comment.getComment(), user.getNickname());
    	}
    	List<ItemTitle> itemTitles = itemTitleDao.findListByItem(item.getId());
    	for(ItemTitle title:itemTitles){
    		Language lang = languageDao.findById(title.getLanguage());
    		form.getTitles().put(lang.getId(), title.getContent());
    	}
    	if(item.getNote()!=null)
    		form.setNote(item.getNote());
    	if(item.getCategory()!=null)
    		form.setCategory(item.getCategory());
    	if(item.getDisabled()!=null)
    		form.setDisabled(item.getDisabled());
    	
    	if(item.getQuestionId()!=null){
    		Question ques = questionDao.findById(item.getQuestionId());
    		form.setQuestionId(ques.getId());
    		form.setQuestion(ques.getContent());
    		if(ques.getLanguageId()!=null){
    			Language lang = languageDao.findById(ques.getLanguageId());
    			form.setQuesLanCode(lang.getCode());
    			List<Answer> answers = this.answerDao.findListByQuestionId(ques.getId());
    			if(!answers.isEmpty()){
    				List<QuestionAnswerForm> answerForms  = new ArrayList<QuestionAnswerForm>();
    				for(Answer answer:answers){
    					QuestionAnswerForm questionAnswerForm = new QuestionAnswerForm();
    					questionAnswerForm.setQuestion_id(answer.getQuestionId());
    					questionAnswerForm.setContent(answer.getAnswer());
    					questionAnswerForm.setAnswer_id(answer.getId());
    					questionAnswerForm.setNickname(usersDao.findById(answer.getAuthorId()).getNickname());
    					questionAnswerForm.setUpdatetime(answer.getCreateDate().getTime());
    					questionAnswerForm.setAuthor_id(answer.getAuthorId());
    					answerForms.add(questionAnswerForm);
    				}
    				form.setAnswers(answerForms);
    			}
    		}
    	}
		return form;
	}
	
	public ItemEditForm convertItemToItemEditForm(Item item) {
		ItemEditForm form = new ItemEditForm();
		form.setItemId(item.getId());
		form.setBarcode(item.getBarcode());
		form.setQrcode(item.getQrcode());
		form.setRfid(item.getRfid());
		form.setNote(item.getNote());
		form.setPlace(item.getPlace());
		form.setItemLat(item.getItemLat());
		form.setItemLng(item.getItemLng());
		form.setSpeed(item.getSpeed());
		form.setItemZoom(item.getItemZoom());
		List<ItemTitle> titles = this.itemTitleDao.findListByItem(item.getId());
        HashMap<String, String> titleMap = new HashMap<String, String>();
        for(ItemTitle title: titles){
        	Language language = languageDao.findById(title.getLanguage());
        	titleMap.put(language.getCode(), title.getContent());
        }
        form.setTitleMap(titleMap);
        
        
        if (item.getQuestionId() != null) {
        	Question question = questionDao.findById(item.getQuestionId());
            form.setQuestion(question.getContent());
            if (question.getLanguageId() != null) {
            	Language lang = languageDao.findById(question.getLanguageId());
                form.setQuesLan(lang.getCode());
            }
        }

        form.setLocationBased(item.getLocationbased());
        List<ItemQuestionType>quesTypes= itemQuestionTypeDao.findListByItem(item.getId());
        List<Long> qtids = new ArrayList<Long>();
        for(ItemQuestionType qt:quesTypes){
        	qtids.add(qt.getQuestiontypeId());
        }
        form.setQuestionTypeIds(qtids);
		return form;
	}

	public List<ItemTitle> findTitlesByItem(String itemId) {
		return this.itemTitleDao.findListByItem(itemId); 
	}

	public ItemDTO findDTOById(String id) {
		return this.itemDao.findDTOById(id);
	}

	public Item findByIdIgnoreDisableFlg(String id) {
		return itemDao.findByIdIgnoreDisableFlg(id);
	}

	public List<ItemTitle> findContentAndItemId(String id) {
		return this.itemTitleDao.findContentItem(id);
	}

	public List<Item> findItemListHasImage() {
		return this.itemDao.findItemListHasImage();
	}
	
}
