package com.domain.common.framework.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

public class PrefixBeanNameGenerator extends AnnotationBeanNameGenerator {

    String prefix;

    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

        String beanName = null;
        if (!StringUtils.isEmpty(prefix)) {
            beanName = prefix + "_" + super.generateBeanName(definition, registry);
        } else {
            beanName = super.generateBeanName(definition, registry);
        }
        return beanName;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

}
