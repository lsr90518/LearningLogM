package jp.ac.tokushima_u.is.ll.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.service.FutureLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author mouri 未来館のプログラム
 * 
 */

@Controller
@RequestMapping("/future")
public class FutureController {
	@Autowired
	private FutureLogService futureLogService;

	public String nitizi;
	public String aite;
	public String place;
	public String old;
	public String ninzu;
	public String action;
	public String important;
	public String opinion;
	public String user_name;
	String nitizi_end;
	String group;
	String interaction;
	String comentary;
	byte[] bytes;

	@RequestMapping(value = "/req", method = RequestMethod.POST)
	@ResponseBody
	public String test(
			String json,
			ModelMap model,
			@RequestParam(value = "image", required = true) MultipartFile image,
			HttpServletRequest request) throws IOException, ParseException {
		model.clear();
		try {
			String eucjpStr = new String(json.getBytes("UTF-8"), "UTF-8");
			System.out.println(eucjpStr);
		} catch (UnsupportedEncodingException e) {
			System.out.println("ERROR FUTURE84");

		}

		// Gsonオブジェクトを作成
		Gson gson = new Gson();
		if (json != null) {
			System.out.println(gson.toJson(json));
			// パーサーを取得

			JsonParser jp = new JsonParser();

			// 文字列をパース
			try {
				JsonElement je = jp.parse(json);
				JsonArray trade = je.getAsJsonArray();
				int a1 = trade.size();

				for (int i = 0; i < trade.size(); i++) {
					// for(int i=0;i<=trade.size();i++)
					Set<Map.Entry<String, JsonElement>> entrySet = trade.get(i)
							.getAsJsonObject().entrySet();
					// Set<Map.Entry<String, JsonElement>> entrySet =
					// je.getAsJsonObject()
					// .entrySet();
					Iterator<Map.Entry<String, JsonElement>> it = entrySet
							.iterator();

					// 一覧表示

					while (it.hasNext())

					{

						Map.Entry<String, JsonElement> entry = it.next();

						String key = entry.getKey();

						JsonElement value = entry.getValue();

						switch (key) {
						case "nitizi":
							this.nitizi = value.getAsString();
							break;
						case "aite":
							this.aite = value.getAsString();
							break;
						case "place":
							this.place = value.getAsString();
							break;
						case "old":
							this.old = value.getAsString();
							break;
						case "ninzu":
							this.ninzu = value.getAsString();
							break;
						case "action":
							this.action = value.getAsString();
							break;
						case "important":
							this.important = value.getAsString();
							break;
						case "opinion":
							this.opinion = value.getAsString();
							break;
						case "user_name":
							this.user_name = value.getAsString();
							break;

						case "nitizi_end":
							this.nitizi_end = value.getAsString();
							break;
						case "category":
							this.group = value.getAsString();
							break;
						case "interaction":
							this.interaction = value.getAsString();
							break;
						case "comentary":
							this.comentary = value.getAsString();
							break;
						}

						System.out.print(key + " : ");

						if (value.isJsonObject()) {

							System.out.println("★-->");

						}

						else {

							if (value.isJsonNull()) {

								System.out.println("NULL");

							}

							else {

								System.out.println(value.getAsString());

							}

						}
					}
					futureLogService.Regstertable2(this.nitizi, this.aite,
							this.place, this.old, this.ninzu, this.action,
							this.important, this.opinion, this.user_name,
							this.nitizi_end, this.group, this.interaction,
							this.comentary, image);

					System.out.println(request.getParameter("json"));
				}
			} catch (JsonSyntaxException e) {

			}

			// future_logservice関数

		}
		//

		Map<String, String> map = new HashMap<String, String>();
		map.put("nitizi", this.nitizi);
		map.put("aite", this.aite);
		map.put("user_name", this.user_name);
		map.put("old", this.old);
		Gson g = new Gson();

		return g.toJson(map);

		// model.addAttribute("nitizi",this.nitizi_controller);
		// model.addAttribute("aite",this.aite_controller);
		// model.addAttribute("user_name",this.user_name_controller);
		// model.addAttribute("old",this.old_controller);

		// return "/future";
	}

	@RequestMapping
	public String index() {
		return "/future/show";
	}
	// @RequestMapping(value = "/request", method = RequestMethod.POST)
	// @ResponseBody
	// // public String items(String json, ModelMap model, HttpServletRequest
	// request) {
	// public String items(
	// String json,
	// ModelMap model,
	//
	// HttpServletRequest request) throws IOException {
	// model.clear();
	//
	// //
	// //
	// try {
	// String eucjpStr = new String(json.getBytes("UTF-8"), "UTF-8");
	// System.out.println(eucjpStr);
	// } catch (UnsupportedEncodingException e) {
	// System.out.println("ERROR FUTURE84");
	//
	// }
	//
	// // Gsonオブジェクトを作成
	// Gson gson = new Gson();
	// if (json != null) {
	// System.out.println(gson.toJson(json));
	// // パーサーを取得
	//
	// JsonParser jp = new JsonParser();
	//
	// // 文字列をパース
	// try {
	// JsonElement je = jp.parse(json);
	// JsonArray trade = je.getAsJsonArray();
	// int a1 = trade.size();
	//
	// for (int i = 0; i < trade.size(); i++) {
	// // for(int i=0;i<=trade.size();i++)
	// Set<Map.Entry<String, JsonElement>> entrySet = trade.get(i)
	// .getAsJsonObject().entrySet();
	// // Set<Map.Entry<String, JsonElement>> entrySet =
	// // je.getAsJsonObject()
	// // .entrySet();
	// Iterator<Map.Entry<String, JsonElement>> it = entrySet
	// .iterator();
	//
	// // 一覧表示
	//
	// while (it.hasNext())
	//
	// {
	//
	// Map.Entry<String, JsonElement> entry = it.next();
	//
	// String key = entry.getKey();
	//
	// JsonElement value = entry.getValue();
	//
	// switch (key) {
	// case "nitizi":
	// this.nitizi = value.getAsString();
	// break;
	// case "aite":
	// this.aite = value.getAsString();
	// break;
	// case "place":
	// this.place = value.getAsString();
	// break;
	// case "old":
	// this.old = value.getAsString();
	// break;
	// case "ninzu":
	// this.ninzu = value.getAsString();
	// break;
	// case "action":
	// this.action = value.getAsString();
	// break;
	// case "important":
	// this.important = value.getAsString();
	// break;
	// case "opinion":
	// this.opinion = value.getAsString();
	// break;
	// case "user_name":
	// this.user_name = value.getAsString();
	// break;
	//
	// case "nitizi_end":
	// this.nitizi_end = value.getAsString();
	// break;
	// case "group":
	// this.group = value.getAsString();
	// break;
	// case "interaction":
	// this.interaction = value.getAsString();
	// break;
	// case "comentary":
	// this.comentary = value.getAsString();
	// break;
	// }
	//
	// System.out.print(key + " : ");
	//
	// if (value.isJsonObject()) {
	//
	// System.out.println("★-->");
	//
	// }
	//
	// else {
	//
	// if (value.isJsonNull()) {
	//
	// System.out.println("NULL");
	//
	// }
	//
	// else {
	//
	// System.out.println(value.getAsString());
	//
	// }
	//
	// }
	// }
	// future_logservice
	// .Regstertable2(this.nitizi, this.aite, this.place,
	// this.old, this.ninzu, this.action,
	// this.important, this.opinion,
	// this.user_name,this.nitizi_end,this.group,this.interaction,this.comentary);
	//
	// System.out.println(request.getParameter("json"));
	// }
	// } catch (JsonSyntaxException e) {
	//
	// }
	//
	// // future_logservice関数
	//
	// }
	// //
	// //
	// // Map<String, String> map = new HashMap<String, String>();
	// // map.put("nitizi", this.nitizi);
	// // map.put("aite", this.aite);
	// // map.put("user_name", this.user_name);
	// // map.put("old", this.old);
	// // Gson g = new Gson();
	//
	// // return g.toJson(map);
	//
	//
	//
	// return "item/list";
	// }

}
