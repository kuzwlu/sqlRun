package rainbow.kuzwlu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rainbow.kuzwlu.DataSourceRunTime;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.exception.SqlException;
import rainbow.kuzwlu.framework.common.ErrorResult;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.framework.security.UserSession;
import rainbow.kuzwlu.sql.DBInfo;
import rainbow.kuzwlu.utils.Resource;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;
import rainbow.kuzwlu.web.mapper.master.SysSqlMapper;
import rainbow.kuzwlu.web.mapper.master.SysUserSqlMapper;
import rainbow.kuzwlu.web.model.master.SysLog;
import rainbow.kuzwlu.web.model.master.SysSql;
import rainbow.kuzwlu.web.model.master.SysUser;
import rainbow.kuzwlu.web.model.master.SysUserSql;

import javax.annotation.security.RolesAllowed;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/1/9 00:04
 * @Email kuzwlu@gmail.com
 */
@RestController
public class SqlExecController {

    private static final String RUNCODE = "运行代码";

    @Autowired
    private SysUserSqlMapper sysUserSqlMapper;

    @Autowired
    private SysSqlMapper sysSqlMapper;

    @Autowired
    private SysLogMapper sysLogMapper;

    @PostMapping("/getDataBase")
    @Log("获取数据库")
    public JsonResult getAllDataBase(Authentication authentication) throws Exception {
        UserSession userSession = (UserSession) authentication.getPrincipal();

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("userId",userSession.getId());
        SysUserSql sysUserSql = sysUserSqlMapper.selectOne(queryWrapper1);
        String[] split = sysUserSql.getSqlId().split(",");

        JsonResult jsonResult = JsonResult.newInstance();
        Map<String, DataSource> dataSourceMap = DynamicDataSource.getDataSourceMap();
        Map<String, Map> dataSource = new LinkedHashMap<>();

        for (String key : dataSourceMap.keySet()) {
            if (!("master").equals(key)) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("DBName", key);
                SysSql sysSql = sysSqlMapper.selectOne(queryWrapper);
                if (sysSql != null) {
                    if (sysSql.getStatus() != 1) {
                        continue;
                    }
                    if (!Arrays.stream(split).collect(Collectors.toList()).contains(sysSql.getId()+"")) {
                        continue;
                    }
                }

                Connection connection = null;
                try {
                    connection = dataSourceMap.get(key).getConnection();
                    String DBType = connection.getMetaData().getDatabaseProductName().toLowerCase();
                    DBInfo sqlTool = DataSourceRunTime.getRuntime().getSQLTool(DBType);
                    if (sqlTool != null) {
                        List tables = getTables(sqlTool, key);
                        Map<String, List> tableMap = new LinkedHashMap<>(tables.size());
                        for (Object table : tables) {
                            List columns = getColumns(sqlTool, key, table.toString());
                            tableMap.put(table.toString(), columns);
                        }
                        dataSource.put(key + "-" + DBType, tableMap);
                    }
                } finally {
                    if (connection != null) {
                        connection.close();
                    }
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("data", dataSource);
        jsonResult.declareSuccess(HttpStatus.OK, "查询成功", data);
        return jsonResult;
    }

    @Log(RUNCODE)
    @PostMapping("/runCode")
    public JsonResult runCode(@NonNull String dataSourceName, @NonNull String DBType, @NonNull String code, Authentication authentication) {
        JsonResult jsonResult = JsonResult.newInstance();
        DBInfo sqlTool;
        code = code.trim();
        Object result;
        UserSession principal = (UserSession) authentication.getPrincipal();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId",principal.getId());
        SysUserSql sysUserSql = sysUserSqlMapper.selectOne(queryWrapper);
        String[] split = sysUserSql.getSqlId().split(",");
        List<SysSql> sysSqls = sysSqlMapper.selectBatchIds(Arrays.asList(split));
        List<String> sqlName = new ArrayList<>();
        boolean flag = false;
        for (SysSql sysSql : sysSqls) {
            if (sysSql != null) {
                sqlName.add(sysSql.getDBName());
            }
            if (sysSql.getDBName().equals(dataSourceName) && sysSql.getStatus() != 1) {
                flag = true;
            }
        }
        if (!sqlName.contains(dataSourceName)) {
            flag = true;
        }
        if ("master".equals(dataSourceName) || flag) {
            return ErrorResult.jsonError("系统出错，权限不足","data","系统出错，权限不足");
        }
        //执行代码
        try {
            jsonResult.declareSuccess(HttpStatus.OK, "执行成功");
            sqlTool = DataSourceRunTime.getRuntime().getSQLTool(DBType);
            if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(DBType)) {
                if ((code.length() >= 4 && "SHOW".equals(code.substring(0, 4).toUpperCase()))) {
                    List<Object> objects = sqlTool.executeOneColumnQuery("show tables;", dataSourceName);
                    List<Map<String, Object>> list = new LinkedList<>();
                    Map<String, Object> map = new LinkedHashMap<>(objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        map.put("table" + i, objects.get(i));
                    }
                    list.add(map);
                    result = list;
                } else if (code.length() >= 6 && "SELECT".equals(code.substring(0, 6).toUpperCase())) {
                    result = sqlTool.executeQuery(code, dataSourceName);
                } else {
                    result = sqlTool.executeUpdate(code, dataSourceName);
                }
            } else {
                jsonResult.declareFailure(HttpStatus.OK, "执行出错");
                result = "代码或数据库类型为空";
            }
        } catch (SqlException e) {
            jsonResult.declareFailure(HttpStatus.OK, "执行出错");
            result = e.getMessage();
            String msg = "command denied to user";
            if (e.getMessage().contains(msg)) {
                String table = e.getMessage().split("table")[1];
                result = "执行SQL语句时发生错误：Table " + table + " doesn't exist";
            }
        }
        jsonResult.addContent("data", result);
        return jsonResult;
    }


    @PostMapping("/getCodeLog")
    public JsonResult getCodeLog(Authentication authentication, Integer limit) {
        UserSession userSession = (UserSession) authentication.getPrincipal();
        String user = userSession.getUser();
        QueryWrapper<SysLog> sysLogQueryWrapper = new QueryWrapper<>();
        Map<String, String> map = new HashMap<>();
        map.put("username", user);
        map.put("operation", RUNCODE);
        sysLogQueryWrapper.allEq(map);
        sysLogQueryWrapper.orderByDesc("id");
        IPage<SysLog> userPage = new Page<>(0, limit);
        userPage = sysLogMapper.selectPage(userPage, sysLogQueryWrapper);
        List<String> result = new LinkedList<>();
        List<SysLog> sysLogs = userPage.getRecords();
        if (sysLogs.size() > 0) {
            sysLogs.forEach(sysLog -> {
                String params = sysLog.getParams();
                String substring = params.substring(0, params.length() - 1);
                String[] split = substring.split("\",\"");
                result.add(split[2].substring(1, split[2].length() - 1).replaceAll("\\\\", ""));
            });
            return SuccessResult.jsonSuccess("查询成功", "data", result);
        }
        return ErrorResult.jsonError("没有数据");
    }

    /**
     * 通过DBType获取对应的SQLTool，通过SQLTool获取所有的表名
     *
     * @param dataSourceName
     * @return
     */
    public List getTables(DBInfo DBInfo, String dataSourceName) {
        List<Object> tables = DBInfo.getTables(dataSourceName);
        return tables;
    }

    /**
     * 获取字段名称
     *
     * @param DBInfo
     * @param dataSourceName
     * @param tableName
     * @return
     */
    public List getColumns(DBInfo DBInfo, String dataSourceName, String tableName) {
        List<Map<String, Object>> columnInfo = DBInfo.getColumnInfo(tableName, dataSourceName);
        List<Object> columnName = DBInfo.getColumnName(tableName, dataSourceName);
        return columnName;
    }

}
