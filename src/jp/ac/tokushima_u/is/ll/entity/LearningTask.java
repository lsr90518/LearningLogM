/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author houbin
 */
public class LearningTask implements Serializable {

    private static final long serialVersionUID = -3165888418479945907L;
    private String id;
    private String title;
    private String learnPlace;
    private Date learnTime;
    private String learnObject;
    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLearnObject() {
        return learnObject;
    }

    public void setLearnObject(String learnObject) {
        this.learnObject = learnObject;
    }

    public String getLearnPlace() {
        return learnPlace;
    }

    public void setLearnPlace(String learnPlace) {
        this.learnPlace = learnPlace;
    }

    public Date getLearnTime() {
        return learnTime;
    }

    public void setLearnTime(Date learnTime) {
        this.learnTime = learnTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
