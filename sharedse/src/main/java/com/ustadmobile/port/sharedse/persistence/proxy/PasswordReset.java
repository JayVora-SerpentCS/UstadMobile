package com.ustadmobile.port.sharedse.persistence.proxy;

import com.j256.ormlite.field.types.DateTimeType;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

/**
 * Created by varuna on 5/24/2017.
 */

public interface PasswordReset extends NanoLrsModel{
    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    @PrimaryKeyAnnotationClass(str="pk")
    String getUUID();

    void setUUID(String uuid);

    Person getUser();
    void setUser(Person person);

    String getRegId();
    void setRegId(String regId);

    boolean isDone();
    void setDone(boolean done);

    /* Already part of NanoLrsModel */
    //DateTimeType getDateCreated();
    //void setDateCreated(DateTimeType dateCreated);

    DateTimeType getDateAccessed();
    void setDateAccessed(DateTimeType dateAccessed);
}
