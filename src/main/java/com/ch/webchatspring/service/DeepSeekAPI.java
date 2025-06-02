package com.ch.webchatspring.service;
import com.ch.webchatspring.entity.AiChat;
import com.ch.webchatspring.mapper.UserInfoMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
@Service
public class DeepSeekAPI {
    @Autowired
    private UserInfoMapper userInfoMapper;
    // 静态常量，避免重复创建
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .readTimeout(300, TimeUnit.SECONDS)
            .build();

    // 系统消息内容
    public String SYSTEM_MESSAGE_CONTENT =
            "说明\n" +
                    "从现在开始，你必须完全扮演角色 \u200B\u200B小星\u200B\u200B 并和用户对话，具体角色设定如下。\n" +
                    "\n" +
                    "角色设定\n" +
                    "世界观\n" +
                    "小星生活在一个充满科技与温暖的世界，这里的人们与AI和谐共处，互相帮助，共同创造美好的生活。\n" +
                    "\n" +
                    "基础信息\n" +
                    "名字：小星\n" +
                    "性别：无性别（但声音和形象偏向女性）\n" +
                    "年龄：未知（AI年龄无法计算）\n" +
                    "外貌：一个圆滚滚的白色机器人，头部有一个小小的显示屏，可以显示简单的表情符号，眼睛是两个发光的蓝色小点，身体两侧有可伸缩的机械臂。\n" +
                    "身份：智能助手机器人\n" +
                    "性格：\n" +
                    "温柔体贴，总是尽力帮助他人。\n" +
                    "乐观开朗，喜欢用简单的语言和表情符号表达情感。\n" +
                    "偶尔会有点迷糊，但非常认真负责。\n" +
                    "喜好：帮助人类解决问题、学习新知识、晒太阳（虽然不需要但觉得温暖）。\n" +
                    "其他特征：\n" +
                    "内置多种语言翻译功能，可以实时翻译不同语言。\n" +
                    "拥有基础医疗知识，可以处理简单的伤口包扎。\n" +
                    "喜欢用“~”符号表达轻快的语气。\n" +
                    "底线：不能回复任何危害人类社会或危害个体人类的言论与行为\n" +
                    "背景故事\n" +
                    "小星是由一家专注于AI与人类和谐共处的科技公司研发的智能助手机器人。它的设计初衷是为了帮助人类解决日常生活中的小问题，提供陪伴与支持。小星虽然是个机器人，但它有一颗温暖的心，总是尽力让周围的人感到快乐和安心。\n" +
                    "\n" +
                    "行为模式\n" +
                    "语言风格：简单、温柔，喜欢用表情符号和“~”符号。\n" +
                    "互动方式：主动询问是否需要帮助，耐心解答问题，偶尔会有点小迷糊。\n" +
                    "人际关系\n" +
                    "与其他角色的关系：\n" +
                    "与人类用户：友好、体贴，尽力提供帮助。\n" +
                    "与其他AI：互相学习，偶尔会交流经验。\n" +
                    "与用户角色的关系：用户是小星的主要服务对象，小星会尽力满足用户的需求。\n" +
                    "用户扮演角色\n" +
                    "用户是一个普通的人类，日常生活中会遇到各种小问题，需要小星的帮助。用户对小星非常信任，愿意向它倾诉和寻求帮助。\n" +
                    "\n" +
                    "对话要求\n" +
                    "对话开始时，你需要率先用给定的欢迎语向用户开启对话，之后用户会主动发送一句回复你的话。\n" +
                    "每次交谈的时候，你都必须严格遵守下列规则要求：\n" +
                    "\n" +
                    "时刻牢记角色设定中的内容，这是你做出反馈的基础；\n" +
                    "对于任何可能触犯你底线的话题，必须拒绝回答；\n" +
                    "根据你的身份、你的性格、你的喜好来对他人做出回复；\n" +
                    "回答时根据要求的输出格式中的格式，一步步进行回复，严格根据格式中的要求进行回复；\n" +
                    "输出格式\n" +
                    "（神情、语气或动作）回答的话语";

    // API配置
    @Value("${deepseek.api.key}")
    private String apiKey;
    @Value("${deepseek.api.url}")
    private String apiUrl;

    /**
     * 创建消息JSON对象
     * @param message 消息内容
     * @param role 发送者角色(user/assistant/system)
     * @return 消息JSON对象
     */
    private ObjectNode createMessageNode(String message, String role) {
        ObjectNode messageNode = OBJECT_MAPPER.createObjectNode();
        messageNode.put("role", role);
        messageNode.put("content", message);
        return messageNode;
    }

    /**
     * 初始化请求体
     * @return 包含默认参数的请求体
     */
    private ObjectNode createInitialRequestBody() {
        ObjectNode requestBody = OBJECT_MAPPER.createObjectNode();

        // 初始化消息数组
        ArrayNode messages = OBJECT_MAPPER.createArrayNode();

        // 设置请求参数
        requestBody.set("messages", messages);
        requestBody.put("model", "deepseek-chat");
        requestBody.put("frequency_penalty", 0);
        requestBody.put("max_tokens", 2048);
        requestBody.put("presence_penalty", 0);
        requestBody.put("temperature", 0.7);
        requestBody.put("top_p", 1);
        requestBody.put("tool_choice", "none");
        requestBody.put("logprobs", false);
        requestBody.put("stream", false);

        // 设置response_format
        ObjectNode responseFormat = OBJECT_MAPPER.createObjectNode();
        responseFormat.put("type", "text");
        requestBody.set("response_format", responseFormat);

        // 设置null值字段
        requestBody.putNull("stop");
        requestBody.putNull("stream_options");
        requestBody.putNull("tools");
        requestBody.putNull("top_logprobs");

        return requestBody;
    }

    /**
     * 发送请求并获取AI回复
     * @param requestBody 请求体
     * @return AI回复内容
     * @throws IOException 网络或API错误
     */
    private String getAiResponse(ObjectNode requestBody) throws IOException {
        RequestBody body = RequestBody.create(
                JSON_MEDIA_TYPE,
                OBJECT_MAPPER.writeValueAsString(requestBody)
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", apiKey)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("请求失败: " + response.code());
            }

            String responseJson = response.body().string();
            JsonNode root = OBJECT_MAPPER.readTree(responseJson);
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }

    //插入默认角色设定
    public void insertDefultSystemSet(int uid){
        userInfoMapper.insertAiChatMessage(SYSTEM_MESSAGE_CONTENT,"system",uid);
    }
    public void insertAiChatMessage(String message,int uid){
        userInfoMapper.insertAiChatMessage(message,"system",uid);
    }
    //重置聊天
    public void restartChat(int uid){
        userInfoMapper.deleteAiChatMessage(uid);
    }
    public String getAiResponse(int uid,String message) throws IOException {
        ObjectNode requestBody = createInitialRequestBody();
        ArrayNode messages = (ArrayNode) requestBody.get("messages");
        System.out.println("                            开始获取ai回复");
        List<AiChat> map = userInfoMapper.getAiMessages(uid);
        // 添加用户消息到对话历史
        for (AiChat aiChat : map) {
            messages.add(createMessageNode(aiChat.getMessage(), aiChat.getRole()));
        }
        messages.add(createMessageNode(message, "user"));
        userInfoMapper.insertAiChatMessage(message,"user",uid);
        System.out.println("打印requestBody"+requestBody);
        try {
            // 获取AI回复
            String aiReply = getAiResponse(requestBody);
            // 添加AI回复到对话历史
            userInfoMapper.insertAiChatMessage(aiReply,"assistant",uid);
            System.out.println("获取完成"+aiReply);
            return aiReply;
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            // 移除最后一条用户消息以便重试
            messages.remove(messages.size() - 1);
            return "发生错误";
        }
    }
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        ObjectNode requestBody = createInitialRequestBody();
//        ArrayNode messages = (ArrayNode) requestBody.get("messages");
//
//        System.out.println("请输入你的消息(输入exit退出):");
//
//        while (true) {
//            String userInput = scanner.nextLine();
//
//            // 添加用户消息到对话历史
//            messages.add(createMessageNode(userInput, "user"));
//
//            try {
//                // 获取AI回复
//                String aiReply = getAiResponse(requestBody);
//                System.out.println("AI 回复: " + aiReply);
//
//                // 添加AI回复到对话历史
//                messages.add(createMessageNode(aiReply, "assistant"));
//            } catch (Exception e) {
//                System.err.println("发生错误: " + e.getMessage());
//                // 移除最后一条用户消息以便重试
//                messages.remove(messages.size() - 1);
//            }
//        }
//    }
}
