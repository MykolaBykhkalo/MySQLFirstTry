package com.mnemonicsdev.android.mysqlfirsttry;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Repository {

    private static final String DATABASE_URL = "jdbc:mysql://192.168.1.4:3306/myDB";
    private static final String USER_NAME = "max";
    private static final String USER_PASSWORD = "root";
    private static final String QUERY_INSERT_USER = "INSERT INTO users(name, surname, age) VALUES (?, ?, ?)";
    private static final String QUERY_GET_USER_BY_SURNAME = "SELECT * FROM users WHERE surname = ?";

    private static Repository repository;
    private Connection connection;
    private PreparedStatement st;

    private Repository() {


    }

    public static Repository getInstance() {
        if (repository == null) repository = new Repository();
        return repository;
    }

    synchronized boolean ConnectToDataBase() {

        Thread initialThread = new Thread() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWORD);
                    Log.d("tag", "Constructor OK!");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("tag", "Constructor Exception\n" + e);

                }
            }
        };
        initialThread.start();
        try {
            initialThread.join();
            return connection != null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return connection != null;
        }

    }


    synchronized void DisconnectDatabase() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void InsertUser(final User user) {
        Thread insertThread = new Thread() {
            @Override
            public void run() {
                try {
                    st = connection.prepareStatement(QUERY_INSERT_USER);
                    st.setString(1, user.getName());
                    st.setString(2, user.getSurName());
                    st.setInt(3, user.getAge());
                    st.executeUpdate();
                    Log.d("tag", "InsertUser OK!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d("tag", "InsertUser Exception\n" + e);
                }
            }
        };

        insertThread.start();
    }

    /*st = connection.prepareStatement(QUERY_GET_USER_BY_SURNAME);
                    st.setString(1, surname);
                    ResultSet resultSet = st.executeQuery();*/

    @SuppressLint("StaticFieldLeak")
    User getUserBySurname(String surname) {

        AsyncTask<Void, Void, User> asyncTask = new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... voids) {
                try {
                    st = connection.prepareStatement(QUERY_GET_USER_BY_SURNAME);
                    st.setString(1, surname);
                    ResultSet resultSet = st.executeQuery();
                    resultSet.next();
                    String name = resultSet.getString(2);
                    String surname = resultSet.getString(3);
                    int age = resultSet.getInt(4);
                    return new User(name, surname, age);

                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d("tag", "repository: return user exeption\n" + e);
                    return null;
                }

            }

        };
        asyncTask.execute();
        try {
            return (asyncTask).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("tag", "repository: get user exception\n" + e);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("tag", "repository: interrupted exception\n" + e);
            return null;
        }
    }


}

//    private static Repository repository;
//    private Formatter formatter;
//
//
//    private Repository(){
//        formatter = new Formatter();
//        /*try {
//            Class.forName("com.mysql.jdbc.Driver");
//            mysqlConnection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWORD);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/
//    }
//
//    public static Repository getInstance(){
//        if (repository == null) repository = new Repository();
//        return repository;
//    }
//
//
//
//
//    public int insertUser(User user){
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection mysqlConnection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWORD);
//            Statement st = mysqlConnection.createStatement();
////            return st.executeUpdate(String.valueOf(formatter.format("INSERT INTO users(name, surname, age) VALUES ('%s', '%s', %d)" ,
////                    user.getName(), user.getSurName(), user.getAge())));
//            return st.executeUpdate("INSERT INTO users(name, surname, age) VALUES ('%Ivan', '%Orlov', 25)");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    public String getUsers(){
//
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection mysqlConnection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWORD);
//            Statement st = mysqlConnection.createStatement();
//            ResultSet rs = st.executeQuery("SELECT * FROM mydb.users");
//            return rs.getString(1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Exeption";
//        }
//
//    }
//
//
//    public User getUserBySurName(String userSurname){
//
//        return new User();
//    }
//
//


