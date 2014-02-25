package jp.ac.tokushima_u.is.ll.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.FutureDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Future_data;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.HashUtils;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
//import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mouri　未来館のプログラム
 * 
 * 
 */
@Service
@Transactional
public class FutureLogService {

	@Autowired(required = true)
	private FutureDao futureDao;
	@Autowired(required = true)
	private FileDataDao fileDataDao;
	@Autowired(required = true)
	private ItemDao itemdao;
	@Autowired(required = true)
	private ItemTitleDao itemtitledao;
	@Autowired
	private StaticServerService staticServerService;
	

	private Map<String, String> _translateCache = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void Regstertable2(String nitizi, String aite, String place,
			String old, String ninzu, String action, String important,
			String opinion,String user_name,String nitizi_end,String group,String interaction,String comentary,MultipartFile upim) throws IOException, ParseException {

		Future_data future_data = new Future_data();
		future_data.setId(KeyGenerateUtil.generateIdUUID());
		future_data.setNitizi(nitizi);
		future_data.setAite(aite);
		future_data.setPlace(place);
		future_data.setOld(old);
		future_data.setNinzu(ninzu);
		future_data.setAction(action);
		future_data.setImportant(important);
		future_data.setOpinion(opinion);
		future_data.setUser_name(user_name);
		future_data.setNitizi_end(nitizi_end);
		future_data.setGroupshow(group);
		future_data.setInteraction(interaction);
		future_data.setComentary(comentary);

		Date current = new Date(System.currentTimeMillis());
		boolean imageFlg = false;
		// Attached File
		if (upim != null) {
			MultipartFile file = upim;
			if (!file.isEmpty() && file.getSize() != 0) {

				FileData image = new FileData();
				MultipartFile uploadFile = upim;
				image.setOrigName(uploadFile.getOriginalFilename());
				image.setCreateAt(current);

				String fileType = "";
				if (!StringUtils.isBlank(uploadFile.getOriginalFilename())) {
					fileType = FilenameUtil.checkMediaType(uploadFile
							.getOriginalFilename());
					image.setFileType(fileType);
					if (FilenameUtil.IMAGE.equals(fileType))
						imageFlg = true;
				}
				
				byte[] b = uploadFile.getBytes();
				
				image.setId(KeyGenerateUtil.generateIdUUID());
				image.setMd5(HashUtils.md5Hex(b));
				fileDataDao.insert(image);
				staticServerService.upload(b, image.getId(), FilenameUtils.getExtension(uploadFile.getOriginalFilename()));
				future_data.setImage(image.getId());

			}
		}

		this.futureDao.insert(future_data);


		
	}

}
