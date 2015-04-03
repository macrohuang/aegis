package com.baidu.fengchao.aegis.exception;

public class TooFrequentInvokeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8679067636382582834L;

    private Object result;
    private boolean isResult = false;

    public TooFrequentInvokeException() {
        super();
    }

    public TooFrequentInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooFrequentInvokeException(String message) {
        super(message);
    }

    public TooFrequentInvokeException(Throwable cause) {
        super(cause);
    }

    public TooFrequentInvokeException(Object result) {
        this.result = result;
    }

    public TooFrequentInvokeException(String message, boolean isResult) {
        super(message);
        this.isResult = isResult;
    }

    public TooFrequentInvokeException(String message, Throwable cause, Object result) {
        super(message, cause);
        this.result = result;
    }
    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    public boolean isResult() {
        return isResult;
    }
}
