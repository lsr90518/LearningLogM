package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 *
 * @author houbin
 */
public class Country implements Serializable {

    private static final long serialVersionUID = -7714583775370107525L;
    private String id;
    /**
     * ISO 3166 Country Codes, two Letter code
     */
    private String code;
    private String name;

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
}
