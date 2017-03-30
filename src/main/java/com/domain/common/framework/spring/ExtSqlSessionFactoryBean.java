package com.domain.common.framework.spring;

import com.domain.common.utils.ClassUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class ExtSqlSessionFactoryBean extends SqlSessionFactoryBean {

    private static final Logger logger = LoggerFactory.getLogger(ExtSqlSessionFactoryBean.class);

    String extPackages;

    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {

        SqlSessionFactory sqlSessionFactory = super.buildSqlSessionFactory();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        logger.debug("starting extends ibatis mapper");
        String[] subPackages = StringUtils.tokenizeToStringArray(this.extPackages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        if (subPackages != null) {
            for (String subPkg : subPackages) {
                List<Class<?>> clsList = ClassUtil.getClasses(subPkg);
                for (Class<?> cls : clsList) {
                    buildExtMapper(cls, configuration);
                    logger.debug("buildExtMapper: " + cls.getName());
                }
            }
        }
        //		if(logger.isDebugEnabled()){
        //			Collection<String> list=configuration.getMappedStatementNames();
        //			for(String str: list){
        //				logger.debug(str);
        //			}
        //		}

        return sqlSessionFactory;
    }

    public void buildExtMapper(Class<?> cls, Configuration configuration) {

        Map<String, Set<String>> map = getSupperMapperMethods(cls);
        if (map.size() > 0) {
            for (String name : map.keySet()) {
                Set<String> ms = map.get(name);
                for (String m : ms) {
                    String parentId = name + "." + m;
                    String extId = cls.getName() + "." + m;
                    extMappedStatement(parentId, extId, configuration);
                    String parentSelectKeyId = parentId + "!selectKey";
                    if (configuration.hasStatement(parentSelectKeyId)) {
                        String extSelectKeyId = extId + "!selectKey";
                        extMappedStatement(parentSelectKeyId, extSelectKeyId, configuration);
                    }
                }
            }
        }
    }

    void extMappedStatement(String parentId, String extId, Configuration configuration) {

        MappedStatement mappedStatement = null;

        try {
            if (configuration.hasStatement(parentId)) {
                mappedStatement = configuration.getMappedStatement(parentId);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return;
        }
        if (mappedStatement != null) {
            if (!configuration.hasStatement(extId)) {
                MappedStatement mappedStatementExt = buildNewStatement(mappedStatement, extId);
                configuration.addMappedStatement(mappedStatementExt);
            }
        }
    }

    private MappedStatement buildNewStatement(MappedStatement mappedStatement, String id) {

        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(mappedStatement.getConfiguration(), id, mappedStatement.getSqlSource(), mappedStatement.getSqlCommandType());
        statementBuilder.resource(mappedStatement.getResource());
        statementBuilder.fetchSize(mappedStatement.getFetchSize());
        statementBuilder.statementType(mappedStatement.getStatementType());
        statementBuilder.keyGenerator(mappedStatement.getKeyGenerator());
        statementBuilder.keyProperty(joinArr(mappedStatement.getKeyProperties()));
        statementBuilder.keyColumn(joinArr(mappedStatement.getKeyColumns()));
        statementBuilder.databaseId(mappedStatement.getDatabaseId());
        statementBuilder.timeout(mappedStatement.getTimeout());
        statementBuilder.parameterMap(mappedStatement.getParameterMap());
        statementBuilder.resultMaps(mappedStatement.getResultMaps());
        statementBuilder.cache(mappedStatement.getCache());
        statementBuilder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
        statementBuilder.useCache(mappedStatement.isUseCache());
        MappedStatement statement = statementBuilder.build();

        return statement;
    }

    String joinArr(String[] strArr) {

        if (strArr == null || strArr.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]);
            if (i < strArr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public Map<String, Set<String>> getSupperMapperMethods(Class<?> mapperCls) {

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        Class<?>[] its = mapperCls.getInterfaces();
        for (Class<?> cls : its) {

            Set<String> list = new HashSet<String>();
            Method[] ms = cls.getMethods();
            for (Method m : ms) {
                if (!m.getDeclaringClass().equals(Object.class)) {
                    list.add(m.getName());
                }
            }
            map.put(cls.getName(), list);
        }
        return map;
    }

    public String getExtPackages() {

        return extPackages;
    }

    public void setExtPackages(String extPackages) {

        this.extPackages = extPackages;
    }

}
