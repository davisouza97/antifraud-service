package br.com.matera.antifraudservice.client;

import br.com.matera.antifraudservice.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-client", url = "${clients.account.url}")
public interface AccountClient {


    @GetMapping(value = "/contas/{idConta}")
    AccountDTO getAccountData(@PathVariable("idConta") String idConta);


}
