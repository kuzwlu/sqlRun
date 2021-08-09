package rainbow.kuzwlu.framework.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rainbow.kuzwlu.framework.security.exception.UserAuthenticationException;
import rainbow.kuzwlu.web.mapper.master.SysRoleMapper;
import rainbow.kuzwlu.web.mapper.master.SysRoleResourceMapper;
import rainbow.kuzwlu.web.mapper.master.SysUserMapper;
import rainbow.kuzwlu.web.model.master.SysRole;
import rainbow.kuzwlu.web.model.master.SysRoleResource;
import rainbow.kuzwlu.web.model.master.SysUser;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/14 01:03
 * @Email kuzwlu@gmail.com
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleResourceMapper sysRoleResourceMapper;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user",user);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        if (sysUser == null) {
            throw new UserAuthenticationException("用户不存在");
        }
        if (sysUser.getStatus() == 0) {
            throw new UserAuthenticationException("账号已被封禁，请联系管理员");
        }
        SysRole sysRole = sysRoleMapper.selectById(sysUser.getRoleId());
        if (sysRole == null) {
            throw new UserAuthenticationException("用户不存在");
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("roleId", sysUser.getRoleId());
        List<SysRoleResource> list = sysRoleResourceMapper.selectList(queryWrapper);
        Optional<List<SysRoleResource>> optionalList = Optional.of(list);

        // 返回对象
        UserSession userSession = UserSession.builder()
                .id(sysUser.getId())
                .roleId(sysRole.getId())
                .user(sysUser.getUser())
                .userName(sysUser.getUserName())
                .password(sysUser.getPassword())
                .status(sysUser.getStatus())
                .createBy(sysUser.getCreateBy())
                .createTime(sysUser.getCreateTime())
                .updateBy(sysUser.getUpdateBy())
                .updateTime(sysUser.getUpdateTime())
                .comment(sysUser.getComment())
                .roles(optionalList.orElseGet(ArrayList::new))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userSession,userSession.getPassword(),userSession.getAuthorities()));
        return userSession;
    }
}
