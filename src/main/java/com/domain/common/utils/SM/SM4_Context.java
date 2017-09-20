package com.domain.common.utils.SM;

/**
 * com.weyao.calc.util.SM
 * @author Mark Li
 * @version 1.0.0
 * @since 2017/9/20
 */
public class SM4_Context {

    public int mode;

    public long[] sk;

    public boolean isPadding;

    public SM4_Context() {

        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }
}

