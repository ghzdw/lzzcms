package com.lzzcms.dao.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.lzzcms.utils.PageContext;


public abstract class AbstractBaseDao<T> implements BaseDao<T> {
	//也可以写成private
	@Resource
	protected SessionFactory sessionFactory;
	@Resource
	protected JdbcTemplate  jdbcTemplate;
    private Class<T> clazz;//get、被类的构造、list<T> findbysql的时候用到了
	@SuppressWarnings("unchecked")
	public AbstractBaseDao(){
		ParameterizedType type= (ParameterizedType) this.getClass().getGenericSuperclass();//得到this的泛型父类，即将来的 AbstractBaseDao<PageInterceptor>
		Type param=  (type.getActualTypeArguments())[0];//得到泛类型PageInterceptor
		this.clazz=(Class<T>) param;
	}
	private Session getCurrentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Serializable saveEntity(T t) {
		return getCurrentSession().save(t);
	}
	@Override
	public void saveOrUpdate(T t) {
		getCurrentSession().saveOrUpdate(t);
	}
	@Override
	public void deleteEntity(T t) {
		getCurrentSession().delete(t);
	}

	@Override
	public void updateEntity(T t) {
		getCurrentSession().update(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEntityById(Integer id) {
		return (T) getCurrentSession().get(clazz, id);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByHql(String hql, Object... params) {
		Query query=getCurrentSession().createQuery(hql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		if (PageContext.getPageUtil()!=null) {//前端增加评论的时候不走拦截器，因此为null
			if (PageContext.getPageUtil().isNeedPage()) {
				int pageNow=PageContext.getPageUtil().getPageNow();
				int pageSize=PageContext.getPageUtil().getPageSize();
				int start=(pageNow-1)*pageSize;
				query.setFirstResult(start);
				query.setMaxResults(pageSize);
			}	
		}	
		return query.list();
	}
	@SuppressWarnings("unchecked") 	@Override
	public List<Map<String, Object>> queryForListByHql(String hql,Object... params){
		Query query=getCurrentSession().createQuery(hql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			query.setFirstResult(start);
			query.setMaxResults(pageSize);
		}	
		 return query.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBySql(String sql, Object... params) {
		SQLQuery sqlQuery=getCurrentSession().createSQLQuery(sql);
			for (int i = 0; i < params.length; i++) {
				sqlQuery.setParameter(i, params[i]);
			}
		sqlQuery.addEntity(clazz);
		return sqlQuery.list();
	}
	@Override
	public List<T> findBySql2(String sql, Object... params) {
		List<T> list = jdbcTemplate.query(sql, params,new RowMapper<T>(){
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				return null;
			}
		});
		return list;
	}
	@Override
	public List<Map<String, Object>> queryForList(String sql,Object...objects){
		return jdbcTemplate.queryForList(sql, objects);
 	}
	@Override
	public List<String> queryForListString(String sql,Object...objects){
		return jdbcTemplate.queryForList(sql, String.class, objects);
	}
	@Override
	public List<Integer> queryForListInteger(String sql,Object...objects){
		return jdbcTemplate.queryForList(sql, Integer.class, objects);
	}
	@Override
	public void executeHql(String hql, Object... params) {
		Query query=getCurrentSession().createQuery(hql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		query.executeUpdate();
	}

	@Override
	public int executeSql(String sql, Object... params) {
		SQLQuery sqlQuery=getCurrentSession().createSQLQuery(sql);
			for (int i = 0; i < params.length; i++) {
				sqlQuery.setParameter(i, params[i]);
			}
		return	sqlQuery.executeUpdate();
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E>  E    uniqueResult(String hql,Object... params ){
		Query query=getCurrentSession().createQuery(hql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
			return (E) query.uniqueResult();
	}
	@Override 
	public Map<String, Object> queryForMap(String sql,Object...objects){
		return jdbcTemplate.queryForMap(sql, objects);
	}
	@Override
	public Integer queryForInt(String sql,Object...objects){
		return  jdbcTemplate.queryForObject(sql, objects, Integer.class);
	}
	@Override 
	public Object queryObject(Class<?> clazz,Integer id) {
		return  getCurrentSession().get(clazz, id);
	}
	
	@Override
	public String queryForString(String sql, Object... objects) {
		return  jdbcTemplate.queryForObject(sql, objects, String.class);
	}
	@Override
	public void batchExecuteSql(String sql,List<Object[]> batchArgs) {
			jdbcTemplate.batchUpdate(sql, batchArgs);
	}
	@Override
	public Long queryForLong(String sql, Object... objects) {
		return jdbcTemplate.queryForObject(sql, objects, Long.class);
	}
	
}
