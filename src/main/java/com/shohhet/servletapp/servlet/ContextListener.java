package com.shohhet.servletapp.servlet;

import com.shohhet.servletapp.model.entity.EventEntity;
import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.model.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.FileRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.service.EventService;
import com.shohhet.servletapp.service.FileService;
import com.shohhet.servletapp.service.UserService;
import com.shohhet.servletapp.service.mapper.*;
import com.shohhet.servletapp.utils.TransactionInterceptor;
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

@WebListener()
public class ContextListener implements ServletContextListener {
    private SessionFactory sessionFactory;


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
        var currentSession = (Session) Proxy.newProxyInstance(ContextListener.class.getClassLoader(),
                new Class[]{Session.class},
                (proxy, method, methodArgs) -> method.invoke(sessionFactory.getCurrentSession(), methodArgs));

        var transactionInterceptor = new TransactionInterceptor(sessionFactory);
        var userRepository = new UserRepositoryImpl(currentSession, UserEntity.class);
        var fileRepository = new FileRepositoryImpl(currentSession, FileEntity.class);
        var eventRepository = new EventRepositoryImpl(currentSession, EventEntity.class);


        var userToDtoMapper = new UserToDtoMapper();
        var fileToDtoMapper = new FileToDtoMapper();
        var eventToDtoMapper = new EventToDtoMapper(userToDtoMapper, fileToDtoMapper);
        var dtoToUserMapper = new DtoToUserMapper();
        var dtoToFileMapper = new UploadDtoToFileMapper();

        var userService = new ByteBuddy()
                .subclass(UserService.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(transactionInterceptor))
                .make()
                .load(ContextListener.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor(UserRepositoryImpl.class, UserToDtoMapper.class, DtoToUserMapper.class)
                .newInstance(userRepository, userToDtoMapper, dtoToUserMapper);


        var fileService = new ByteBuddy()
                .subclass(FileService.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(transactionInterceptor))
                .make()
                .load(ContextListener.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor(FileRepositoryImpl.class, UserRepositoryImpl.class, EventRepositoryImpl.class, FileToDtoMapper.class, UploadDtoToFileMapper.class)
                .newInstance(fileRepository, userRepository, eventRepository, fileToDtoMapper, dtoToFileMapper);

        var eventService = new ByteBuddy()
                .subclass(EventService.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(transactionInterceptor))
                .make()
                .load(ContextListener.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor(EventRepositoryImpl.class, EventToDtoMapper.class)
                .newInstance(eventRepository, eventToDtoMapper);

        servletContext.setAttribute(UserService.class.getSimpleName(), userService);
        servletContext.setAttribute(FileService.class.getSimpleName(), fileService);
        servletContext.setAttribute(EventService.class.getSimpleName(), eventService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
