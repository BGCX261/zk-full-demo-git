package org.hxzon.util;

public interface StringParserExMatcher {

    boolean hasBracket();

    String isLeftBracket(CharSequence str, int curIndex, int end);

    String isRightBracket(CharSequence str, int curIndex, int end, String leftBracket);

    int[] trim(CharSequence str, int curIndex, int end);

    boolean isWhitespace(char c);

    String isDelimiter(CharSequence str, int curIndex, int end);
}
