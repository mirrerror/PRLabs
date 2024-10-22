package md.mirrerror.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyUtils {

    private static final double GBP_TO_MDL_EXCHANGE_RATE = 22.96;

    public static double convertGbpToMdl(double priceInGbp) {
        return Double.parseDouble(String.format("%.2f", priceInGbp * GBP_TO_MDL_EXCHANGE_RATE));
    }

    public static double convertMdlToGbp(double priceInMdl) {
        return Double.parseDouble(String.format("%.2f", priceInMdl / GBP_TO_MDL_EXCHANGE_RATE));
    }

}
