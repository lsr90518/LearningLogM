package jp.ac.tokushima_u.is.ll.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class MultipleFileUploadForm implements Serializable {

	private static final long serialVersionUID = 576667813944114729L;

	private List<MultipartFile> files = new ArrayList<MultipartFile>();

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
}
