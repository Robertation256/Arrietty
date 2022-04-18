package com.arrietty.service;


import com.arrietty.consts.AccessControl;
import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BlacklistServiceImpl {

    private static final String UPDATE_LOCK = "black_list_update_lock";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisServiceImpl redisService;

    public List<String> getBlacklistedUserNetIds(){
        Set<String> result = redisService.getBlacklistedUserNetIds();
        return new ArrayList<>(result);
    }

    public void updateBlacklist(String action, String netId){
        User user;
        if("add".equals(action)){
            synchronized (UPDATE_LOCK){
                user = userMapper.selectByNetId(netId);
                if(user==null){
                    throw new LogicException(ErrorCode.INVALID_URL_PARAM, "User not found");
                }

                if(AccessControl.ADMIN.equals(user.getAccessControl())){
                    throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Cannot blacklist an admin");
                }

                user.setAccessControl(AccessControl.BLACKLISTED);
                userMapper.updateAccessControl(user);
                redisService.addBlacklistedUserNetId(user.getNetId());
            }

        }
        else if ("delete".equals(action)){
            synchronized (UPDATE_LOCK){
                user = userMapper.selectByNetId(netId);
                if(user==null){
                    throw new LogicException(ErrorCode.INVALID_URL_PARAM, "User not found");
                }

                if(!AccessControl.BLACKLISTED.equals(user.getAccessControl())){
                    throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This user is not blacklisted.");
                }

                user.setAccessControl(AccessControl.REGULAR);
                userMapper.updateAccessControl(user);
                redisService.removeBlacklistedUserNetId(user.getNetId());
            }
        }
        else {
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid action type.");
        }
    }
}
