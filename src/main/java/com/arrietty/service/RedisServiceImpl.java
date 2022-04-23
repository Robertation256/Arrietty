package com.arrietty.service;

import com.arrietty.consts.RedisKey;
import com.arrietty.consts.RedisKeyTimeout;
import com.arrietty.dao.*;
import com.arrietty.entity.Bulletin;
import com.arrietty.entity.Favorite;
import com.arrietty.entity.TextbookTag;
import com.arrietty.entity.User;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.SessionPO;
import com.arrietty.utils.session.SessionContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/28 10:56
 */

@Service
public class RedisServiceImpl {

    private static final Type LIST_OF_BULLETIN_TYPE = new TypeToken<ArrayList<Bulletin>>() {}.getType();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TextbookTagMapper textbookTagMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private BulletinMapper bulletinMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;


    @Autowired
    private TapMapper tapMapper;

    public void init(){
        // 重启清空redis
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        // initialize valid textbook tag ids
        List<Long> textbookTagIds = textbookTagMapper.selectAllIds();
        if(textbookTagIds!=null){
            for(Long textbookId : textbookTagIds){
                addToValidTextbookTagIdSet(textbookId);
            }
        }


        // initialize advertisement timestamp
        getAdvertisementTimestamp();


        // initialize blacklisted user net id list
        List<String> blockNetIds = userMapper.selectBlacklistedUserNetIds();
        if(blockNetIds!=null){
            for(String netId: blockNetIds){
                redisTemplate.opsForSet().add(RedisKey.BLACKLISTED_USER_NET_ID_SET, netId);
            }
        }


        // initialize ad timestamp
        Date timestamp = advertisementMapper.getLatestAdCreateTime();
        if (timestamp==null){
            timestamp = new Date();
        }
        setAdvertisementTimestamp(timestamp);

        // initialize bulletin cache
        List<Bulletin> bulletins = bulletinMapper.selectAll();
        setBulletin(bulletins);

        // initialize admin stats
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPLOAD_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPDATE_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_DELETE_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_MARK_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_UNMARK_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_SEARCH_NUM, 0);


        // initialize blacklist
        List<String> blacklistedNetIds = userMapper.selectBlacklistedUserNetIds();
        if(blacklistedNetIds!=null){
            for (String blacklistedNetId: blacklistedNetIds){
                addBlacklistedUserNetId(blacklistedNetId);
            }
        }

        
    }

    public long incrementRequestNum(String ip){
        Long timestamp = Instant.now().getEpochSecond()/60;
        String key = String.format("rate_limit:ip=%s:timestamp=%d",ip, timestamp);
        List<Object> txResults = (List<Object>) redisTemplate.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Long requestNum = operations.opsForValue().increment(key);
                if(requestNum==null || requestNum.equals(1L)){
                    operations.expire(key,1, TimeUnit.MINUTES);
                }
                // This will contain the results of all ops in the transaction
                return operations.exec();
            }
        });
        return (long) txResults.get(0);
    }

    public void loadUserCache(User user){

        Long userId = user.getId();

        // load user profile
        ProfilePO profile = new ProfilePO();
        profile.setNetId(user.getNetId());
        profile.setUsername(user.getUsername());
        profile.setSchoolYear(user.getSchoolYear());
        profile.setMajor(user.getMajor());
        profile.setBio(user.getBio());
        profile.setAvatarImageId(user.getAvatarImageId());
        setUserProfile(userId, profile);


        // load tapped ad ids
        List<Long> adIds = tapMapper.selectTappedAdIdsBySenderId(userId);
        if(adIds!=null){
            for(Long id: adIds){
                addUserTappedAdId(userId, id.toString());
            }
        }


        // load marked ad ids
        List<Favorite> favoriteList = favoriteMapper.selectByUserId(userId);
        if(favoriteList!=null){
            for (Favorite favorite : favoriteList){
                addUserMarkedAdId(userId, favorite.getAdId().toString());
            }
        }


        // load user has new notification flag
        Boolean result = tapMapper.getUserHasNewNotification(userId);
        setUserHasNewNotification(userId, result);

        //set expiration time
        extendUserCache(userId);
    }


    public void extendUserCache(Long userId){
        redisTemplate.expire(RedisKey.USER_PROFILE+userId.toString(), RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);
        redisTemplate.expire(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+userId.toString(), RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);
        redisTemplate.expire(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+userId.toString(), RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);
        redisTemplate.expire(RedisKey.USER_NOTIFICATION_HAS_NEW+userId.toString(), RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);

    }

    public void adDeleteCleanUp(Long adId){
        redisTemplate.delete(RedisKey.NUMBER_OF_TAPS+adId.toString());
    }



    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key){

        return redisTemplate.opsForValue().get(key);
    }

    public void extendUserSession(String userSessionId){
        redisTemplate.expire(RedisKey.USER_SESSION+userSessionId, RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);
    }

    public void setUserSession(String userSessionId, SessionPO sessionPO){
        Gson gson = new Gson();
        String serialized = gson.toJson(sessionPO, SessionPO.class);
        redisTemplate.opsForValue().set(RedisKey.USER_SESSION+userSessionId,serialized, RedisKeyTimeout.USER_CACHE_TIMEOUT, TimeUnit.MINUTES);
    }

    public SessionPO getUserSession(String  userSessionId){
        Object obj = redisTemplate.opsForValue().get(RedisKey.USER_SESSION+userSessionId);
        if (obj == null) return null;
        String jsonString = (String) obj;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, SessionPO.class);
    }

    public void removeUserSession(Long userId){
        redisTemplate.delete(RedisKey.USER_SESSION+userId.toString());
    }

    public void setUserProfile(Long userId, ProfilePO profile){
        if (userId==null) return;

        String serialized = new Gson().toJson(profile, ProfilePO.class);
        redisTemplate.opsForValue().set(RedisKey.USER_PROFILE+userId.toString(),serialized);
    }

    public ProfilePO getUserProfile(Long  userId){
        if(userId==null) return null;

        Object obj = redisTemplate.opsForValue().get(RedisKey.USER_PROFILE+userId.toString());
        if (obj == null) return null;
        String jsonString = (String) obj;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ProfilePO.class);
    }

    public boolean existsTextbookTagId(Long textbookTagId){
        if(textbookTagId==null) return false;
        Boolean result = redisTemplate.opsForSet().isMember(RedisKey.VALID_TEXTBOOK_TAG_ID_SET, textbookTagId.toString());
        return Boolean.TRUE.equals(result);
    }

    public void addToValidTextbookTagIdSet(Long textbookId){
        redisTemplate.opsForSet().add(RedisKey.VALID_TEXTBOOK_TAG_ID_SET,textbookId.toString());
    }

    public void removeFromValidTextbookTagIdSet(Long textbookId){
        redisTemplate.opsForSet().remove(RedisKey.VALID_TEXTBOOK_TAG_ID_SET,textbookId.toString());
    }


    public void addUserTappedAdId(Long userId, String adId){
        redisTemplate.opsForSet().add(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+ userId.toString(),adId);

    }

    public void addUserTappedAdIds(Long userId, Set<String> adIds){
        for(String id: adIds){
            addUserTappedAdId(userId, id);
        }
    }

    public Set<String> getUserTappedAdIds(Long userId){
        Set<String> ret = redisTemplate.opsForSet().members(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+ userId.toString());
        if(ret==null){
            return new HashSet<>();
        }
        return ret;
    }

    public Long incrementNumberOfTaps(Long adId){
        return redisTemplate.opsForValue().increment(RedisKey.NUMBER_OF_TAPS+adId.toString(), 1L);
    }

    public void setNumberOfTaps(Long adId, Integer num){
        redisTemplate.opsForValue().set(RedisKey.NUMBER_OF_TAPS+adId.toString(), num);
    }

    public Integer getNumberOfTaps(Long adId){
        return (Integer) redisTemplate.opsForValue().get(RedisKey.NUMBER_OF_TAPS);
    }


    public Date getAdvertisementTimestamp(){
        String json = (String) redisTemplate.opsForValue().get(RedisKey.AD_TIMESTAMP);
        if(json!=null){
            return new Gson().fromJson(json, Date.class);
        }
        return null;
    }

    public void setAdvertisementTimestamp(Date timestamp){
        redisTemplate.opsForValue().set(RedisKey.AD_TIMESTAMP, new Gson().toJson(timestamp));
    }

    public Boolean getUserHasNewNotification(Long userId){
        String result = (String) redisTemplate.opsForValue().get(RedisKey.USER_NOTIFICATION_HAS_NEW+userId.toString());
        if(result!=null){
            return result.equals("true");
        }
        return null;
    }

    public void setUserHasNewNotification(Long userId, boolean hasNew){
        redisTemplate.opsForValue().set(RedisKey.USER_NOTIFICATION_HAS_NEW+userId.toString(), hasNew?"true":"false");
    }



    public Set<String> getUserMarkedAdIds(Long userId){
        return redisTemplate.opsForSet().members(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+userId.toString());
    }

    public boolean isMarkedByCurrentUser(String adId){
        return redisTemplate.opsForSet().isMember(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString(), adId);
    }

    public void addUserMarkedAdId(Long userId, String adId){
        redisTemplate.opsForSet().add(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+userId.toString(), adId);
    }

    public void addUserMarkedAdIds(Long userId, Set<String> adIds){
        for(String id: adIds){
            addUserMarkedAdId(userId, id);
        }
    }

    public void removeUserMarkedAdId(Long userId, String adId){
        redisTemplate.opsForSet().remove(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+userId.toString(), adId);
    }

    public List<Bulletin> getBulletin(){
        String json = (String) redisTemplate.opsForValue().get(RedisKey.BULLETIN_CACHE);
        if(json==null){
            return null;
        }
        return new Gson().fromJson(json, LIST_OF_BULLETIN_TYPE);
    }

    public void setBulletin(List<Bulletin> bulletins){
        String json = new Gson().toJson(bulletins);
        redisTemplate.opsForValue().set(RedisKey.BULLETIN_CACHE, json);
    }


    public void incrementUserAdUploadNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_UPLOAD_NUM,1);
    }

    public int getUserAdUploadNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_UPLOAD_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPLOAD_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserAdUpdateNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_UPDATE_NUM,1);
    }

    public int getUserAdUpdateNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_UPDATE_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPDATE_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserAdDeleteNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_DELETE_NUM,1);
    }

    public int getUserAdDeleteNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_DELETE_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_DELETE_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserMarkNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_MARK_NUM,1);
    }

    public int getUsermarkNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_MARK_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_MARK_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserUnmarkNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_UNMARK_NUM,1);
    }

    public int getUserUnmarkNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_UNMARK_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_UNMARK_NUM, 0);
        return result==null?0:result;
    }

    public void incrementSearchRequestNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_SEARCH_NUM,1);
    }

    public int getSearchRequestNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_SEARCH_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_SEARCH_NUM, 0);
        return result==null?0:result;
    }

    public void addBlacklistedUserNetId(String netId){
        redisTemplate.opsForSet().add(RedisKey.BLACKLISTED_USER_NET_ID_SET, netId);
    }

    public void removeBlacklistedUserNetId(String netId){
        redisTemplate.opsForSet().remove(RedisKey.BLACKLISTED_USER_NET_ID_SET, netId);
    }

    public boolean isNetIdBlacklisted(String netId){
        Boolean result = redisTemplate.opsForSet().isMember(RedisKey.BLACKLISTED_USER_NET_ID_SET, netId);
        return Boolean.TRUE.equals(result);
    }

    public Set<String> getBlacklistedUserNetIds(){
        return redisTemplate.opsForSet().members(RedisKey.BLACKLISTED_USER_NET_ID_SET);
    }

}
