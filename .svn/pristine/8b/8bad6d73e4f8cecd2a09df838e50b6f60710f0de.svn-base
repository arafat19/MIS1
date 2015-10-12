/* this program will display number in words
for eg. if you enter 101,it will show "ONE HUNDRED AND ONE"*/
package com.athena.mis.utility

class N2W {
    private static String[] st1 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine",];
    private static String[] st2 = ["Hundred", "Thousand", "Lakh", "Crore"];
    private static String[] st3 = ["Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen",];
    private static String[] st4 = ["Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy",
            "Eighty", "Ninety"];
    private static final String TAKA = "Taka "
    private static final String PAISA = " Poisha"
    private static final String STR_AND = " and "
    private static final String STR_ONLY = " Only"
    private static final String EMPTY_SPACE = ""    // replacing 'Taka '

    public static String convert(BigDecimal amount) {
        try {

            long number = amount.longValue()
            if (number > 999999999999) return Tools.EMPTY_SPACE   // @todo: eliminate the limitations
            //get word of amount
            String amountInWord = EMPTY_SPACE
            amountInWord = amountInWord + processAmount(number)
            float decimalValue = amount - number
            decimalValue = decimalValue * 100
            long decimalNumber = (long) decimalValue
            if (decimalNumber > 0) {
                amountInWord = amountInWord + STR_AND + processAmount(decimalNumber) + PAISA
            }

            return amountInWord + STR_ONLY;
        } catch (Exception e) {
            return Tools.EMPTY_SPACE
        }

    }

    private static String processAmount(long number) {
        int n = 1;
        long word;
        String currentStr = Tools.EMPTY_SPACE
        String mainStr = Tools.EMPTY_SPACE
        while (number != 0) {
            switch (n) {
                case 1:
                    word = number % 100;
                    currentStr = pass(word);
                    if (number > 100 && number % 100 != 0) {
//                            show(STR_AND);
                        currentStr = Tools.SINGLE_SPACE + currentStr;
                    }
                    number /= 100;
                    break;
                case 2:
                    word = number % 10;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[0] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    }
                    number /= 10;
                    break;
                case 3:
                    word = number % 100;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[1] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    }
                    number /= 100;
                    break;
                case 4:
                    word = number % 100;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[2] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    }
                    number /= 100;
                    break;
                case 5:
                    word = number % 100;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[3] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    } else if (number > 99 && word == 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[3] + Tools.SINGLE_SPACE;
                    }
                    number /= 100;
                    break;
                case 6:
                    word = number % 10;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[0] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    }
                    number /= 10;
                    break;
                case 7:
                    word = number % 100;
                    if (word != 0) {
                        currentStr = Tools.SINGLE_SPACE + st2[1] + Tools.SINGLE_SPACE;
                        currentStr = pass(word) + currentStr;
                    }
                    number /= 100;
                    break;
            }

            mainStr = currentStr + mainStr
            currentStr = Tools.EMPTY_SPACE
            n++;
        }
        return mainStr
    }

    private static String pass(long number) {
        int word, q;
        String passStr = Tools.EMPTY_SPACE
        if (number < 10) {
            passStr = st1[number];
        }
        if (number > 9 && number < 20) {
            passStr = st3[number - 10];
        }
        if (number > 19) {
            word = number % 10;
            if (word == 0) {
                q = number / 10;
                passStr = st4[q - 2];
            } else {
                q = number / 10;
                passStr = st4[q - 2] + Tools.SINGLE_SPACE + st1[word];
            }
        }

        return passStr
    }
}

