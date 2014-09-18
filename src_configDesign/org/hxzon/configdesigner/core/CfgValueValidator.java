package org.hxzon.configdesigner.core;

import org.hxzon.util.DefaultStringParserExMatcher;
import org.hxzon.util.Dt;
import org.hxzon.util.StringParserEx;

public class CfgValueValidator {

    private CfgType type;
    private boolean required;
    private long minInt = Long.MIN_VALUE;
    private long maxInt = Long.MAX_VALUE;
    private double minDouble = Double.MIN_VALUE;
    private double maxDouble = Double.MAX_VALUE;
    private int minlen = 0;
    private int maxlen = Integer.MAX_VALUE;

    public CfgValueValidator(CfgType type, String vstr) {
        this.type = type;
        StringParserEx parser = new StringParserEx(vstr);
        DefaultStringParserExMatcher matcher = new DefaultStringParserExMatcher();
        parser.setMatcher(matcher);
        matcher.setBrackets(new String[] { " \"", " \"" });
        parser.setTrimInBracket(true);
        matcher.setWithDefaultWps(true);
        matcher.setWps(new char[] { ',', '，', ';', '；', ':', '：' });
        parse(parser, vstr);
    }

    private void parse(StringParserEx parser, String vstr) {
        String token = parser.nextToken();
        if ("select".equals(token)) {
            return;
        }
        while (token != null) {
            if ("required".equals(token)) {
                required = true;
            }
            if ("min".equals(token)) {
                if (type == CfgType.Integer) {
                    minInt = Dt.toLong(parser.nextToken(), 0);
                }
                if (type == CfgType.Real) {
                    minDouble = Dt.toDouble(parser.nextToken(), 0.0);
                }
            }
            if ("max".equals(token)) {
                if (type == CfgType.Integer) {
                    maxInt = Dt.toLong(parser.nextToken(), 0);
                }
                if (type == CfgType.Real) {
                    maxDouble = Dt.toDouble(parser.nextToken(), 0.0);
                }
            }
            if ("minlen".equals(token) && type == CfgType.String) {
                minlen = Dt.toInt(parser.nextToken(), 0);
            }
            if ("maxlen".equals(token) && type == CfgType.String) {
                maxlen = Dt.toInt(parser.nextToken(), 0);
            }
            token = parser.nextToken();
        }
    }

    public Object convert(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (type == CfgType.Integer) {
            try {
                Long tmpV = Long.parseLong(str);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static class ValidateResult {
        public final String errorMsg;
        public final Object value;

        private ValidateResult(String errorMsg, Object value) {
            this.errorMsg = errorMsg;
            this.value = value;
        }
    }
}
