package com.lzzcms.dao.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public   interface  BaseDao<T> {
	/**
	 * 保存一个实体
	 * @param t实体对象
	 */
	 Serializable saveEntity(T t);
	/**
	 * 保存或者更新一个实体
	 * @param t
	 */
	 void    saveOrUpdate(T t);
	/**
	 * 删除一个实体
	 * @param t
	 */
	 void deleteEntity(T t);
	/**
	 * 更新一个实体
	 * @param t
	 */
	 void updateEntity(T t);
	/**
	 * 通过ID来查询一个实体
	 * @param clazz
	 * @param id
	 * @return
	 */
	 T  getEntityById(Integer id);
	/**
	 * 通过Hql语句来查询，返回list的实体集合:from ColumnInfo
	 * @param hql
	 * @param params默认是[]
	 * @return
	 */
	 List<T> findByHql(String hql,Object... params);
	 /**
	 * 通过Hql语句来查询，返回list的map集合
	 * @param hql:select new Map(c.id as ID,c.name AS NAME) from ColumnInfo c
	 * @param params
	 * @return
	 */
	 List<Map<String, Object>> queryForListByHql(String hql,Object... params);
	/**
	 * 通过sql来查询，返回list的实体集合,内部由sqlquery实现
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 */
	 List<T> findBySql(String sql,Object... params);
	 /**
		 * 通过sql来查询，返回list的实体集合,内部由jdbcTemplate实现。主要使用1
		 * @param clazz
		 * @param sql
		 * @param params
		 * @return
		 */
	List<T> findBySql2(String sql,Object... params);
	 /**
	 * 通过sql语句来查询，返回list的map集合:select id,name from stu where addr=?。主要使用2
	 * 当返回值是list<string>也可以用这种
	 * @param hql
	 * @param params
	 * @return
	 */
	 List<Map<String, Object>> queryForList(String sql,Object...objects);
	/**
	 * 执行Hql形式的DML
	 * @param hql
	 * @param params
	 */
	 void    executeHql(String hql,Object... params);
	/**
	 * 执行sql形式的DML、DDL。主要使用3
	 * @param sql:delete from lzz_columninfo  where id in ( "+ids+")
	 * @param parmas
	 */
	 int    executeSql(String  sql,Object... parmas);
	/**
	 * 使用hql返回一个结果
	 * @param <E>
	 * @param hql
	 * @param params
	 * @return
	 */
	 <E>  E    uniqueResult(String hql,Object... params );
	 /**
	 * 使用sql返回map。主要使用4
	 * @param sql:sb.append("select * from sc_basemodel bm join "+additionalTable +" a on bm.comm_id=a.comm_id_fk where bm.comm_id=? ");
	 * @param params
	 * @return
	 */
	Map<String, Object> queryForMap(String sql,Object...objects);
	/**
	 * @param sql:select count(*) from sc_columninfo
	 * @param objects
	 * @return
	 */
	Integer queryForInt(String sql,Object...objects);
	
	Object queryObject(Class<?> clazz,Integer id);
	
	String queryForString(String sql,Object...objects);
	List<String> queryForListString(String sql, Object... objects);
	List<Integer> queryForListInteger(String sql,Object... objects);
	/**
	 * 同样的sql，需要执行多次，每次执行的参数不一样
	 * insert into link_admin_role values(?,?)
	 * @param sql
	 * @param batchArgs
	 */
	void batchExecuteSql(String sql,List<Object[]> batchArgs);
	/*
	 * 查询条数
	 */
	Long queryForLong(String sql,Object...objects);
}
