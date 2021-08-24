# sqlRun
## 简介
    基于Springboot和dynamic-datasource-boot-starter多数据源的SQL代码执行器
    
    1、SpringBoot 2.4.0
    
    2、dynamic-datasource-boot-starter 1.2 
        <dependency>
            <groupId>rainbow.kuzwlu</groupId>
            <artifactId>dynamic-datasource-boot-starter</artifactId>
            <version>1.2</version>
        </dependency>
    
    详情见项目：https://github.com/kuzwlu/dynamic-datasource-boot-starter
    [一个基于SpringBoot的Mybatis-Plus多数据源依赖包]

    3、redis
    4、前后端分离（Nginx）
    5、Mysql
    6、Authentication——Token认证
    7、AOP切面，使用@Log注解，将操作记录到数据库中
    8、统一API风格
    9、CORS跨域
    10、侧栏菜单管理（分角色权限显示）
    11、角色管理
    12、权限管理
    13、日志记录
    14、数据源管理（支持在线添加，分角色显示）
    15、使用开源编辑器CodeMirror,代码支持补全,颜色,高度,风格
    16、执行后即可显示结果，保留用户最后10条执行代码


## 使用方法

## 配合前端项目使用 项目地址：https://github.com/kuzwlu/sqlRun-HTML
    sqlRun项目提供后端接口，sqlRun-HTML提供前端显示

### 1、application.yml详解 (配置数据源,log目录,Mapper包名)
    logging:
        file:
        path: /Users/kuzwlu/Downloads

    mybatis-plus:
        configuration:
        # 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射
        map-underscore-to-camel-case: false    

    rainbow:
      kuzwlu:
        #mapper包名
        mapper-package: rainbow.kuzwlu.web.mapper
        mapper-xml: classpath*:mapper/*.xml
        #设置数据库(动态数据源)
        # 一、无Service层（无事物管理）
        # 1、需要mapper接口上    标明@DataSource注解
        # 二、有Service层（有事物管理，可在Serviec层使用@Transactional注解）
        # 1、需要在service接口或者service接口的实现类    标明@DataSource注解
        datasource:
        #四种状态  一、数据库中读取副数据源 二、配置文件读取数据源 三、两者一起 四、没有副数据源(默认  NONE)
        #在DataSourceType枚举类下
        #DB PROPERTY TOGETHER NONE
        type: TOGETHER

      #如果不设置则不设置事务
      transaction:
        #切面
        aop-point-out: execution(* ${rainbow.kuzwlu.mapper-package}..*.*(..))
        #方法名规则限制，以下会加入事务管理当中
        require: add*,save*,create*,insert*,submit*,del*,remove*,update*,exec*,set*
        #对于查询方法，根据实际情况添加事务管理 可能存在查询多个数据时，已查询出来的数据刚好被改变的情况
        require-readonly: get*,select*,query*,find*,list*,count*,is*
      #主
      master:
        #副数据源---表 如果type为NONE可以不填写或者删除datasource-table等五个配置
        datasource-table: t_sys_sql
        #数据库自定义字段名，用来判断类型，数据库名称，状态
        datasource-table-type: type
        datasource-table-DBName: DBName
        datasource-table-status: status
        #status启用状态
        datasource-table-status-enabled: 1
        ###########################################################
        # mysql默认数据库的配置
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sql?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
        username: root
        password: root
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL

      #副---未声明则数据库的数据源数据不生效，如果type为NONE可以不填写subsidiary
      subsidiary: sqls,postgresql,test2
      sqls:
        # mysql数据库的配置
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sqls?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
        username: sqls
        password: sqls
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
      postgresql:
        # postgresql数据库的配置
        driverClassName: org.postgresql.Driver
        url: jdbc:postgresql://localhost:5432/postgres
        username: postgres
        password: root
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL

### 2、下拉后可直接打成jar包运行  
    项目端口为1000
    管理员后台账号：admin   密码：123456
        
## 可能出现问题：
    1、dynamic-datasource-boot-starter依赖包找不到，可至
    https://github.com/kuzwlu/dynamic-datasource-boot-starter下载成品依赖包
    放入你的maven仓库，路径为:<你的maven仓库路径>/rainbow/kuzwlu/dynamic-datasource-boot-starter/1.2/
    
    2、注意数据源是否和表、配置文件绑定，出现问题可删除t_sys_user除admin以外的用户

## 效果图：
### 登录：
![](https://i.loli.net/2021/08/09/d5raEWRyAmzGMDp.png)
### 主页：
![](https://i.loli.net/2021/08/09/TVO7HX9cRKbGoLZ.png)
### 用户列表
![](https://i.loli.net/2021/08/09/YN9RxmMHB4rZSXw.png)
### 添加用户
![](https://i.loli.net/2021/08/09/UC83maKR6YJM4ri.png)
### 数据源列表
![](https://i.loli.net/2021/08/09/VMAmXyhx9UprfZt.png)
### 添加数据源
![](https://i.loli.net/2021/08/09/iqL8CfvHUMpmJKe.png)
### SQL代码执行器
![](https://i.loli.net/2021/08/09/pmRbDgoGIWU7Y6y.png)
### 代码补全
![](https://i.loli.net/2021/08/09/mk9W2zjMgcGHhl1.png)
### 执行结果
![](https://i.loli.net/2021/08/09/iH5W6NFvmtMfdLA.png)
### 执行出错--提示
![](https://i.loli.net/2021/08/09/81vLKarWosXAfjg.png)
### 记录log
![](https://i.loli.net/2021/08/09/LvIH2E98XsogWxA.png)
![](https://i.loli.net/2021/08/09/ZO2lEBdfr3bWFo5.png)
