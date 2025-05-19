package apiFactus.factusBackend.integration.payment.paypal.dto;

import lombok.Data;

@Data
public class Link {
    private String href;
    private String rel;
    private String method;
}
