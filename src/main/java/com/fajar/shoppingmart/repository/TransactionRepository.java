package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.constants.TransactionType;
import com.fajar.shoppingmart.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	public List<Transaction> findByType(TransactionType type);

	public List<Transaction> findByTypeAndIdGreaterThan(TransactionType type, long l);

	public List<Transaction> findByTypeAndIdGreaterThanAndIdLessThan(TransactionType type, long from, long to);

	@Query( value = "select YEAR(  transaction .transactionDate) from Transaction transaction  where "
			+ " transaction .transactionDate is not null order by transaction.transactionDate asc")
	public List<Object> findTransactionYearAsc(Pageable pageable);

	@Query(nativeQuery = true, value = "select * from  transaction  "
			+ "left join product_flow on product_flow.transaction_id = transaction.id "
			+ "where product_flow.product_id = ?1 and  transaction . type  = 'PURCHASING' "
			+ "group by transaction.supplier_id, transaction.id , product_flow.id limit ?2 offset ?3")
	public List<Transaction> findProductSupplier(Long id, int limit, int offset);

	@Query(nativeQuery = true, value = "select  * from  transaction  left join product_flow on  product_flow .transaction_id= transaction .id  "
			+ "WHERE  product_flow .product_id = ?1  and  transaction . type  = 'PURCHASING' "
			+ "order by  transaction .transaction_date asc limit 1")
	public List<Transaction> findFirstTransaction(Long productId);

	@Query("select transaction from Transaction transaction where type=?1"
			+ " and YEAR(transaction.transactionDate) = ?3 and MONTH(transaction.transactionDate ) = ?2 ")
	public List<Transaction> findTransactionByTypeAndPeriod(String type, int month, int year);

	public Transaction findTop1ByCode(String code);

}