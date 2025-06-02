package com.ch.webchatspring.service;

import com.ch.webchatspring.entity.AiChat;
import com.ch.webchatspring.entity.UserInfo;
import com.ch.webchatspring.mapper.UserInfoMapper;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Value("${avatar.upload-dir}")
    private String uploadDir;
    @Override
    public List<UserInfo> findAll(){
        return null;
    }

    @Override
    public Map<String,Object> userLogin(String name) {
        //数据库查询用户是否存在，存在查询登录状态，如果都通过就返回可登录
        UserInfo user = userInfoMapper.findByName(name);
        if(user==null){//判断查询结果是否为空如果是空的话返回错误代码
            return Map.of("code",404);
        }//如果用户不存在就返回null，让contrller进行处理
        else if(user.getLogin_status()==1){//判断查询用户的登录状态，如果是登录状态返回相应代码
            return Map.of("code",403);//404是用户不存在，403是用户已登录
        }
        else {//登录成功这里的i是在数据库中的数据更新条数（这里没有任何用户）
            int i = userInfoMapper.updateUserLoginStatus(name, 1);
            return Map.of("code", 200);//返回用户名和200代表用户登录成功
        }
    }

    @Override
    public int userRegister(String name) {
        System.out.println("开始插入");
        return userInfoMapper.userRegister(0,name);
    }

    @Override
    public String compressAndSaveAvatar(MultipartFile file ,String username) throws IOException {
        // 生成唯一文件名
        int uid = userInfoMapper.getUserUid(username);//使用查询查询到刚刚插入用户的uid
        String fileName = uid + ".jpg";//用刚刚更改的uid命名文件
        Path outputPath = Paths.get(uploadDir, fileName);//获取输入路径和输入文件名
        // 使用Thumbnailator进行压缩
        Thumbnails.of(file.getInputStream())
                .size(700, 700)
                .outputFormat("jpg")
                .outputQuality(0.9)
                .toFile(outputPath.toFile());
        System.out.println("文件名："+fileName);
        return fileName;
    }
    public List<AiChat> getUserAiChatOldMessages(int uid){
        System.out.println(userInfoMapper.getAiMessages(uid));
        return userInfoMapper.getAiMessages(uid);
    }
    @Override
    public Boolean userIsExist(String username) {
        return userInfoMapper.findByName(username) != null;
    }
    public int getUserUid(String username) {return userInfoMapper.getUserUid(username);}
}
