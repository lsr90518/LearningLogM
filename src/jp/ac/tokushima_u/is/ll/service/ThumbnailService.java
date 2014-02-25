package jp.ac.tokushima_u.is.ll.service;

import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ThumbnailService {

	private static final Logger logger = LoggerFactory
			.getLogger(ThumbnailService.class);
	

	private String imageMagickPath = "/usr/local/bin/convert";

	public void thumbnail(File src, File dst, int width, int height) {
		if(!src.exists() || src.length()==0){
			return;
		}
		try {
			if (StringUtils.isBlank(imageMagickPath)) {
				logger.debug("Using Thumbnailator to create thumbnail");
				Thumbnails
						.of(src)
						.size(width, height)
						.outputFormat(FilenameUtils.getExtension(dst.getName()))
						.toFile(dst);
			} else {
				logger.debug("Using ImageMagick to create thumbnail");
				ConvertCmd cmd = new ConvertCmd();
				cmd.setSearchPath(imageMagickPath);
				IMOperation op = new IMOperation();
				op.addImage();
				op.resize(width, height);
				op.addImage();
				cmd.run(op, src.getCanonicalPath(), dst.getCanonicalPath());
			}
		} catch (IOException | InterruptedException | IM4JavaException e) {
			logger.error("Error when create thumbnail", e);
		}
	}
}
