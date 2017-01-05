
### 更新日志

#### 1.4 (2015-06-05)
* 优化后台部分逻辑
* 加入邮件服务
* 移出kindeditor相关文件

#### 1.4.2 (2015-09-20)
* 加入新的评论邮件通知选项
* 多说绑定页面，不用手动输入
* IP 黑名单
* 文件 html,css,js 支持在线编辑

#### 1.4.3 (2015-12-20)
* 优化对接多说相关代码（Map -> Bean）
* 更改 ehcache 默认配置（ehcache内存申请过多，内存较小的主机被杀JVM进程）

#### 1.4.4 (2016-1-16)
* JFinal 升级至 2.1
* 添加后台检索文章功能
* 优化编辑文章代码
* 加入主题预览
* 修复黑名单无法正确的拦截所有请求的问题

#### 1.5 (2016-4-12)

* 新特
    * 构建全新的插件模式
    * 多说，七牛，备份数据库，邮件服务改为插件方式实现（需要进行下载才可以使用）
    * 记录管理界面的侧边栏状态，可设置默认管理界面的主题
    * 移除大量并未使用到的静态文件 （war包体积缩小到7M左右）
    * 可在zrlog主题中心下载主题，插件
    
* 优化
    * 使用Maven管理jar文件,JFinal 升级到 2.2
    * 优化主题管理页面
    * 管理员登录忽略大小写
    * 优化文章管理页面的弹出框，弃用Zdialog，使用eModal
    * log4j 日志文件分天存储，方便查找
    * 开启静态化后，只是储存 /post/* 的文章页
    * 其他css问题

* 修复
    * 修复 v1.4.4 搜索乱码
    * 修复 editor.md 与 bootstrap 的样式问题 (升级 editor.md 到v1.5)
    * 修复 editor.md z-index 问题
    * 修复在编辑过程中尚未触发存草稿导致就尝试预览 `NullPointerException`

#### 1.6 (2016-12-13)

#### 新特
* 自动更新功能
* 博客搜索结果高亮检索的关键字
* 七牛插件支持全站静态资源托管
* 添加本地主题上传
* 主题数据可以存放到数据库（及主题可以配置）
* 全新的后台管理界面
* 管理博客时支持按时间，浏览量等信息进行排序
* 提供多语言
* 添加mysql数据版本信息在管理后台主页

#### 优化
* 重构管理相关代码，实现了接口数据与模板数据渲染的控制器代码分离
* 简化分页数据的遍历，优化模板数据，更加轻松的编写主题
* 独立后台页面的javascript部分
* 优化安装引导界面
* 部分图标的优化
* 优化默认主题的一些样式
* 移除`Ehcache`，改用内存的方式存放全局数据（war体积减小到6M）

#### 修复
* 部分平台插件默认编码问题
* 程序停止后，对应的插件服务无法停止的问题
* 修复静态化开启后部分平台乱码问题

#### 1.6.1 (2017-01-05)

#### 新特
* 更新管理界面添加手动检测按钮
* 增强了主题开发（引入dev.jsp可以快速浏览存放在request域的数据）

#### 优化
* 在代码添加了大量的注释
* 下载插件核心服务时关闭缓存
* 优化更新流程
* 管理主面板添加系统编码信息

#### 修复
* 导航条数据无法更新
* 默认主题无法上传图片
* 关闭更新功能后，无法正常启动的bug（感谢 @说好不上学 发现的bug）