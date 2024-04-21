package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) { //Dependência da conexão com o banco de dados.
		this.conn = conn;
	}
	
	public void insert(Seller obj) {
	
		
	}

	@Override
	public void update(Seller obj) {
	
		
	}

	@Override
	public void deleteById(Integer id) {
	
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null; //Consulta a ser feita
		ResultSet rs = null; //Resultado da consulta
		try {
			st = conn.prepareStatement( //Consulta
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.ID "
					+ "WHERE seller.Id = ?");
			
			st.setInt(1, id); //Valor que vai para "?"
			rs = st.executeQuery();
			if (rs.next()) { //testando se veio algum resultado
				Department dep = new Department();
				dep.setId(rs.getInt("DepartmentId"));
				dep.setName(rs.getString("DepName"));
				Seller obj = new Seller();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setEmail(rs.getString("Email"));
				obj.setBaseSalary(rs.getDouble("BaseSalary"));
				obj.setBirthDate(rs.getDate("BirthDate"));
				obj.setDepartment(dep);
				return obj;
				
			}
			return null; //se não vier resultado, retorna nulo
		} catch (SQLException e) {
			throw new DbException(e.getMessage());			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}

	@Override
	public List<Seller> findAll() {
		return null;
	}

}