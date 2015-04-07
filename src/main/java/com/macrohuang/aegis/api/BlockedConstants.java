package com.macrohuang.aegis.api;

public class BlockedConstants {
    public static final String STRATEGY_FREQUENCE = "frequency";
    public static final String STRATEGY_FRAME = "frame";
    public static final String STRATEGY_QUOTA = "quota";

    public static final String POLICY_NULL = "null";
    public static final String POLICY_EXCEPTION = "exception";
    public static final String POLICY_BLANK = "blank";
    public static final String KEY_BINKING = "binding";
    public static final String BEAN_PREFIX = "bean_";
    public static final String MULI_VALUE_SPLIT = ",";
    public static final String VALUE_ASSIGN_CHAR = "=";

    public enum BlockedTimeType {
        Day('D', "天", 24 * 3600 * 1000), Hour('H', "小时", 3600 * 1000), Minute('M', "分钟", 60 * 1000), Second(
                'S', "秒", 1000), Week('W', "周", 7 * 24 * 3600 * 1000);
        private char c;
        private String desc;
        private long timeInMS;

        private BlockedTimeType(char c, String desc, long timeInMS) {
            this.c = c;
            this.desc = desc;
            this.timeInMS = timeInMS;
        }

        public char getChar() {
            return c;
        }

        public long getTimeInMs() {
            return timeInMS;
        }

        public String getDescription() {
            return desc;
        }

        public static BlockedTimeType fromTypeChar(char c) {
            for (BlockedTimeType timeType : values()) {
                if (timeType.getChar() == c || timeType.getChar() + 32 == c) {
                    return timeType;
                }
            }
            return Minute;
        }

        public long getFrequency(long limit) {
            return limit <= 0 ? Long.MAX_VALUE : timeInMS / limit;
        }
    }

    public enum BlockedType {
        Default('B'), Black('B'), White('W');
        private char c;

        private BlockedType(char c) {
            this.c = c;
        }

        public char getBlockedTypeChar() {
            return c;
        }

        public BlockedType fromChar(char c) {
            for (BlockedType type : values()) {
                if (type.getBlockedTypeChar() == c) {
                    return type;
                }
            }
            return Default;
        }
    }
}
