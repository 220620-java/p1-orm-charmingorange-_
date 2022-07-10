package com.revature.orm;

import java.util.List;

public interface Orm_Interface {
	
	<T> T findById(Class<T> objectClass, Object key);

    <T> List<T> findAll(Class<T> objectClass);

    //<T> List<T> findAll(Class<T> objectClass, Criteria criteria);

    <T> T create(T object);

    <T> boolean update(T object);

    <T> boolean delete(T object);
	
	
	
	

}
