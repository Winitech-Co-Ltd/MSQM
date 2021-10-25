package com.peru.ncov2019.ml.cmm.vo;

import org.apache.commons.collections.map.ListOrderedMap;


/**
 * Clase de servicio en común
 * @author Winitech (지승, Park)
 *
 */
@SuppressWarnings("serial")
public class MlMap extends ListOrderedMap{

	@Override
	public Object put(Object key, Object value) {
		
		//return super.put(((String)key).toUpperCase(), (value == null) ? null : value);
		return super.put(((String)key).toUpperCase(), (value == null) ? null : String.valueOf(value));
	}
}
