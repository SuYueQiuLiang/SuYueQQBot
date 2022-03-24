# SuYueQQBot

*自用的QQBot，采用模块化设计，动态插件加载，使用开源的mirai核心*

主工程为Bot主体，其他moudle均为各类插件。

可以按需求编译插件后添加进入./mods/文件中，会自动从jar根目录下识别pluginInfo.json加载mods文件夹下的各类mod。

主体自带一套多Bot管理，权限管理，各插件均沿用主体携带的权限管理
