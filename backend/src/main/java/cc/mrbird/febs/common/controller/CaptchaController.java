package cc.mrbird.febs.common.controller;

import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.service.RedisService;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class CaptchaController {
    @Autowired
    private RedisService redisService;

    @ResponseBody
    @RequestMapping("/captcha")
    public FebsResponse captcha() throws Exception {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisService.set(key, verCode, 30 * 60 * 1000l);
        // 将key和base64返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("image", specCaptcha.toBase64());
        return new FebsResponse().data(data);
    }
}