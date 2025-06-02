package com.ch.webchatspring.service.chatservice;

import com.ch.webchatspring.entity.FriendInfo;
import com.ch.webchatspring.entity.Message;
import com.ch.webchatspring.mapper.UserInfoMapper;
import com.ch.webchatspring.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl {
    @Value("${avatar.upload-dir}")
    private String uploadDir;
    @Autowired
    private UserInfoMapper userInfoMapper;
    public List<Message> getOldChatMessages(int uid, String receiverName, Timestamp timestamp) {
        int receiverUid = userInfoMapper.getUserUid(receiverName);
        if(timestamp==null){//如果时间为空就加载以现在时间为起点的最近20条消息，空说明是第一次加载消息
            timestamp = new Timestamp(System.currentTimeMillis());
        }
        List<Message> messages = userInfoMapper.getUsersMessages(uid,receiverUid,timestamp);
        System.out.println(messages);
        return messages;
    }



    public Map<String,Message> getNewMessages(int uid, String sendName,String receiverName, Timestamp timestamp) {
        Map<String,Message> map = new HashMap<>();
        map.put(sendName,userInfoMapper.getNewMessage(uid,userInfoMapper.getUserUid(receiverName),timestamp));
        System.out.println("获取到用户"+receiverName+"的最新消息"+map);
        return map;

    }


    public Map<String,String> getUserList(int uid) {//这里查询联系人获取到uid和姓名后分开查询，uid去做成路径返回给前端，姓名保持不变
        List<FriendInfo> FriendInfo=userInfoMapper.getUserFriend(uid); //这里的uid是查询当前联系人的所有好友
        System.out.println(FriendInfo);
        Map<String,String> nameAndPath=new HashMap<>();
        for(FriendInfo friendInfo:FriendInfo){
            nameAndPath.put(friendInfo.getName(),getUserAvatarPath(friendInfo.getUid()));//键是获取的用户名，值是根据getUserAvatarPath获取的头像地址
            System.out.println(nameAndPath.get(friendInfo.getName()));
        }
        System.out.println(nameAndPath);
        return nameAndPath;//返回一个map值，键是用户名，值是头像路径
    }

    public void sendMessage(int uid,String receiverName,Timestamp timestamp,String message){
        userInfoMapper.sendMessage(uid,userInfoMapper.getUserUid(receiverName),timestamp,message);
    }

//    public List<String> getUserAvatarPath(List<Integer> useruid) {
//        List<String> paths = new ArrayList<>();
//        for (int i : useruid) {//遍历每一个数将相应的path添加到paths里。
//            System.out.println(i);
//            paths.add(uploadDir + i+".jpg");
//        }
//        return paths;
//    }//这个方法本来是用来批量获取用户头像地址的，但是可能存在头像姓名不匹配的情况所以弃用了
    public String getUserAvatarPath(int uid) {
        return uploadDir+uid+".jpg";
    }
}
