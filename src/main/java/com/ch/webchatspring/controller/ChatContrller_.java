/*package com.ch.webchatspring.controller;

import com.ch.webchatspring.mapper.UserInfoMapper;
import com.ch.webchatspring.model.Chat;
import com.ch.webchatspring.service.chatservice.ChatServiceImpl;
import com.ch.webchatspring.service.heartbeat.HeartbeatServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatContrller_ {
    @Autowired
    private ChatServiceImpl chatService;
    @Autowired
    private HeartbeatServiceImpl heartbeatService;
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
        System.out.println("开始处理聊天请求");
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
            System.out.println("Switch语句开始前");
            switch (chat.getOperateSymbol()) {
                case 0: // 心跳
                    System.out.println("     处理心跳请求");
                    heartbeatService.userSetOnLine(chat.getUid());
                    result.put("status", "success");
                    break;

                case 1: // 刷新聊天
                    System.out.println("     处理聊天请求");
                    result.put("status", "success");
                    result.put("contacts", chatService.getNewMessages(chat.getUid(),chat.getSendName(),chat.getReceiveName(),chat.getTime()));
                    break;
                case 2:

                    break;
                case 3: // 历史记录
                    System.out.println("     处理历史聊天请求");
                    result.put("status", "success");
                    result.put("contacts", chatService.getOldChatMessages(
                            chat.getUid(),
                            chat.getReceiveName(),
                            chat.getTime()));
                    break;

                case 4: // 联系人列表
                    System.out.println("     处理联系人列表请求");
                    result.put("status", "success");
                    result.put("contacts", chatService.getUserList(chat.getUid()));
                    break;
                case 5://发送消息
                    System.out.println("     处理搜索好友");
                    chatService.sendMessage(chat.getUid(),chat.getReceiveName(),chat.getTime(),chat.getMessage());
                    result.put("status", "success");
                case 6://发送消息
                    System.out.println("     搜索添加好友");
                    chatService.sendMessage(chat.getUid(),chat.getReceiveName(),chat.getTime(),chat.getMessage());
                    result.put("status", "success");

                default:
                    System.out.println("     处理位置聊天类型"+chat.getOperateSymbol());
                    result.put("status", "error");
                    result.put("contacts", "未知操作类型");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("contacts", e.getMessage());
        }

        return ResponseEntity.ok(result);




*/

















//        if (heartbeatService.userOnLineStatus(chat.getSendName())) {
//            if (chat.getOperateSymbol() == 0) {
//                heartbeatService.userSetOnLine(chat.getSendName());
//            }
//            if (chat.getOperateSymbol() == 1) {
//                model.addAttribute("sendName", chat.getSendName());
//            }
//            if (chat.getOperateSymbol() == 3) {
//                model.addAttribute(
//                        "getOldChatMessages",
//                        chatService.getOldChatMessages(
//                                chat.getUid(),
//                                chat.getReceiveName(),
//                                chat.getTime()));
//            }
//            if (chat.getOperateSymbol() == 4) {
//                model.addAttribute("getUserList", chatService.getUserList(chat.getUid()));
//            }
//            return "chat";
//        }
//        return "/placeReLogin";
//    }
//}
