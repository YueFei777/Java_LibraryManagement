package redlib.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redlib.backend.dao.*;
import redlib.backend.model.*;
import redlib.backend.service.TokenService;
import redlib.backend.service.utils.TokenUtils;
import redlib.backend.utils.FormatUtils;
import redlib.backend.vo.OnlineUserVO;
import lombok.extern.slf4j.Slf4j;
import ua_parser.Parser;
import ua_parser.Client;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    @Autowired private RootMapper rootMapper;
    @Autowired private SupervisorMapper supervisorMapper;
    @Autowired private ReadersMapper readersMapper;
    @Autowired private redlib.backend.dao.UserPrivilegeMapper userPrivilegeMapper;

    private final Map<String, Token> tokenMap = new ConcurrentHashMap<>(1 << 8);

    /**
     * 用户登录，返回令牌信息
     *
     * @param username    用户名称
     * @param password  密码
     * @return 令牌信息
     */
    @Override
    public Token login(String username, String password, String ipAddress, String userAgent) {
        // 1. 依次尝试三种用户类型登录
        Root root = rootMapper.login(username, FormatUtils.password(password));
        if (root != null) {
            return buildToken(UserType.root, root.getUsername(), ipAddress, userAgent);
        }

        Supervisor supervisor = supervisorMapper.login(username, FormatUtils.password(password));
        if (supervisor != null) {
            return buildToken(UserType.supervisor, supervisor.getUsername(), ipAddress, userAgent);
        }

        Readers readers = readersMapper.login(username, FormatUtils.password(password));
        if (readers != null) {
            return buildToken(UserType.reader, readers.getUsername(), ipAddress, userAgent);
        }

        throw new RuntimeException("用户名或密码错误");
    }

    private Token buildToken(UserType userType, String username,
                             String ipAddress, String userAgent) {
        Parser uaParser = new Parser();
        Client c = uaParser.parse(userAgent);

        Token token = new Token();
        token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        token.setUserType(userType);
        token.setUserName(username);
        token.setIpAddress(ipAddress);
        token.setLastAction(new Date());
        token.setBrowser(c.userAgent.family + " " + c.userAgent.major);
        token.setOs(c.os.family + " " + c.os.major);
        token.setDevice(c.device.family);

        switch (userType) {
            case root:
                token.setPrivSet(new HashSet<>(Collections.singletonList("*")));
                break;
            case supervisor:
                Supervisor supervisor = supervisorMapper.selectByUsername(username);
                token.setUserId(supervisor.getSupervisorId());
                token.setPrivSet(getUserPrivileges(userType, username));
                break;
            case reader:
                Readers reader = readersMapper.selectByUsername(username);
                token.setUserId(reader.getReaderId());
                token.setPrivSet(getUserPrivileges(userType, username));
                break;
        }
        tokenMap.put(token.getAccessToken(), token);
        return token;
    }


    /**
     * 根据token获取令牌信息
     *
     * @param accessToken token
     * @return 令牌信息
     */
    @Override
    public Token getToken(String accessToken) {
        if (FormatUtils.isEmpty(accessToken)) {
            return null;
        }

        return tokenMap.get(accessToken);
    }

    /**
     * 登出系统
     *
     * @param accessToken 令牌token
     */
    @Override
    public void logout(String accessToken) {

    }

    /**
     * 获取在线用户列表
     *
     * @return
     */
    @Override
    public List<OnlineUserVO> list() {
        Collection<Token> tokens = tokenMap.values();
        return tokens.stream().map(TokenUtils::convertToVO).collect(Collectors.toList());
    }

    /**
     * 将在线用户踢出系统
     *
     * @param accessToken 用户的accessToken
     */
    @Override
    public void kick(String accessToken) {

    }

    private String makeToken() {
        return UUID.randomUUID().toString().replaceAll("-", "") + "";
    }

    private Set<String> getUserPrivileges(UserType userType, String username) {
        // 根据 userType 和 username 查询用户ID
        Integer userId = switch (userType) {
            case supervisor -> supervisorMapper.getIdByUsername(username);
            case reader -> readersMapper.getIdByUsername(username);
            default -> throw new IllegalArgumentException("无效的用户类型");
        };

        // 查询权限表
        List<UserPrivilege> privileges = userPrivilegeMapper.listPrivileges(userType, userId);
        return privileges.stream()
                .map(p -> p.getModId() + "." + p.getPriv())
                .collect(Collectors.toSet());
    }

}
