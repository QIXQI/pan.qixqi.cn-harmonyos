## 基于鸿蒙的网盘前端实现

### [TODO] LoginAbilitySlice 跳转到 register页面再回来时，登录按钮点击没有反应
### [TODO] RetrievePassAbilitySlice authCodeText 先输入 "1111"，点击 retrievePassBtn，提示 ”验证码无效“，此时焦点仍在 authCodeText，继续输入 "111111"后, ”验证码无效“仍不隐藏 
### [TODO] 如何判断令牌是否失效，以及失效处理策略
### [TODO] 标题栏细化
### [TODO] SimpleFolderLink 应该还包括 createTime
### [TODO] 文件夹列表、文件列表都有滚动回滚效果，冲突了
### [TODO] 后端 file_share 表应包含 share_name 字段
### [TODO] FileDownloadAbilitySlice 与 FileUploadAbilitySlice 通过底部导航栏跳转时，底部子项有时候图片无法加载，有时候子项文字一直显示蓝色，离谱，是因为 AbilitySlice 吗？
### [TODO] 文件上传时，通过真实文件路径访问没有权限，只能通过字节数组，但对大文件不友好
### [TODO] FileSystemAbilitySlice 中 foldersContainer 与 filesContainer 合并
### [TODO] FileSystemAbilitySlice 根据md5判断文件是否存在时，如果文件不存在，提交md5值时出现 网关504超时异常，此时没有响应，但fileMd5表添加记录成功，但文件没有上传，下次再次上传报错
### [TODO] FastDFS 客户端偶尔异常：上传文件Exception，异常信息：Attempt to invoke virtual method 'java.lang.String java.net.InetAddress.getHostAddress()' on a null object reference
### [TODO] ListContainer 显示不全，最后几个不显示