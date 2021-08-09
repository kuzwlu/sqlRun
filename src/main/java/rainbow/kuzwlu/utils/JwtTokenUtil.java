package rainbow.kuzwlu.utils;

import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.security.UserSession;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:54
 * @Email kuzwlu@gmail.com
 */
@Component
public class JwtTokenUtil implements Serializable{

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET = "jwtsecret";
    public static final String ISS = "echisan";

    public static final Long EXPIRATION = 60 * 60 *3L; //过期时间3小时

    private static final String ROLE = "ROLE_";
    private static final String CLAIM_KEY_USERNAME = "kuzwlu";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final long serialVersionUID = -8305152446124853696L;

    @Resource
    RedisUtil redisUtil;

    /**
     * 将token存储到redis
     */
    public void setExpire(String key, String val, long time) {
        redisUtil.setExpire(key, val, time);
    }

    /**
     * 移除
     */
    public void del(String key) {
        redisUtil.del(key);
    }

    /**
     * 判断是否有效
     * @param key
     * @return
     */
    public Boolean validateToken(String key) {
        Object o = redisUtil.get(key);
        if(null != o){
            return true;
        }
        return false;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        return Jwts.builder().setClaims(claims).setIssuer(ISS).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    public String generateToken(UserSession userSession,String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userSession.getUser());
        claims.put(ROLE,role);
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = (String) claims.get(CLAIM_KEY_USERNAME);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
