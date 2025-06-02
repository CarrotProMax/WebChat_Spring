package com.ch.webchatspring.mapper;

import com.ch.webchatspring.entity.AiChat;
import com.ch.webchatspring.entity.FriendInfo;
import com.ch.webchatspring.entity.Message;
import com.ch.webchatspring.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserInfoMapper {//这里放所有所有的sql语句相应的接口
    List<UserInfo> findAll();
    UserInfo findByName(String name);
    /**
     * 根据用户登录状态信息
     * @param name 要更新的用户名（条件）
     * @param login_status 更新登录状态
     * @return 受影响的行数（通常 1 表示成功，0 表示未找到记录）
     */
    int updateUserLoginStatus(
            @Param("name") String name,
            @Param("login_status") int login_status
    );
    int userRegister(@Param("login_status") int login_status,
                   @Param("name") String name);
    int getUserUid(String name);
    String getUserName(int uid);
    List<Message> getUsersMessages(@Param("user_send_uid") int user_send_uid,
                                   @Param("user_receiver_uid") int user_receiver_uid,
                                   Timestamp send_time);
    List<FriendInfo> getUserFriend(int uid);
    Message getNewMessage(int sendUid, int receiverUid, Timestamp timestamp);
    void sendMessage(int uid,int receiverUid,Timestamp timestamp,String message);
    List<AiChat> getAiMessages(@Param("uid") int uid);
    List<AiChat> getAiOldMessages(@Param("uid") int uid);
    void insertAiChatMessage(@Param("message") String message,
                             @Param("role")String role,
                             @Param("uid")int uid);
    void deleteAiChatMessage(@Param("uid") int uid);//这里升级一下，把用户原来的聊天放一个标记作废语句
}
