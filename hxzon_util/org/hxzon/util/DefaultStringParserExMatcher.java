package org.hxzon.util;

public class DefaultStringParserExMatcher implements StringParserExMatcher {
    private String[] brackets;
    private char[] wps;
    private boolean withDefaultWps;
    private String[] delimiters;
    private boolean withDefaultDelimiters;

    @Override
    public boolean hasBracket() {
        return brackets != null && brackets.length > 0;
    }

    @Override
    public String isLeftBracket(CharSequence str, int curIndex, int end) {
        if (brackets == null) {
            return null;
        }
        for (int i = 0; i < brackets.length; i += 2) {
            String leftBracket = brackets[i];
            int leftBracketLen = leftBracket.length();
            if (end - curIndex < leftBracketLen) {
                continue;
            }
            int pos = 0;
            for (; pos < leftBracketLen; pos++) {
                if (str.charAt(curIndex + pos) != leftBracket.charAt(pos)) {
                    break;
                }
            }
            if (pos == leftBracketLen) {
                return leftBracket;
            }
        }
        return null;
    }

    @Override
    public String isRightBracket(CharSequence str, int curIndex, int end, String leftBracket) {
        String rightBracket = null;
        for (int i = 0; i < brackets.length; i += 2) {
            if (brackets[i].equals(leftBracket)) {
                rightBracket = brackets[i + 1];
                break;
            }
        }
        int rightBracketLen = rightBracket.length();
        if (end - curIndex < rightBracketLen) {
            return null;
        }
        int pos = 0;
        for (; pos < rightBracketLen; pos++) {
            if (str.charAt(curIndex + pos) != rightBracket.charAt(pos)) {
                return null;
            }
        }
        return rightBracket;
    }

    @Override
    public int[] trim(CharSequence str, int curIndex, int end) {
        int left = curIndex;
        int right = end;
        for (; left < right; left++) {
            if (!isWps(str.charAt(left))) {
                break;
            }
        }
        for (; right > left; right--) {
            if (!isWps(str.charAt(right - 1))) {
                break;
            }
        }
        return new int[] { left, right };
    }

    @Override
    public boolean isWhitespace(char c) {
        return isWps(c);
    }

    @Override
    public String isDelimiter(CharSequence str, int curIndex, int end) {
        if (delimiters != null) {
            for (String delim : delimiters) {
                int delimLen = delim.length();
                if (end - curIndex < delimLen) {
                    continue;
                }
                int i = 0;
                for (; i < delimLen; i++) {
                    if (str.charAt(curIndex + i) != delim.charAt(i)) {
                        break;
                    }
                }
                if (i == delimLen) {
                    return delim;
                }
            }
        }
        if (withDefaultDelimiters) {
            char c = str.charAt(curIndex);
            if (curIndex + 1 < end) {
                if (c == '\r' && str.charAt(curIndex + 1) == '\n') {
                    return "\r\n";
                } else if (c == '\n' && str.charAt(curIndex + 1) == '\r') {
                    return "\n\r";
                }
            }
            if (StringUtil.isWhitespace(c, null, withDefaultDelimiters)) {
                return String.valueOf(c);
            }
        }
        return null;
    }

    private boolean isWps(char c) {
        return StringUtil.isWhitespace(c, wps, withDefaultWps);
    }

    //==================
    public String[] getBrackets() {
        return brackets;
    }

    public void setBrackets(String[] brackets) {
        this.brackets = brackets;
    }

    public char[] getWps() {
        return wps;
    }

    public void setWps(char[] wps) {
        this.wps = wps;
    }

    public boolean isWithDefaultWps() {
        return withDefaultWps;
    }

    public void setWithDefaultWps(boolean withDefaultWps) {
        this.withDefaultWps = withDefaultWps;
    }

    public String[] getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(String[] delimiters) {
        this.delimiters = delimiters;
    }

    public boolean isWithDefaultDelimiters() {
        return withDefaultDelimiters;
    }

    public void setWithDefaultDelimiters(boolean withDefaultDelimiters) {
        this.withDefaultDelimiters = withDefaultDelimiters;
    }

}
