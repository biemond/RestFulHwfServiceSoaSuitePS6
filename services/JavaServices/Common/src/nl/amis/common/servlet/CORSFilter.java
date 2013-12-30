package nl.amis.common.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSFilter implements Filter {
    private FilterConfig _filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        _filterConfig = filterConfig;
    }

    public void destroy() {
        _filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException,
                                                   ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String origin = req.getHeader("Origin");

        if (origin != null && !origin.isEmpty()) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Allow-Credentials", "true");

            String corsMethod = req.getHeader("Access-Control-Request-Method");
            if (corsMethod != null && !corsMethod.isEmpty() && "OPTIONS".equalsIgnoreCase(req.getMethod())) {
                res.setHeader("Access-Control-Allow-Methods", corsMethod);
                res.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
