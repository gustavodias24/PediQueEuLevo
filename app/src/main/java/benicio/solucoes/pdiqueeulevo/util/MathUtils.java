package benicio.solucoes.pdiqueeulevo.util;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

public class MathUtils {
    public static double converterParaDouble(String valor) {
        // Remover pontos e substituir vírgulas por pontos
        String valorFormatado = valor.replaceAll("\\.", "").replace(",", ".").replace(" ", "").replace("R$", "");

        try {
            return Double.parseDouble(valorFormatado);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Tratar erro de conversão
        }
    }

    public static String formatarMoeda(double valor) {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);

        return formatoMoeda.format(valor);
    }

    public static String padWithZeros(String input, int desiredLength) {
        StringBuilder result = new StringBuilder(input);

        while (result.length() < desiredLength) {
            result.insert(0, "0");
        }

        return result.toString();
    }
}
