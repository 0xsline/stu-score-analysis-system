package cc.mrbird.febs;

import cc.mrbird.febs.common.authentication.JWTToken;
import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.domain.ActiveUser;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.properties.FebsProperties;
import cc.mrbird.febs.common.service.RedisService;
import cc.mrbird.febs.common.utils.*;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.manager.UserManager;
import cc.mrbird.febs.system.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserManager userManager;

    @Autowired
    private FebsProperties properties;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SecurityManager securityManager;

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Before
    public void setup() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRemoteAddr("127.0.0.1");
        String username = "admin";
        String password = "1234qwer";
        try {
            password = MD5Util.encrypt(username, password);
        }catch (Exception e){
           e.printStackTrace();
        }

        final String errorMessage = "用户名或密码错误";
        User user = this.userManager.getUser(username);

        if (user == null||password == null){
            throw new FebsException(errorMessage);
        }
        if (!StringUtils.equals(user.getPassword(), password)){
            throw new FebsException(errorMessage);
        }
        if (User.STATUS_LOCK.equals(user.getStatus())){
            throw new FebsException("账号已被锁定,请联系管理员！");
        }

        String token = FebsUtil.encryptToken(JWTUtil.sign(username, password));
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(properties.getShiro().getJwtTimeOut());
        String expireTimeStr = DateUtil.formatFullTime(expireTime);
        JWTToken jwtToken = new JWTToken(token, expireTimeStr);
        String userId = this.saveTokenToRedis(user, jwtToken, mockHttpServletRequest);
        user.setId(userId);
        SecurityUtils.setSecurityManager(securityManager);
        JWTToken jwtToken1 = new JWTToken(FebsUtil.decryptToken(token));
        SecurityUtils.getSubject().login(jwtToken1);
    }

    private String saveTokenToRedis(User user, JWTToken token, HttpServletRequest request) throws Exception {
        String ip = IPUtil.getIpAddr(request);
        System.out.println("之前的数据：" + ip  + "token: "+ token.getToken());
        // 构建在线用户
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUsername(user.getUsername());
        activeUser.setIp(ip);
        activeUser.setToken(token.getToken());
        activeUser.setLoginAddress(AddressUtil.getCityInfo(ip));

        // zset 存储登录用户，score 为过期时间戳
        this.redisService.zadd(FebsConstant.ACTIVE_USERS_ZSET_PREFIX, Double.valueOf(token.getExipreAt()), mapper.writeValueAsString(activeUser));
        // redis 中存储这个加密 token，key = 前缀 + 加密 token + .ip
        this.redisService.set(FebsConstant.TOKEN_CACHE_PREFIX + token.getToken() + StringPool.DOT + ip, token.getToken(), properties.getShiro().getJwtTimeOut() * 1000);

        return activeUser.getId();
    }


    @Test
    public void testSelect() {
        User user = userService.getById(1);
        System.out.println(user);
    }
}
