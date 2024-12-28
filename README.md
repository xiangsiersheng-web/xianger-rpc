# xianger-rpc

## 介绍

xianger-rpc 是一个rpc框架，是对rpc的学习和实践。

目前参考了：

- guide-rpc：https://github.com/Snailclimb/guide-rpc-framework
- wxy-rpc：https://github.com/viego1999/wxy-rpc

已经完成了初始分支，后续将会继续完善。

- 已完成：
    - 框架设计
    - zk 注册、发现
    - socket 通信
    - client端jdk/cglib代理对象生成
    - 基于spring进行扫描注册、代理生成
    - 简单的测试用例-example

- 待完成：
    - 消息头设计
    - 负载均衡：一致性hash实现
    - 序列化算法实现
    - netty、http通信实现
    - nacos注册实现
    - 失败重试、超时重试？
    - 测试TPS？