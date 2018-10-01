package com.rentbud.helpers;

import android.widget.EditText;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateAndCurrencyDisplayer {
    public static final int CURRENCY_US = 200;
    public static final int CURRENCY_UK = 201;
    public static final int CURRENCY_JAPAN = 202;
    public static final int CURRENCY_KOREA = 203;
    public static final int CURRENCY_GERMANY = 204;
    public static final int DATE_MMDDYYYY = 300;
    public static final int DATE_DDMMYYYY = 301;
    public static final int DATE_YYYYMMDD = 302;
    public static final int DATE_YYYYDDMM = 303;

    public static String getCurrencyToDisplay(int currencyCode, BigDecimal amount) {
        String convertedString = "";
        if (amount != null) {
            if (currencyCode == CURRENCY_US) {
                amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                NumberFormat costFormat = NumberFormat.getCurrencyInstance(Locale.US);
                costFormat.setMinimumFractionDigits(2);
                costFormat.setMaximumFractionDigits(2);
                convertedString = costFormat.format(amount.doubleValue());
            } else if (currencyCode == CURRENCY_UK) {
                amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                NumberFormat costFormat = NumberFormat.getCurrencyInstance(Locale.UK);
                costFormat.setMinimumFractionDigits(2);
                costFormat.setMaximumFractionDigits(2);
                convertedString = costFormat.format(amount.doubleValue());
            } else if (currencyCode == CURRENCY_JAPAN) {
                amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                NumberFormat costFormat = NumberFormat.getCurrencyInstance(Locale.JAPAN);
                costFormat.setMinimumFractionDigits(2);
                costFormat.setMaximumFractionDigits(2);
                amount.multiply(new BigDecimal(100));
                convertedString = costFormat.format(amount.doubleValue());
            } else if (currencyCode == CURRENCY_KOREA) {
                amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                NumberFormat costFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
                costFormat.setMinimumFractionDigits(2);
                costFormat.setMaximumFractionDigits(2);
                //amount.multiply(new BigDecimal(100));
                convertedString = costFormat.format(amount.doubleValue());
            } else if (currencyCode == CURRENCY_GERMANY) {
                amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                NumberFormat costFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                costFormat.setMinimumFractionDigits(2);
                costFormat.setMaximumFractionDigits(2);
                convertedString = costFormat.format(amount.doubleValue());
            }
        }
        return convertedString;
    }

    public static String getDateToDisplay(int dateDisplayCode, Date date) {
        String convertedString = "";
        if (date != null) {
            if (dateDisplayCode == DATE_MMDDYYYY) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                convertedString = formatter.format(date);
            } else if (dateDisplayCode == DATE_DDMMYYYY) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                convertedString = formatter.format(date);
            } else if (dateDisplayCode == DATE_YYYYMMDD) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                convertedString = formatter.format(date);
            } else if (dateDisplayCode == DATE_YYYYDDMM) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/dd/MM", Locale.US);
                convertedString = formatter.format(date);
            }
        }
        return convertedString;
    }

    public static String getDateToDisplay(int dateDisplayCode, LocalDate date) {
        String convertedString = "";
        if (date != null) {
            if (dateDisplayCode == DATE_MMDDYYYY) {
                convertedString = date.toString("MM/dd/yyyy");
            } else if (dateDisplayCode == DATE_DDMMYYYY) {
                convertedString = date.toString("dd/MM/yyyy");
            } else if (dateDisplayCode == DATE_YYYYMMDD) {
                convertedString = date.toString("yyyy/MM/dd");
            } else if (dateDisplayCode == DATE_YYYYDDMM) {
                convertedString = date.toString("yyyy/dd/MM");
            }
        }
        convertedString.replaceAll("[-]", "/");
        return convertedString;
    }

    public static Date getDateFromDisplay(int dateDisplayCode, String dateString) {
        Date date = null;
        if (dateDisplayCode == DATE_MMDDYYYY) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (dateDisplayCode == DATE_DDMMYYYY) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (dateDisplayCode == DATE_YYYYMMDD) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (dateDisplayCode == DATE_YYYYDDMM) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/dd/MM", Locale.US);
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static String cleanMoneyString(String string) {
        return string.replaceAll("[$£¥₩€￥,./\\s+/g]", "");
    }

    public static int getEndCursorPositionForMoneyInput(int length, int currencyCode) {
        int position = length;
        if (currencyCode == CURRENCY_GERMANY) {
            position = position - 2;
        }
        return position;
    }
}
