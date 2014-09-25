package org.hxzon.configdesigner.core;

import java.util.regex.Pattern;

import org.hxzon.util.DefaultStringParserExMatcher;
import org.hxzon.util.Dt;
import org.hxzon.util.StringParserEx;

public class CfgValueValidator {

    private CfgType type;
    private boolean required;
    //
    private long minInteger = Long.MIN_VALUE;
    private long maxInteger = Long.MAX_VALUE;
    //
    private double minDouble = Double.MIN_VALUE;
    private double maxDouble = Double.MAX_VALUE;
    //
    private int minlen = 0;
    private int maxlen = Integer.MAX_VALUE;
    private Pattern regex = null;

    public CfgValueValidator(CfgType type, String vstr) {
        this.type = type;
        parse(vstr);
    }

    private void parse(String vstr) {
        StringParserEx parser = new StringParserEx(vstr);
        DefaultStringParserExMatcher matcher = new DefaultStringParserExMatcher();
        parser.setMatcher(matcher);
        matcher.setBrackets(new String[] { " \"", " \"" });
        parser.setTrimInBracket(true);
        matcher.setWithDefaultWps(true);
        matcher.setWps(new char[] { ',', '，', ';', '；', ':', '：' });
        matcher.setWithDefaultDelimiters(true);
        matcher.setDelimiters(new String[] { ",", "，", ";", "；", ":", "：" });
        //
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
                    minInteger = Dt.toLong(parser.nextToken(), 0);
                }
                if (type == CfgType.Real) {
                    minDouble = Dt.toDouble(parser.nextToken(), 0.0);
                }
            }
            if ("max".equals(token)) {
                if (type == CfgType.Integer) {
                    maxInteger = Dt.toLong(parser.nextToken(), 0);
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
            if ("regex".equals(token) && type == CfgType.String) {
                String regexStr = parser.remainString();
                regex = Pattern.compile(regexStr);
            }
            token = parser.nextToken();
        }
    }

    public boolean isRequired() {
        return required;
    }

    public long getMinInteger() {
        return minInteger;
    }

    public long getMaxInteger() {
        return maxInteger;
    }

    public double getMinDouble() {
        return minDouble;
    }

    public double getMaxDouble() {
        return maxDouble;
    }

    public int getMinlen() {
        return minlen;
    }

    public int getMaxlen() {
        return maxlen;
    }

    public Pattern getRegex() {
        return regex;
    }

}
