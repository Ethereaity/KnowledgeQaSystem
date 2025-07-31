package org.example.knowledgeqasystem.service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class AIController {

    private final Gson gson = new Gson();

    @PostMapping("tall")
    public String tallQuestion(@org.springframework.web.bind.annotation.RequestBody String question,String FileContent) throws IOException, UnirestException {

        Unirest.setTimeouts(0, 0);
        List<DeepseekRequest.Message> messages = new ArrayList<>();
        messages.add(DeepseekRequest.Message.builder().role("system").content("你是一个知识库问答助手,根据用户提供的文档内容回答问题。").build());
        messages.add(DeepseekRequest.Message.builder().role("user").content("用户提供的问档内容如下：" + FileContent + " 用户提问如下：" + question).build());

        DeepseekRequest requestBody = DeepseekRequest.builder()
                .model("deepseek-chat")
                .messages(messages)
                .build();
        HttpResponse<String> response = Unirest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer "+"sk-463195e3a7bc4f569c8ce2abe4af0552")
                .body(gson.toJson(requestBody))
                .asString();
        return  response.getBody();

    }
}