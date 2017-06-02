package nna.base.protocol.http;

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
public class Http extends AbstractDispatch<HttpServletRequest, HttpServletResponse> implements Filter {
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
			dispatch((HttpServletRequest) request,(HttpServletResponse)response);
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

    public OutputStream getOutPutStream(HttpServletResponse response) throws IOException {
        return response.getOutputStream();
    }
    public Map<String,String[]> getReqColMap(HttpServletRequest request) {
	    return request.getParameterMap();
    }
    public String[] getPlatformEntryId(HttpServletRequest request) {
		return new String[]{request.getRequestURI(),request.getSession().getId()};
	}
}
