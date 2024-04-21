package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	
	public static SellerDao createSellerDao() { //Para não expor a implementação
		return new SellerDaoJDBC();
	}

}
