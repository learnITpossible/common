package com.domain.common.framework.spring;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class WriteReadMapperProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    String[] writePatterns;

    String[] readPatterns;

    private SqlSessionFactory writeSqlSessionFactory;

    private List<SqlSessionFactory> readSqlSessionFactorys;

    private SqlSession writeSqlSession;

    private List<SqlSession> readSqlSessions = new ArrayList<SqlSession>();

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {

        WriteReadMapperProxy<T> writeReadMapperProxy = new WriteReadMapperProxy<T>(mapperInterface, writePatterns, readPatterns, writeSqlSession, readSqlSessions);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, writeReadMapperProxy);
    }

    @Override
    public Class<T> getObjectType() {

        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {

        return true;
    }

    public Class<T> getMapperInterface() {

        return mapperInterface;
    }

    public void setMapperInterface(Class<T> mapperInterface) {

        this.mapperInterface = mapperInterface;
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

        this.writeSqlSession = new SqlSessionTemplate(writeSqlSessionFactory);
        this.writeSqlSessionFactory = writeSqlSessionFactory;
    }

    public List<SqlSessionFactory> getReadSqlSessionFactorys() {

        return readSqlSessionFactorys;
    }

    public void setReadSqlSessionFactorys(List<SqlSessionFactory> readSqlSessionFactorys) {

        for (SqlSessionFactory readSqlSessionFactory : readSqlSessionFactorys) {
            this.readSqlSessions.add(new SqlSessionTemplate(readSqlSessionFactory));
        }
        this.readSqlSessionFactorys = readSqlSessionFactorys;

    }

}
