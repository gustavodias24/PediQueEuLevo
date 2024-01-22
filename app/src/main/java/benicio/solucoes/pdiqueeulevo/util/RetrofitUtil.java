package benicio.solucoes.pdiqueeulevo.util;

import benicio.solucoes.pdiqueeulevo.services.MercadoPagoService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    public static Retrofit createRetrofit(){
        return new Retrofit.Builder().baseUrl("https://api.mercadopago.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static MercadoPagoService createService(Retrofit retrofit){
        return retrofit.create(MercadoPagoService.class);
    }
}
