package com.ecommerce.pedido.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookMercadoPagoDTO {

    private String id;
    private String action;
    private String type;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("application_id")
    private String applicationId;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    @JsonProperty("user_id")
    private String userId;

    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String id;
        private String status;

        @JsonProperty("status_detail")
        private String statusDetail;

        @JsonProperty("external_reference")
        private String externalReference;

        @JsonProperty("total_amount")
        private String totalAmount;

        @JsonProperty("total_paid_amount")
        private String totalPaidAmount;
    }
}
