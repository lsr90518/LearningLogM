package jp.ac.tokushima_u.is.ll.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.wordnet.SynsetDef;
import jp.ac.tokushima_u.is.ll.entity.wordnet.Word;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ItemRelationService;
import jp.ac.tokushima_u.is.ll.service.LanguageService;
import jp.ac.tokushima_u.is.ll.service.PropertyService;
import jp.ac.tokushima_u.is.ll.service.WordNetService;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
@RequestMapping("/wordnet")
public class WordNetController {

    private static final Logger logger = LoggerFactory.getLogger(WordNetController.class);
    @Autowired
    private LanguageService languageService;
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private WordNetService wordnetService;
	@Autowired
	private ItemRelationService itemRelationService;
	
    @ModelAttribute("langList")
    public List<Language> populateLanguageList() {
    	List<Language> langList = languageService.searchAllLanguage();
        return langList;
    }

    @ModelAttribute("systemUrl")
    public String systemUrl(){
    	return propertyService.getSystemUrl();
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    /**
     * word_show
     * WordNetでWordと関連するSynsetの一覧表示
     * 
     */
    @RequestMapping(value = "/{word}", method = RequestMethod.GET)
    public String word_show(@PathVariable String word, ModelMap model) {
    	word = word.replace(" ", "_");
		// set word
        model.addAttribute("Word", StringEscapeUtils.escapeHtml4(word));
        
        // create synset list
		Map<String, Map<String,String>> dataset = new LinkedHashMap<String, Map<String,String>>();
        List<SynsetDef> SynsetList = wordnetService.getSynset(word);
        for (SynsetDef define : SynsetList) {
			String synset = define.getSynset();
			String lang = define.getLang();
			String def = define.getDef();

			Map<String,String> map;
			if (dataset.containsKey(synset)) {
				map = dataset.get(synset);
				if (map.get(lang).length() != 0) {
					def = map.get(lang) + "; " + def;
				}
			} else {
				map = new LinkedHashMap<String,String>();
				map.put("eng", "");
				map.put("jpn", "");
			}
			if (lang.equals("eng") || lang.equals("jpn")) {
				map.put(lang, def);
			}
			dataset.put(synset, map);
        }
        model.addAttribute("Synset", dataset);

        
        return "wordnet/word";
    }


    @RequestMapping(value = "/{word}/wordmap", method = RequestMethod.GET)
    public String wordmap(@PathVariable String word, ModelMap model) {
		// set synset
        model.addAttribute("WordEntitle", StringEscapeUtils.escapeHtml4(word));
        
        return "wordnet/wordmap";
    }
    
    
    @RequestMapping(value = "/{word}/wordmap", params="format=json")
	@ResponseBody
	public String wordmapJson(@PathVariable String word, HttpServletRequest request){
    	String userid = SecurityUserHolder.getCurrentUser().getId();
		Map<String, Object> dataset = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> children = new ArrayList<Object>();
		
		word = word.replace(" ", "_");
		
        // Synonym
        List<Map<String, Object>> synonyms = wordnetService.getSynonym(word, userid);
        for (Map<String, Object>synonym : synonyms) {
			String name = ((String)synonym.get("lemma")).replaceAll("_", " ");
			String learned = (String)synonym.get("learned");
	        Map<String, Object> child = new HashMap<String, Object>();
	        child.put("word", name);
 			child.put("type", "syno");
			child.put("flag", learned);
			children.add(child);
        }
		// Hypenym
        List<Map<String, Object>> hypenyms = wordnetService.getSynsetByLink(word, "hype", userid);
		for (Map<String, Object> hypenym: hypenyms) {
			String name = ((String)hypenym.get("name")).replaceAll("_", " ");
			String learned = (String)hypenym.get("learned");
			Map<String, Object> child = new HashMap<String, Object>();
			child.put("word", name);
			child.put("type", "hype");
			child.put("flag", learned);
			children.add(child);
		}
		// Hyponym
        List<Map<String, Object>> hyponyms = wordnetService.getSynsetByLink(word, "hypo", userid);
		for (Map<String, Object> hyponym: hyponyms) {
			String name = ((String)hyponym.get("name")).replaceAll("_", " ");
			String learned = (String)hyponym.get("learned");
	        Map<String, Object> child = new HashMap<String, Object>();
			child.put("word", name);
			child.put("type", "hypo");
			child.put("flag", learned);
			children.add(child);
		}
		data.put("children", children);
		dataset.put(word.replaceAll("_", " "), data);
		Gson gson = new Gson();
		return gson.toJson(dataset);
    }
     

    /**
     * synset_show
     * WordNetのsynset情報を表示
     * 
     */
    @RequestMapping(value = "/synset/{synset}", method = RequestMethod.GET)
    public String synset_show(@PathVariable String synset, ModelMap model) {

		// set synset
        model.addAttribute("Synset", StringEscapeUtils.escapeHtml4(synset));


		// set synonym
		Map<String, String> synonymdata = new LinkedHashMap<String, String>();
		synonymdata.put("eng", "");
		synonymdata.put("jpn", "");
        List<Word> synonymList = wordnetService.getSynonymBySynset(synset);
        for (Word data : synonymList) {
        	String lang = data.getLang();
        	String lemma = data.getLemma().replace("_", " ");
        	
			if (synonymdata.get(lang).length() != 0) {
				lemma = synonymdata.get(lang) + "; " + lemma;
			}
			synonymdata.put(lang, lemma);
        }
        model.addAttribute("Synonym", synonymdata);
        
        
		// set synonym def
		Map<String, String> synonymdef = new LinkedHashMap<String, String>();
		synonymdef.put("eng", "");
		synonymdef.put("jpn", "");
        List<SynsetDef> synsetdefList = wordnetService.getSynsetDef(synset);
        for (SynsetDef data : synsetdefList) {
        	String lang = data.getLang();
        	String def = data.getDef();
        	
			if (synonymdef.get(lang).length() != 0) {
				def = synonymdef.get(lang) + "; " + def;
			}
			synonymdef.put(lang, def);
        }
        model.addAttribute("SynonymDef", synonymdef);
        
        
        // set synlink
		Map<String, Map<String,String>> dataset = new LinkedHashMap<String, Map<String,String>>();
        List<Map<String, Object>> synlinkList = wordnetService.getSynlink(synset);
        for (Map<String, Object> data : synlinkList) {
//			Synlink synlink = (Synlink)data[0];
//			Word worddata = (Word)data[1];

			String link = (String)data.get("link");
			String synset2 = (String)data.get("synset2");
			String lemma = ((String)data.get("lemma")).replace("_", " ");

			Map<String,String> map;
			if (dataset.containsKey(link)) {
				map = dataset.get(link);
				if (map.containsKey(synset2)) {
					//lemma = map.get(synset2) + "; " + lemma;
					lemma = map.get(synset2);
				}
			} else {
				map = new LinkedHashMap<String,String>();
			}
			map.put(synset2, lemma);
			dataset.put(link, map);
        }
        model.addAttribute("Synlink", dataset);
        

        return "wordnet/synset";
    }


    @RequestMapping(value = "/synset/{synset}/wordmap", method = RequestMethod.GET)
    public String wordmap_synset(@PathVariable String synset, ModelMap model) {
		// set synset
        model.addAttribute("Synset", StringEscapeUtils.escapeHtml4(synset));
        
        return "wordnet/wordmap";
    }


	@RequestMapping(value="/synset/{synset}/", params="format=json")
	@ResponseBody
	public String wordmapJson_synset(@PathVariable String word, HttpServletRequest request){
        String userid = SecurityUserHolder.getCurrentUser().getId();
		Map<String, Object> dataset = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> children = new ArrayList<Object>();
        // Synonym
        List<Map<String, Object>> synonyms= wordnetService.getSynonym(word, userid);
        for (Map<String, Object> synonym: synonyms) {
			String name = ((String)synonym.get("lemma")).replaceAll("_", " ");
			String learned = (String)synonym.get("learned");
	        Map<String, Object> child = new HashMap<String, Object>();
			child.put("word", name);
			child.put("type", "syno");
			child.put("flag", learned);
			children.add(child);
        }
		// Hypenym
        List<Map<String, Object>> hypenyms = wordnetService.getSynsetByLink(word, "hype", userid);
		for (Map<String, Object> hypenym: hypenyms) {
			String name = ((String)hypenym.get("name")).replaceAll("_", " ");
			String learned = (String)hypenym.get("learned");
	        Map<String, Object> child = new HashMap<String, Object>();
			child.put("word", name);
			child.put("type", "hype");
			child.put("flag", learned);
			children.add(child);
		}
		// Hyponym
        List<Map<String, Object>> hyponyms = wordnetService.getSynsetByLink(word, "hypo", userid);
		for (Map<String, Object> hyponym: hyponyms) {
			String name = ((String)hyponym.get("name")).replaceAll("_", " ");
			String learned = (String)hyponym.get("learned");
	        Map<String, Object> child = new HashMap<String, Object>();
			child.put("word", name);
			child.put("type", "hypo");
			child.put("flag", learned);
			children.add(child);
		}
		data.put("children", children);
		dataset.put(word.replaceAll("_", " "), data);
		Gson gson = new Gson();
		return gson.toJson(dataset);
	}

    
}
