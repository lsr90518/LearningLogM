package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 *
 * @author houbin
 */
public class Language implements Serializable {
	
    private static final long serialVersionUID = -175586803239488933L;

    private String id;
    /**
     * ISO 639 Language Codes, two Letter code
     */
    private String code;
    private String name;


    public Language() {
    }

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Language && this.getId()!=null){
			return this.getId().equals(((Language)obj).getId());
		}else{
			return false;
		}
	}
}
