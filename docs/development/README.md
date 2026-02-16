# 开发文档

## 项目结构
```
com.tingyu/
├── admin/                     # 管理员模块
├── command/                   # 全局命令入口
├── common/                    # 通用基础设施
│   ├── abstracts/             # 抽象基类
│   └── interfaces/            # 通用接口契约
├── item/                      # 物品系统
├── player/                    # 玩家系统核心模块
│   ├── attribute/             # 属性系统
│   ├── bukkit/                # Bukkit 交互层（基础设施）
│   ├── data/                  # 数据存储
│   ├── job/                   # 职业领域
│   ├── race/                  # 种族领域
│   ├── PlayerManager.kt       # 玩家管理器（单例协调者）
│   └── PlayerSession.kt       # 玩家会话（运行时上下文）
├── forge/                     # 锻造模块
├── ui/                        # 用户界面
└── Main.kt                    # 插件主入口
```

## 相关文档
* [玩家系统架构设计文档](/docs/development/玩家系统架构设计文档.md)