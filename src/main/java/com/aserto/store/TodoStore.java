package com.aserto.store;

import com.aserto.model.Todo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;

public class TodoStore {
    private SessionFactory sessionFactory;

    public TodoStore() {
        // Only log sever errros from Hibernate
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        Configuration configuration = new Configuration()
                .addAnnotatedClass(Todo.class)
                .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                .setProperty("hibernate.connection.url", "jdbc:sqlite:mydb.db")
                .setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.format_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
    }

    public void saveTodo(Todo todo){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.merge(todo);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteTodoById(String id){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Todo todo = session.get(Todo.class, id);
        session.remove(todo);
        session.getTransaction().commit();
        session.close();
    }

    public void updateTodoById(String id, Todo todo){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Todo todoFromDB = session.get(Todo.class, id);
        todoFromDB.setCompleted(todo.getCompleted());
        session.getTransaction().commit();
        session.close();
    }

    public Todo getTodo(String id) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Todo todo = session.get(Todo.class, id);
        session.getTransaction().commit();
        session.close();

        return todo;
    }

    public Todo[] getTodos(){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Todo[] todoList = session.createQuery("SELECT t FROM Todo t", Todo.class).getResultList().toArray(new Todo[0]);
        session.close();

        return todoList;
    }
}
