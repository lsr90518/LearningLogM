package jp.ac.tokushima_u.is.ll.service.task;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.service.LuceneIndexService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class IndexAllItemTaskService {

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private LuceneIndexService luceneIndexService;
	
	@RequestMapping
	@ResponseBody
	public void execute() {
		List<Item> items = itemDao.findListAll();
		for(Item item: items){
			if(StringUtils.isNotBlank(item.getImage())){
				luceneIndexService.addItemIdToIndex(item.getId());
			}
		}
	}

}
