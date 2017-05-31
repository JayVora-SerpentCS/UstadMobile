package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

import java.util.Collection;

/**
 * Created by varuna on 5/24/2017.
 */

public interface Alert extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    /* Trying this for Alert2School : TODO: Check if this is the best way to do m2m*/
    /*
    @nanolrs.foreignFieldName=school
     */
    Collection<? extends Alert2School> getSchools();
    void setSchools(Collection<? extends Alert2School> schools);

    /* Trying this for Alert2Clazz : TODO: Check if this is the best way to do m2m*/
    /*
    @nanolrs.foreignFieldName=clazz
     */
    Collection<? extends Alert2Clazz> getClazzes();
    void setClazzes(Collection<? extends Alert2Clazz> clazzes);

    /* What day of the week should this Alert be for */
    int getDayOfWeek();
    void setDayOfWeek(int dayOfWeek);

    /* What (if specified) month this Alert is in */
    int getMonth();
    void setMonth(int month);

    /* What (if specified) day of month this Alert is in */
    int getDayOfMonth();
    void setDayOfMonth(int dayOfMonth);

    int getMinute();
    void setMinute(int minute);

    /* Is this Alert active */
    boolean isActive();
    void setActive(boolean active);

    /* Trying this for Alert2AlertType: TODO: Check if this is the best way to do m2m */
    /*
    @nanolrs.foreignFieldName=alertType
    */
    Collection<? extends Alert2AlertType> getAlertTypes();
    void setAlertTypes(Collection <? extends Alert2AlertType> alertTypes);

    /* The emails where the alert is to be sent. Applicable to emial alerts
    * Multiple emails seperated by comma . Basically the "to" field in an email*/
    String getToEmails();
    void setToEmails(String toEmails);
}
