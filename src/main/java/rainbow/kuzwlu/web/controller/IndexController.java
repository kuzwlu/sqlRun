package rainbow.kuzwlu.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.security.UserSession;
import rainbow.kuzwlu.web.model.master.SysUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 22:41
 * @Email kuzwlu@gmail.com
 */
@RestController
public class IndexController {

    @PostMapping("/getUserInfo")
    public JsonResult getUserInfo(Authentication authentication){
        UserSession principal = (UserSession) authentication.getPrincipal();
        SysUser sysUser = new SysUser();
        sysUser.setId(principal.getId());
        sysUser.setUser(principal.getUser());
        sysUser.setUserName(principal.getUsername());
        sysUser.setPassword(principal.getPassword());
        sysUser.setRoleId(principal.getRoleId());
        return SuccessResult.jsonSuccess("查询成功","data",sysUser);
     }

}
