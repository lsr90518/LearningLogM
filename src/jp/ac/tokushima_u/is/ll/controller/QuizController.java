/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.tokushima_u.is.ll.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.C2DMessage;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.QuizAnswerForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.C2DMService;
import jp.ac.tokushima_u.is.ll.service.C2DMessageService;
import jp.ac.tokushima_u.is.ll.service.LLQuizService;
import jp.ac.tokushima_u.is.ll.service.LanguageService;
import jp.ac.tokushima_u.is.ll.service.MyQuizService;
import jp.ac.tokushima_u.is.ll.service.UserService;
import jp.ac.tokushima_u.is.ll.service.helper.QuizCondition;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizWrapper;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.SerializeUtil;
import jp.ac.tokushima_u.is.ll.ws.service.model.QuizForm;

import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author lemonrain
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {

	@Autowired
	private LLQuizService llquizService;
	@Autowired
	private MyQuizService myquizService;
	@Autowired
	private LanguageService languageService;
	@Autowired
	private UserService userService;

	@Autowired
	private C2DMService c2dmService;

	@Autowired
	private C2DMessageService c2dmMessageService;

	@Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;

	private static String RightAnswerComment = "Very good, your answer is right! You can challenge more!";
	private static String WrongAnswerComment = "Sorry, your answer is not right. The right answer is: ";

	@ModelAttribute("languages")
	public List<Language> getLanguages() {
		List<Language> langList = languageService.findStudyLangs(SecurityUserHolder.getCurrentUser()
				.getId());
		return langList;
	}

	public Map<String,Long> getQuizinfo(){
		Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
		return this.myquizService.searchOneDayQuiz(new Date(), user);
	}

	@RequestMapping
	public String show(@ModelAttribute("form") QuizAnswerForm form,ModelMap model) {
		QuizCondition quizCon = new QuizCondition();
		Users user = userService.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		quizCon.setUserId(user.getId());
//		if (form.getLanguage() != null && form.getLanguage().length() > 0) {
//			quizCon.setLanCode(form.getLanguage());
//		}

		if(form.getAlarmtype()==null)
			quizCon.setAlarmtype(Constants.WebRequestType);
		quizCon.setVersioncode(Constants.AndroidNewVersion);

		MyQuizDTO quiz = llquizService.findQuiz(quizCon);

		if (quiz != null) {
			model.addAttribute("quiz", quiz);
//			form.setLanguage(CourseUtil.convertCodeFromId(quiz.getl.getCourseid()));
		}else{
			System.out.println("quiz is null can not be created");
		}

		model.put("quizinfos", this.getQuizinfo());

		return "quiz/show";
	}


	// ■クイズの結果画面へ異動
	@RequestMapping(method = RequestMethod.POST)
	public String answer(@ModelAttribute("form") QuizAnswerForm form, ModelMap model, HttpServletRequest request) {
		QuizCondition quizCon = new QuizCondition();
		String quizid = form.getQuizid();
		quizCon.setQuizid(form.getQuizid());
		quizCon.setAnswer(form.getAnswer());
		quizCon.setVersioncode(Constants.AndroidNewVersion);
		quizCon.setAlarmtype(form.getAlarmtype());
		Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
		quizCon.setUserId(user.getId());
		try {
//			if(form.getPass()!=null&&Constants.QuizPass.equals(form.getPass())){
			if(form.getPass()!=null){
				quizCon.setPass(form.getPass());
				form.setPass(null);
			}else{
				quizCon.setPass(0);
			}
			MyQuiz myquiz = this.llquizService.checkQuiz(quizCon);
			model.addAttribute("quiz", myquiz);
			boolean answered = (myquiz==null || Constants.NotAnsweredState.equals(myquiz.getAnswerstate()))? Boolean.FALSE:Boolean.TRUE;
			if(answered){
				String comment = "";
				if (Constants.CorrectAnsweredState.equals(myquiz.getAnswerstate())) {
					// クイズに正解
					comment = RightAnswerComment;
					model.addAttribute("faceicon", "smile"+(GeneticAlgorithm.getRandomGenerator().nextInt(5)+1));
					model.addAttribute("result", true);
				} else {
					// クイズに不正解
					comment = WrongAnswerComment + " " + myquiz.getAnswer();
					model.addAttribute("faceicon", "sad"+(GeneticAlgorithm.getRandomGenerator().nextInt(5)+1));
					model.addAttribute("result", false);
				}
				model.addAttribute("youranswer", myquiz.getMyanswer());
				model.addAttribute("rightanswer", myquiz.getAnswer());
				model.addAttribute("comment", comment);
			}
			model.addAttribute("answered", answered);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.put("quizinfos", this.getQuizinfo());
		HashMap<String,String[]>params = new HashMap<String,String[]>();
		params.put("quizId", new String[]{quizid});
		C2DMessage c2dmessage = new C2DMessage();
		c2dmessage.setCollapse(Constants.COLLAPSE_KEY_SYNC);
		c2dmessage.setIsDelayIdle(new Integer(0));
		try{
			c2dmessage.setParams(SerializeUtil.serialize(params));
		}catch(Exception e){

		}
		this.c2dmMessageService.addMessage(c2dmessage,user);

		return "quiz/show";
	}


//	@RequestMapping(method = RequestMethod.POST)
//	public String answer(@ModelAttribute("form") QuizAnswerForm form,
//			ModelMap model, HttpServletRequest request) {
//		QuizCondition quizCon = new QuizCondition();
//		quizCon.setQuizid(form.getQuizid());
//		quizCon.setAnswer(form.getAnswer());
//		quizCon.setVersioncode(Constants.AndroidNewVersion);
//		quizCon.setAlarmtype(form.getAlarmtype());
//		Users user = userService.getById(SecurityUserHolder
//				.getCurrentUser().getId());
//		quizCon.setUser(user);
//		try {
//			if(form.getPass()!=null&&Constants.QuizPass.equals(form.getPass())){
//				quizCon.setPass(Constants.QuizPass);
//				this.llquizService.checkQuiz(quizCon);
//				form.setPass(null);
//				return this.show(form, model);
//			}else{
//				quizCon.setPass(0);
//				MyQuiz myquiz = this.llquizService.checkQuiz(quizCon);
//				model.addAttribute("quiz", myquiz);
//				boolean answered = Constants.NotAnsweredState.equals(myquiz.getAnswerstate())? Boolean.FALSE:Boolean.TRUE;
//				if(answered){
//					String comment = "";
//					if (Constants.CorrectAnsweredState.equals(myquiz.getAnswerstate())) {
//						comment = RightAnswerComment;
//						model.addAttribute("faceicon", "smile"+(RandomUtils.nextInt(5)+1));
//						model.addAttribute("result", true);
//					} else {
//						comment = WrongAnswerComment + " " + myquiz.getAnswer();
//						model.addAttribute("faceicon", "sad"+(RandomUtils.nextInt(5)+1));
//						model.addAttribute("result", false);
//					}
//					model.addAttribute("youranswer", myquiz.getMyanswer());
//					model.addAttribute("rightanswer", myquiz.getAnswer());
//					model.addAttribute("comment", comment);
//				}
//				model.addAttribute("answered", answered);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		model.put("quizinfos", this.getQuizinfo());
//
//		return "quiz/show";
//	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@ModelAttribute("form") QuizCondition quizCon,
			ModelMap model) {
		model.clear();

		Users user = userService.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		quizCon.setUserId(user.getId());
		QuizWrapper quizwrapper = this.llquizService.findQuizWrapper(quizCon);
		if(quizwrapper!=null){
			quizCon.setUserId(null);
			model.addAttribute("quizform", quizwrapper);
		}
		return "quiz/show";
	}

	@RequestMapping(value="/item", method=RequestMethod.POST)
	public String itemRemember(ModelMap model) {
//		Users user = userService.getById(SecurityUserHolder.getCurrentUser()
//				.getId());


		return "quiz/show";
	}


	@RequestMapping(value="/test", method=RequestMethod.GET)
	public @ResponseBody QuizForm test(@ModelAttribute("form") QuizCondition quizcon,
			ModelMap model) {
		QuizForm quizform =  new QuizForm();

		quizform.setContent("asdfaf test");

		return quizform;
	}


	@RequestMapping(value="/check", method=RequestMethod.POST)
	public String check(@ModelAttribute("form") QuizCondition quizcon,
			ModelMap model) {
		Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
		quizcon.setUserId(user.getId());

		QuizWrapper quizwrapper = this.llquizService.checkQuizWrapper(quizcon);

		if (quizwrapper != null) {
			model.clear();
			model.addAttribute("quizform", quizwrapper);
		}
		return "quiz/show";
	}


	//TODO dameng

//	@RequestMapping(value="/check", method=RequestMethod.POST)
//	public String check(@ModelAttribute("form") QuizCondition quizcon,
//			ModelMap model) {
//		Users user = userService.getById(SecurityUserHolder.getCurrentUser()
//				.getId());
//
//		String email = null;
//		if (user.getMobileEmail() != null
//				&& user.getMobileEmail().length() > 0) {
//			email = user.getMobileEmail();
//		} else {
//			email = user.getPcEmail();
//		}
//
//		quizcon.setUser(user);
//
//		MyQuiz myquiz = this.llquizService.checkAnswer(quizcon);
//
//		quizcon.setUser(null);
//		if(Constants.QuizPass.equals(quizcon.getPass()))
//			return "quiz/show";
//
//		QuizForm quizform = null;
//
//		if (myquiz != null) {
//			quizform = new QuizForm();
//			quizform.setEmail(email);
//			if (Constants.rightAnswerComment.equals(myquiz.getAnswerstate())) {
//				quizform.setComment(Constants.rightAnswerComment);
//			} else {
//				quizform.setComment(Constants.wrongAnswerComment);
//			}
//			quizform.setId(myquiz.getId());
//			List<MyQuizChoice> choices = myquiz.getLlquiz().getChoices();
//			if (choices != null && choices.size() >= 4) {
//				if (choices.get(0) != null) {
//					quizform.setChoice1(choices.get(0).getContent());
//					quizform.setNote1(choices.get(0).getNote());
//				}
//				if (choices.get(1) != null) {
//					quizform.setChoice2(choices.get(1).getContent());
//					quizform.setNote2(choices.get(1).getNote());
//				}
//				if (choices.get(2) != null) {
//					quizform.setChoice3(choices.get(2).getContent());
//					quizform.setNote3(choices.get(2).getNote());
//				}
//				if (choices.get(3) != null) {
//					quizform.setChoice4(choices.get(3).getContent());
//					quizform.setNote4(choices.get(3).getNote());
//				}
//			}
//			quizform.setQuiztypeid(myquiz.getLlquiz().getQuiztypeid());
//			quizform.setPhotourl(myquiz.getLlquiz().getPhotoUrl());
//			quizform.setContent(myquiz.getLlquiz().getQuizcontent());
//			quizform.setAnswer(Integer.valueOf(myquiz.getLlquiz().getAnswer()));
//			quizform.setAnswerstate(myquiz.getAnswerstate());
//			model.clear();
//			model.addAttribute("quizform", quizform);
//		}
//		return "quiz/show";
//	}
}
