package com.domain.common.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外边有括号
 * @author liukuikui
 * @desc
 * @date 2016年8月24日
 */
public class StrategyExpression {

    private static final Logger logger = LoggerFactory.getLogger(StrategyExpression.class);

    /**
     * 目前支持 &&和||两种表达式
     * @author liukuikui
     */
    public static enum ExpressionOperator {
        AND("&&"),
        OR("||");

        private String operator;

        private ExpressionOperator(String operator) {

            this.setOperator(operator);
        }

        public String getOperator() {

            return operator;
        }

        public void setOperator(String operator) {

            this.operator = operator;

        }
    }

    public static final String EMPTY_EXPRESSION = "";

    private Expression aviatorExpression;

    private boolean hasModification = true;

    private StrategyExpression() {

        super();
    }

    private List<StrategyCondition> conditions = new ArrayList<>();

    private List<StrategyExpression> expressions = new ArrayList<>();

    private ExpressionOperator operator;

    public static StrategyExpression build(ExpressionOperator operator, StrategyCondition... conditions) {

        StrategyExpression expression = new StrategyExpression();
        expression.operator = operator;
        if (conditions != null && conditions.length > 0) {
            for (StrategyCondition strategyCondition : conditions) {
                expression.addCondition(strategyCondition);
            }
        }

        return expression;
    }

    public static StrategyExpression build(ExpressionOperator operator) {

        StrategyExpression expression = new StrategyExpression();
        expression.operator = operator;
        return expression;
    }

    public static StrategyExpression build(ExpressionOperator operator, StrategyExpression... expressions) {

        StrategyExpression expression = new StrategyExpression();
        expression.operator = operator;
        if (expressions != null && expressions.length > 0) {
            for (StrategyExpression strategyExpression : expressions) {
                expression.addExpression(strategyExpression);
            }
        }
        return expression;
    }

    /**
     * 构建一个（name == value1 || name == value2 ...）表达式
     * @param <T>
     * @param name
     * @param values
     * @return
     */
    public static <T> StrategyExpression buildMultipleOrConditions(String name, List<T> values) {

        StrategyExpression expression = new StrategyExpression();
        expression.operator = ExpressionOperator.OR;
        if (name != null && !name.isEmpty() && values != null && !values.isEmpty()) {
            for (T value : values) {
                expression.conditions.add(StrategyCondition.build(StrategyCondition.ConditionOperator.EQUAL, name, value));
            }
        }
        return expression;
    }

    public StrategyExpression addCondition(StrategyCondition condition) {

        if (condition != null && !condition.isEmpty()) {
            hasModification = true;
            conditions.add(condition);
        }

        return this;
    }

    public StrategyExpression addExpression(StrategyExpression expression) {

        if (expression != null && !expression.isEmpty()) {
            hasModification = true;
            expressions.add(expression);
        }
        return this;
    }

    public boolean isEmpty() {

        return conditions.isEmpty() && expressions.isEmpty();
    }

    public String buildRuler() {

        StringBuilder ruleBuilder = new StringBuilder("(");
        StringBuilder componentBuilder = new StringBuilder();

        for (StrategyCondition condition : conditions) {
            componentBuilder.append(operator.getOperator());
            componentBuilder.append(condition.buildRule());
        }

        for (StrategyExpression exp : expressions) {
            componentBuilder.append(operator.getOperator());
            componentBuilder.append(exp.buildRuler());
        }

        if (componentBuilder.length() > 0) {
            String content = componentBuilder.substring(operator.getOperator().length());
            ruleBuilder.append(content);
        }
        ruleBuilder.append(")");
        return ruleBuilder.length() > 2 ? ruleBuilder.toString() : EMPTY_EXPRESSION;

    }

    private Expression buildAviatorExpression() {

        if (!hasModification) {
            return aviatorExpression;
        } else {
            String ruler = this.buildRuler();
            logger.debug(String.format("构建规则表达式，规则表达式：【%s】", ruler));
            aviatorExpression = AviatorEvaluator.compile(ruler, true);
            hasModification = false;
            return aviatorExpression;
        }
    }

    public <T> boolean matches(T pojo) {

        logger.debug(String.format("匹配规则表达式，匹配条件：【%s】", JsonHelper2.toJson(pojo)));
        Map<String, Object> env = new HashMap<>();
        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                env.put(field.getName(), field.get(pojo));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Boolean match = (Boolean) this.buildAviatorExpression().execute(env);
        logger.debug(String.format("匹配规则表达式，匹配结果：【%s】", match ? "匹配成功" : "匹配失败"));
        return match;
    }
}
