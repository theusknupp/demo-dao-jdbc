package model.dao;

import db.DB;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	
	public static SellerDao createSellerDao() { //Para não expor a implementação
		return new SellerDaoJDBC(DB.getConnection());
	}

}
