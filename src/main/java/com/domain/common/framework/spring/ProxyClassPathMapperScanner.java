package com.domain.common.framework.spring;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**

 */
public class ProxyClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

    String[] writePatterns;

    String[] readPatterns;

    private SqlSessionFactory writeSqlSessionFactory;

    private List<SqlSessionFactory> readSqlSessionFactorys;

    private String writeSqlSessionFactoryBeanName;

    private List<String> readSqlSessionFactoryBeanNames;

    public ProxyClassPathMapperScanner(BeanDefinitionRegistry registry) {

        super(registry, false);
    }

    public void registerFilters() {

        boolean acceptAllInterfaces = true;

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

                    return true;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No MyBatis mapper was found in '"
                    + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder
                        .getBeanDefinition();

                if (logger.isDebugEnabled()) {
                    logger.debug("Creating WriteReadMapperProxyFactoryBean with name '"
                            + holder.getBeanName() + "' and '"
                            + definition.getBeanClassName()
                            + "' mapperInterface");
                }

                definition.getPropertyValues().add("mapperInterface",
                        definition.getBeanClassName());
                definition.setBeanClass(WriteReadMapperProxyFactoryBean.class);

                definition.getPropertyValues().add("writePatterns",
                        writePatterns);
                definition.getPropertyValues().add("readPatterns",
                        readPatterns);

                if (StringUtils.hasText(this.writeSqlSessionFactoryBeanName)) {
                    definition.getPropertyValues().add(
                            "writeSqlSessionFactory",
                            new RuntimeBeanReference(
                                    this.writeSqlSessionFactoryBeanName));
                } else if (this.writeSqlSessionFactory != null) {
                    definition.getPropertyValues().add(
                            "writeSqlSessionFactory",
                            this.writeSqlSessionFactory);
                }
                if (readSqlSessionFactoryBeanNames != null) {
                    ManagedList<Object> refList = new ManagedList<Object>();
                    for (String readSqlSessionFactoryBeanName : this.readSqlSessionFactoryBeanNames) {
                        refList.add(new RuntimeBeanReference(readSqlSessionFactoryBeanName));
                    }
                    definition.getPropertyValues().add("readSqlSessionFactorys", refList);

                } else if (this.readSqlSessionFactorys != null) {
                    definition.getPropertyValues().add("readSqlSessionFactorys", this.readSqlSessionFactorys);
                }
            }
        }

        return beanDefinitions;
    }

    /**
     */
    @Override
    protected boolean isCandidateComponent(
            AnnotatedBeanDefinition beanDefinition) {

        return (beanDefinition.getMetadata().isInterface() && beanDefinition
                .getMetadata().isIndependent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName,
                                     BeanDefinition beanDefinition) throws IllegalStateException {

        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping WriteReadMapperProxyFactoryBean with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName()
                    + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }

    public String[] getWritePatterns() {

        return writePatterns;
    }

    public void setWritePatterns(String[] writePatterns) {

        this.writePatterns = writePatterns;
    }

    public String[] getReadPatterns() {

        return readPatterns;
    }

    public void setReadPatterns(String[] readPatterns) {

        this.readPatterns = readPatterns;
    }

    public SqlSessionFactory getWriteSqlSessionFactory() {

        return writeSqlSessionFactory;
    }

    public void setWriteSqlSessionFactory(SqlSessionFactory writeSqlSessionFactory) {

        this.writeSqlSessionFactory = writeSqlSessionFactory;
    }

    public String getWriteSqlSessionFactoryBeanName() {

        return writeSqlSessionFactoryBeanName;
    }

    public void setWriteSqlSessionFactoryBeanName(
            String writeSqlSessionFactoryBeanName) {

        this.writeSqlSessionFactoryBeanName = writeSqlSessionFactoryBeanName;
    }

    public List<SqlSessionFactory> getReadSqlSessionFactorys() {

        return readSqlSessionFactorys;
    }

    public void setReadSqlSessionFactorys(
            List<SqlSessionFactory> readSqlSessionFactorys) {

        this.readSqlSessionFactorys = readSqlSessionFactorys;
    }

    public void setReadSqlSessionFactoryBeanNames(
            List<String> readSqlSessionFactoryBeanNames) {

        this.readSqlSessionFactoryBeanNames = readSqlSessionFactoryBeanNames;
    }

    public List<String> getReadSqlSessionFactoryBeanNames() {

        return readSqlSessionFactoryBeanNames;
    }

}
