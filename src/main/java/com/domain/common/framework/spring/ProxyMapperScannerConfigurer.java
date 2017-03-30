package com.domain.common.framework.spring;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.notNull;

public class ProxyMapperScannerConfigurer implements
        BeanDefinitionRegistryPostProcessor, InitializingBean,
        ApplicationContextAware, BeanNameAware {

    private String basePackage;

    private ApplicationContext applicationContext;

    private String beanName;

    private BeanNameGenerator nameGenerator;

    private boolean processPropertyPlaceHolders;

    private SqlSessionFactory writeSqlSessionFactory;

    private SqlSessionFactory readSqlSessionFactory;

    private String writeSqlSessionFactoryBeanName;

    private String readSqlSessionFactoryBeanName;

    /**
     * 支持多个从库，用逗号隔开
     */
    private String readSqlSessionFactoryBeanNames;

    String writePatterns;

    String readPatterns;

    public void setBasePackage(String basePackage) {

        this.basePackage = basePackage;
    }

    /**
     */
    public void setApplicationContext(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    /**
     */
    public void setBeanName(String name) {

        this.beanName = name;
    }

    /**
     * Gets beanNameGenerator to be used while running the scanner.
     * @return the beanNameGenerator BeanNameGenerator that has been configured
     * @since 1.2.0
     */
    public BeanNameGenerator getNameGenerator() {

        return nameGenerator;
    }

    /**
     */
    public void setNameGenerator(BeanNameGenerator nameGenerator) {

        this.nameGenerator = nameGenerator;
    }

    /**
     */
    public void afterPropertiesSet() throws Exception {

        notNull(this.basePackage, "Property 'basePackage' is required");
        notNull(this.writePatterns, "Property 'writePatterns' is required");
        notNull(this.readPatterns, "Property 'readPatterns' is required");
    }

    /**
     */
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) {

    }

    /**
     */
    public void postProcessBeanDefinitionRegistry(
            BeanDefinitionRegistry registry) throws BeansException {

        if (this.processPropertyPlaceHolders) {
            processPropertyPlaceHolders();
        }

        ProxyClassPathMapperScanner scanner = new ProxyClassPathMapperScanner(
                registry);
        scanner.setResourceLoader(this.applicationContext);
        scanner.setBeanNameGenerator(this.nameGenerator);
        scanner.setWriteSqlSessionFactoryBeanName(writeSqlSessionFactoryBeanName);
        scanner.setWriteSqlSessionFactory(writeSqlSessionFactory);
        scanner.setWritePatterns(StringUtils.tokenizeToStringArray(this.writePatterns,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        scanner.setReadPatterns(StringUtils.tokenizeToStringArray(this.readPatterns,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        List<String> nameList = new ArrayList<String>();
        if (this.readSqlSessionFactoryBeanNames != null) {
            nameList.addAll(Arrays.asList(StringUtils.tokenizeToStringArray(this.readSqlSessionFactoryBeanNames,
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS)));
        }
        if (this.readSqlSessionFactoryBeanName != null) {
            nameList.add(this.readSqlSessionFactoryBeanName);
        }
        scanner.setReadSqlSessionFactoryBeanNames(nameList);

        if (readSqlSessionFactory != null) {
            List<SqlSessionFactory> readSqlSessionFactorys = new ArrayList<SqlSessionFactory>();
            readSqlSessionFactorys.add(readSqlSessionFactory);
            scanner.setReadSqlSessionFactorys(readSqlSessionFactorys);
        }

        scanner.registerFilters();

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    public String getWritePatterns() {

        return writePatterns;
    }

    public void setWritePatterns(String writePatterns) {

        this.writePatterns = writePatterns;
    }

    public String getReadPatterns() {

        return readPatterns;
    }

    public void setReadPatterns(String readPatterns) {

        this.readPatterns = readPatterns;
    }

    public SqlSessionFactory getWriteSqlSessionFactory() {

        return writeSqlSessionFactory;
    }

    public void setWriteSqlSessionFactory(SqlSessionFactory writeSqlSessionFactory) {

        this.writeSqlSessionFactory = writeSqlSessionFactory;
    }

    public SqlSessionFactory getReadSqlSessionFactory() {

        return readSqlSessionFactory;
    }

    public void setReadSqlSessionFactory(SqlSessionFactory readSqlSessionFactory) {

        this.readSqlSessionFactory = readSqlSessionFactory;
    }

    private void processPropertyPlaceHolders() {

        Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);

        if (!prcs.isEmpty() && applicationContext instanceof GenericApplicationContext) {
            BeanDefinition mapperScannerBean = ((GenericApplicationContext) applicationContext)
                    .getBeanFactory().getBeanDefinition(beanName);

            DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
            factory.registerBeanDefinition(beanName, mapperScannerBean);

            for (PropertyResourceConfigurer prc : prcs.values()) {
                prc.postProcessBeanFactory(factory);
            }

            PropertyValues values = mapperScannerBean.getPropertyValues();

            this.basePackage = updatePropertyValue("basePackage", values);
        }
    }

    private String updatePropertyValue(String propertyName, PropertyValues values) {

        PropertyValue property = values.getPropertyValue(propertyName);

        if (property == null) {
            return null;
        }

        Object value = property.getValue();

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value.toString();
        } else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        } else {
            return null;
        }
    }

    public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {

        this.processPropertyPlaceHolders = processPropertyPlaceHolders;
    }

    public String getWriteSqlSessionFactoryBeanName() {

        return writeSqlSessionFactoryBeanName;
    }

    public void setWriteSqlSessionFactoryBeanName(
            String writeSqlSessionFactoryBeanName) {

        this.writeSqlSessionFactoryBeanName = writeSqlSessionFactoryBeanName;
    }

    public String getReadSqlSessionFactoryBeanName() {

        return readSqlSessionFactoryBeanName;
    }

    public void setReadSqlSessionFactoryBeanName(
            String readSqlSessionFactoryBeanName) {

        this.readSqlSessionFactoryBeanName = readSqlSessionFactoryBeanName;
    }

    public String getReadSqlSessionFactoryBeanNames() {

        return readSqlSessionFactoryBeanNames;
    }

    public void setReadSqlSessionFactoryBeanNames(
            String readSqlSessionFactoryBeanNames) {

        this.readSqlSessionFactoryBeanNames = readSqlSessionFactoryBeanNames;
    }

}
