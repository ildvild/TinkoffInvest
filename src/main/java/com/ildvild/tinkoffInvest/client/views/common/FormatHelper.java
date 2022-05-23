package com.ildvild.tinkoffInvest.client.views.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class FormatHelper {
    public static DecimalFormat quantityFormatter = new DecimalFormat("###,###,##0.##");
    public static DecimalFormat priceFormatter = new DecimalFormat("###,###,##0.00");
    public static DecimalFormat priceUSDFormatter = new DecimalFormat("###,###,##0.00 $");
    public static DecimalFormat priceRUBFormatter = new DecimalFormat("###,###,##0.00 ₽");
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    public static NumberFormat getQuantityFormat() {
        return quantityFormatter;
    }

    public static NumberFormat getPriceFormat() {
        return priceFormatter;
    }

    public static NumberFormat getPriceFormat(String currency) {
        switch (currency) {
            case "RUB":
                return priceRUBFormatter;

            case "USD":
                return priceUSDFormatter;

            default:
                return priceFormatter;
        }
    }

    public static SimpleDateFormat getDateFormat() {
        return dateFormatter;
    }
}
