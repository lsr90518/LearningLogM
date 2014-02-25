package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.tokushima_u.is.ll.dao.StatusDao;
import jp.ac.tokushima_u.is.ll.entity.ChartObj;
import jp.ac.tokushima_u.is.ll.entity.StatusWord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("StatisticService")
public class StatisticService {
	@Autowired
	public StatusDao sd;
	
	public List<StatusWord> getJPUserwordlistByAuthorId(String id){
		return this.sd.getUserJPWordlistByAuthorId(id);
	}
	
	public List<String> getUserJPCreatetimeByAuthorId(String id){
		return this.sd.getUserJPCreatetimeByAuthorId(id);
	}
	
	public List<String> getUserJPCreatetimeByAuthorIdDate(Map<String, Object> param){
		return this.sd.getUserJPCreatetimeByAuthorIdDate(param);
	}
	
	public List<StatusWord> getUserJPWordlistByAuthorIdDate(Map<String,Object> param){
		return this.sd.getUserJPWordlistByAuthorIdDate(param);
	}
	
	public List<ChartObj> distinguishLevelByList(List<StatusWord> wordlist){
		ArrayList<Integer> levellist = new ArrayList<Integer>();
		List<ChartObj> chartlist = new ArrayList<ChartObj>();
		
		int j = 0;
		for(int i = 0;i < wordlist.size();i++){
			levellist.add(wordlist.get(i).getLevel());
		}
		int current = -1;
		int firstFlag = 0;
		for(int i = 1;i<levellist.size();i++){
			if(levellist.get(i).equals(current)){
			} else {
				if(firstFlag == 0){
					current = levellist.get(i);
					firstFlag = 1;
					continue;
				}
				ChartObj temp = new ChartObj(current + "",Collections.frequency(levellist, current));
				chartlist.add(temp);
				current = levellist.get(i);
			}
		}
		ChartObj temp = new ChartObj(current + "",Collections.frequency(levellist, current));
		chartlist.add(temp);
		
		return chartlist;
	}

	public List<ChartObj> countMonthTimes(List<String> leveltime) {
		List<ChartObj> lineList = new ArrayList<ChartObj>();
		List<Integer> monList = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
		
		//put them into set
		for(int i = 0;i<leveltime.size();i++){
			String[] temp = leveltime.get(i).split("-");
			monList.add(Integer.valueOf(temp[1]));
			set.add(Integer.valueOf(temp[1]));
		}
		for (int mon : set) {
			ChartObj temp = new ChartObj(mon + "",Collections.frequency(monList, mon));
			lineList.add(temp);
		}
		return lineList;
	}
}
