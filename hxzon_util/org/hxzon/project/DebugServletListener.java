package org.hxzon.project;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugServletListener implements ServletContextListener, ServletContextAttributeListener, HttpSessionActivationListener, HttpSessionAttributeListener, HttpSessionListener,
        ServletRequestAttributeListener, ServletRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(DebugServletListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.debug("create session:" + event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        logger.debug("destroy session:" + event.getSession().getId());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        logger.debug("session add attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getSession().getId());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        logger.debug("session remove attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getSession().getId());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        logger.debug("session replace attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getSession().getId());
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent event) {
        logger.debug("session activate:" + event.getSession().getId());
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent event) {
        logger.debug("session passivate:" + event.getSession().getId());
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        logger.debug("context add attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletContext());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        logger.debug("context remove attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletContext());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        logger.debug("context replace attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("context destroy:" + event.getServletContext());

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.debug("context init:" + event.getServletContext());
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent event) {
        logger.debug("request add attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletRequest());
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent event) {
        logger.debug("request remove attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletRequest());
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent event) {
        logger.debug("request replace attribute:" + event.getName() + "=" + event.getValue() + "[" + event.getServletRequest());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        logger.debug("request destroy:" + event.getServletRequest());

    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        logger.debug("request init:" + event.getServletRequest());
    }

}
