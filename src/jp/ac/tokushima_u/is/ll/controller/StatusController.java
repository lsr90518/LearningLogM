package jp.ac.tokushima_u.is.ll.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jp.ac.tokushima_u.is.ll.entity.ChartObj;
import jp.ac.tokushima_u.is.ll.entity.StatusWord;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.MyQuizService;
import jp.ac.tokushima_u.is.ll.service.StatisticService;
import jp.ac.tokushima_u.is.ll.service.UserService;
import jp.ac.tokushima_u.is.ll.util.ChartUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

@Controller
@RequestMapping("/status")
public class StatusController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private StatisticService statisticService;

	// ■wakebe クイズ情報取得のため
	@Autowired
	private MyQuizService myQuizService;


	@RequestMapping
	public String index(ModelMap model) {
		Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
		model.addAttribute("user", user);
		model.addAttribute("uploadItemRanking", this.itemService.uploadRanking());
		model.addAttribute("answerRanking", this.itemService.answerRanking());


		// ■wakebe レベルランキング
		List<Users> levelRankingList = this.userService.levelRanking();
		model.addAttribute("levelRanking", levelRankingList);
		List<Map<String, Object>> quizRankingList = this.myQuizService.getQuizScoreRanking();
		model.addAttribute("quizRanking", quizRankingList);


		// ■wakebe クイズ自分の順位
		int myQuizRank = 0;
		for(int i = 0; i < quizRankingList.size(); i++){
			if(user.getId().equals(quizRankingList.get(i).get("id"))){
				myQuizRank = i;
				break;
			}
		}
		int myQuizRankStart = myQuizRank - 2;
		int myQuizRankEnd = myQuizRank + 2;
		if(myQuizRankStart < 0) myQuizRankStart = 0;
		if(myQuizRankEnd < 4) myQuizRankEnd = 4;
		model.addAttribute("myQuizRank", myQuizRank);
		model.addAttribute("myQuizRankStart", myQuizRankStart);
		model.addAttribute("myQuizRankEnd", myQuizRankEnd);


		// ■wakebe レベル自分の順位
		int myLevelRank = 0;
		for (Users levelrank: levelRankingList) {
			if(user.getId().equals(levelrank.getId())){
				break;
			}
			myLevelRank++;
		}
		int myLevelRankStart = myLevelRank - 2;
		int myLevelRankEnd = myLevelRank + 2;
		if(myLevelRankStart < 0) myLevelRankStart = 0;
		if(myLevelRankEnd < 4) myLevelRankEnd = 4;
		model.addAttribute("myLevelRank", myLevelRank);
		model.addAttribute("myLevelRankStart", myLevelRankStart);
		model.addAttribute("myLevelRankEnd", myLevelRankEnd);


		// ■wakebe アップロード数自分の順位
		List<Map<String, Object>>  uploadRankingList = this.itemService.uploadRanking();
		int myUploadRank = 0;
		for(int i = 0; i < uploadRankingList.size(); i++){
			if(user.getId().equals(uploadRankingList.get(i).get("userId"))){
				myUploadRank = i;
				break;
			}
		}
		int myUploadRankStart = myUploadRank - 2;
		int myUploadRankEnd = myUploadRank + 2;
		if(myUploadRankStart < 0) myUploadRankStart = 0;
		if(myUploadRankEnd < 4) myUploadRankEnd = 4;
		model.addAttribute("myUploadRank", myUploadRank);
		model.addAttribute("myUploadRankStart", myUploadRankStart);
		model.addAttribute("myUploadRankEnd", myUploadRankEnd);


		// ■wakebe 回答数自分の順位
		List<Map<String, Object>>  answerRankingList = this.itemService.answerRanking();
		int myAnswerRank = 0;
		for(int i = 0; i < answerRankingList.size(); i++){
			if(user.getId().equals(answerRankingList.get(i).get("userId"))){
				myAnswerRank = i;
				//int answerCount = new Integer((Users)answerRankingList.get(i)[1]);
//				model.addAttribute("answerCount", answerRankingList.get(i)[1]);
				break;
			}
		}
		int myAnswerRankStart = myAnswerRank - 2;
		int myAnswerRankEnd = myAnswerRank + 2;
		if(myAnswerRankStart < 0) myAnswerRankStart = 0;
		if(myAnswerRankEnd < 4) myAnswerRankEnd = 4;
		model.addAttribute("myAnswerRank", myAnswerRank);
		model.addAttribute("myAnswerRankStart", myAnswerRankStart);
		model.addAttribute("myAnswerRankEnd", myAnswerRankEnd);


		// ■wakebe 質問回答数の取得
		model.addAttribute("answerCount", this.itemService.getAnswerCount(user.getId()));

		// ■wakebe クイズ点数の取得
		model.addAttribute("quizInfos", myQuizService.searchOneDayQuiz(new Date(), user));

		// ■wakebe 経験値の加算(仮)
		userService.addExperiencePoint(user.getId(), 0);

		// ■wakebe 次のレベルまでの経験値取得
		model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

		// ■wakebe 現在の合計経験値取得
		model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));


		//model.addAttribute("readCount", this.itemService.findReadAllCount(user.getId()));

		return "status/index";
	}
	
	@RequestMapping(value="/graph")
    public String graphMaker(ModelMap model){
		Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
		model.addAttribute("user", user);
        return "status/graph";
    }
	
	@RequestMapping(value="/getBarData")
	public void getBarData(@RequestParam("id") String id,@RequestParam("startdate") String startdate,@RequestParam("enddate") String enddate,HttpServletResponse response) throws IOException{
		//makeup params
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		param.put("startdate", startdate);
		param.put("enddate", enddate);
		//get word list by date and id
		List<StatusWord> wordlist = statisticService.getUserJPWordlistByAuthorIdDate(param);
		List<ChartObj> barlist = statisticService.distinguishLevelByList(wordlist);
		ChartUtil.wordlist = (ArrayList<StatusWord>) wordlist;
		
		Gson gson = new Gson();
		PrintWriter out=response.getWriter();
		out.write(gson.toJson(barlist));
		
		out.close();
	}
	
	@RequestMapping(value="/getLineData")
	public void getLineData(@RequestParam("id") String id,@RequestParam("startdate") String startdate,@RequestParam("enddate") String enddate, HttpServletResponse response) throws IOException{
		//makeup params
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		param.put("startdate", startdate);
		param.put("enddate", enddate);
		//get word list by date and id
		List<String> leveltime = statisticService.getUserJPCreatetimeByAuthorIdDate(param);
		List<ChartObj> lineList = statisticService.countMonthTimes(leveltime);
		
		Gson gson = new Gson();
		PrintWriter out=response.getWriter();
		out.write(gson.toJson(lineList));
		out.close();
	}
	
	@RequestMapping(value="/getPieData")
	public void getPieData(@RequestParam("id") String id,@RequestParam("startdate") String startdate,@RequestParam("enddate") String enddate, HttpServletResponse response) throws IOException{
		//makeup params
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		param.put("startdate", startdate);
		param.put("enddate", enddate);
		//get word list by date and id
		List<StatusWord> wordlist = statisticService.getUserJPWordlistByAuthorIdDate(param);
		List<ChartObj> pieList = statisticService.distinguishLevelByList(wordlist);
		
		Gson gson = new Gson();
		PrintWriter out=response.getWriter();
		out.write(gson.toJson(pieList));
		
		out.close();
	}
	
	@RequestMapping(value="/getWordlistByLevel")
	public void getWordlistByLevel(@RequestParam("level") String level,HttpServletResponse response) throws IOException{
		ArrayList<StatusWord> templist = new ArrayList<StatusWord>();
		for (int i = 0; i < ChartUtil.wordlist.size(); i++) {
			if(level.equals(ChartUtil.wordlist.get(i).getLevel()+"")){
				templist.add(ChartUtil.wordlist.get(i));
			}
		}
		Gson gson = new Gson();
		PrintWriter out=response.getWriter();
		out.write(gson.toJson(templist));
		
		out.close();
	}
}
