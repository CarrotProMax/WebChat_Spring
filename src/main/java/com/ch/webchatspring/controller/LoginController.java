package com.ch.webchatspring.controller;

import com.ch.webchatspring.mapper.UserInfoMapper;
import com.ch.webchatspring.model.Login;
import com.ch.webchatspring.service.UserInfoService;
import com.ch.webchatspring.service.heartbeat.HeartbeatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController {
    @Autowired
    UserInfoService userinfo;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private HeartbeatServiceImpl heartbeatService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("login", new Login());
        return "index";
    }

    @GetMapping("/index")
    public String reIndex(Model model) {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // 返回 register.html
    }

    @PostMapping("/login")
    public String Login(
            @RequestParam String uname,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("获取到用户名:" + uname);
        if (Objects.equals(uname, "")) {//如果前端返回的值为空，直接返回值不允许为空
            System.out.println("用户名不能为空");
            redirectAttributes.addFlashAttribute("message", "用户名不能为空");
            return "redirect:/index";
        }
        System.out.println("服务端处理登录逻辑");
        Map<String, Object> map = userinfo.userLogin(uname);
        System.out.println("服务端处理完成");
        if (404 == (Integer) map.get("code")) {//根据服务端返回的代码来决定返回给前端的message
            System.out.println("用户名不存在");
            redirectAttributes.addFlashAttribute("message", "用户不存在");
            return "redirect:/index";
        }
        if (403 == (Integer) map.get("code")) {//根据服务端返回的代码来决定返回给前端的message
            redirectAttributes.addFlashAttribute("message", "用户已登录");
            System.out.println("用户名登录反馈成功");
            return "redirect:/index";
        }
        heartbeatService.userSetOnLine(uname);
        int uid = userInfoMapper.getUserUid(uname);
        redirectAttributes.addAttribute("uid", uid);
        return "redirect:/chat";//这里会返回一个值给前端，如果不是对应就Json格式的化前端解析器会报错，可在前端选择是否返回给用户
        //返回给前端值为成功就可以利用前端进行页面跳转
    }
}