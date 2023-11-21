### 技术选型

#### 前端

- [Vue 2.6.10](https://cn.vuejs.org/),[Vuex](https://vuex.vuejs.org/zh/),[Vue Router](https://router.vuejs.org/zh/)
- [Axios](https://github.com/axios/axios)
- [vue-apexcharts](https://apexcharts.com/vue-chart-demos/line-charts/)
- [ant-design-vue](https://vuecomponent.github.io/ant-design-vue/docs/vue/introduce-cn/)
- [webpack](https://www.webpackjs.com/),[yarn](https://yarnpkg.com/zh-Hans/)

#### 后端

- [Spring Boot 2.1.7](http://spring.io/projects/spring-boot/)
- [Mybatis-Plus](https://mp.baomidou.com/guide/)
- [MySQL 5.7](https://dev.mysql.com/downloads/mysql/5.7.html#downloads),[Hikari](https://brettwooldridge.github.io/HikariCP/),[Redis](https://redis.io/)
- [Shiro](http://shiro.apache.org/),[JWT](https://jwt.io/)



![](backend/picture/01.png)
![](backend/picture/02.png)
![](backend/picture/03.png)
![](backend/picture/04.png)
![](backend/picture/05.png)
![](backend/picture/06.png)
![](backend/picture/07.png)
![](backend/picture/08.png)
![](backend/picture/09.png)
![](backend/picture/10.png)
![](backend/picture/11.png)
![](backend/picture/12.png)
![](backend/picture/13.png)
![](backend/picture/14.png)
![](backend/picture/15.png)
![](backend/picture/16.png)
![](backend/picture/17.png)

账号密码：

账号 | 密码| 权限
---|---|---
scott | 1234qwer | 注册账号，拥有查看，新增导出等权限，但不能新增用户
jack | 1234qwer |普通账户，仅拥有所有页面查看权限
mrbird | 1234qwer |超级管理员，拥有所有增删改查权限

### 使用教程

#### 后端

1. IDEA 或者 Eclipse安装lombok插件

2. 新建MySQL（版本5.7.x）数据库，导入[SQL](https://github.com/wuyouzhuguli/FEBS-Vue/blob/master/sql/febs.sql)文件

3. 导入[backend项目](https://github.com/wuyouzhuguli/FEBS-Vue/tree/master/backend)

4. 修改数据库配置，redis配置，等待Maven下载依赖

5. 启动backend项目

#### 前端

1. 安装node.js

2. 切换到frontend文件夹下
```
# 安装yarn
npm install -g yarn

# 下载依赖
yarn install

# 启动
yarn start
```

### 功能模块
```
├─系统管理
│  ├─用户管理
│  ├─角色管理
│  ├─菜单管理
│  ├─部门管理
│  └─字典管理
├─系统监控
│  ├─在线用户
│  ├─系统日志
│  ├─Redis监控
│  ├─请求追踪
│  └─系统信息
│     ├─JVM信息
│     ├─服务器信息
│     └─Tomcat信息
│─任务调度
│  ├─定时任务
│  └─调度日志
│─网络资源
│  ├─天气查询
│  ├─影视资讯
│  │  ├─即将上映
│  │  └─正在热映
│  └─每日一文
└─其他模块
   └─导入导出

```
