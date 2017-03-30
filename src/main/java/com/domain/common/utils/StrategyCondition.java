package com.domain.common.utils;

/**
 * @author liukuikui
 * @desc
 * @date 2016年8月24日
 */
public class StrategyCondition {

    public static enum ValueType {
        STRING,
        NUMBER,
        EMPTY
    }

    public static enum ConditionOperator {
        GREATER_THAN(">"),
        GREATER_OR_EQUAL(">="),
        EQUAL("=="),
        LESS_THAN("<"),
        LESS_OR_EQUAL("<="),
        NOT_EQUAL("!=");

        private String operatorString;

        public String getOperator() {

            return operatorString;
        }

        private ConditionOperator(String op) {

            operatorString = op;
        }

    }

    private String name;

    private Object value;

    private ValueType valueType = ValueType.EMPTY;

    private ConditionOperator operator;

    public static StrategyCondition EMPTY = new StrategyCondition();

    /**
     * 判断是否此条件表达式的内容为空
     * @return
     */
    public boolean isEmpty() {

        return valueType == ValueType.EMPTY;
    }

    /**
     * 如果name 或value为NULL，则返回EMPTY
     * @param operator
     * @param name
     * @param value
     * @return
     */
    public static StrategyCondition build(ConditionOperator operator, String name, Object value) {

        StrategyCondition condition = new StrategyCondition();
        if (name == null || value == null) {
            return EMPTY;
        }
        condition.name = name;
        condition.value = value;
        if (value.getClass().equals(Long.class) ||
                value.getClass().equals(Integer.class) ||
                value.getClass().equals(Double.class)
                ) {
            condition.valueType = ValueType.NUMBER;
        } else {
            condition.valueType = ValueType.STRING;
        }
        condition.operator = operator;
        return condition;
    }

    String buildRule() {

        StringBuilder builder = new StringBuilder("");
        builder.append(name);
        builder.append(operator.getOperator());
        if (valueType.equals(ValueType.STRING)) {
            builder.append("'");
            builder.append(String.valueOf(value));
            builder.append("'");
        } else {
            builder.append(String.valueOf(value));
        }
        return builder.toString();
    }
}
