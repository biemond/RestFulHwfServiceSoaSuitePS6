package nl.amis.common.servlet;

import java.io.IOException;

import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import weblogic.servlet.security.ServletAuthentication;
import static weblogic.servlet.security.ServletAuthentication.AUTHENTICATED;


public class LoginServlet extends HttpServlet {
    private static final ServletAuthentication AUTH =
        new ServletAuthentication("username", "password");

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException,
                                                            IOException {

        String op = request.getParameter("op");

        System.out.println("doPost: " + op);

        if ("logout".equals(op)) {
            AUTH.invalidateAll(request);
            return;
        }

        if ("getUsername".equals(op)) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                response.getWriter().write(principal.getName());
            }
            return;
        }

        if ("login".equals(op)) {
            if (AUTH.weak(request, response) != AUTHENTICATED) {
                System.out.println("login forbidden");
                response.setStatus(SC_FORBIDDEN);
                return;
            }
            System.out.println("login");

            AUTH.generateNewSessionID(request);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        doPost(request, response);
    }
}
