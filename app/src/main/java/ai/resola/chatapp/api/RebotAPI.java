package ai.resola.chatapp.api;

import ai.resola.chatapp.model.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by dotuan on 2017/03/22.
 */

public interface RebotAPI {
    @FormUrlEncoded
    @POST("/api/web")
    Call<ChatResponse> retrieveChatResponse(@Field("appid") String appid, @Field("s") String s, @Field("content") String content);
}
