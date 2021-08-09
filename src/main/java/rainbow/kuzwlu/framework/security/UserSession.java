package rainbow.kuzwlu.framework.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import rainbow.kuzwlu.utils.Resource;
import rainbow.kuzwlu.web.model.master.SysRoleResource;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 13:55
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession implements UserDetails{

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 用户账号
     */
    private String user;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否可用。1表示可用，0表示不可用
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String comment;

    /**
     * 用户角色
     **/
    private List<SysRoleResource> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (1 == this.status){
            SysRoleResource autherResource = new SysRoleResource();
            autherResource.setSign(Resource.ADMIN);
            roles.add(autherResource);
        }
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles.stream().map(e -> "ROLE_" + e.getSign()).collect(Collectors.joining(",")));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated()?true:false;
    }

    @Override
    public boolean isEnabled() {
        return 1 == this.status;
    }
}
