package com.lrh.common.nacos;

import com.alibaba.cloud.commons.io.FileUtils;
import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import com.alibaba.cloud.nacos.NacosServiceAutoConfiguration;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lrh.common.constant.PathConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.common.nacos
 * @ClassName: NacosConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/18 16:49
 */
@Slf4j
public class NacosBeanAutowire implements BeanPostProcessor, EnvironmentAware {

    private ConfigurableEnvironment environment;

    private final String path;


    public NacosBeanAutowire() {
        this.path = PathConstant.NACOS_CONFIG_PATH;
    }

    public NacosBeanAutowire(String path) {
        this.path = path;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof NacosConfigAutoConfiguration) {
            try {
                File file = new File(path);
                log.info("[NacosConfig] NacosConfigAutoConfiguration 找到数据库配置json文件: {}", file.getAbsolutePath());
                String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                JSONObject configJson = JSON.parseObject(config);
                environment.getSystemProperties().put("spring.cloud.nacos.config.server-addr", configJson.getString("server-addr"));
                environment.getSystemProperties().put("spring.cloud.nacos.config.username", configJson.getString("username"));
                environment.getSystemProperties().put("spring.cloud.nacos.config.password", configJson.getString("password"));
            } catch (IOException e) {
                log.error("[NacosConfig]配置NacosConfigAutoConfiguration失败,原因为: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (bean instanceof NacosServiceAutoConfiguration) {
            try {
                File file = new File(path);
                log.info("[NacosConfig] NacosServiceAutoConfiguration 找到数据库配置json文件: {}", file.getAbsolutePath());
                String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                JSONObject configJson = JSON.parseObject(config);
                environment.getSystemProperties().put("spring.cloud.nacos.discovery.server-addr", configJson.getString("server-addr"));
                environment.getSystemProperties().put("spring.cloud.nacos.discovery.username", configJson.getString("username"));
                environment.getSystemProperties().put("spring.cloud.nacos.discovery.password", configJson.getString("password"));
            } catch (IOException e) {
                log.error("[NacosConfig]配置NacosServiceAutoConfiguration失败,原因为: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}