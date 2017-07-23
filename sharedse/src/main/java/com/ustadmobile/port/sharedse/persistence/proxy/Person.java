package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

/**
 * TODO: This should extend NanoLrs's BaseModel for things like
 * primary key, common fields, last modified, last updated, pushed,
 * etc and common serialised toString fromString methods to be used
 * in Sync API.
 *
 * Created by Varuna on 4/30/2017.
 */

public interface Person extends NanoLrsModel{

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    @PrimaryKeyAnnotationClass(str = "pk")
    String getUUID();

    void setUUID(String uuid);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    long getLastLogin();
    void setLastLogin(long lastLogin);

    boolean isSuperuser();
    void setSuperuser(boolean superuser);

    String getEmail();
    void setEmail(String email);

    boolean isStaff();
    void setStaff(boolean staff);

    boolean isActive();
    void setActive(boolean active);

    long getDateJoined();
    void setDateJoined(long dateJoined);

    String getWebsite();
    void setWebsite(String website);

    String getCompanyName();
    void setCompanyName(String companyName);

    String getJobTitle();
    void setJobTitle(String jobTitle);

    long getDateOfBirth();
    void setDateOfBirth(long dateOfBirth);

    String getAddress();
    void setAddress(String address);

    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);

    String getGender();
    void setGender(String gender);

    boolean isAdminApproved();
    void setAdminApproved(boolean adminApproved);

    Organisation getOrganisationRequested();
    void setOrganisationRequested(Organisation organisationRequested);

    //Image: avatar avatar = ImageField

    String getNotes();
    void setNotes(String notes);

    long getLastActivityDate();
    void setLastActivityDate(long lastActivityDate);

    String getCustomRollNumber();
    void setCustomRollNumber(String customRollNumber);

    /* NEW: Setting organisation from Person itself - Might not need UserOrganisation*/
    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    /* NEW: Setting role from Person itself - Might not need UserRole*/
    Role getRole();
    void setRole(Role role);

}
