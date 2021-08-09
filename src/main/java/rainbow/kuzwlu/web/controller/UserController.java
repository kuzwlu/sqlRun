package rainbow.kuzwlu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import rainbow.kuzwlu.framework.common.BootPage;
import rainbow.kuzwlu.framework.common.ErrorResult;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.framework.security.UserSession;
import rainbow.kuzwlu.web.mapper.master.SysRoleMapper;
import rainbow.kuzwlu.web.mapper.master.SysSqlMapper;
import rainbow.kuzwlu.web.mapper.master.SysUserMapper;
import rainbow.kuzwlu.web.mapper.master.SysUserSqlMapper;
import rainbow.kuzwlu.web.model.master.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 14:15
 * @Email kuzwlu@gmail.com
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysSqlMapper sysSqlMapper;

    @Autowired
    private SysUserSqlMapper sysUserSqlMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @PostMapping("/getList")
    @Log("获取用户列表")
    public BootPage<SysUserSqls> getList(BootPage<SysUser> page) {
        BootPage<SysUserSqls> sysUserSqlsBootPage = new BootPage<>();
        QueryWrapper queryWrapper = new QueryWrapper();
        if (!StringUtils.isEmpty(page.getSearch())) {
            String search = page.getSearch();
            queryWrapper.like("name", search);
        }
        IPage<SysUser> userPage = new Page<>(page.getOffset(), page.getLimit());
        userPage = sysUserMapper.selectPage(userPage, queryWrapper);
        List<SysUser> records = userPage.getRecords();
        List<SysUserSqls> sysUserSqls = new ArrayList<>(records.size());

        for (SysUser sysUser : records) {
            SysUserSqls sysUserSqls1 = new SysUserSqls();
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("userId", sysUser.getId());
            SysUserSql list = sysUserSqlMapper.selectOne(queryWrapper1);
            String[] split = list.getSqlId().split(",");
            List<String> dataSourceName = new ArrayList<>();
            if (!StringUtils.isEmpty(split)) {
                for (String s : split) {
                    if (!StringUtils.isEmpty(s)) {
                        SysSql sysSql = sysSqlMapper.selectById(Integer.parseInt(s));
                        dataSourceName.add(sysSql.getDBName());
                    }
                }
            }
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("id", sysUser.getRoleId());
            SysRole sysRole = sysRoleMapper.selectOne(queryWrapper2);
            sysUserSqls1.setSysUser(sysUser);
            sysUserSqls1.setDataSourceName(dataSourceName);
            sysUserSqls1.setSysRole(sysRole);
            sysUserSqls.add(sysUserSqls1);
        }

        sysUserSqlsBootPage.setData(sysUserSqls);
        sysUserSqlsBootPage.setRecordsTotal((int) userPage.getTotal());
        return sysUserSqlsBootPage;
    }

    @PostMapping("/getUser/{id}")
    @Log("/用户详情")
    public JsonResult getUser(@PathVariable("id") Integer id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser != null) {
            return SuccessResult.jsonSuccess("查询成功", "data", sysUser);
        }
        return ErrorResult.jsonError("查询失败");
    }

    @PostMapping("/add")
    @Log("添加用户")
    public JsonResult addUser(@Valid @RequestBody SysUser sysUser, Authentication authentication, HttpServletRequest request) {
        if (hasUser(sysUser.getUser())) {
            String sql = request.getParameter("sql");
            StringBuilder sb = new StringBuilder();
            if (sql != null || sql != "") {
                List<String> sqlList = Arrays.asList(sql.split(",").clone());
                for (String s : sqlList) {
                    QueryWrapper<SysSql> sysSqlQueryWrapper = new QueryWrapper<>();
                    sysSqlQueryWrapper.eq("DBName", s);
                    SysSql sysSql = sysSqlMapper.selectOne(sysSqlQueryWrapper);
                    if (sysSql != null) {
                        sb.append(sysSql.getId()).append(",");
                    }
                }
            }

            sysUser.setCreateBy(((UserSession) authentication.getPrincipal()).getUser());
            sysUser.setCreateTime(LocalDateTime.now());
            int insert = sysUserMapper.insert(sysUser);
            if (insert == 1) {
                QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
                sysUserQueryWrapper.eq("user", sysUser.getUser());
                SysUser sysUser1 = sysUserMapper.selectOne(sysUserQueryWrapper);
                SysUserSql sysUserSql = new SysUserSql();
                sysUserSql.setSqlId(sb.toString());
                sysUserSql.setUserId(sysUser1.getId());
                sysUserSqlMapper.insert(sysUserSql);
                return SuccessResult.jsonSuccess("添加成功");
            }
        } else {
            return ErrorResult.jsonError("用户名重复");
        }
        return ErrorResult.jsonError("添加失败");
    }

    @PostMapping("/update")
    @Log("修改用户")
    public JsonResult updateUser(@Valid @RequestBody SysUser sysUser, Authentication authentication, HttpServletRequest request) {
        //防止user被更改
        if (sysUser.getId() == 1) {
            return ErrorResult.jsonError("不能修改管理员");
        }
        sysUser.setUser(null);
        String sql = request.getParameter("sql");
        StringBuilder sb = new StringBuilder();
        if (sql != null || sql != "") {
            List<String> sqlList = Arrays.asList(sql.split(",").clone());
            for (String s : sqlList) {
                QueryWrapper<SysSql> sysSqlQueryWrapper = new QueryWrapper<>();
                sysSqlQueryWrapper.eq("DBName", s);
                SysSql sysSql = sysSqlMapper.selectOne(sysSqlQueryWrapper);
                if (sysSql != null) {
                    sb.append(sysSql.getId()).append(",");
                }
            }
        }

        sysUser.setUpdateBy(((UserSession) authentication.getPrincipal()).getUser());
        sysUser.setUpdateTime(LocalDateTime.now());
        int update = sysUserMapper.updateById(sysUser);
        if (update == 1) {
            QueryWrapper<SysUserSql> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", sysUser.getId());
            SysUserSql sysUserSql = sysUserSqlMapper.selectOne(queryWrapper);
            sysUserSql.setSqlId(sb.toString());
            sysUserSqlMapper.updateById(sysUserSql);
            return SuccessResult.jsonSuccess("修改成功");
        }
        return ErrorResult.jsonError("修改失败");
    }

    @PostMapping("/delete/{id}")
    @Log("删除单个用户")
    public JsonResult deleteUser(@PathVariable("id") Integer id) {
        int i = sysUserMapper.deleteById(id);
        if (i == 1) {
            return SuccessResult.jsonSuccess("删除成功");
        } else {
            return ErrorResult.jsonError("删除失败");
        }
    }

    @PostMapping("/delete")
    @Log("批量删除用户")
    public JsonResult deleteUsers(Integer[] ids) {
        int i = sysUserMapper.deleteBatchIds(Arrays.asList(ids));
        if (i >= 1) {
            return SuccessResult.jsonSuccess("删除成功");
        } else {
            return ErrorResult.jsonError("删除失败");
        }
    }

    public boolean hasUser(String user) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("user", user);
        SysUser sysUser = sysUserMapper.selectOne(sysUserQueryWrapper);
        if (sysUser == null) {
            return true;
        }
        return false;
    }

}
