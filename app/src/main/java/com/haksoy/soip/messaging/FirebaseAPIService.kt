package com.haksoy.soip.messaging

import com.haksoy.soip.data.notification.MessageBody
import com.haksoy.soip.data.notification.MessageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseAPIService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA3_TIxcE:APA91bHbzHH_p0j3nqLLLjMpYOGNgAphtmtWW5prsXACWQ8rfCew1epj5QnvSeP2bZLXUl05QD2khlNf889am2yI1K_PfxFf7AgthwayNoIORZbTX9swkgMLAKbP3mAD0F46kX64Ah6E"
    )
    @POST("fcm/send")
    fun sendNotification(@Body messageBody: MessageBody):Call<MessageResponse>

}