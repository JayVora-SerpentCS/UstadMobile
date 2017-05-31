package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 */

public interface UMPackage extends NanoLrsModel {

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    String getName();
    void setName(String name);

    String getDesc();
    void setDesc(String desc);

    int getMaxStudents();
    void setMaxStudents(int maxStudents);

    int getMaxTeachers();
    void setMaxTeachers(int maxTeachers);

    int getMaxPublishers();
    void setMaxPublishers(int maxPublishers);

    float getPriceRangePerMonth();
    void setPriceRangePerMonth(float priceRangePerMonth);

}
