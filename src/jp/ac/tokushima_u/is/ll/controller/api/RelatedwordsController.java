package jp.ac.tokushima_u.is.ll.controller.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jp.ac.tokushima_u.is.ll.service.YahooApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/api/relatedwords.json")

public class RelatedwordsController {

	@Autowired
	private YahooApi yahooApi;


	public class Relate {
		private String relate;
		public Relate(){
			
		}
		public Relate(String relate){
			this.relate = relate;
		}
		
		@Override
		public String toString(){
			return String.format(relate);
		}
	}
	
	@RequestMapping
	@ResponseBody
	public String execute(String w) {
		List<String> words = yahooApi.searchWikipediaSummary(w);
		String relate = words.get(0);
		System.out.println(relate);
		Gson gson = new Gson();
				
		ArrayList<Relate> relatedlist = new ArrayList<Relate>();
		Scanner scan = new Scanner(relate);
		scan.useDelimiter("\\s*,\\s*");
		while(scan.hasNext()){
			relatedlist.add(new Relate(scan.next()));
		}scan.close();
			
		gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(relatedlist));

		return gson.toJson(relatedlist);
	}
}