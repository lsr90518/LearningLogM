package jp.ac.tokushima_u.is.ll.service.quiz;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionTypeDao;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemQuestionType;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.QuestionType;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("convertWorkerService")
@Transactional
public class ConvertWorkerService {

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemQuestionTypeDao itemQuestionTypeDao;
	@Autowired
	private QuestionTypeDao questionTypeDao;
	@Autowired
	private ItemTitleDao itemTitleDao;

	public void convert() {
		List<Item> items = this.itemDao.findListAll();
		QuestionType textchoice = getTextChoiceType();
		QuestionType yeno = getYesNoType();
		QuestionType imagechoice = getImageChoiceType();
		for (Item item : items) {
			if(item.getRelogItem()!=null)
				continue;
			List<ItemTitle> titles = itemTitleDao.findListByItem(item.getId());
			
			for(ItemTitle t:titles){
				ItemQuestionType iqt1 = new ItemQuestionType();
				iqt1.setItemId(item.getId());
				iqt1.setLanguageId(t.getLanguage());
				iqt1.setQuestiontypeId(textchoice.getId());
				iqt1.setId(KeyGenerateUtil.generateIdUUID());
				this.itemQuestionTypeDao.insert(iqt1);
				
				ItemQuestionType iqt2 = new ItemQuestionType();
				iqt2.setItemId(item.getId());
				iqt2.setQuestiontypeId(yeno.getId());
				iqt2.setId(KeyGenerateUtil.generateIdUUID());
				this.itemQuestionTypeDao.insert(iqt2);
				
				if(item.getImage()!=null){
					ItemQuestionType iqt3 = new ItemQuestionType();
					iqt3.setItemId(item.getId());
					iqt3.setLanguageId(t.getLanguage());
					iqt3.setQuestiontypeId(imagechoice.getId());
					iqt3.setId(KeyGenerateUtil.generateIdUUID());
					this.itemQuestionTypeDao.insert(iqt3);
				}
			}
		}
		System.out.println("covert finished");
		
	}
	
	public QuestionType getTextChoiceType(){
		return this.questionTypeDao.findById(Constants.QuizTypeTextMutiChoice);
	}
	
	public QuestionType getImageChoiceType(){
		return this.questionTypeDao.findById(Constants.QuizTypeImageMutiChoice);
	}
	
	public QuestionType getYesNoType(){
		return this.questionTypeDao.findById(Constants.QuizTypeYesNoQuestion);
	}
}
