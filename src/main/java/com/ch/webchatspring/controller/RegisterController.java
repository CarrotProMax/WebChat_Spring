package com.ch.webchatspring.controller;

import com.ch.webchatspring.service.DeepSeekAPI;
import com.ch.webchatspring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@Controller
public class RegisterController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private DeepSeekAPI chatService;
    @PostMapping("/register")
    public String handleRegistration(
            @RequestParam String username,
            @RequestParam MultipartFile avatar,
            RedirectAttributes redirectAttributes
    ) {
        // 1. 验证用户名
        if (StringUtils.isEmpty(username)) {
            System.out.println("用户名不能为空");//这里算是冗余了，这个前端已经帮你做了
            redirectAttributes.addFlashAttribute("error", "用户名不能为空");
            return "redirect:/register";
        }
        // 基本验证
        if (avatar.isEmpty()) {
            System.out.println("头像不能为空");//这里算是冗余了，这个前端已经帮你做了
            redirectAttributes.addFlashAttribute("error", "请选择头像文件");
            return "redirect:/register";
        }

        // 验证图片类型
        if (!avatar.getContentType().startsWith("image/")) {
            System.out.println("图片格式不正确");
            redirectAttributes.addFlashAttribute("error", "仅支持图片格式");
            return "redirect:/register";
        }

        try {
            if (userInfoService.userIsExist(username)) {//调用数据库查询用户名是否存在(虽然设置了uid作为非空主键，但是用户登录时无法判断重名用户，所以还是在数据库中用户名还是唯一的 )
                System.out.println("用户已存在");
                redirectAttributes.addFlashAttribute("error", "用户名已存在");
                return "redirect:/register";
            }
            int i = userInfoService.userRegister(username);
            System.out.println("插入了 " + i+" 行");
            // 处理头像并保存
            System.out.println("开始处理压缩逻辑");
            String avatarPath = userInfoService.compressAndSaveAvatar(avatar,username);
            System.out.println("压缩逻辑处理完成");

            // TODO: 保存用户信息到数据库
            redirectAttributes.addFlashAttribute("success", "注册成功");
            chatService.insertDefultSystemSet(userInfoService.getUserUid(username));
            return "redirect:/register";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "头像处理失败");
            return "redirect:/register";
        }
    }
}
