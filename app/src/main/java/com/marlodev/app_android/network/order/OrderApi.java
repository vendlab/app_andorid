package com.marlodev.app_android.network.order;

import com.marlodev.app_android.model.order.OrderResponse;
import com.marlodev.app_android.model.order.OrderTrackingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderApi {

    @GET("orders/history")
    Call<List<OrderResponse>> getOrderHistory();

    @GET("orders/{orderId}")
    Call<OrderResponse> getOrder(@Path("orderId") Long orderId);

    @GET("orders/{orderId}/tracking")
    Call<OrderTrackingResponse> getOrderTracking(@Path("orderId") Long orderId);

    @GET("orders/active")
    Call<List<OrderResponse>> getActiveOrders();



}