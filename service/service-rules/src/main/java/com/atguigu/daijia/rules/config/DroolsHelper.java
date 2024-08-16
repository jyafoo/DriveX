package com.atguigu.daijia.rules.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * DroolsHelper类提供加载Drools规则并返回一个规则运行时会话（KieSession）的方法
 */
public class DroolsHelper {

    /**
     * 根据提供的DRL字符串加载规则并返回一个KieSession对象
     *
     * @param drlStr 规则的DRL字符串表示
     * @return 一个KieSession对象，用于执行加载的规则
     * @throws RuntimeException 如果规则编译过程中出现错误
     */
    public static KieSession loadForRule(String drlStr) {
        // 获取KieServices实例
        KieServices kieServices = KieServices.Factory.get();

        // 创建一个新的KieFileSystem实例
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        // 将DRL字符串写入到KieFileSystem中的一个文件中
        kieFileSystem.write("src/main/resources/rules/" + drlStr.hashCode() + ".drl", drlStr);

        // 将KieFileSystem加入到KieBuilder
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        // 编译此时的builder中所有的规则
        kieBuilder.buildAll();
        // 检查编译结果是否有错误消息
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }

        // 创建一个新的KieContainer实例
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        // 从KieContainer中创建一个新的KieSession
        return kieContainer.newKieSession();
    }
}