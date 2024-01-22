package benicio.solucoes.pdiqueeulevo.services;

import benicio.solucoes.pdiqueeulevo.model.BodyItems;
import benicio.solucoes.pdiqueeulevo.model.ResponseMercadoPagoModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MercadoPagoService {
    @POST("/checkout/preferences?access_token=TEST-3237615834213855-061314-089b4e11d795e4c0347b6eca3f1c9791-782330883")
    Call<ResponseMercadoPagoModel> criarLinkPagamento(@Body BodyItems bodyItem);
}
