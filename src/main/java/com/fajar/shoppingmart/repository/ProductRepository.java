package com.fajar.shoppingmart.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fajar.shoppingmart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query( "select p from Product p where p.name LIKE CONCAT('%',:name,'%')")
	public List<Product> getByLimitAndOffset(Pageable pageable, @Param("name") String name);

	@Query(nativeQuery = true, value = "select sum(product_flow.count) as productCount  from product   "
			+ "left join product_flow on product.id = product_flow.product_id "
			+ "left join transaction on transaction.id = product_flow.transaction_id  where transaction.type = 'SELLING' and  "
			+ "date_part('month', transaction.transaction_date) = ?1 and  date_part('year', transaction.transaction_date) = ?2"
			+ " and product.id = ?3")
	public Object findProductSales(int month, int year, Long productId);
 
	@Query(nativeQuery = true, value = "select sum(product_flow.count) as productCount from product_flow  "
			+ " left join transaction on transaction.id = product_flow.transaction_id "
			+ " where transaction.type = 'SELLING' and product_flow.product_id = ?3"
			+ " and transaction.transaction_date >= ?1 and transaction.transaction_date <= ?2 ")
	public BigDecimal findProductSalesBetween(Date period1, Date period2, Long productId);

	@Query(nativeQuery = true, value="select * from product where image_url is not null limit 7")
	public List<Product> getRandomProducts();
	
	@Query(nativeQuery = true, value="select  *  from product  " + 
			"left join product_flow on product_flow.product_id = product.id " + 
			"left join transaction on product_flow.transaction_id = transaction.id " + 
			"left join supplier on supplier.id = transaction.supplier_id " + 
			"where transaction.supplier_id = ?1 group by product.id")
	public List<Product> getProductsSuppliedBySupplier(long supplierId);

	@Query( value = "select count(p) from Product p")
	public BigInteger getTotalProduct();
	
	

}