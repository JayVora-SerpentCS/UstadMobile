package com.ustadmobile.port.sharedse.persistence.manager;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Organisation;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;
import com.ustadmobile.port.sharedse.persistence.proxy.School;

import java.sql.SQLException;
import java.util.List;

/**
 * TODO: This needs to extend BaseManager from NanoLrs for things like
 * persist, makenew, findbyid, etc
 *
 * Created by Varuna on 4/30/2017.
 */
public interface SchoolManager extends NanoLrsManager {
    //Person specific methods  to work with.
    List<School> findAllBySchoolName(Object dbContext, String schoolName) throws SQLException;
    List<School> findAllByLattitudeLongitude(Object dbContext, Long lattitude, Long longitude) throws SQLException;
    List<School> findAllByOrganisation(Object dbContext, Organisation organisation) throws SQLException;

}
