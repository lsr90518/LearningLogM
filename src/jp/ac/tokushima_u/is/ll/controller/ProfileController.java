package jp.ac.tokushima_u.is.ll.controller;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ProfileEditForm;
import jp.ac.tokushima_u.is.ll.form.validator.ProfileEditFormValidator;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.CategoryService;
import jp.ac.tokushima_u.is.ll.service.LanguageService;
import jp.ac.tokushima_u.is.ll.service.ProfileService;
import jp.ac.tokushima_u.is.ll.service.UserService;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("langList")
    public List<Language> populateLanguageList() {
        return languageService.searchAllLanguage();
    }

    @RequestMapping
    public String index(ModelMap model) {
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("myLangs", languageService.findMyLangs(user.getId()));
        model.addAttribute("studyLangs", languageService.findStudyLangs(user.getId()));
        model.addAttribute("myCategoryList", categoryService.findListByUserId(user.getId()));


		// ■wakebe 次のレベルまでの経験値取得
		model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

		// ■wakebe 現在の合計経験値取得
		model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));


        return "profile/index";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(@ModelAttribute("form") ProfileEditForm form, ModelMap model) {
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        form = this.profileService.convertUserToProfileEditForm(user.getId());
        model.addAttribute("form", form);


		// ■wakebe 次のレベルまでの経験値取得
		model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

		// ■wakebe 現在の合計経験値取得
		model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));


        return "profile/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String update(@ModelAttribute("form") ProfileEditForm form, BindingResult result, ModelMap model) {
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        new ProfileEditFormValidator().validate(form, result);
        if(!StringUtils.isBlank(form.getOldpassword())){
	        if(userService.validateUser(user.getPcEmail(),form.getOldpassword())==null){
	        	result.reject("oldpassword", "profileEditForm.oldpassword.error");
	        }
        }
        if (result.hasErrors()) {
            return "profile/edit";
        }
        profileService.editProfile(form);
        return this.index(model);
    }

//    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET)
//    public void avatar(@PathVariable String id, HttpServletResponse response) throws IOException {
//        Users user = this.userService.getById(id);
//        if (user == null) {
//            return;
//        }
//        FileData fileData = userService.getAvatarByUser(user.getId());
//        if (fileData == null) {
//            return;
//        }
//        OutputStream out = response.getOutputStream();
//        if(fileData.getFileBin()!=null)
//        	out.write(fileData.getFileBin().getBin());
//        out.flush();
//        out.close();
//    }

    @RequestMapping(value = "/avataredit", method = RequestMethod.GET)
    public String avatarEdit(ModelMap model) {
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        return "profile/avataredit";
    }
    private static long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static String[] ALLOW_TYPES = new String[]{
        "image/pjpeg",
        "image/jpeg",
        "image/x-png",
        "image/png",
        "image/gif"
    };

    @RequestMapping(value = "/avataredit", method = RequestMethod.POST)
    public String avatarUpload(@ModelAttribute("form") ProfileEditForm form, BindingResult result, ModelMap model) {
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);

        MultipartFile file = form.getPhoto();
        if (file.isEmpty() || file.getSize() == 0) {
            result.rejectValue("photo", "form.photo.empty");
        }else if (file.getSize() > MAX_FILE_SIZE) {
            result.rejectValue("photo", "form.photo.fileSizeTooBig", new String[]{"5M"}, "Max file size is 5M!");
        }else if (!ArrayUtils.contains(ALLOW_TYPES, file.getContentType())) {
            result.rejectValue("photo", "form.photo.invalidFormat");
        }
        if (result.hasErrors()) {
            return "profile/avataredit";
        }
        try {
            this.profileService.uploadAvatar(file);
            user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "profile/avataredit";
    }

    @RequestMapping(value="/changepassword", method=RequestMethod.GET)
    public String changePassword(@ModelAttribute("form") ProfileEditForm form, ModelMap model){
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        form = this.profileService.convertUserToProfileEditForm(user.getId());
        model.addAttribute("form", form);


		// ■wakebe 次のレベルまでの経験値取得
		model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

		// ■wakebe 現在の合計経験値取得
		model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));


    	return "profile/changepassword";
    }

    @RequestMapping(value="/changepassword", method=RequestMethod.POST)
    public String changePasswordUpdate(@ModelAttribute("form") ProfileEditForm form, BindingResult result, ModelMap model){
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        model.addAttribute("user", user);
        new ProfileEditFormValidator().validatePassword(form, result);
        if(!StringUtils.isBlank(form.getOldpassword())){
	        if(userService.validateUser(user.getPcEmail(),form.getOldpassword())==null){
	        	result.rejectValue("oldpassword", "profileEditForm.oldpassword.error");
	        }
        }


		// ■wakebe 次のレベルまでの経験値取得
		model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

		// ■wakebe 現在の合計経験値取得
		model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));


        if (result.hasErrors()) {
            return "profile/changepassword";
        }
        profileService.editPassword(form);
    	return "redirect:/profile";
    }
}
