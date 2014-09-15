package org.hxzon.util;

import java.util.ArrayList;
import java.util.List;

public class StringParserEx {
    public static final String[] lineDelimiter = new String[] { "\r\n", "\n\r", "\r", "\n" };
    private static final char R = '\r';
    private static final char N = '\n';
    public static final String[] END = new String[0];
    private static final String _end = null;
    //
    private CharSequence str;
    private int end;
    private int mark;
    private int curIndex;
    private int lastTokenCount;
    private boolean isLastTokenNotComplete;
    //tokens，自动跳过空行，自动进入下一行
    private boolean includeBracket;
    private boolean removeEmptyInBracket;
    private boolean trimInBracket;
    private boolean keepEmptyInDelim;
    private boolean withNextLine;
    private StringParserExMatcher matcher;
    //line
    private boolean keepEmptyLine;

    //
    public StringParserEx(CharSequence str) {
        setStr(str);
    }

    //=============
    public String[] remainTokens() {
        return remainTokens_impl();
    }

    public String remainString() {
        if (curIndex == end) {
            return _end;
        }
        String result = str.subSequence(curIndex, end).toString();
        curIndex = end;
        return result;
    }

    public String[] nextTokensCurLine() {
        return remainTokensCurLine_impl();
    }

    public String remainStringCurLine() {
        return remainStringCurLine_impl();
    }

    public String[] nextTokens(int size) {
        return nextTokens(size, withNextLine);
    }

    public String[] nextTokens(int size, boolean withNextLine) {
        return withNextLine ? nextTokens_withNextLine(size)//
                : nextTokens_notWithNextLine(size);
    }

    public String nextToken() {
        return nextToken(withNextLine);
    }

    public String nextToken(boolean withNextLine) {
        String[] result = nextTokens(1, withNextLine);
        return result == END ? _end : result[0];
    }

    //============
    private String nextToken_impl() {
        if (curIndex == end) {
            return _end;
        }
        int start = curIndex;
        while (curIndex < end) {
            //
            String leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                if (start != curIndex) {
                    //start 到 左括号之前
                    String token = nextToken_remainInCurIndex(start);
                    if (token != null) {
                        return token;
                    }
                }
                String token = nextToken_inBracket(leftBracket);
                start = curIndex;
                leftBracket = null;
                if (token != null) {
                    return token;
                }
                continue;
            }
            //
            String delim = matcher.isDelimiter(str, curIndex, end);
            if (delim != null) {
                String token = nextToken_byDelimiter(start, delim);
                start = curIndex;
                if (token != null) {
                    return token;
                }
                continue;
            }
            //
            curIndex++;
        }
        return nextToken_remainInCurIndex(start);
    }

    private String nextToken_inBracket(String leftBracket) {
        curIndex += leftBracket.length();
        int start = curIndex;
        String rightBracket = null;
        for (; curIndex < end; curIndex++) {
            rightBracket = matcher.isRightBracket(str, curIndex, end, leftBracket);
            if (rightBracket != null) {
                break;
            }
        }
        int[] trim = trimInBracket ? matcher.trim(str, start, curIndex)//
                : new int[] { start, curIndex };
        if (rightBracket == null) {
            isLastTokenNotComplete = true;
        } else {
            isLastTokenNotComplete = false;
            curIndex += rightBracket.length();
        }
        if (trim[0] != trim[1]) {
            String token = str.subSequence(trim[0], trim[1]).toString();
            if (includeBracket) {
                if (rightBracket != null) {
                    return leftBracket + token + rightBracket;
                }
                return leftBracket + token;
            }
            return token;
        }
        if (!removeEmptyInBracket) {
            if (includeBracket) {
                return rightBracket == null ? leftBracket : (leftBracket + rightBracket);
            }
            return "";
        }
        return null;
    }

    private String nextToken_remainInCurIndex(int start) {
        //到达文件尾，或行尾，仍没找到左括号和分隔符
        //从 start 到 左括号前，或到分隔符前
        int[] trim = matcher.trim(str, start, curIndex);
        if (trim[0] != trim[1]) {
            return str.subSequence(trim[0], trim[1]).toString();
        }
        if (keepEmptyInDelim) {
            return "";
        }
        return null;
    }

    private String nextToken_byDelimiter(int start, String delim) {
        String token = nextToken_remainInCurIndex(start);
        curIndex += delim.length();
        return token;
    }

    private String[] nextTokens_withNextLine(int size) {
        String token = nextToken_impl();
        lastTokenCount = 0;
        if (token == null) {
            return END;
        }
        String[] result = new String[size];
        result[lastTokenCount] = token;
        lastTokenCount++;
        while (lastTokenCount < size) {
            token = nextToken_impl();
            if (token == null) {
                break;
            }
            result[lastTokenCount] = token;
            lastTokenCount++;
        }
        return result;
    }

    private String[] remainTokens_impl() {
        String token = nextToken_impl();
        if (token == null) {
            return END;
        }
        List<String> result = new ArrayList<String>();
        while (token != null) {
            result.add(token);
            token = nextToken_impl();
        }
        lastTokenCount = result.size();
        return result.toArray(new String[result.size()]);
    }

    private String[] remainTokensCurLine_impl() {
        if (curIndex == end) {
            lastTokenCount = 0;
            return END;
        }
        List<String> result = new ArrayList<String>();
        int start = curIndex;
        String leftBracket = null;
        while (true) {
            if (leftBracket != null) {
                String token = nextToken_inBracket(leftBracket);
                start = curIndex;
                leftBracket = null;
                if (token != null) {
                    result.add(token);
                }
                continue;
            }
            if (curIndex == end) {
                String token = nextToken_remainInCurIndex(start);
                if (token != null) {
                    result.add(token);
                }
                break;
            }

            char c = str.charAt(curIndex);
            if ((c == R || c == N) && !result.isEmpty()) {
                String token = nextToken_remainInCurIndex(start);
                if (token != null) {
                    result.add(token);
                }
                rn(c);
                break;
            }
            leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                continue;
            }
            String delim = matcher.isDelimiter(str, curIndex, end);
            if (delim != null) {
                String token = nextToken_byDelimiter(start, delim);
                start = curIndex;
                if (token != null) {
                    result.add(token);
                }
                continue;
            }
            curIndex++;
        }

        lastTokenCount = result.size();
        return result.toArray(new String[lastTokenCount]);
    }

    private void rn(char c) {
        if (c == R && curIndex < end) {
            if (str.charAt(curIndex) == N) {
                curIndex++;
            }
        }
        if (c == N && curIndex < end) {
            if (str.charAt(curIndex) == R) {
                curIndex++;
            }
        }
    }

    //会自动进入下一行（且跳过空行）
    private String[] nextTokens_notWithNextLine(int size) {
        if (curIndex == end) {
            lastTokenCount = 0;
            return END;
        }
        String[] result = new String[size];
        lastTokenCount = 0;
        int start = curIndex;
        String leftBracket = null;
        while (lastTokenCount < size) {
            if (leftBracket != null) {
                String token = nextToken_inBracket(leftBracket);
                start = curIndex;
                leftBracket = null;
                if (token != null) {
                    result[lastTokenCount] = token;
                    lastTokenCount++;
                }
                continue;
            }
            if (curIndex == end) {
                String token = nextToken_remainInCurIndex(start);
                if (token != null) {
                    result[lastTokenCount] = token;
                    lastTokenCount++;
                }
                break;
            }

            char c = str.charAt(curIndex);
            if ((c == R || c == N) && lastTokenCount != 0) {
                String token = nextToken_remainInCurIndex(start);
                if (token != null) {
                    result[lastTokenCount] = token;
                    lastTokenCount++;
                }
                rn(c);
                break;
            }

            leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                continue;
            }
            String delim = matcher.isDelimiter(str, curIndex, end);
            if (delim != null) {
                String token = nextToken_byDelimiter(start, delim);
                start = curIndex;
                if (token != null) {
                    result[lastTokenCount] = token;
                    lastTokenCount++;
                }
                continue;
            }
            curIndex++;

        }
        return result;
    }

    //===============
    public void ignoreRemainCurLine() {
        ignoreRemainCurLine(false);
    }

    public void ignoreRemainCurLine(boolean dontBreakInBracket) {
        if (matcher.hasBracket() && dontBreakInBracket) {
            ignoreRemainCurLine_impl2();
        } else {
            ignoreRemainCurLine_impl1();
        }
    }

    private void ignoreRemainCurLine_impl1() {
        if (isNewLineBegin()) {
            //本行恰好读完，已进入下一行
            return;
        }
        while (curIndex < end) {
            char c = str.charAt(curIndex);
            if (c == R || c == N) {
                curIndex++;
                rn(c);
                break;
            }
            curIndex++;
        }
    }

    private void ignoreRemainCurLine_impl2() {
        if (isNewLineBegin()) {
            //本行恰好读完，已进入下一行
            return;
        }
        String leftBracket = null;
        while (curIndex < end) {
            if (leftBracket != null) {
                findRightBracket(leftBracket);
                leftBracket = null;
                continue;
            }
            leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                continue;
            }
            char c = str.charAt(curIndex);
            if (c == R || c == N) {
                curIndex++;
                rn(c);
                break;
            }
            curIndex++;
        }
    }

    private String remainStringCurLine_impl() {
        int start = curIndex;
        String leftBracket = null;
        while (true) {

            if (leftBracket != null) {
                findRightBracket(leftBracket);
                leftBracket = null;
                continue;
            }
            if (curIndex == end) {
                return str.subSequence(start, end).toString();
            }
            leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                continue;
            }
            char c = str.charAt(curIndex);
            if (c == R || c == N) {
                int lineEnd = curIndex;
                curIndex++;
                rn(c);
                return str.subSequence(start, lineEnd).toString();
            }
            curIndex++;
        }
    }

    //==========================
    public String nextLine() {
        return nextLine(keepEmptyLine);
    }

    //达到文件末尾时，返回null
    public String nextLine(boolean keepEmptyLine) {
        return nextLine_impl(keepEmptyLine);
    }

    private String nextLine_impl(boolean keepEmptyLine) {
        if (isEnd()) {
            return _end;
        }
        boolean isEmptyLine = !keepEmptyLine;
        int lineStart = curIndex;
        String leftBracket = null;
        while (true) {
            if (leftBracket != null) {
                findRightBracket(leftBracket);
                leftBracket = null;
                continue;
            }
            if (curIndex == end) {
                if (!isEmptyLine) {
                    return str.subSequence(lineStart, end).toString();
                }
                return _end;
            }
            leftBracket = matcher.isLeftBracket(str, curIndex, end);
            if (leftBracket != null) {
                isEmptyLine = false;
                continue;
            }
            char c = str.charAt(curIndex);
            if (c == R || c == N) {
                int lineEnd = curIndex;
                curIndex++;
                rn(c);
                if (!isEmptyLine) {
                    return str.subSequence(lineStart, lineEnd).toString();
                }
                isEmptyLine = !keepEmptyLine;
                lineStart = curIndex;
                continue;
            }
            //
            if (isEmptyLine && !matcher.isWhitespace(c)) {
                isEmptyLine = false;
            }
            curIndex++;
        }
    }

    private String findRightBracket(String leftBracket) {
        isLastTokenNotComplete = true;
        curIndex += leftBracket.length();
        String rightBracket = null;
        while (curIndex < end && rightBracket == null) {
            rightBracket = matcher.isRightBracket(str, curIndex, end, leftBracket);
            curIndex++;
        }
        if (rightBracket != null) {
            isLastTokenNotComplete = false;
            curIndex += rightBracket.length();
            return rightBracket;
        }
        return _end;
    }

    //=================
    public boolean isEnd() {
        return curIndex == end;
    }

    //已达文件末尾时，视为有新的“空行”（但读取会返回null或END）
    public boolean isNewLineBegin() {
        int c = str.charAt(curIndex - 1);
        return c == R || c == N;
    }

    public void mark() {
        mark = curIndex;
    }

    public void reset() {
        curIndex = mark;
    }

    //=====================
    public void setStr(CharSequence str) {
        this.str = str;
        end = str.length();
        mark = 0;
        curIndex = 0;
    }

    public CharSequence getStr() {
        return str;
    }

    public boolean isIncludeBracket() {
        return includeBracket;
    }

    public void setIncludeBracket(boolean includeBracket) {
        this.includeBracket = includeBracket;
    }

    public boolean isRemoveEmptyInBracket() {
        return removeEmptyInBracket;
    }

    public void setRemoveEmptyInBracket(boolean removeEmptyInBracket) {
        this.removeEmptyInBracket = removeEmptyInBracket;
    }

    public boolean isTrimInBracket() {
        return trimInBracket;
    }

    public void setTrimInBracket(boolean trimInBracket) {
        this.trimInBracket = trimInBracket;
    }

    public boolean isKeepEmptyInDelim() {
        return keepEmptyInDelim;
    }

    public void setKeepEmptyInDelim(boolean keepEmptyInDelim) {
        this.keepEmptyInDelim = keepEmptyInDelim;
    }

    public boolean isWithNextLine() {
        return withNextLine;
    }

    public void setWithNextLine(boolean withNextLine) {
        this.withNextLine = withNextLine;
    }

    public boolean isKeepEmptyLine() {
        return keepEmptyLine;
    }

    public void setKeepEmptyLine(boolean keepEmptyLine) {
        this.keepEmptyLine = keepEmptyLine;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLastTokenCount() {
        return lastTokenCount;
    }

    public void setLastTokenCount(int lastTokenCount) {
        //do nothing
    }

    public boolean isLastTokenNotComplete() {
        return isLastTokenNotComplete;
    }

    public void setLastTokenNotComplete(boolean isLastTokenNotComplete) {
        //do nothing
    }

    public StringParserExMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(StringParserExMatcher matcher) {
        this.matcher = matcher;
    }

}
