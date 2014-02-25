package jp.ac.tokushima_u.is.ll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.translate.Language;

@Service
public class GoogleTranslateService {
	private static Logger logger = LoggerFactory.getLogger(GoogleTranslateService.class);
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private TranslationService translationService;

	public String translate(String text, Language from, Language to) {
		
		try {
			String result = translationService.translate(from.toString(), to.toString(), text);
			return result;
		} catch (Exception e) {
			logger.error("Google Translate Error", e);
		} 
		return "";
	}

	public String translateByCode(String text, String from, String to) {
		return translate(text, com.google.api.translate.Language.fromString(from), com.google.api.translate.Language.fromString(to));
	}
}
