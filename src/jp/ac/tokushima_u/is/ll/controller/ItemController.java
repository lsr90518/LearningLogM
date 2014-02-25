package jp.ac.tokushima_u.is.ll.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.dto.AnswerDTO;
import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.entity.Answer;
import jp.ac.tokushima_u.is.ll.entity.C2DMessage;
import jp.ac.tokushima_u.is.ll.entity.Category;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Question;
import jp.ac.tokushima_u.is.ll.entity.QuestionType;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ItemCommentForm;
import jp.ac.tokushima_u.is.ll.form.ItemEditForm;
import jp.ac.tokushima_u.is.ll.form.ItemSearchCondForm;
import jp.ac.tokushima_u.is.ll.form.validator.ItemEditFormValidator;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.AnswerService;
import jp.ac.tokushima_u.is.ll.service.C2DMessageService;
import jp.ac.tokushima_u.is.ll.service.CategoryService;
import jp.ac.tokushima_u.is.ll.service.FileDataService;
import jp.ac.tokushima_u.is.ll.service.ItemRatingService;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.LanguageService;
import jp.ac.tokushima_u.is.ll.service.LogService;
import jp.ac.tokushima_u.is.ll.service.PropertyService;
import jp.ac.tokushima_u.is.ll.service.QuestionService;
import jp.ac.tokushima_u.is.ll.service.UserService;
import jp.ac.tokushima_u.is.ll.service.visualization.ReviewHistoryService;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.SerializeUtil;
import jp.ac.tokushima_u.is.ll.util.TagCloudConverter;
import jp.ac.tokushima_u.is.ll.ws.service.model.ItemForm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

@Controller
@RequestMapping("/item")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private ItemService itemService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private FileDataService fileDataService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private LogService logService;
    @Autowired
    private ItemRatingService itemRatingService;
    @Autowired
    private UserService userService;
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private ReviewHistoryService reviewHistoryService;
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private C2DMessageService c2dmMessageService;
	
    @ModelAttribute("langList")
    public List<Language> populateLanguageList() {
    	List<Language> langList = languageService.searchAllLanguage();
        return langList;
    }

    @ModelAttribute("systemUrl")
    public String systemUrl(){
    	return propertyService.getSystemUrl();
    }

    @ModelAttribute("questionTypes")
    public List<QuestionType> searchQuestionTypes(){
    	return itemService.searchAllQuestionTypes();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping
    public String index(@ModelAttribute("searchCond") ItemSearchCondForm form, ModelMap model) {
    	Page<ItemDTO> itemPage = itemService.searchItemPageByCond(form);
        model.addAttribute("itemPage", itemPage);
        List<Map<String, Object>> positions = new ArrayList<Map<String, Object>>();
        for(Item item: itemPage.getContent()){
        	Map<String, Object> m = new HashMap<String, Object>();
        	if(item.getItemLat()==null || item.getItemLng()==null || (item.getItemLat()==0 && item.getItemLng()==0))continue;
        	m.put("id", item.getId());
        	m.put("lat", item.getItemLat());
        	m.put("lng", item.getItemLng());
        	positions.add(m);
        }
        model.addAttribute("positions", new Gson().toJson(positions));
        addTagCloud(model);
        return "item/list";
    }

    private void addTagCloud(ModelMap model) {
    	Map<String, Integer> tagCloud = TagCloudConverter.convert(itemService.findTagCloud());
    	model.addAttribute("tagCloud", tagCloud);
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
    public String test(@ModelAttribute("searchCond") ItemSearchCondForm form, ModelMap model, HttpServletRequest request) {
		model.clear();
    	Page<ItemDTO> page = itemService.searchItemPageByCond(form);
    	if(page!=null&&page.getContent()!=null&&page.getContent().size()>0)
    	{
    		List<ItemDTO>results = page.getContent();
    		List<ItemForm>forms = new ArrayList<ItemForm>();
    		for(Item item:results){
    			ItemForm itemform = itemService.convertItemToItemForm(item);
    			forms.add(itemform);
    		}
    		model.addAttribute("items", forms);
    	}
        return "item/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable String id, ModelMap model) {
        ItemDTO item = itemService.findDTOById(id);
        Users user = SecurityUserHolder.getCurrentUser();

        if (item == null || (item.getShareLevel() == Item.SHARE_LEVEL_PRIVATE && !item.getAuthorId().equals(user.getId()))) {
            return "redirect:/item";
        }
        model.addAttribute("item", item);

        //FileType{video, image}
        FileData fileData = fileDataService.findById(item.getImage());
        String fileType = "";
        if(fileData!=null && !StringUtils.isBlank(fileData.getOrigName())){
        	fileType = FilenameUtil.checkMediaType(fileData.getOrigName());
        }
        model.addAttribute("fileType", fileType);

        boolean ratingExist = itemRatingService.ratingExist(id);
        model.addAttribute("ratingExist", ratingExist);
        if (ratingExist) {
            model.put("votes", itemRatingService.countVotesNumber(item.getId()));
            model.put("avg", itemRatingService.avgRating(item.getId()));
        }
        model.addAttribute("relogable", itemService.isRelogable(item.getId(), user.getId()));

        //Category Path
        if(item.getCategory()!=null){
        	Category category = categoryService.findById(item.getCategory());
        	List<Category> rvCatList = new ArrayList<Category>();
        	Category node = category;
        	while(node!=null){
        		rvCatList.add(node);
        		node = categoryService.findById(node.getParent());
        	}
        	List<Category> catList = new ArrayList<Category>();
        	for(int i = rvCatList.size()-1;i>=0;i--){
        		catList.add(rvCatList.get(i));
        	}
        	model.addAttribute("categoryPath", catList);
        }
        logService.logUserReadItem(item.getId(), user.getId(), null, null,null);
        model.addAttribute("readCount", itemService.findReadCount(item.getId()));
        model.addAttribute("relogCount", itemService.findRelogCount(item.getId()));
        
        //Select Question
        if(item.getQuestionId()!=null){
        	Question question = questionService.findById(item.getQuestionId());
        	model.addAttribute("question", question);
        	List<Answer> answerList = answerService.findByQuestionId(question.getId());
        	List<AnswerDTO> answerDTOList = new ArrayList<AnswerDTO>();
        	for(Answer answer:answerList){
        		AnswerDTO dto = new AnswerDTO();
        		dto.setAnswer(answer.getAnswer());
        		dto.setAuthor(userService.findById(answer.getAuthorId()));
        		dto.setAuthorId(answer.getAuthorId());
        		dto.setCreateDate(answer.getCreateDate());
        		dto.setId(answer.getId());
        		dto.setQuestionId(answer.getQuestionId());
        		answerDTOList.add(dto);
        	}
        	model.addAttribute("answers", answerDTOList);
        }
        
//        model.addAttribute("itemState", reviewHistoryService.searchUserItemState(user, item.getId()));
        model.addAttribute("itemState", reviewHistoryService.searchUserItemState(user.getId(), item.getId()));
        return "item/show";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(@ModelAttribute("item") ItemEditForm form, ModelMap model) {
    	Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
    	model.addAttribute("user", user);

    	List<Language> langs = new ArrayList<Language>();
    	for(Language lang: languageService.findMyLangs(user.getId())){
    		if(!langs.contains(lang))langs.add(lang);
    	}
    	for(Language lang: languageService.findStudyLangs(user.getId())){
    		if(!langs.contains(lang))langs.add(lang);
    	}
    	Language english = languageService.findLangByCode("en");
    	if(!langs.contains(english))langs.add(english);

    	
    	model.addAttribute("langs", langs);
    	form.setCategoryId(user.getDefaultCategory());
    	
    	model.addAttribute("myCategoryList", categoryService.findListByUserId(user.getId()));

        return "item/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@ModelAttribute("item") ItemEditForm form, BindingResult result, ModelMap model) {
        new ItemEditFormValidator().validate(form, result);
        if (result.hasErrors()) {
        	return this.add(form, model);
        }
        try {
            Item item = this.itemService.createByForm(form);

            Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());

        	HashMap<String,String[]>params = new HashMap<String,String[]>();
    		C2DMessage c2dmessage = new C2DMessage();
    		c2dmessage.setCollapse(Constants.COLLAPSE_KEY_SYNC);
    		c2dmessage.setIsDelayIdle(new Integer(0));
    		try{
    			c2dmessage.setParams(SerializeUtil.serialize(params));
    		}catch(Exception e){

    		}
    		this.c2dmMessageService.addMessage(c2dmessage,user);


    		// ■wakebe ULLO登録による経験値取得 (値要修正)
            userService.addExperiencePoint(user.getId(), 10);


            return "redirect:/item/" + item.getId() + "/related?fromcreated=true";
        } catch (IOException ex) {
            logger.error("Error occurred when create item", ex);
        }

        return "redirect:/item";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable String id, @ModelAttribute("form") ItemEditForm form, ModelMap model) {
        Item item = itemService.findByIdIgnoreDisableFlg(id);
        if (item == null || (!SecurityUserHolder.getCurrentUser().getId().equals(item.getAuthorId()) && !SecurityUserHolder.getCurrentUser().getAuth().equals(Users.USERS_AUTH_ADMIN))) {
            return "redirect:/item";
        }
        form = itemService.convertItemToItemEditForm(item);

    	List<ItemTitle> titles = itemService.findTitlesByItem(item.getId());
    	HashMap<String, String> titleMap = new HashMap<String, String>();
    	for(ItemTitle title: titles){
    		Language lang = languageService.findById(title.getLanguage());
    		titleMap.put(lang.getCode(), title.getContent());
    	}
    	form.setTitleMap(titleMap);

        model.addAttribute("item", item);
        model.addAttribute("form", form);

        //FileType{video, image}
        FileData fileData = fileDataService.findById(item.getImage());
        String fileType = "";
        if(fileData!=null && !StringUtils.isBlank(fileData.getOrigName())){
        	fileType = FilenameUtil.checkMediaType(fileData.getOrigName());
        }
        model.addAttribute("fileType", fileType);

    	Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
    	model.addAttribute("user", user);

       	List<Language> langs = new ArrayList<Language>();
    	for(ItemTitle t:itemService.findTitlesByItem(item.getId())){
    		if(!langs.contains(t))langs.add(languageService.findById(t.getLanguage()));
    	}
    	for(Language lang: languageService.findMyLangs(user.getId())){
    		if(!langs.contains(lang))langs.add(lang);
    	}
    	for(Language lang: languageService.findStudyLangs(user.getId())){
    		if(!langs.contains(lang))langs.add(lang);
    	}
    	Language english = languageService.findLangByCode("en");
    	if(!langs.contains(english))langs.add(english);
    	model.addAttribute("langs", langs);
    	
    	model.addAttribute("langs", langs);
    	model.addAttribute("myCategoryList", categoryService.findListByUserId(user.getId()));

    	if(item.getCategory()!=null) form.setCategoryId(item.getCategory());
        return "item/edit";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable String id, @ModelAttribute("form") ItemEditForm form, BindingResult result, ModelMap model) {
        Item item = itemService.findByIdIgnoreDisableFlg(id);
        if (item == null || (!SecurityUserHolder.getCurrentUser().getId().equals(item.getAuthorId()) && !SecurityUserHolder.getCurrentUser().getAuth().equals(Users.USERS_AUTH_ADMIN))) {
            return "redirect:/item";
        }
        if (item.getImage() != null) {
            form.setFileExist(true);
        }
        new ItemEditFormValidator().validate(form, result);
        if (result.hasErrors()) {
            return "item/" + id + "/edit";
        }
        try {
            this.itemService.updateByForm(id, form);

            Users user = userService.findById(SecurityUserHolder
    				.getCurrentUser().getId());

        	HashMap<String,String[]>params = new HashMap<String,String[]>();
    		C2DMessage c2dmessage = new C2DMessage();
    		c2dmessage.setCollapse(Constants.COLLAPSE_KEY_SYNC);
    		c2dmessage.setIsDelayIdle(new Integer(0));
    		try{
    			c2dmessage.setParams(SerializeUtil.serialize(params));
    		}catch(Exception e){

    		}
    		this.c2dmMessageService.addMessage(c2dmessage,user);

        } catch (IOException ex) {
            logger.error("Error occurred when create item", ex);
        }
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String delete(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null || (!SecurityUserHolder.getCurrentUser().getId().equals(item.getAuthorId()) && !SecurityUserHolder.getCurrentUser().getAuth().equals(Users.USERS_AUTH_ADMIN))) {
            return "redirect:/item";
        }
        this.itemService.delete(id);
        return "redirect:/item";
    }

    @RequestMapping(value = "/{id}/related", method = RequestMethod.GET)
    public String related(@PathVariable String id, String fromcreated, ModelMap model) {
        ItemDTO item = itemService.findDTOById(id);
        if (item == null) {
            return "redirect:/item";
        }
        model.addAttribute("item", item);
        model.addAttribute("relatedItemList", itemService.searchRelatedItemList(id));
        model.addAttribute("latestItems", itemService.searchLatestItems(item.getId(), null, 5));
        if(item.getItemLat()!=null && item.getItemLng()!=null){
        	model.addAttribute("nearestItems", itemService.searchNearestItems(item.getId(), item.getItemLat(), item.getItemLng(), 1, null, 5));
        }
        if(!StringUtils.isBlank(fromcreated)){
        	model.addAttribute("fromcreated", true);
        }else{
        	model.addAttribute("fromcreated", false);
        }

        //FileType{video, image}
        FileData fileData = fileDataService.findById(item.getImage());
        String fileType = "";
        if(fileData!=null && !StringUtils.isBlank(fileData.getOrigName())){
        	fileType = FilenameUtil.checkMediaType(fileData.getOrigName());
        }
        model.addAttribute("fileType", fileType);
        return "item/related";
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.POST)
    public String submitComment(@PathVariable String id, @ModelAttribute ItemCommentForm form) {
        this.itemService.createCommentByForm(id, form);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/{id}/question", method = RequestMethod.POST)
    public String submitQuestion(@PathVariable String id, HttpServletRequest request) {
        String content = request.getParameter("content");
        this.itemService.createAnswerByForm(id, content);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/moretoanswer", method = RequestMethod.GET)
    public String toAnswerMore(@ModelAttribute("searchCond") ItemSearchCondForm form, ModelMap model) {
        model.addAttribute("itemList", itemService.searchAllToAnswer());
        addTagCloud(model);
        return "item/list";
    }

    @RequestMapping(value = "/{id}/relog", method = RequestMethod.POST)
    public String relog(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return "redirect:/item/" + id;
        }
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        if (user == null) {
            return "redirect:/item/" + id;
        }
        Item reItem = this.itemService.relog(item.getId(), user.getId());
        return "redirect:/item/" + reItem.getId();
    }

    @RequestMapping(value = "/{id}/questionconfirm", method = RequestMethod.POST)
    public String questionConfirm(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return "redirect:/item/" + id;
        }
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        if (user == null) {
            return "redirect:/item/" + id;
        }
        
        if (!item.getAuthorId().equals(user.getId()) || item.getQuestionId() == null || this.answerService.findByQuestionId(item.getQuestionId()).isEmpty()) {
            return "redirect:/item/" + id;
        }
        itemService.questionConfirm(item, user);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/{id}/teacherconfirm", method = RequestMethod.POST)
    public String teacherConfirm(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return "redirect:/item/" + id;
        }
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
//        if (user == null || user.getAuth() != Users.UsersAuth.ADMIN) {
//            return "redirect:/item/" + id;
//        }
        itemService.teacherConfirm(item, user);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/{id}/teacherreject", method = RequestMethod.POST)
    public String teacherReject(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return "redirect:/item/" + id;
        }
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        itemService.teacherReject(item, user);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value = "/{id}/teacherdelcfmstatus", method = RequestMethod.POST)
    public String teacherDeleteStatus(@PathVariable String id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return "redirect:/item/" + id;
        }
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        itemService.teacherDeleteStatus(item, user);
        return "redirect:/item/" + id;
    }

    @RequestMapping(value="/view", method=RequestMethod.GET)
    public String view(String id, Double latitude,Double longitude, Float speed, ModelMap model){
    	Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
    	model.clear();
    	Item item = null;
    	if(id!=null)
    		item = this.itemService.findById(id);
		if(item!=null&&item.getId()!=null){
			this.logService.logUserReadItem(item.getId(), user.getId(), latitude, longitude, speed);
		}
    	return null;
    }
}
