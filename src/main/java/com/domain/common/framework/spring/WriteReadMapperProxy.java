package com.domain.common.framework.spring;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WriteReadMapperProxy<T> implements InvocationHandler {

    private Class<T> mapperInterface;

    String[] writePatterns;

    String[] readPatterns;

    private SqlSession writeSqlSession;

    private List<SqlSession> readSqlSessions = new ArrayList<SqlSession>();

    int currentIndex = 0, readSqlSessionSize = 0;

    public WriteReadMapperProxy() {

    }

    public WriteReadMapperProxy(Class<T> mapperInterface,
                                String[] writePatterns, String[] readPatterns,
                                SqlSession writeSqlSession,
                                List<SqlSession> readSqlSessions) {

        super();
        this.mapperInterface = mapperInterface;
        this.writePatterns = writePatterns;
        this.readPatterns = readPatterns;
        this.writeSqlSession = writeSqlSession;
        this.readSqlSessions = readSqlSessions;
        if (this.readSqlSessions != null) {
            this.readSqlSessionSize = this.readSqlSessions.size();
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        }

        String methodName = method.getName();
        SqlSession sqlSession = null;
        if (isWrite(methodName)) {
            sqlSession = this.writeSqlSession;
        } else if (isRead(methodName)) {
            sqlSession = getReadSqlSession();
        } else {
            sqlSession = this.writeSqlSession;
            // throw new WriteReadMapperException("dao method name["+methodName+"] prefix not support!, only supports writePatterns: "+ Arrays.toString(writePatterns) +"; readPatterns: "+ Arrays.toString(readPatterns));
        }
        T mapper = sqlSession.getMapper(mapperInterface);
        Object result = MethodUtils.invokeMethod(mapper, methodName, args, method.getParameterTypes());
        return result;
    }

    protected SqlSession getReadSqlSession() {

        if (readSqlSessions != null && readSqlSessions.size() > 0) {
            if (readSqlSessions.size() == 1) {
                return readSqlSessions.get(0);
            } else {
                return readSqlSessions.get(getReadSqlSessionIndex(this.readSqlSessionSize));
            }
        }
        return this.writeSqlSession;
    }

    /**
     * 简单平均轮询调度算法
     * @return index
     */
    protected synchronized int getReadSqlSessionIndex(int size) {

        if (size == 0) return -1;

        int j = currentIndex;
        do {
            j = (j + 1) % size;
            currentIndex = j;
            return currentIndex;
        } while (j != currentIndex);
    }

    protected boolean isWrite(String methodName) {

        for (String pattern : writePatterns) {
            if (methodName.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param methodName
     * @return
     */
    protected boolean isRead(String methodName) {

        for (String pattern : readPatterns) {
            // TODO 暂时改成完全匹配的才走读库
            if (methodName.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    public Object invokeMethod(Object methodObject, String methodName,
                               Object[] args) throws Exception {

        Class<?> ownerClass = methodObject.getClass();
        Class<?>[] argsClass = new Class<?>[args.length];
        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(methodObject, args);
    }

    public final static void main(String[] args) throws InterruptedException {

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(100);
        final WriteReadMapperProxy writeReadMapperProxy = new WriteReadMapperProxy();
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < 1000000; i++) {
            newFixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {

                    Integer readSqlSessionIndex = writeReadMapperProxy.getReadSqlSessionIndex(10);
                    synchronized (writeReadMapperProxy) {
                        Integer integer = map.get(readSqlSessionIndex);
                        if (integer == null) {
                            integer = 0;
                        }
                        map.put(readSqlSessionIndex, integer + 1);
                    }
                }
            });
        }
        newFixedThreadPool.shutdown();
        newFixedThreadPool.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println(map);
        int tot = 0;
        for (Integer key : map.keySet()) {
            tot += map.get(key);
        }
        System.out.println(tot);
    }

}
