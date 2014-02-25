package jp.ac.tokushima_u.is.ll.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
/**
 *
 * @author HOU Bin
 */
public class ProfileEditForm implements Serializable {
    private static final long serialVersionUID = -4944483509197934833L;

    private String nickname;
    private String firstName;
    private String lastName;
    private String interesting;
    private List<String> myLangs = new ArrayList<String>();
    private List<String> studyLangs = new ArrayList<String>();
    private String oldpassword;
    private String password;
    private String passwordConfirm;
    private String userid;
    private MultipartFile photo;


    public ProfileEditForm(){
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInteresting() {
        return interesting;
    }

    public void setInteresting(String interesting) {
        this.interesting = interesting;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public List<String> getMyLangs() {
        return myLangs;
    }

    public void setMyLangs(List<String> myLangs) {
        this.myLangs = myLangs;
    }

    public List<String> getStudyLangs() {
        return studyLangs;
    }

    public void setStudyLangs(List<String> studyLangs) {
        this.studyLangs = studyLangs;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }
}
