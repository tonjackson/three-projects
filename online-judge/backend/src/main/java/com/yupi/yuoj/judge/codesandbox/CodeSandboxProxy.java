package com.yupi.yuoj.judge.codesandbox;

import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String requestStr = executeCodeRequest.toString();
        // 截断过长日志，避免大代码量时OOM
        if (requestStr.length() > 500) {
            requestStr = requestStr.substring(0, 500) + "...(truncated)";
        }
        log.info("代码沙箱请求信息：" + requestStr);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        String responseStr = executeCodeResponse.toString();
        if (responseStr.length() > 500) {
            responseStr = responseStr.substring(0, 500) + "...(truncated)";
        }
        log.info("代码沙箱响应信息：" + responseStr);
        return executeCodeResponse;
    }
}
