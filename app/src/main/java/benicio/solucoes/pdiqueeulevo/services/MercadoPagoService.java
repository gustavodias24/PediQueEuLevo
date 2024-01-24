package benicio.solucoes.pdiqueeulevo.services;

import benicio.solucoes.pdiqueeulevo.model.BodyItems;
import benicio.solucoes.pdiqueeulevo.model.ResponseMercadoPagoModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MercadoPagoService {
    @POST("/checkout/preferences?access_token=APP_USR-1676541494858991-012315-8952871b379a822788606307ee722f47-1638695857")
    Call<ResponseMercadoPagoModel> criarLinkPagamento(@Body BodyItems bodyItem);
}
