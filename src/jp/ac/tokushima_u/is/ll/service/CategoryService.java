package jp.ac.tokushima_u.is.ll.service;

import java.util.List;
import java.util.Set;

import jp.ac.tokushima_u.is.ll.dao.CategoryDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dto.CategoryDTO;
import jp.ac.tokushima_u.is.ll.entity.Category;
import jp.ac.tokushima_u.is.ll.form.CategoryEditForm;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ItemDao itemDao;

	public List<CategoryDTO> findRoots() {
		List<CategoryDTO> result = categoryDao.findAllRoots();
		return result;
	}

	@Transactional(readOnly = false)
	public Category createByForm(CategoryEditForm form, Category parent) {
		Category category = new Category();
		category.setName(form.getName());
		category.setNote(category.getNote());
		if(parent==null){
			category.setParent(null);
		}else{
			Category p = categoryDao.findById(parent.getId());
			category.setParent(p.getId());
		}
		category.setId(KeyGenerateUtil.generateIdUUID());
		categoryDao.insert(category);
		return category;
	}

	public Category findById(String id) {
		return categoryDao.findById(id);
	}

	public List<Category> findByName(String name, String parentId) {
		return categoryDao.findListByNameAndParent(name, parentId);
	}

	@Transactional(readOnly = false)
	public void delete(String categoryId) {
		categoryDao.delete(categoryId);
	}
	
	public Set<Category> getAllSubCategories(Category category){
		return loadAllSubCategories(category);
	}
	
	private Set<Category> loadAllSubCategories(
			Category category) {
		Set<Category> catSet = categoryDao.findListByParentId(category.getId());
		for(Category cat:catSet){
			catSet.addAll(loadAllSubCategories(cat));
		}
		return catSet;
	}
	
	public int sizeOfCategory(Category category){
		Set<Category> allSubCat = getAllSubCategories(category);
		int size = categoryDao.findCountItemsInCategory(category.getId());
		for(Category cat:allSubCat){
			size+=categoryDao.findCountItemsInCategory(cat.getId());
		}
		return size;
	}

	public List<Category> findListByUserId(String userId) {
		return categoryDao.findListByUserId(userId);
	}

	public List<CategoryDTO> findListDTOByUserId(String userId) {
		return categoryDao.findListDTOByUserId(userId);
	}

	public CategoryDTO findDTOById(String id) {
		CategoryDTO categoryDTO =  categoryDao.findDTOById(id);
		return categoryDTO;
	}
}