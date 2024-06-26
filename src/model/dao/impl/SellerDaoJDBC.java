package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());	
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) { //Verificando quantidade de linhas alteradas para ver se houve alteração
				ResultSet rs = st.getGeneratedKeys();
					if (rs.next()) {
						int id = rs.getInt(1);
						obj.setId(id);
					}
					DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No arrows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());	
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
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
				Department dep = instantiateDepartment(rs); //Chamando função de instanciação do departamento
				Seller obj = instantiateSeller(rs, dep); //Chamando função de instanciação de vendedor
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
 
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException { //Ja esta tratando exceção na chamada da função
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException { //Ja está tratando exceção na chamada da função
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null; //Consulta a ser feita
		ResultSet rs = null; //Resultado da consulta
		try {
			st = conn.prepareStatement( //Consulta
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>(); //Map vazio, para guardar departamento instanciado
			
			while (rs.next()) { //testando se veio algum resultado -- whiller pois, pode vir mais de um resultado
				
				Department dep = map.get(rs.getInt("DepartmentId")); //Buscando no map se existe algum departamento com devido id
				
				if (dep == null) { //So instancia o departamento se for nullo, para não ter dois departamentos iguais
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); //Salvando departamento no map
				}
				Seller obj = instantiateSeller(rs, dep); //Chamando função de instanciação de vendedor
				list.add(obj);
				
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null; //Consulta a ser feita
		ResultSet rs = null; //Resultado da consulta
		try {
			st = conn.prepareStatement( //Consulta
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
				    + "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, department.getId()); //Valor que vai para "?"
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>(); //Map vazio, para guardar departamento instanciado
			
			while (rs.next()) { //testando se veio algum resultado -- whiller pois, pode vir mais de um resultado
				
				Department dep = map.get(rs.getInt("DepartmentId")); //Buscando no map se existe algum departamento com devido id
				
				if (dep == null) { //So instancia o departamento se for nullo, para não ter dois departamentos iguais
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); //Salvando departamento no map
				}
				Seller obj = instantiateSeller(rs, dep); //Chamando função de instanciação de vendedor
				list.add(obj);
				
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
		
	}

}
