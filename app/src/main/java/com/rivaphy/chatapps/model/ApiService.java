package com.rivaphy.chatapps.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJvvPNK8:APA91bE2A0wVuQhgg9KdMF4NZjVu19rbF6CRLeMuhz-R1Wyu-Shx44XiS8o-c8P5hzvFIIA3aAuNbBqkCx4hJGllaM05cajgA_oTMQPTA2NVLMBsYz0RcmbH8x_pm3Rxu--2Nm-AX1Rc"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotif(@Body Sender body);
}
