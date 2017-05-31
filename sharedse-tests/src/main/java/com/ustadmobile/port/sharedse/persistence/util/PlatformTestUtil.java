package com.ustadmobile.port.sharedse.persistence.util;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.ustadmobile.port.ormlite.persistence.generated.model.ClazzEntity;
import com.ustadmobile.port.ormlite.persistence.generated.model.OrganisationEntity;
import com.ustadmobile.port.ormlite.persistence.generated.model.PersonEntity;
import com.ustadmobile.port.ormlite.persistence.generated.model.SchoolEntity;
import com.ustadmobile.port.sharedse.buildconfig.TestConstantsSharedSe;

import java.sql.SQLException;

/**
 * Created by varuna on 5/2/2017.
 */

public class PlatformTestUtil {
    public static ConnectionSource connectionSource;


    public static Object getContext() {
        if(connectionSource == null) {
            try {
                connectionSource = new JdbcConnectionSource(TestConstantsSharedSe.TEST_JDBC_URL);

                /* Create classes. Need a better way to do this. TODO: Replace this */
                try {
                    System.out.println("onCreate");
                    for(Class clazz : TABLE_CLASSES) {
                        TableUtils.createTableIfNotExists(connectionSource, clazz);
                    }
                }catch(SQLException e) {
                    System.out.println("can't create database");
                    throw new RuntimeException(e);
                }

            }catch(SQLException se){
                se.printStackTrace();
                System.out.println( "DB Sync Sql Exception! " + se );
            }

        }

        return connectionSource;
    }

    public static Class[] TABLE_CLASSES = new Class[]{
            //Person.class, AnotherEntity.class, .. etc
            PersonEntity.class, ClazzEntity.class, OrganisationEntity.class, SchoolEntity.class,
    };
}

