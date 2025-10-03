package br.com.matera.antifraudservice.dto;

import br.com.matera.antifraudservice.enums.FraudStatus;

public class UpdateAnalysisStatusDTO {

    private FraudStatus status;

    public FraudStatus getStatus() {
        return status;
    }

    public void setStatus(FraudStatus status) {
        this.status = status;
    }
}
