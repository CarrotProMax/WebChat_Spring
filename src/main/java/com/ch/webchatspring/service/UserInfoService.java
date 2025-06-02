package com.ch.webchatspring.service;

import com.ch.webchatspring.entity.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserInfoService {
    public List<UserInfo> findAll();
    public Map<String,Object> userLogin(String name);
    public int userRegister(String name);
    public String compressAndSaveAvatar(MultipartFile file,String username) throws IOException;
    Boolean userIsExist(String username);
    public int getUserUid(String username);
}
