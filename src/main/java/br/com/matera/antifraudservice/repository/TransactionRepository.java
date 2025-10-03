package br.com.matera.antifraudservice.repository;


import br.com.matera.antifraudservice.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByIdConta(String idConta);

    @Query("""
        SELECT t
        FROM TransactionEntity t
        WHERE (:accountId IS NULL OR t.idConta = :accountId)
          AND (
                (:startDate IS NULL OR t.dataHoraTransacao >= :startDate) AND
                (:endDate IS NULL OR t.dataHoraTransacao <= :endDate)
              )
    """)
    List<TransactionEntity> findWithFilter(
            @Param("accountId") String accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT t
        FROM TransactionEntity t
        WHERE t.idConta = :idConta
          AND t.manualAnalysis = false
          AND t.dataHoraPersistencia BETWEEN :startDate AND :endDate
    """)
    List<TransactionEntity> findTransactionsWithoutManualAnalysis(
            @Param("idConta") String idConta,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
