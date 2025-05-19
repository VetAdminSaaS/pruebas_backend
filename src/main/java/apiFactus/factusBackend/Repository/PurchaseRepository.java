package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Purchase;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findByPaymentStatus(PaymentStatus paymentStatus);
    @Query("SELECT SUM(p.total) FROM Purchase p WHERE p.paymentStatus = :status")
    Double sumTotalByPaymentStatus(@Param("status") PaymentStatus status);
    List<Purchase> findByUserId(Integer userId);
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.number IS NOT NULL")
    Long countByNumberNotNull();
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM purchases " +
            "WHERE created_at >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') " +
            "AND created_at < DATE_TRUNC('month', CURRENT_DATE)",
            nativeQuery = true)
    Double sumTotalSalesPreviousMonthNative();
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM purchases " +
            "WHERE created_at >= DATE_TRUNC('month', CURRENT_DATE)",
            nativeQuery = true)
    Double sumTotalSalesCurrentMonthNative();
    @Query(value = "SELECT " +
            "COALESCE(SUM(CASE WHEN created_at >= DATE_TRUNC('month', CURRENT_DATE) AND payment_status = 'PAID' THEN total END), 0) AS total_actual, " +
            "COALESCE(SUM(CASE WHEN created_at >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') " +
            "AND created_at < DATE_TRUNC('month', CURRENT_DATE) AND payment_status = 'paid' THEN total END), 0) AS total_anterior " +
            "FROM purchases", nativeQuery = true)
    List<Object[]> getSalesComparison();
    List<Purchase> findTop10ByUserAndPaymentStatusOrderByCreatedAtDesc(Usuario user, PaymentStatus paymentStatus);






}
