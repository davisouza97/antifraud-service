package br.com.matera.antifraudservice.client;

import br.com.matera.antifraudservice.dto.AccountDTO;
import br.com.matera.antifraudservice.exceptions.RestRequisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountProxy {

    private static final Logger log = LoggerFactory.getLogger(AccountProxy.class);

    AccountClient accountClient;

    public AccountProxy(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    public AccountDTO getAccountData(String accountId){
        try {
            log.debug("Iniciando requisição para /contas/" + accountId);
            return accountClient.getAccountData(accountId);
        }catch (Exception e){
            log.error("Erro ao fazer requisição ao endereço /contas/" + accountId + " - " + e.getMessage());
            throw new RestRequisitionException("Erro ao fazer requisição ao endereço contas/" + accountId, e);
        }
    }
}
