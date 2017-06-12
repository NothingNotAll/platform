package nna.base.protocol.http;

import nna.Marco;
import nna.base.protocol.dispatch.AbstractDispatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * Servlet Filter implementation class Http
 */
public class Http extends AbstractDispatch implements Filter {
    /**
     * Default constructor. 
     */
    public Http() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			Map<String,String[]> map=request.getParameterMap();
			HashMap<String,String[]> newMap=new HashMap<String, String[]>(map.size()+2);
			newMap.putAll(map);
			newMap.put(Marco.HEAD_ENTRY_CODE,new String[]{((HttpServletRequest)request).getRequestURI()});
            newMap.put(Marco.HEAD_ENTRY_SESSION_NM,new String[]{((HttpServletRequest) request).getSession().getId()});
			dispatch(newMap);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {

	}

}
