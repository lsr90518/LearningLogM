package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 *
 * @author Bin Hou
 */
public class FileBin implements Serializable {

    private static final long serialVersionUID = -8157161763918179866L;
    private String id;
    private byte[] bin;

    public byte[] getBin() {
        return bin;
    }

    public void setBin(byte[] bin) {
        this.bin = bin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
