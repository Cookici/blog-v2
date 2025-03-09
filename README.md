# Blog-v2

Blog系统后端，一个简单的博客系统后端。有文章管理，好友功能，以及评论功能

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]


<!-- PROJECT LOGO -->
<br />

<p align="center">
  <a href="https://github.com/Cookici/blog-v2/">
    <img src="./img/logo.jpg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Blog</h3>
  <p align="center">
    Blog-v2后端系统
    <br />
    <a href="https://github.com/Cookici/blog-v2"><strong>Blog后端项目文档 »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Cookici/blog-v2">查看Demo</a>
    ·
    <a href="https://github.com/Cookici/blog-v2/issues">报告Bug</a>
    ·
    <a href="https://github.com/Cookici/blog-v2/issues">提出新特性</a>
  </p>
</p>

本篇README.md面向开发者


<br /><br />

## 目录

- [上手指南](#上手指南)
    - [开发前的配置要求](#开发前的配置要求)
    - [主要依赖](#主要依赖)
- [文件目录说明](#文件目录说明)
- [开发的架构](#开发的架构)
- [部署](#部署)
- [使用到的框架](#使用到的框架)
- [贡献者](#贡献者)
    - [如何参与开源项目](#如何参与开源项目)
- [版本控制](#版本控制)
- [作者](#作者)
- [项目参考以及鸣谢](#项目参考以及鸣谢)
- [Blog-Vue3前端项目](#Blog-Vue3前端项目)
- [项目展示](#项目展示)


<br /><br />

### 上手指南
    需要一定的硬件配置以及编程基础



###### 开发前的环境配置
1. JAVA JDK8（运行环境）
2. IDEA（开发环境）
3. Maven 3.9.2（依赖管理）


###### **主要依赖**
1. SpringBoot 2.7.13
2. SpringCloud 2021.0.5
3. SpringCloudAlibaba 2021.0.6.1


<br /><br />

### 文件目录说明

```
Blog
├── blog-article
├── blog-common
├── blog-gateway
├── blog-identity
├── blog-message-netty
├── blog-oss
├── blog-user
├── sql
├── README.md
```

1. blog-article文章相关功能
2. blog-message-netty聊天信息相关功能
3. blog-common各种实体类和工具
4. blog-identify权限系统
5. blog-oss阿里云OSS实现图片上传和三方组件
6. blog-user用户管理的相关功能
7. sql包含项目的所有表结构


<br /><br />

### 开发的架构
项目主要使用SpringCloud微服务架构，每个微服务使用MVC架构

<br /><br />


### 使用到的框架
- SpringBoot 2.6.3
- SpringCloud 2021.0.1
- SpringCloudAlibaba 2021.0.1.0  具体可见:<a href="https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E#%E7%BB%84%E4%BB%B6%E7%89%88%E6%9C%AC%E5%85%B3%E7%B3%BB">版本对应</a>
- MySQL 5.7.13
- Redis 7.0.0
- RocketMQ 5.2.0
- Elasticsearch 7.15.2
- MyBatis-Plus 3.5.9

<br /><br />

### 贡献者
1. RanRan
2. JQ


<br /><br />

#### 如何参与开源项目
贡献使开源社区成为一个学习、激励和创造的绝佳场所。你所作的任何贡献都是**非常感谢**的。


<br /><br />

### 版本控制
该项目使用Git进行版本管理。您可以在repository参看当前可用版本。

<br /><br />


### 作者
✉️632832232@qq.com
🐧632832232


<br /><br />

### 项目参考以及鸣谢
- 本项目中使用到的各种开源框架的开发者们

<br /><br />


<br /><br />

### Blog-Vue3前端项目
<a href="https://github.com/Cookici/blog-v2-vue/tree/main">Blog-Vue3</a>


<br /><br />

### 项目展示


<!-- links -->

[your-project-path]: https://github.com/Cookici/blog-v2/tree/main

[contributors-shield]: https://img.shields.io/github/contributors/Cookici/blog-v2.svg?style=flat-square

[contributors-url]: https://github.com/Cookici/blog-v2/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/Cookici/blog-v2.svg?style=flat-square

[forks-url]: https://github.com/Cookici/blog-v2/network/members

[stars-shield]: https://img.shields.io/github/stars/Cookici/blog-v2.svg?style=flat-square

[stars-url]: https://github.com/Cookici/blog-v2/stargazers

[issues-shield]: https://img.shields.io/github/issues/Cookici/blog-v2.svg?style=flat-square

[issues-url]: https://img.shields.io/github/issues/Cookici/blog-v2.svg

[license-shield]: https://img.shields.io/github/license/Cookici/blog-v2.svg?style=flat-square