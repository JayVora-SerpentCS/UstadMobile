package com.ustadmobile.port.sharedse.persistence.manager;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Organisation;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;
import com.ustadmobile.port.sharedse.persistence.proxy.School;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by varuna on 5/11/2017.
 */

public interface OrganisationManager extends NanoLrsManager {
    List<Organisation> findAllByOrganisationtName (Object dbContext, String organisationName) throws SQLException;
    List<Organisation> findAllPublic(Object dbContext) throws SQLException;
    List<Organisation> findAllPrivate(Object dbContext) throws SQLException;
    List<School> findAllSchoolsInThisOrgnaisation(Object dbContext, Organisation organisation) throws SQLException;

    //TODO:
    //List<Org_alert_settings> findOrgAlertSettings(Object dbContext, Organisation organistaion) throws SQLException;
    //String getOrganisationCode(Object dbContext, Organisation organisation) throws SQLException;
    //List<Person> getAllPersonsInThisOrganisation(Object dbContext, Organisation organisation) throws SQLException;
    //List<Clazz> getAllClassesInThisOrganisation(Object dbContext, Organisation organisation) throws SQLException;
    //HolidayCalendar getHolidayCalendar(Object dbContext, Organisation Organisation) throws SQLException;
    //UMCloudPackage getUMCloudPackage(Object dbContext, Organisation UMCloudPackage) throws SQLException;
}
