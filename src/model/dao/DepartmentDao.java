package model.dao;

import java.util.List;
import model.entities.Department;

//Model não compreende apenas as entidades, mas também as classes que fazem transformações dessas classes.

public interface DepartmentDao {
	
	void insert(Department obj);
	void update(Department obj);
	void deleteById(Integer id);
	Department findById(Integer id); //Responsavél por consultar no banco de dados o objeto com o id referênciado 
	List<Department> findAll();

}
