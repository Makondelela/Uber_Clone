package Models;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("json")
    Call<Result> getDistance(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("key") String apiKey
    );
}
