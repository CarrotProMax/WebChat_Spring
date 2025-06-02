package com.ch.webchatspring.controller;

import com.ch.webchatspring.mapper.UserInfoMapper;
import com.ch.webchatspring.model.Chat;
import com.ch.webchatspring.service.DeepSeekAPI;
import com.ch.webchatspring.service.UserInfoServiceImpl;
import com.ch.webchatspring.service.heartbeat.HeartbeatServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatContrller {
    @Autowired
    private DeepSeekAPI deepSeekAPI;
    @Autowired
    private HeartbeatServiceImpl heartbeatService;
    @Autowired
    UserInfoServiceImpl userInfoServiceImpl;
    @Autowired
    UserInfoMapper userinfo;
    Map<String, Chat> userChatInfo = new HashMap<>();

    @GetMapping("/chat")
    public String chat(Model model) {
        return "chat";
    }

    @GetMapping("/placeReLogin")
    public String placeReLogin() {
        return "/placeReLogin";
    }

    @RequestMapping("/function")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> receiveChat(@RequestBody Chat chat, HttpServletResponse response) {//这里的操作位0是心跳,1是刷新聊天(以获取最新的聊天),2是发送信息,3是获取历史聊天记录，4是获取联系人列表
        System.out.println("收到参数:" + chat.toString());
        Map<String, Object> result = new HashMap<>();
        if (!heartbeatService.userOnLineStatus(chat.getUid())) {
            Map<String, Object> json = new HashMap<>();
            json.put("status", "UNAUTHORIZED");
            json.put("code", 401);
            json.put("redirect", "/placeReLogin");  // 可以放完整URL
            json.put("message", "会话已过期，请重新登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(json);
        }

        try {
            switch (chat.getOperateSymbol()) {
                case 0: // 心跳
                    System.out.println("     处理心跳请求");//每隔五秒前端向后端发送0号操作，附上uid
                    heartbeatService.userSetOnLine(chat.getUid());
                    result.put("status", "success");
                    break;
                case 1:// 发送信息获取回复
                    System.out.println("     处理获取回复请求");
                    result.put("status", "success");
                    result.put("message", deepSeekAPI.getAiResponse(chat.getUid(), chat.getMessage()));
                    break;
                case 3: // 历史记录
                    System.out.println("     处理历史聊天请求");
                    result.put("status", "success");
                    result.put("message", userInfoServiceImpl.getUserAiChatOldMessages(chat.getUid()));
                    break;
                case 4://更改角色设定
                    System.out.println("     更改角色设定");//开始的时候发一次，保存默认角色，后面如果更改就使用这个操作位
                    deepSeekAPI.insertAiChatMessage(chat.getMessage(),chat.getUid());
                    result.put("status", "success");//处理完前端要显示处理成功
                    break;
                case 5://重置聊天
                    System.out.println(("    重置聊天"));
                    deepSeekAPI.restartChat(chat.getUid());//后端会清除聊天
                    result.put("status", "success");//前端要显示处理成功
                    break;
                default:
                    System.out.println("     处理位置聊天类型" + chat.getOperateSymbol());
                    result.put("status", "error");
                    result.put("contacts", "未知操作类型");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("contacts", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}