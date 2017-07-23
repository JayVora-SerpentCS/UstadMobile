package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 *
 * The Enrollment Class: This is an extended mapping of Classes and
 Users (Students). Teachers enroll students in their classes. The
 reason for this m2m through table is so that we can assign roll
 numbers and find out when they were added and updated. Students
 can be unasigned to the class by their active status and can re
 join by this status. This would not disrupt the roll numbers if
 a teacher wants to use those and returning students still have
 theirs. By default the roll numbers are assigned and updated in
 order of the enrollment date. There could be another method that
 changes the order of the roll numbers in the future.
 */

public interface Enrollment extends NanoLrsModel{
    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    Clazz getClazz();
    void setClazz(Clazz clazz);

    Person getUser();
    void setUser(Person person);

    long getDateJoined();
    void setDateJoined(long dateJoined);

    long getDateModified();
    void setDateModified(long dateModified);

    long getDateUpdated();
    void setDateUpdated(long dateUpdated);

    boolean isActive();
    void setActive(boolean active);

    String getNotes();
    void setNotes(String notes);

    int getRollNumber();
    void setRollNumber(int rollNumber);

}
