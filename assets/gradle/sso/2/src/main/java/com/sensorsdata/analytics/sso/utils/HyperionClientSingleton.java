package com.sensorsdata.analytics.sso.utils;

import com.sensorsdata.armada.hyperionclient.HyperionClientFactory;
import com.sensorsdata.armada.hyperionclient.client.ConfigManager;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author zhanzhenghao
 * @version 1.0.0
 * @since 2025/01/08 11:02
 */
@Slf4j
public class HyperionClientSingleton {

    private static volatile HyperionClientSingleton singleton;
    private final ConfigManager configManager;

    private HyperionClientSingleton() {
        configManager = HyperionClientFactory.newConfigManager();
    }

    public static HyperionClientSingleton me() {
        if (singleton == null) {
            synchronized (HyperionClientSingleton.class) {
                if (singleton == null) {
                    singleton = new HyperionClientSingleton();
                    // 可在此处通过 hook 来释放链接资源，也可在业务方自己定义的 hook 处调用 close 释放链接资源
                    Runtime.getRuntime()
                            .addShutdownHook(new Thread(() -> {
                                singleton.close();
                            }));
                }
            }
        }
        return singleton;
    }

    public void close() {
        try {
            singleton.configManager.close();
            singleton = null;
        } catch (Exception e) {
            log.error("failed to release resource.", e);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
