package com.shohhet.servletapp.servlet;

import com.shohhet.servletapp.model.entity.EventEntity;
import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.model.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.FileRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.service.UserService;
import com.shohhet.servletapp.service.mapper.EventToDtoMapper;
import com.shohhet.servletapp.service.mapper.UserToDtoMapper;
import com.shohhet.servletapp.utils.TransactionInterceptor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.lang.reflect.Proxy;

@WebListener
public class ContextListener implements ServletContextListener {
    private SessionFactory sessionFactory;
    private Session currentSession;
    private EventRepositoryImpl eventRepository;
    private FileRepositoryImpl fileRepository;
    private UserRepositoryImpl userRepository;


    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        var servletContext = contextEvent.getServletContext();
        var configuration = new Configuration()
                .addAnnotatedClass(EventEntity.class)
                .addAnnotatedClass(UserEntity.class)
                .addAnnotatedClass(FileEntity.class)
                .setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy())
                .configure();
        sessionFactory = configuration.buildSessionFactory();
        currentSession = (Session) Proxy.newProxyInstance(ContextListener.class.getClassLoader(),
                new Class[]{Session.class},
                (proxy, method, methodArgs) -> method.invoke(sessionFactory.getCurrentSession(), methodArgs));

        userRepository = new UserRepositoryImpl(currentSession, UserEntity.class);
        var eventToDtoMapper = new EventToDtoMapper();
        var userToDtoMapper = new UserToDtoMapper(eventToDtoMapper);
        var transactionInterceptor = new TransactionInterceptor(sessionFactory);
        var userService = new ByteBuddy()
                .subclass(UserService.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(transactionInterceptor))
                .make()
                .load(ContextListener.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor(UserRepositoryImpl.class, UserToDtoMapper.class)
                .newInstance(userRepository, userToDtoMapper);

        servletContext.setAttribute(UserService.class.getSimpleName(), userService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
