package com.peru.ncov2019.ml.sqs.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
/**
 * Clase dedicada a la función de consultar o manipular datos utilizando el DB (base de datos) 
 * @author Winitech (지승, Park)
 *
 */
@Repository("ScmDao")
public class ScmDao {

	/**
	 * Componentes que proporcionan API para ejecución de SQL y control de transacciones
	 */
	@Resource(name = "scmSession") 
	private SqlSession sqlSession;
	
	/**
	 * Consulta de datos
	 * @param queryId	NameSpace escrito en XML
	 * @return List 	Resultado de consulta Query
	 * @throws Exception
	 */
	public <E> List<E> selectList(String queryId) throws Exception{
		return sqlSession.selectList(queryId);
	}
	
	/**
	 * Consulta de datos
	 * @param queryId	NameSpace escrito en XML
	 * @param param		Condiciones para la consulta de datos	
	 * @return List     Resultado de consulta Query
	 * @throws Exception
	 */
	public <E> List<E> selectList(String queryId, Object param) throws Exception{
		return sqlSession.selectList(queryId,param);
	}
	
	/**
	 * Consulta de datos
	 * @param queryId	NameSpace escrito en XML
	 * @param param		Condiciones para la consulta de datos
	 * @return T		Resultado de consulta Query
	 * @throws Exception
	 */
	public <T> T selectOne(String queryId, Object param) throws Exception{
		return sqlSession.selectOne(queryId,param);
	}
	
	/**
	 * 
	 * @param queryId	NameSpace escrito en XML
	 * @param param		Datos a ingresar
	 * @return int  	Exitoso o no exitoso Query 
	 * @throws Exception
	 */
	public int insert(String queryId, Object param) throws Exception{
		return sqlSession.insert(queryId,param);
	}
	
	/**
	 * 
	 * @param queryId	NameSpace escrito en XML
	 * @param param		Condiciones sobre la eliminación
	 * @return int		Exitoso o no exitoso Query 
	 * @throws Exception
	 */
	public int delete(String queryId, Object param) throws Exception{
		return sqlSession.delete(queryId,param);
	}
	
	/**
	 * 
	 * @param queryId	NameSpace escrito en XML
	 * @param param		Datos a modificar
	 * @return int 		Exitoso o no exitoso Query 
	 * @throws Exception
	 */
	public int update(String queryId, Object param) throws Exception{
		return sqlSession.update(queryId,param);
	}
}
