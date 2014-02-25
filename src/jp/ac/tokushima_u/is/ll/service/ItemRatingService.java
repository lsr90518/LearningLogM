package jp.ac.tokushima_u.is.ll.service;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemRatingDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.ItemRating;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Houbin
 */
@Service
@Transactional(readOnly = true)
public class ItemRatingService {

	@Autowired
    private ItemDao itemDao;
	@Autowired
    private ItemRatingDao itemRatingDao;
	@Autowired
    private UsersDao usersDao;

    public boolean ratingExist(String itemId) {
        String userId = SecurityUserHolder.getCurrentUser().getId();
        return this.itemRatingDao.findBooleanIfRatingExist(itemId, userId);
    }

    @Transactional(readOnly = false)
    public void create(String itemId, Integer rate) {
        String userId = SecurityUserHolder.getCurrentUser().getId();
        if(this.itemRatingDao.findBooleanIfRatingExist(itemId, userId)){
        	return;
        }
        ItemRating rating = new ItemRating();
        rating.setItemId(itemId);
        rating.setUserId(userId);
        rating.setRating(new Double(rate));
        Double currentRatingSum = itemRatingDao.findDoubleRatingSum(rating.getItemId());
        if (currentRatingSum == null) {
            currentRatingSum = 0d;
        }
        Long count = itemRatingDao.findLongRatingCount(rating.getItemId());
        if (count == null) {
            count = 0l;
        }
        rating.setId(KeyGenerateUtil.generateIdUUID());
        itemRatingDao.insert(rating);
        double avg = (currentRatingSum + rating.getRating()) / (count + 1);
        itemDao.updateRatingAvg(itemId, avg);
    }

    public Long countVotesNumber(String itemId) {
        Long count = itemRatingDao.findLongRatingCount(itemId);
        return count == null?0:count;
    }

    public Double avgRating(String itemId) {
        Double avg = itemRatingDao.findDoubleRatingAvg(itemId);
        return avg == null?0:avg;
    }
}
