package model.dao;

import java.util.List;

import model.entities.Seller;

public interface SellerDao {
	
	void insert(Seller obj);
	void update(Seller obj);
	void deleteById(Integer id);
	Seller findById(Integer id); //Responsavél por consultar no banco de dados o objeto com o id referênciado 
	List<Seller> findAll();

}
