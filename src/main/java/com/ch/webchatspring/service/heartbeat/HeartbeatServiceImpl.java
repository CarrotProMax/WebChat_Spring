package com.ch.webchatspring.service.heartbeat;

import com.ch.webchatspring.mapper.UserInfoMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@EnableScheduling
public class HeartbeatServiceImpl {
    @Autowired
    private UserInfoMapper userInfoMapper;
    ConcurrentHashMap <String, Integer> userUid = new ConcurrentHashMap<>();
    public boolean userOnLineStatus(int uid){
        String username = userInfoMapper.getUserName(uid);
//        System.out.println("获取到判断是否在线用户名:"+username);
//        System.out.println("判断结果"+userUid.containsKey(username));
        return userUid.containsKey(username);
    }
    public boolean userOnLineStatus(String username){
//        System.out.println("获取到判断是否在线用户名:"+username);
//        System.out.println("判断结果"+userUid.containsKey(username));
        return userUid.containsKey(username);
    }
    public void userSetOnLine(String userName){
        System.out.println("用户:"+userName+"  已上线");
        userUid.put(userName,1);
    }
    public void userSetOnLine(int uid){
        String userName = userInfoMapper.getUserName(uid);
        System.out.println("用户:"+userName+"  已上线");
        userUid.put(userName,1);
    }
    public void userSetOffLine(String userName){
        userUid.remove(userName);
    }
    @Scheduled(fixedRate = 12000)
    public void startHeartbeat() {//最后发现spring中有自带的定时任务，就用了spring自带的注解进行定时执行心跳任务//这里使用了Spring的自己的线程管理，我这里只有这一个线程所以没有使用线程池
            for (Map.Entry<String, Integer> entry : userUid.entrySet()) {//这里遍历userUid中的每一个值
                String username = entry.getKey();
                int number = entry.getValue();//这里的number初始值为二，一次没接收到心跳减一，第二次没接收到心跳就归零并且移除
                if (number == 0) {
                    userInfoMapper.updateUserLoginStatus(username, 0);
                    userUid.remove(username);
                    System.out.println("下线用户:" + username);
                } else {
                    userUid.put(username, number - 1);
                }
        }
    }
}
